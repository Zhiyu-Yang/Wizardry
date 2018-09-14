package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer;
import com.teamwizardry.librarianlib.features.gui.components.SpriteLayer;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.teamwizardry.wizardry.client.gui.worktable.WorktableGui.PLATE;
import static com.teamwizardry.wizardry.client.gui.worktable.WorktableGui.PLATE_HIGHLIGHTED;

public class ModuleLayer extends GuiLayer {

    private boolean isHighlighted = false;
    private String moduleID = "missingno";

    private SpriteLayer iconLayer;
    private SpriteLayer backgroundLayer;

    public ModuleLayer(int posX, int posY) {
        super(posX, posY, PLATE.getWidth(), PLATE.getHeight());
        this.getTransform().setAnchor(new Vec2d(0.5, 0.5));
        iconLayer = new SpriteLayer(null, 0, 0, PLATE.getWidth(), PLATE.getHeight());
        backgroundLayer = new SpriteLayer(PLATE, 0, 0, PLATE.getWidth(), PLATE.getHeight());
        this.add(backgroundLayer, iconLayer);
    }

    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
        backgroundLayer.setSprite(isHighlighted() ? PLATE_HIGHLIGHTED : PLATE);
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
        iconLayer.setSprite(new Sprite(
                new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + this.getModuleID() + ".png")
        ));
    }

    public String getModuleID() {
        return moduleID;
    }

    @Override
    public void layoutChildren() {
        backgroundLayer.setPos(Vec2d.ZERO);
        backgroundLayer.setSize(this.getSize());
        double yShrink = this.getSize().getY() * 4/PLATE.getHeight();
        double xShrink = this.getSize().getX() * 4/PLATE.getWidth();
        iconLayer.setPos(new Vec2d(xShrink/2, yShrink/2));
        iconLayer.setSize(this.getSize().sub(xShrink, yShrink));
    }
}
