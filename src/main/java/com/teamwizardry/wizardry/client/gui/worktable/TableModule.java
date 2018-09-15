package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer;
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.components.TextLayer;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.gui.value.GuiAnimator;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier2D;
import com.teamwizardry.wizardry.api.spell.module.*;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.teamwizardry.wizardry.client.gui.worktable.WorktableGui.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class TableModule extends GuiComponent {

	@Nonnull
	private final WorktableGui worktable;
	@Nonnull
	private final Module module;
	private final boolean draggable;
	private final boolean benign;
	public float radius = 16, textRadius = 10;
	@Nullable
	private TableModule linksTo = null;
	private boolean enableTooltip;
	private ModuleLayer moduleLayer;
	private GuiLayer centerLayer = new GuiLayer(8, 8, 0, 0);
	/**
	 * ALWAYS from the context of null. Never to any other component.
	 */
	private Vec2d initialPos;
	private Map<String, ModuleLayer> modifiers = new HashMap<>();
	private Map<String, TextLayer> modifierText = new HashMap<>();

	public TableModule(@Nonnull WorktableGui worktable, @Nonnull Module module, boolean draggable, boolean benign) {
		super(0, 0, PLATE.getWidth(), PLATE.getHeight());
		this.worktable = worktable;
		this.module = module;
		this.draggable = draggable;
		this.benign = enableTooltip = benign;

		this.centerLayer.setPos(this.getSize().divide(2));
		this.add(this.centerLayer);

		this.moduleLayer = new ModuleLayer(0, 0);
		this.moduleLayer.getTransform().setAnchor(new Vec2d(0.5, 0.5));
		this.moduleLayer.setModuleID(module.getID());
		this.centerLayer.add(moduleLayer);

		initialPos = thisPosToOtherContext(null);

		ComponentVoid paper = worktable.paper;

		if (draggable) getTransform().setTranslateZ(30);

		if (!benign && draggable) {
			setData(UUID.class, "uuid", UUID.randomUUID());
		}

		if (!benign && !draggable)
			BUS.hook(GuiComponentEvents.MouseDownEvent.class, (event) -> {
				if (worktable.animationPlaying) return;
				if (event.getButton() == EnumMouseButton.LEFT && getMouseOver()) {
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_IN, 1f, 1f);
					TableModule item = new TableModule(this.worktable, this.module, true, false);
					item.setPos(paper.otherPosToThisContext(this, event.getMousePos()));
					DragMixin drag = new DragMixin(item, vec2d -> vec2d);
					drag.setClickedPoint(new Vec2d(6, 6));
					drag.setMouseDown(event.getButton());
					paper.add(item);

					event.cancel();
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
				if (worktable.animationPlaying) return;
				if (!getMouseOver()) return;
				initialPos = event.component.thisPosToOtherContext(null);
				if (event.getButton() == EnumMouseButton.RIGHT) {
					event.component.addTag("connecting");
					Minecraft.getMinecraft().player.playSound(ModSounds.POP, 1f, 1f);
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragMoveEvent.class, (event) -> {
				if (worktable.animationPlaying || event.getButton() == EnumMouseButton.RIGHT) {
					// event.getPos returns the before-moving position. Setting it back to it's place.
					// This allows the component to stay where it is while also allowing us to draw a line
					// outside of it's box
					event.setNewPos(event.getPos());
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
				if (worktable.animationPlaying) return;

				if (!event.component.hasTag("placed")) event.component.addTag("placed");

				Vec2d currentPos = event.component.thisPosToOtherContext(null);
				if (event.getButton() == EnumMouseButton.LEFT && initialPos.squareDist(currentPos) < 0.1) {

					if (worktable.selectedModule == this) {
						Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_OUT, 1f, 1f);
						worktable.selectedModule = null;

						add(GuiAnimator.animate(5, Easing.easeOutCubic, () -> {
							this.moduleLayer.setSize(new Vec2d(20, 20));
							this.layoutLayer();
						}));
						add(GuiAnimator.animate(20, Easing.easeOutCubic, () -> {
							this.radius = 16;
							this.layoutLayer();
						}));
						add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
							this.textRadius = 30;
							this.layoutLayer();
						}));

					} else {
						Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_IN, 1f, 1f);
						if (worktable.selectedModule != null) {

							worktable.selectedModule.layoutLayer();
							add(GuiAnimator.animate(5, Easing.easeOutCubic, () -> {
								worktable.selectedModule.moduleLayer.setSize(new Vec2d(16, 16));
								worktable.selectedModule.radius = 10;
								worktable.selectedModule.layoutLayer();
							}));

							add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
								worktable.selectedModule.textRadius = 5;
								worktable.selectedModule.layoutLayer();
							}));
							worktable.selectedModule.setNeedsLayout();

							this.layoutLayer();
							add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
							    this.textRadius = 0;
								this.layoutLayer();
							}));
						}

						worktable.selectedModule = this;

						add(GuiAnimator.animate(5, Easing.easeOutCubic, () -> {
							this.moduleLayer.setSize(new Vec2d(24, 24));
							this.layoutLayer();
						}));

						add(GuiAnimator.animate(20, Easing.easeOutCubic, () -> {
							this.radius = 24;
							this.layoutLayer();
						}));

						add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
							this.textRadius = 40;
							this.layoutLayer();
						}));
					}

					worktable.modifiers.refresh();

					event.component.removeTag("connecting");
					return;
				}

				Vec2d plateSize = paper.getSize();
				Vec2d platePos = event.component.getPos();
				boolean isInsidePaper = platePos.getX() >= 0 && platePos.getX() <= plateSize.getX() && platePos.getY() >= 0 && platePos.getY() <= plateSize.getY();

				if (!isInsidePaper) {
					if (!event.component.hasTag("connecting")) {

						for (GuiComponent paperComponent : paper.getSubComponents()) {
							if (paperComponent == event.component) continue;

							if (!(paperComponent instanceof TableModule)) continue;
							TableModule linkTo = (TableModule) paperComponent;

							if (linkTo.getLinksTo() == this) {
								linkTo.setLinksTo(null);
							}
						}

						if (worktable.selectedModule == this) worktable.selectedModule = null;

						Minecraft.getMinecraft().player.playSound(ModSounds.ZOOM, 1f, 1f);
						event.component.invalidate();

						if (event.component.hasTag("placed"))
							worktable.setToastMessage("", Color.GREEN);

						worktable.modifiers.refresh();
					}
					event.component.removeTag("connecting");
					return;
				}

				if (event.component.hasTag("connecting")) {
					for (GuiComponent paperComponent : paper.getSubComponents()) {
						if (paperComponent == event.component) continue;
						if (!paperComponent.getMouseOverNoOcclusion()) continue;

						if (!(paperComponent instanceof TableModule)) continue;
						TableModule linkTo = (TableModule) paperComponent;
						if (!linkTo.draggable) continue;
						if (linkTo == this) continue;

						if (checkSafety(paper)) {
							if (getLinksTo() == linkTo) {
								event.component.removeTag("connecting");
								setLinksTo(null);
								worktable.setToastMessage("", Color.GREEN);
								return;
							} else if (isCompatibleWith(linkTo)) {
								setLinksTo(linkTo);
								Minecraft.getMinecraft().player.playSound(ModSounds.BELL_TING, 1f, 1f);

								boolean linkedToSelf = false;
								if (linkTo.getLinksTo() == this) {
									linkedToSelf = true;
									linkTo.setLinksTo(null);
								}

								if (checkSafety(paper)) {
									worktable.setToastMessage("", Color.GREEN);
								} else {
									setLinksTo(null);

									if (linkedToSelf) {
										linkTo.setLinksTo(this);
									}

									worktable.setToastMessage(LibrarianLib.PROXY.translate("wizardry.table.loop_error"), Color.RED);
								}
							} else {
								String connectionFail = LibrarianLib.PROXY.translate("wizardry.table.connection_doesnt_work");
								if (getModule() instanceof ModuleEffect && linkTo.getModule() instanceof ModuleEvent) {
									connectionFail += "\n\n" + LibrarianLib.PROXY.translate("wizardry.table.connection_effect_to_event");
								} else if (getModule() instanceof ModuleEffect) {
									connectionFail += "\n\n" + LibrarianLib.PROXY.translate("wizardry.table.connection_effect_to_any");
								} else if (getModule() instanceof ModuleEvent && linkTo.getModule() instanceof ModuleShape) {
									connectionFail += "\n\n" + LibrarianLib.PROXY.translate("wizardry.table.connection_event_to_something");
								}
								worktable.setToastMessage(connectionFail, Color.RED);
							}
						} else {
							worktable.setToastMessage(LibrarianLib.PROXY.translate("wizardry.table.loop_found"), Color.RED);
						}

						event.component.removeTag("connecting");
						return;
					}
				}

				event.component.removeTag("connecting");
			});

//		if (!benign || enableTooltip)
//			getTooltip_im().set(() -> {
//				List<String> txt = new ArrayList<>();
//
//				if (worktable.animationPlaying) return txt;
//				if (this.hasTag("connecting")) return txt;
//
//				txt.add(TextFormatting.GOLD + module.getReadableName());
//				if (GuiScreen.isShiftKeyDown()) {
//					txt.add(TextFormatting.GRAY + module.getDescription());
//					if (module.getAttributeRanges().keySet().stream().anyMatch(attribute -> attribute.hasDetailedText()))
//						if (GuiScreen.isCtrlKeyDown())
//							module.getDetailedInfo().forEach(info -> txt.add(TextFormatting.GRAY + info));
//						else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.ctrl"));
//				} else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.sneak"));
//				return txt;
//			});

		if (!benign)
			BUS.hook(GuiLayerEvents.MouseInEvent.class, event -> {
				if (worktable.animationPlaying) return;
				if (worktable.selectedModule == this) return;

				add(GuiAnimator.animate(5, Easing.easeOutCubic, () -> {
					this.moduleLayer.setSize(new Vec2d(20, 20));
					this.layoutLayer();
				}));

				add(GuiAnimator.animate(20, Easing.easeOutCubic, () -> {
					this.radius = 16;
					this.layoutLayer();
				}));

				add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
					this.textRadius = 30;
					this.layoutLayer();
				}));
			});

		if (!benign)
			BUS.hook(GuiLayerEvents.MouseOutEvent.class, event -> {
				if (worktable.animationPlaying) return;
				if (worktable.selectedModule == this) return;

				add(GuiAnimator.animate(5, Easing.easeOutCubic, () -> {
					this.moduleLayer.setSize(new Vec2d(16, 16));
					this.layoutLayer();
				}));

				add(GuiAnimator.animate(20, Easing.easeOutCubic, () -> {
					this.radius = 10;
					this.layoutLayer();
				}));

				add(GuiAnimator.animate(40, Easing.easeOutCubic, () -> {
					this.textRadius = 0;
					this.layoutLayer();
				}));
			});

		this.BUS.hook(GuiComponentEvents.SetDataEvent.class, (e) -> {
			this.setNeedsLayout();
		});
	}

	@Override
	public void layoutChildren() {
		layoutModifiers();
	}

	private void layoutModifiers() {
		HashMap<ModuleModifier, Integer> modifiers = new HashMap<>();
		List<ModuleModifier> modifierList = new ArrayList<>();
		Set<String> idSet = new HashSet<>();
		for (Module module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
			if (!(module instanceof ModuleModifier)) continue;
			if (!hasData(Integer.class, module.getID())) continue;

			modifiers.put((ModuleModifier) module, getData(Integer.class, module.getID()));
			modifierList.add((ModuleModifier) module);
			idSet.add(module.getID());
		}

		for (String id : this.modifiers.keySet()) {
			if (!idSet.contains(id)) {
				ModuleLayer moduleLayer = this.modifiers.get(id);
				moduleLayer.invalidate();
				this.modifiers.remove(id);
			}
		}
		for (String id : this.modifierText.keySet()) {
			if (!idSet.contains(id)) {
				TextLayer textLayer = this.modifierText.get(id);
				textLayer.invalidate();
				this.modifierText.remove(id);
			}
		}

		int count = modifierList.size();
		for (int i = 0; i < count; i++) {
			ModuleModifier modifier = modifierList.get(i);
			float angle = (float) (i * Math.PI * 2.0 / count);

			layoutModifier(modifier, modifiers.get(modifier), angle);
		}
	}

	private void layoutModifier(ModuleModifier modifier, int count, float angle) {
		ModuleLayer moduleLayer = this.modifiers.get(modifier.getID());
		TextLayer textLayer = this.modifierText.get(modifier.getID());
		if(moduleLayer == null) {
			moduleLayer = new ModuleLayer(0, 0);
			moduleLayer.getTransform().setAnchor(new Vec2d(0.5, 0.5));
			moduleLayer.setZIndex(-100);
			moduleLayer.setModuleID(modifier.getID());
			this.centerLayer.add(moduleLayer);
			this.modifiers.put(modifier.getID(), moduleLayer);
		}
		if(textLayer == null) {
			textLayer = new TextLayer(0, 0);
			textLayer.getTransform().setAnchor(new Vec2d(0.5, 0.5));
			textLayer.setZIndex(-2000);
			this.centerLayer.add(textLayer);
			this.modifierText.put(modifier.getID(), textLayer);
		}

		moduleLayer.setSize(this.moduleLayer.getSize().mul(0.75f));
		moduleLayer.setPos(new Vec2d(MathHelper.cos(angle) * radius, MathHelper.sin(angle) * radius));

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		String txt = "x" + count;
		// temporary, just until I sort out the text component
		textLayer.setSize(new Vec2d(font.getStringWidth(txt), font.FONT_HEIGHT));
		textLayer.setPos(new Vec2d(MathHelper.cos(angle) * textRadius, MathHelper.sin(angle) * textRadius));
		textLayer.setText(txt);
	}

	public static void drawWire(Vec2d start, Vec2d end, Color primary, Color secondary) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 0, -10);
		STREAK.bind();
		InterpBezier2D bezier = new InterpBezier2D(start, end);
		List<Vec2d> list = bezier.list(50);

		float p = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			float x = (float) (start.length() + ClientTickHandler.getTicks() + ClientTickHandler.getPartialTicks()) / 30f;
			if (i == (int) ((x - Math.floor(x)) * 50f)) {
				p = i / (list.size() - 1.0f);
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		Vec2d lastPoint = null;
		for (int i = 0; i < list.size() - 1; i++) {
			Vec2d point = list.get(i);
			if (lastPoint == null) {
				lastPoint = point;
				continue;
			}

			float dist = (i / (list.size() - 1.0f));

			float wire;
			if (dist < p) {
				float z = Math.abs(dist - p);
				wire = 256.0f / 27.0f * (z * z * z - z * z * z * z);
			} else {
				float z = Math.abs(dist - (p + 1f));
				wire = 256.0f / 27.0f * (z * z * z - z * z * z * z);
			}

			float r = lerp(primary.getRed(), secondary.getRed(), wire) / 255f;
			float g = lerp(primary.getGreen(), secondary.getGreen(), wire) / 255f;
			float b = lerp(primary.getBlue(), secondary.getBlue(), wire) / 255f;

			Vec2d normal = point.sub(lastPoint).normalize();
			Vec2d perp = new Vec2d(-normal.getYf(), normal.getXf()).mul((1.0f - 2.0f * Math.abs(dist - 0.5f) + 0.3f));
			Vec2d point1 = lastPoint.sub(normal.mul(0.5)).add(perp);
			Vec2d point2 = point.add(normal.mul(0.5)).add(perp);
			Vec2d point3 = point.add(normal.mul(0.5)).sub(perp);
			Vec2d point4 = lastPoint.sub(normal.mul(0.5)).sub(perp);

			vb.pos(point1.getXf(), point1.getYf(), 0).tex(0, 0).color(r, g, b, 1f).endVertex();
			vb.pos(point2.getXf(), point2.getYf(), 0).tex(0, 1).color(r, g, b, 1f).endVertex();
			vb.pos(point3.getXf(), point3.getYf(), 0).tex(1, 0).color(r, g, b, 1f).endVertex();
			vb.pos(point4.getXf(), point4.getYf(), 0).tex(1, 1).color(r, g, b, 1f).endVertex();

			lastPoint = point;
		}
		tessellator.draw();

		GlStateManager.translate(0, 0, 10);
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	private static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	public static Color getColorForModule(ModuleType type) {
		switch (type) {
			case EVENT:
				return Color.PINK;
			case SHAPE:
				return Color.CYAN;
			case EFFECT:
				return Color.ORANGE;
			case MODIFIER:
				return Color.GREEN;
			default:
				return Color.BLACK;
		}
	}

	@Override
	public void preFrame() {
		this.moduleLayer.setHighlighted(worktable.selectedModule == this);
		if(worktable.selectedModule == this ||
				(!benign && !worktable.animationPlaying && getMouseOver() && !hasTag("connecting"))
		) {
			this.setZIndex(100);
		} else {
            this.setZIndex(0);
		}
	}

	@Override
	public void draw(float partialTicks) {
		super.draw(partialTicks);
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();

		Vec2d pos = Vec2d.ZERO;

		GlStateManager.translate(0, 0, -20);
		if (hasTag("connecting")) {
			drawWire(pos.add(getSize().getX() / 2.0, getSize().getY() / 2.0), getMousePos(), getColorForModule(module.getModuleType()), Color.WHITE);
		}
		if (linksTo != null && linksTo.getRoot() == this.getRoot()) {
			Vec2d posContext = linksTo.thisPosToOtherContext(this);
			Vec2d posTo = new Vec2d(posContext.getX(), posContext.getY());
			drawWire(pos.add(getSize().getX() / 2.0, getSize().getY() / 2.0), posTo.add(getSize().getX() / 2.0, getSize().getY() / 2.0), getColorForModule(module.getModuleType()), getColorForModule(linksTo.getModule().getModuleType()));
		}

		GlStateManager.translate(0, 0, 20);

	}

	@Nullable
	public TableModule getLinksTo() {
		return linksTo;
	}

	public void setLinksTo(@Nullable TableModule linksTo) {
		this.linksTo = linksTo;
	}

	@Nonnull
	public WorktableGui getWorktable() {
		return worktable;
	}

	public boolean isDraggable() {
		return draggable;
	}

	@Nonnull
	public Module getModule() {
		return module;
	}

	/**
	 * safe means that there's at least one module
	 * that has a link to something but isn't being linked by something
	 * IE: A spell chain head.
	 */
	private boolean checkSafety(GuiComponent paper) {
		for (GuiComponent child : paper.getSubComponents()) {
			if (child == this) continue;
			if (!(child instanceof TableModule)) continue;
			TableModule childModule = (TableModule) child;

			boolean linkedFromSomewhere = false;
			if (childModule.getLinksTo() != null) {
				for (GuiComponent subChild : paper.getSubComponents()) {
					if (subChild == child) continue;
					if (!(subChild instanceof TableModule)) continue;
					TableModule subChildModule = (TableModule) subChild;

					if (childModule.getLinksTo() == subChildModule) continue;

					if (subChildModule.getLinksTo() == childModule) {
						linkedFromSomewhere = true;
						break;
					}
				}
			}

			if (!linkedFromSomewhere) {
				return true;
			}
		}
		return false;
	}

	private boolean isCompatibleWith(TableModule linkTo) {
		switch (getModule().getModuleType()) {
			case SHAPE:
				return true;
			case EVENT:
				return linkTo.getModule() instanceof ModuleEffect;
			default: {
				return false;
			}
		}
	}

	public boolean isEnableTooltip() {
		return enableTooltip;
	}

	public void setEnableTooltip(boolean enableTooltip) {
		this.enableTooltip = enableTooltip;
	}
}
