package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.wizardry.api.capability.mana.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import kotlin.Pair;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;

/**
 * Created by Demoniaque.
 */
@Savable
@SuppressWarnings("rawtypes")
public class SpellData implements INBTSerializable<NBTTagCompound> {

	private static HashMap<Pair, ProcessData.Process> dataProcessor = new HashMap<>();

	@Nonnull
	public final World world;
	@Nonnull
	private final HashMap<Pair, Object> data = new HashMap<>();

	public SpellData(@Nonnull World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T, E extends NBTBase> Pair<String, Class<T>> constructPair(@Nonnull String key, @Nonnull Class<?> type, ProcessData.Process<E, T> data) {
		Pair<String, Class<T>> pair = new Pair(key, type);
		dataProcessor.put(pair, data);
		return pair;
	}

	public void addAllData(HashMap<Pair, Object> data) {
		this.data.putAll(data);
	}

	public <T> void addData(@Nonnull Pair<String, Class<T>> key, @Nullable T value) {
		this.data.put(key, value);
	}

	public <T> void removeData(@Nonnull Pair<String, Class<T>> key) {
		this.data.remove(key);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getData(@Nonnull Pair<String, Class<T>> pair) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return null;
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> T getData(@Nonnull Pair<String, Class<T>> pair, @Nonnull T def) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return def;
	}

	public <T> boolean hasData(@Nonnull Pair<String, Class<T>> pair) {
		return data.containsKey(pair) && data.get(pair) != null;
	}

	public void processTrace(RayTraceResult trace, @Nullable Vec3d fallback) {

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			processEntity(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
			processBlock(trace.getBlockPos(), trace.sideHit, trace.hitVec);
		else {
			Vec3d vec = trace.hitVec == null ? fallback : trace.hitVec;

			if (vec == null) return;
			processBlock(new BlockPos(vec), null, vec);
		}
	}

	public void processTrace(RayTraceResult trace) {
		processTrace(trace, null);
	}

	@Nullable
	public Vec3d getOriginWithFallback() {
		Vec3d origin = getData(DefaultKeys.ORIGIN);
		if (origin == null) {
			Entity caster = getData(DefaultKeys.CASTER);
			if (caster == null) {
				Vec3d target = getData(DefaultKeys.TARGET_HIT);
				if (target == null) {
					BlockPos pos = getData(BLOCK_HIT);
					if (pos == null) {
						Entity victim = getData(DefaultKeys.ENTITY_HIT);
						if (victim == null) {
							return null;
						} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
					} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
				} else return target;
			} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getOrigin() {
		Vec3d origin = getData(DefaultKeys.ORIGIN);
		if (origin == null) {
			Entity caster = getData(DefaultKeys.CASTER);
			if (caster == null) {
				return null;
			} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getTargetWithFallback() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					Vec3d origin = getData(DefaultKeys.ORIGIN);
					if (origin == null) {
						Entity caster = getData(DefaultKeys.CASTER);
						if (caster == null) {
							return null;
						} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
					}
					return origin;
				} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public Vec3d getTarget() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					return null;
				} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public BlockPos getTargetPos() {
		return getData(BLOCK_HIT);
	}

	@Nullable
	public EnumFacing getFaceHit() {
		return getData(DefaultKeys.FACE_HIT);
	}

	@Nullable
	public Entity getCaster() {
		return getData(DefaultKeys.CASTER);
	}

	@Nullable
	public Entity getVictim() {
		return getData(DefaultKeys.ENTITY_HIT);
	}

	@Nullable
	public IWizardryCapability getCapability() {
		IWizardryCapability capability = getData(DefaultKeys.CAPABILITY);
		if (capability == null) {
			Entity caster = getCaster();
			if (caster == null) {
				return null;
			} else return WizardryCapabilityProvider.getCap(caster);
		} else return capability;
	}

	public RayTraceResult.Type getHitType() {
		if (getVictim() == null) {
			Vec3d vec = getTarget();
			if (vec == null) {
				return RayTraceResult.Type.MISS;
			} else return RayTraceResult.Type.BLOCK;
		} else return RayTraceResult.Type.ENTITY;
	}

	public float getPitch() {
		return getData(DefaultKeys.PITCH, 0f);
	}

	public float getYaw() {
		return getData(DefaultKeys.YAW, 0f);
	}

	@Nullable
	public Vec3d getOriginHand() {
		Vec3d trueOrigin = getOriginWithFallback();
		if (trueOrigin == null) return null;
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - getYaw()));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - getYaw()));
		return new Vec3d(offX, 0, offZ).add(trueOrigin);
	}

	public void processEntity(@Nonnull Entity entity, boolean asCaster) {
		if (asCaster) {
			addData(DefaultKeys.ORIGIN, entity.getPositionVector().add(0, entity.getEyeHeight(), 0));
			addData(DefaultKeys.CASTER, entity);
			addData(DefaultKeys.YAW, entity.rotationYaw);
			addData(DefaultKeys.PITCH, entity.rotationPitch);
			addData(DefaultKeys.LOOK, entity.getLook(0));
			addData(DefaultKeys.CAPABILITY, WizardryCapabilityProvider.getCap(entity));
		} else {
			addData(DefaultKeys.TARGET_HIT, entity.getPositionVector().add(0, entity.height / 2.0, 0));
			addData(DefaultKeys.ENTITY_HIT, entity);
		}
	}

	public void processBlock(@Nullable BlockPos pos, @Nullable EnumFacing facing, @Nullable Vec3d targetHit) {
		if (pos == null && targetHit != null) pos = new BlockPos(targetHit);
		if (targetHit == null && pos != null) targetHit = new Vec3d(pos).add(0.5, 0.5, 0.5);

		addData(BLOCK_HIT, pos);
		addData(DefaultKeys.TARGET_HIT, targetHit);
		addData(DefaultKeys.FACE_HIT, facing);
	}

	public SpellData copy() {
		SpellData spell = new SpellData(world);
		spell.addAllData(data);
		spell.deserializeNBT(serializeNBT());
		return spell;
	}

	public static SpellData deserializeData(World world, NBTTagCompound compound) {
		SpellData data = new SpellData(world);
		data.deserializeNBT(compound);
		return data;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deserializeNBT(NBTTagCompound nbt) {
		primary:
		for (String key : nbt.getKeySet()) {
			for (Pair pair : dataProcessor.keySet()) {
				if (pair.getFirst().equals(key)) {
					NBTBase nbtType = nbt.getTag(pair.getFirst() + "");
					data.put(pair, dataProcessor.get(pair).deserialize(world, nbtType));
					continue primary;
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (Pair pair : data.keySet()) {
			NBTBase nbtClass = dataProcessor.get(pair).serialize(data.get(pair));
			compound.setTag(pair.getFirst() + "", nbtClass);
		}

		return compound;
	}

	@Override
	public String toString() {
		return "SpellData{" +
				"world=" + world +
				", data=" + data +
				'}';
	}

	public static class DefaultKeys {

		public static final Pair<String, Class<NBTTagList>> TAG_LIST = constructPair("list", NBTTagList.class, new ProcessData.Process<NBTTagList, NBTTagList>() {

			@Nonnull
			@Override
			public NBTTagList serialize(@Nullable NBTTagList object) {
				return object == null ? new NBTTagList() : object;
			}

			@Nullable
			@Override
			public NBTTagList deserialize(@Nullable World world, @Nonnull NBTTagList object) {
				return object;
			}
		});

		public static final Pair<String, Class<NBTTagCompound>> COMPOUND = constructPair("compound", NBTTagCompound.class, new ProcessData.Process<NBTTagCompound, NBTTagCompound>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(@Nullable NBTTagCompound object) {
				return object == null ? new NBTTagCompound() : object;
			}

			@Override
			public NBTTagCompound deserialize(@Nullable World world, @Nonnull NBTTagCompound object) {
				return object;
			}
		});

		public static final Pair<String, Class<Integer>> MAX_TIME = constructPair("max_time", Integer.class, new ProcessData.Process<NBTTagInt, Integer>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(@Nullable Integer object) {
				if (object == null) return new NBTTagInt(1);
				return new NBTTagInt(object);
			}

			@Override
			public Integer deserialize(World world, @Nonnull NBTTagInt object) {
				return object.getInt();
			}
		});

		public static final Pair<String, Class<Entity>> CASTER = constructPair("caster", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(Entity object) {
				if (object != null)
					return new NBTTagInt(object.getEntityId());
				return new NBTTagInt(-1);
			}

			@Override
			public Entity deserialize(World world, @Nonnull NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		public static final Pair<String, Class<Float>> YAW = constructPair("yaw", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Nonnull
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(World world, @Nonnull NBTTagFloat object) {
				return object.getFloat();
			}
		});

		public static final Pair<String, Class<Float>> PITCH = constructPair("pitch", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Nonnull
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(World world, @Nonnull NBTTagFloat object) {
				return object.getFloat();
			}
		});

		public static final Pair<String, Class<Vec3d>> LOOK = constructPair("look", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(World world, @Nonnull NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		public static final Pair<String, Class<Vec3d>> ORIGIN = constructPair("origin", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(World world, @Nonnull NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		public static final Pair<String, Class<Entity>> ENTITY_HIT = constructPair("entity_hit", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(Entity object) {
				if (object == null) return new NBTTagInt(-1);
				return new NBTTagInt(object.getEntityId());
			}

			@Override
			public Entity deserialize(World world, @Nonnull NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		public static final Pair<String, Class<BlockPos>> BLOCK_HIT = constructPair("block_hit", BlockPos.class, new ProcessData.Process<NBTTagLong, BlockPos>() {
			@Nonnull
			@Override
			public NBTTagLong serialize(BlockPos object) {
				if (object == null) return new NBTTagLong(-1L);
				return new NBTTagLong(object.toLong());
			}

			@Override
			public BlockPos deserialize(World world, @Nonnull NBTTagLong object) {
				return BlockPos.fromLong(object.getLong());
			}
		});

		@Nonnull
		public static final Pair<String, Class<EnumFacing>> FACE_HIT = constructPair("face_hit", EnumFacing.class, new ProcessData.Process<NBTTagString, EnumFacing>() {
			@Nonnull
			@Override
			public NBTTagString serialize(EnumFacing object) {
				if (object == null) return new NBTTagString("UP");
				return new NBTTagString(object.name());
			}

			@Override
			public EnumFacing deserialize(World world, @Nonnull NBTTagString object) {
				return EnumFacing.valueOf(object.getString());
			}
		});

		@Nonnull
		public static final Pair<String, Class<IWizardryCapability>> CAPABILITY = constructPair("capability", IWizardryCapability.class, new ProcessData.Process<NBTTagCompound, IWizardryCapability>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(IWizardryCapability object) {
				if (object == null) return new NBTTagCompound();
				return object.serializeNBT();
			}

			@Override
			public IWizardryCapability deserialize(World world, @Nonnull NBTTagCompound object) {
				DefaultWizardryCapability cap = new DefaultWizardryCapability();
				cap.deserializeNBT(object);
				return cap;
			}
		});

		@Nonnull
		public static final Pair<String, Class<Vec3d>> TARGET_HIT = constructPair("target_hit", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				if (object == null) return new NBTTagCompound();
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(World world, @Nonnull NBTTagCompound object) {
				if (!object.hasKey("x") || !object.hasKey("y") || !object.hasKey("z")) return Vec3d.ZERO;
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		@Nonnull
		public static final Pair<String, Class<IBlockState>> BLOCK_STATE = constructPair("block_state", IBlockState.class, new ProcessData.Process<NBTTagCompound, IBlockState>() {

			@Nonnull
			@Override
			public NBTTagCompound serialize(@Nullable IBlockState object) {
				NBTTagCompound nbtState = new NBTTagCompound();
				if (object == null) return nbtState;
				NBTUtil.writeBlockState(nbtState, object);
				return nbtState;
			}

			@Override
			public IBlockState deserialize(@Nullable World world, @Nonnull NBTTagCompound object) {
				return NBTUtil.readBlockState(object);
			}
		});

		@Nonnull
		public static final Pair<String, Class<Long>> SEED = constructPair("seed", Long.class, new ProcessData.Process<NBTTagLong, Long>() {

			@Nonnull
			@Override
			public NBTTagLong serialize(@Nullable Long object) {
				if (object == null) return new NBTTagLong(0);
				return new NBTTagLong(object);
			}

			@Nonnull
			@Override
			public Long deserialize(World world, @Nonnull NBTTagLong object) {
				return object.getLong();
			}
		});

		public static final Pair<String, Class<Set<BlockPos>>> BLOCK_SET = constructPair("block_set", Set.class, new ProcessData.Process<NBTTagList, Set<BlockPos>>() {

			@Nonnull
			@Override
			public NBTTagList serialize(@Nullable Set<BlockPos> object) {
				NBTTagList list = new NBTTagList();

				if (object == null) return list;

				for (BlockPos pos : object) {
					list.appendTag(new NBTTagLong(pos.toLong()));
				}

				return list;
			}

			@NotNull
			@Override
			public Set<BlockPos> deserialize(@Nullable World world, @Nonnull NBTTagList object) {
				Set<BlockPos> poses = new HashSet<>();

				for (NBTBase base : object) {
					if (base instanceof NBTTagLong) {
						poses.add(BlockPos.fromLong(((NBTTagLong) base).getLong()));
					}
				}

				return poses;
			}
		});
		
		public static final Pair<String, Class<HashMap<BlockPos, IBlockState>>> BLOCKSTATE_CACHE = constructPair("block_state_cache", HashMap.class, new ProcessData.Process<NBTTagList, HashMap<BlockPos, IBlockState>>() {

			@Nonnull
			@Override
			public NBTTagList serialize(@Nullable HashMap<BlockPos, IBlockState> object) {
				NBTTagList list = new NBTTagList();

				if (object == null) return list;

				for (Map.Entry<BlockPos, IBlockState> entry : object.entrySet()) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setLong("pos", entry.getKey().toLong());

					NBTTagCompound nbtState = new NBTTagCompound();
					NBTUtil.writeBlockState(nbtState, entry.getValue());
					compound.setTag("blockstate", nbtState);

					list.appendTag(compound);
				}

				return list;
			}

			@Override
			public HashMap<BlockPos, IBlockState> deserialize(@Nullable World world, @Nonnull NBTTagList object) {
				HashMap<BlockPos, IBlockState> stateCache = new HashMap<>();

				for (NBTBase base : object) {
					if (base instanceof NBTTagCompound) {

						NBTTagCompound compound = (NBTTagCompound) base;
						if (compound.hasKey("pos") && compound.hasKey("blockstate")) {
							BlockPos pos = BlockPos.fromLong(compound.getLong("pos"));
							IBlockState state = NBTUtil.readBlockState(compound.getCompoundTag("blockstate"));

							stateCache.put(pos, state);

						}
					}
				}

				return stateCache;
			}
		});
	
	}
}
