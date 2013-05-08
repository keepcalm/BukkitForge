package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.*;
import static net.minecraftforge.event.entity.living.LivingEvent.*;

public abstract class EntityLiving extends Entity
{
    /**
     * An array of probabilities that determines whether a random enchantment should be added to the held item. Indexed
     * by difficulty.
     */
    private static final float[] enchantmentProbability = new float[] {0.0F, 0.0F, 0.1F, 0.2F};

    /** Probability to get enchanted armor */
    private static final float[] armorEnchantmentProbability = new float[] {0.0F, 0.0F, 0.25F, 0.5F};

    /** Probability to get armor */
    private static final float[] armorProbability = new float[] {0.0F, 0.0F, 0.05F, 0.07F};

    /** Probability to pick up loot */
    public static final float[] pickUpLootProability = new float[] {0.0F, 0.1F, 0.15F, 0.45F};
    public int maxHurtResistantTime = 20;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset = 0.0F;
    public float prevRenderYawOffset = 0.0F;

    /** Entity head rotation yaw */
    public float rotationYawHead = 0.0F;

    /** Entity head rotation yaw at previous tick */
    public float prevRotationYawHead = 0.0F;
    protected float field_70768_au;
    protected float field_70766_av;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected boolean field_70753_ay = true;

    /** the path for the texture of this entityLiving */
    protected String texture = "/mob/char.png";
    protected boolean field_70740_aA = true;
    protected float field_70741_aB = 0.0F;

    /**
     * a string holding the type of entity it is currently only implemented in entityPlayer(as 'humanoid')
     */
    protected String entityType = null;
    protected float field_70743_aD = 1.0F;

    /** The score value of the Mob, the amount of points the mob is worth. */
    protected int scoreValue = 0;
    protected float field_70745_aF = 0.0F;

    /**
     * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed,
     * and slipperiness of the current block.
     */
    public float landMovementFactor = 0.1F;

    /**
     * A factor used to determine how far this entity will move each tick if it is jumping or falling.
     */
    public float jumpMovementFactor = 0.02F;
    public float prevSwingProgress;
    public float swingProgress;
    protected int health = this.getMaxHealth();
    public int prevHealth;

    /**
     * in each step in the damage calculations, this is set to the 'carryover' that would result if someone was damaged
     * .25 hearts (for example), and added to the damage in the next step
     */
    public int carryoverDamage;

    /** Number of ticks since this EntityLiving last produced its sound */
    public int livingSoundTime;

    /**
     * The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint)
     */
    public int hurtTime;

    /** What the hurt time was max set to last. */
    public int maxHurtTime;

    /** The yaw at which this entity was last attacked from. */
    public float attackedAtYaw = 0.0F;

    /**
     * The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world.
     */
    public int deathTime = 0;
    public int attackTime = 0;
    public float prevCameraPitch;
    public float cameraPitch;

    /**
     * This gets set on entity death, but never used. Looks like a duplicate of isDead
     */
    protected boolean dead = false;

    /** The experience points the Entity gives. */
    public int experienceValue;
    public int field_70731_aW = -1;
    public float field_70730_aX = (float)(Math.random() * 0.8999999761581421D + 0.10000000149011612D);
    public float prevLimbYaw;
    public float limbYaw;

    /**
     * Only relevant when limbYaw is not 0(the entity is moving). Influences where in its swing legs and arms currently
     * are.
     */
    public float limbSwing;

    /** The most recent player that has attacked this entity */
    public EntityPlayer attackingPlayer = null;

    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity
     * should drop items on death.
     */
    protected int recentlyHit = 0;

    /** is only being set, has no uses as of MC 1.1 */
    private EntityLiving entityLivingToAttack = null;
    private int revengeTimer = 0;
    private EntityLiving lastAttackingEntity = null;
    public int arrowHitTimer = 0;
    protected HashMap activePotionsMap = new HashMap();

    /** Whether the DataWatcher needs to be updated with the active potions */
    private boolean potionsNeedUpdate = true;
    private int field_70748_f;
    private EntityLookHelper lookHelper;
    private EntityMoveHelper moveHelper;

    /** Entity jumping helper */
    private EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    private PathNavigate navigator;
    public final EntityAITasks tasks;
    public final EntityAITasks targetTasks;

    /** The active target the Task system uses for tracking */
    private EntityLiving attackTarget;
    private EntitySenses senses;
    private float AIMoveSpeed;
    private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);

    /** If -1 there is no maximum distance */
    private float maximumHomeDistance = -1.0F;

    /** Equipment (armor and held item) for this entity. */
    private ItemStack[] equipment = new ItemStack[5];

    /** Chances for each equipment piece from dropping when this entity dies. */
    public float[] equipmentDropChances = new float[5];

    /** The equipment this mob was previously wearing, used for syncing. */
    private ItemStack[] previousEquipment = new ItemStack[5];

    /** Whether an arm swing is currently in progress. */
    public boolean isSwingInProgress = false;
    public int swingProgressInt = 0;

    /** Whether this entity can pick up items from the ground. */
    private boolean canPickUpLoot = false;

    /** Whether this entity should NOT despawn. */
    private boolean persistenceRequired = false;
    public final CombatTracker field_94063_bt = new CombatTracker(this);

    /**
     * The number of updates over which the new position and rotation are to be applied to the entity.
     */
    protected int newPosRotationIncrements;

    /** The new X position to be applied to the entity. */
    protected double newPosX;

    /** The new Y position to be applied to the entity. */
    protected double newPosY;

    /** The new Z position to be applied to the entity. */
    protected double newPosZ;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationYaw;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationPitch;
    float field_70706_bo = 0.0F;

    /** Amount of damage taken in last hit, in half-hearts */
    public int lastDamage = 0;

    /** Holds the living entity age, used to control the despawn. */
    protected int entityAge = 0;
    protected float moveStrafing;
    protected float moveForward;
    protected float randomYawVelocity;

    /** used to check whether entity is jumping. */
    public boolean isJumping = false;
    protected float defaultPitch = 0.0F;
    protected float moveSpeed = 0.7F;

    /** Number of ticks since last jump */
    private int jumpTicks = 0;

    /** This entity's current target. */
    private Entity currentTarget;

    /** How long to keep a specific target entity */
    protected int numTicksToChaseTarget = 0;

    public EntityLiving(World par1World)
    {
        super(par1World);
        this.preventEntitySpawning = true;
        this.tasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        this.targetTasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = new PathNavigate(this, par1World, (float)this.func_96121_ay());
        this.senses = new EntitySenses(this);
        this.field_70770_ap = (float)(Math.random() + 1.0D) * 0.01F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * Math.PI * 2.0D);
        this.rotationYawHead = this.rotationYaw;

        for (int i = 0; i < this.equipmentDropChances.length; ++i)
        {
            this.equipmentDropChances[i] = 0.085F;
        }

        this.stepHeight = 0.5F;
    }

    protected int func_96121_ay()
    {
        return 16;
    }

    public EntityLookHelper getLookHelper()
    {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper()
    {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper()
    {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator()
    {
        return this.navigator;
    }

    /**
     * returns the EntitySenses Object for the EntityLiving
     */
    public EntitySenses getEntitySenses()
    {
        return this.senses;
    }

    public Random getRNG()
    {
        return this.rand;
    }

    public EntityLiving getAITarget()
    {
        return this.entityLivingToAttack;
    }

    public EntityLiving getLastAttackingEntity()
    {
        return this.lastAttackingEntity;
    }

    public void setLastAttackingEntity(Entity par1Entity)
    {
        if (par1Entity instanceof EntityLiving)
        {
            this.lastAttackingEntity = (EntityLiving)par1Entity;
        }
    }

    public int getAge()
    {
        return this.entityAge;
    }

    public float getRotationYawHead()
    {
        return this.rotationYawHead;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float par1)
    {
        this.rotationYawHead = par1;
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed()
    {
        return this.AIMoveSpeed;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float par1)
    {
        this.AIMoveSpeed = par1;
        this.setMoveForward(par1);
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        this.setLastAttackingEntity(par1Entity);
        return false;
    }

    /**
     * Gets the active target the Task system uses for tracking
     */
    public EntityLiving getAttackTarget()
    {
        return this.attackTarget;
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLiving par1EntityLiving)
    {
        this.attackTarget = par1EntityLiving;
        ForgeHooks.onLivingSetAttackTarget(this, par1EntityLiving);
    }

    /**
     * Returns true if this entity can attack entities of the specified class.
     */
    public boolean canAttackClass(Class par1Class)
    {
        return EntityCreeper.class != par1Class && EntityGhast.class != par1Class;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus() {}

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (par3 && this.fallDistance > 0.0F)
        {
            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int k = MathHelper.floor_double(this.posZ);
            int l = this.worldObj.getBlockId(i, j, k);

            if (l == 0)
            {
                int i1 = this.worldObj.blockGetRenderType(i, j - 1, k);

                if (i1 == 11 || i1 == 32 || i1 == 21)
                {
                    l = this.worldObj.getBlockId(i, j - 1, k);
                }
            }

            if (l > 0)
            {
                Block.blocksList[l].onFallenUpon(this.worldObj, i, j, k, this, this.fallDistance);
            }
        }

        super.updateFallState(par1, par3);
    }

    /**
     * Returns true if entity is within home distance from current position
     */
    public boolean isWithinHomeDistanceCurrentPosition()
    {
        return this.isWithinHomeDistance(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    public boolean isWithinHomeDistance(int par1, int par2, int par3)
    {
        return this.maximumHomeDistance == -1.0F ? true : this.homePosition.getDistanceSquared(par1, par2, par3) < this.maximumHomeDistance * this.maximumHomeDistance;
    }

    public void setHomeArea(int par1, int par2, int par3, int par4)
    {
        this.homePosition.set(par1, par2, par3);
        this.maximumHomeDistance = (float)par4;
    }

    public ChunkCoordinates getHomePosition()
    {
        return this.homePosition;
    }

    public float getMaximumHomeDistance()
    {
        return this.maximumHomeDistance;
    }

    public void detachHome()
    {
        this.maximumHomeDistance = -1.0F;
    }

    public boolean hasHome()
    {
        return this.maximumHomeDistance != -1.0F;
    }

    public void setRevengeTarget(EntityLiving par1EntityLiving)
    {
        this.entityLivingToAttack = par1EntityLiving;
        this.revengeTimer = this.entityLivingToAttack != null ? 100 : 0;
        ForgeHooks.onLivingSetAttackTarget(this, par1EntityLiving);
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(8, Integer.valueOf(this.field_70748_f));
        this.dataWatcher.addObject(9, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(6, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(5, "");
    }

    /**
     * returns true if the entity provided in the argument can be seen. (Raytrace)
     */
    public boolean canEntityBeSeen(Entity par1Entity)
    {
        return this.worldObj.rayTraceBlocks(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), this.worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + (double)par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the texture's file path as a String.
     */
    public String getTexture()
    {
        return this.texture;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return !this.isDead;
    }

    public float getEyeHeight()
    {
        return this.height * 0.85F;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 80;
    }

    /**
     * Plays living's sound at its position
     */
    public void playLivingSound()
    {
        String s = this.getLivingSound();

        if (s != null)
        {
            this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");

        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++)
        {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock())
        {
            this.attackEntityFrom(DamageSource.inWall, 1);
        }

        if (this.isImmuneToFire() || this.worldObj.isRemote)
        {
            this.extinguish();
        }

        boolean flag = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;

        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water) && !this.canBreatheUnderwater() && !this.activePotionsMap.containsKey(Integer.valueOf(Potion.waterBreathing.id)) && !flag)
        {
            this.setAir(this.decreaseAirSupply(this.getAir()));

            if (this.getAir() == -20)
            {
                this.setAir(0);

                for (int i = 0; i < 8; ++i)
                {
                    float f = this.rand.nextFloat() - this.rand.nextFloat();
                    float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                    float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                    this.worldObj.spawnParticle("bubble", this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
                }

                this.attackEntityFrom(DamageSource.drown, 2);
            }

            this.extinguish();
        }
        else
        {
            this.setAir(300);
        }

        this.prevCameraPitch = this.cameraPitch;

        if (this.attackTime > 0)
        {
            --this.attackTime;
        }

        if (this.hurtTime > 0)
        {
            --this.hurtTime;
        }

        if (this.hurtResistantTime > 0)
        {
            --this.hurtResistantTime;
        }

        if (this.health <= 0)
        {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0)
        {
            --this.recentlyHit;
        }
        else
        {
            this.attackingPlayer = null;
        }

        if (this.lastAttackingEntity != null && !this.lastAttackingEntity.isEntityAlive())
        {
            this.lastAttackingEntity = null;
        }

        if (this.entityLivingToAttack != null)
        {
            if (!this.entityLivingToAttack.isEntityAlive())
            {
                this.setRevengeTarget((EntityLiving)null);
            }
            else if (this.revengeTimer > 0)
            {
                --this.revengeTimer;
            }
            else
            {
                this.setRevengeTarget((EntityLiving)null);
            }
        }

        this.updatePotionEffects();
        this.field_70763_ax = this.field_70764_aw;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime == 20)
        {
            int i;

            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && !this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                i = this.getExperiencePoints(this.attackingPlayer);

                while (i > 0)
                {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }

            this.setDead();

            for (i = 0; i < 20; ++i)
            {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }
        }
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int par1)
    {
        int j = EnchantmentHelper.getRespiration(this);
        return j > 0 && this.rand.nextInt(j + 1) > 0 ? par1 : par1 - 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (this.experienceValue > 0)
        {
            int i = this.experienceValue;
            ItemStack[] aitemstack = this.getLastActiveItems();

            for (int j = 0; j < aitemstack.length; ++j)
            {
                if (aitemstack[j] != null && this.equipmentDropChances[j] <= 1.0F)
                {
                    i += 1 + this.rand.nextInt(3);
                }
            }

            return i;
        }
        else
        {
            return this.experienceValue;
        }
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer()
    {
        return false;
    }

    /**
     * Spawns an explosion particle around the Entity's location
     */
    public void spawnExplosionParticle()
    {
        for (int i = 0; i < 20; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d0 * d3, this.posY + (double)(this.rand.nextFloat() * this.height) - d1 * d3, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d2 * d3, d0, d1, d2);
        }
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        super.updateRidden();
        this.field_70768_au = this.field_70766_av;
        this.field_70766_av = 0.0F;
        this.fallDistance = 0.0F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.yOffset = 0.0F;
        this.newPosX = par1;
        this.newPosY = par3;
        this.newPosZ = par5;
        this.newRotationYaw = (double)par7;
        this.newRotationPitch = (double)par8;
        this.newPosRotationIncrements = par9;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (ForgeHooks.onLivingUpdate(this))
        {
            return;
        }

        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            int i;

            for (i = 0; i < 5; ++i)
            {
                ItemStack itemstack = this.getCurrentItemOrArmor(i);

                if (!ItemStack.areItemStacksEqual(itemstack, this.previousEquipment[i]))
                {
                    ((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet5PlayerInventory(this.entityId, i, itemstack));
                    this.previousEquipment[i] = itemstack == null ? null : itemstack.copy();
                }
            }

            i = this.getArrowCountInEntity();

            if (i > 0)
            {
                if (this.arrowHitTimer <= 0)
                {
                    this.arrowHitTimer = 20 * (30 - i);
                }

                --this.arrowHitTimer;

                if (this.arrowHitTimer <= 0)
                {
                    this.setArrowCountInEntity(i - 1);
                }
            }
        }

        this.onLivingUpdate();
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = (float)(d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0F;
        this.field_70768_au = this.field_70766_av;
        float f3 = 0.0F;

        if (f > 0.0025000002F)
        {
            f3 = 1.0F;
            f2 = (float)Math.sqrt((double)f) * 3.0F;
            f1 = (float)Math.atan2(d1, d0) * 180.0F / (float)Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F)
        {
            f1 = this.rotationYaw;
        }

        if (!this.onGround)
        {
            f3 = 0.0F;
        }

        this.field_70766_av += (f3 - this.field_70766_av) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");

        if (this.isAIEnabled())
        {
            this.bodyHelper.func_75664_a();
        }
        else
        {
            float f4 = MathHelper.wrapAngleTo180_float(f1 - this.renderYawOffset);
            this.renderYawOffset += f4 * 0.3F;
            float f5 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
            boolean flag = f5 < -90.0F || f5 >= 90.0F;

            if (f5 < -75.0F)
            {
                f5 = -75.0F;
            }

            if (f5 >= 75.0F)
            {
                f5 = 75.0F;
            }

            this.renderYawOffset = this.rotationYaw - f5;

            if (f5 * f5 > 2500.0F)
            {
                this.renderYawOffset += f5 * 0.2F;
            }

            if (flag)
            {
                f2 *= -1.0F;
            }
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F)
        {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F)
        {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
        {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F)
        {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F)
        {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.field_70764_aw += f2;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(int par1)
    {
        if (this.health > 0)
        {
            this.setEntityHealth(this.getHealth() + par1);

            if (this.health > this.getMaxHealth())
            {
                this.setEntityHealth(this.getMaxHealth());
            }

            this.hurtResistantTime = this.maxHurtResistantTime / 2;
        }
    }

    public abstract int getMaxHealth();

    public int getHealth()
    {
        return this.health;
    }

    public void setEntityHealth(int par1)
    {
        this.health = par1;

        if (par1 > this.getMaxHealth())
        {
            par1 = this.getMaxHealth();
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (ForgeHooks.onLivingAttack(this, par1DamageSource, par2))
        {
            return false;
        }

        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (this.worldObj.isRemote)
        {
            return false;
        }
        else
        {
            this.entityAge = 0;

            if (this.health <= 0)
            {
                return false;
            }
            else if (par1DamageSource.isFireDamage() && this.isPotionActive(Potion.fireResistance))
            {
                return false;
            }
            else
            {
                if ((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && this.getCurrentItemOrArmor(4) != null)
                {
                    this.getCurrentItemOrArmor(4).damageItem(par2 * 4 + this.rand.nextInt(par2 * 2), this);
                    par2 = (int)((float)par2 * 0.75F);
                }

                this.limbYaw = 1.5F;
                boolean flag = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (par2 <= this.lastDamage)
                    {
                        return false;
                    }

                    this.damageEntity(par1DamageSource, par2 - this.lastDamage);
                    this.lastDamage = par2;
                    flag = false;
                }
                else
                {
                    this.lastDamage = par2;
                    this.prevHealth = this.health;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(par1DamageSource, par2);
                    this.hurtTime = this.maxHurtTime = 10;
                }

                this.attackedAtYaw = 0.0F;
                Entity entity = par1DamageSource.getEntity();

                if (entity != null)
                {
                    if (entity instanceof EntityLiving)
                    {
                        this.setRevengeTarget((EntityLiving)entity);
                    }

                    if (entity instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)entity;
                    }
                    else if (entity instanceof EntityWolf)
                    {
                        EntityWolf entitywolf = (EntityWolf)entity;

                        if (entitywolf.isTamed())
                        {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (flag)
                {
                    this.worldObj.setEntityState(this, (byte)2);

                    if (par1DamageSource != DamageSource.drown)
                    {
                        this.setBeenAttacked();
                    }

                    if (entity != null)
                    {
                        double d0 = entity.posX - this.posX;
                        double d1;

                        for (d1 = entity.posZ - this.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - this.rotationYaw;
                        this.knockBack(entity, par2, d0, d1);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                if (this.health <= 0)
                {
                    if (flag)
                    {
                        this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(par1DamageSource);
                }
                else if (flag)
                {
                    this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
                }

                return true;
            }
        }
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.hurtTime = this.maxHurtTime = 10;
        this.attackedAtYaw = 0.0F;
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        int i = 0;
        ItemStack[] aitemstack = this.getLastActiveItems();
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k)
        {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null && itemstack.getItem() instanceof ItemArmor)
            {
                int l = ((ItemArmor)itemstack.getItem()).damageReduceAmount;
                i += l;
            }
        }

        return i;
    }

    protected void damageArmor(int par1) {}

    /**
     * Reduces damage, depending on armor
     */
    protected int applyArmorCalculations(DamageSource par1DamageSource, int par2)
    {
        if (!par1DamageSource.isUnblockable())
        {
            int j = 25 - this.getTotalArmorValue();
            int k = par2 * j + this.carryoverDamage;
            this.damageArmor(par2);
            par2 = k / 25;
            this.carryoverDamage = k % 25;
        }

        return par2;
    }

    /**
     * Reduces damage, depending on potions
     */
    protected int applyPotionDamageCalculations(DamageSource par1DamageSource, int par2)
    {
        int j;
        int k;
        int l;

        if (this.isPotionActive(Potion.resistance))
        {
            j = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            k = 25 - j;
            l = par2 * k + this.carryoverDamage;
            par2 = l / 25;
            this.carryoverDamage = l % 25;
        }

        if (par2 <= 0)
        {
            return 0;
        }
        else
        {
            j = EnchantmentHelper.getEnchantmentModifierDamage(this.getLastActiveItems(), par1DamageSource);

            if (j > 20)
            {
                j = 20;
            }

            if (j > 0 && j <= 20)
            {
                k = 25 - j;
                l = par2 * k + this.carryoverDamage;
                par2 = l / 25;
                this.carryoverDamage = l % 25;
            }

            return par2;
        }
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(DamageSource par1DamageSource, int par2)
    {
        if (!this.isEntityInvulnerable())
        {
            par2 = ForgeHooks.onLivingHurt(this, par1DamageSource, par2);
            if (par2 <= 0)
            {
                return;
            }
            par2 = this.applyArmorCalculations(par1DamageSource, par2);
            par2 = this.applyPotionDamageCalculations(par1DamageSource, par2);
            int j = this.getHealth();
            this.health -= par2;
            this.field_94063_bt.func_94547_a(par1DamageSource, j, par2);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return null;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "damage.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "damage.hit";
    }

    /**
     * knocks back this entity
     */
    public void knockBack(Entity par1Entity, int par2, double par3, double par5)
    {
        this.isAirBorne = true;
        float f = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
        float f1 = 0.4F;
        this.motionX /= 2.0D;
        this.motionY /= 2.0D;
        this.motionZ /= 2.0D;
        this.motionX -= par3 / (double)f * (double)f1;
        this.motionY += (double)f1;
        this.motionZ -= par5 / (double)f * (double)f1;

        if (this.motionY > 0.4000000059604645D)
        {
            this.motionY = 0.4000000059604645D;
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource))
        {
            return;
        }

        Entity entity = par1DamageSource.getEntity();
        EntityLiving entityliving = this.func_94060_bK();

        if (this.scoreValue >= 0 && entityliving != null)
        {
            entityliving.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null)
        {
            entity.onKillEntity(this);
        }

        this.dead = true;

        if (!this.worldObj.isRemote)
        {
            int i = 0;

            if (entity instanceof EntityPlayer)
            {
                i = EnchantmentHelper.getLootingModifier((EntityLiving)entity);
            }

            captureDrops = true;
            capturedDrops.clear();
            int j = 0;

            if (!this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                this.dropFewItems(this.recentlyHit > 0, i);
                this.dropEquipment(this.recentlyHit > 0, i);

                if (this.recentlyHit > 0)
                {
                    j = this.rand.nextInt(200) - i;

                    if (j < 5)
                    {
                        this.dropRareDrop(j <= 0 ? 1 : 0);
                    }
                }
            }

            captureDrops = false;

            if (!ForgeHooks.onLivingDrops(this, par1DamageSource, capturedDrops, i, recentlyHit > 0, j))
            {
                for (EntityItem item : capturedDrops)
                {
                    worldObj.spawnEntityInWorld(item);
                }
            }
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    protected void dropRareDrop(int par1) {}

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int j = this.getDropItemId();

        if (j > 0)
        {
            int k = this.rand.nextInt(3);

            if (par2 > 0)
            {
                k += this.rand.nextInt(par2 + 1);
            }

            for (int l = 0; l < k; ++l)
            {
                this.dropItem(j, 1);
            }
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return 0;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        par1 = ForgeHooks.onLivingFall(this, par1);
        if (par1 <= 0)
        {
            return;
        }

        super.fall(par1);
        int i = MathHelper.ceiling_float_int(par1 - 3.0F);

        if (i > 0)
        {
            if (i > 4)
            {
                this.playSound("damage.fallbig", 1.0F, 1.0F);
            }
            else
            {
                this.playSound("damage.fallsmall", 1.0F, 1.0F);
            }

            this.attackEntityFrom(DamageSource.fall, i);
            int j = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset), MathHelper.floor_double(this.posZ));

            if (j > 0)
            {
                StepSound stepsound = Block.blocksList[j].stepSound;
                this.playSound(stepsound.getStepSound(), stepsound.getVolume() * 0.5F, stepsound.getPitch() * 0.75F);
            }
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        double d0;

        if (this.isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            d0 = this.posY;
            this.moveFlying(par1, par2, this.isAIEnabled() ? 0.04F : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else if (this.handleLavaMovement() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            d0 = this.posY;
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else
        {
            float f2 = 0.91F;

            if (this.onGround)
            {
                f2 = 0.54600006F;
                int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

                if (i > 0)
                {
                    f2 = Block.blocksList[i].slipperiness * 0.91F;
                }
            }

            float f3 = 0.16277136F / (f2 * f2 * f2);
            float f4;

            if (this.onGround)
            {
                if (this.isAIEnabled())
                {
                    f4 = this.getAIMoveSpeed();
                }
                else
                {
                    f4 = this.landMovementFactor;
                }

                f4 *= f3;
            }
            else
            {
                f4 = this.jumpMovementFactor;
            }

            this.moveFlying(par1, par2, f4);
            f2 = 0.91F;

            if (this.onGround)
            {
                f2 = 0.54600006F;
                int j = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

                if (j > 0)
                {
                    f2 = Block.blocksList[j].slipperiness * 0.91F;
                }
            }

            if (this.isOnLadder())
            {
                float f5 = 0.15F;

                if (this.motionX < (double)(-f5))
                {
                    this.motionX = (double)(-f5);
                }

                if (this.motionX > (double)f5)
                {
                    this.motionX = (double)f5;
                }

                if (this.motionZ < (double)(-f5))
                {
                    this.motionZ = (double)(-f5);
                }

                if (this.motionZ > (double)f5)
                {
                    this.motionZ = (double)f5;
                }

                this.fallDistance = 0.0F;

                if (this.motionY < -0.15D)
                {
                    this.motionY = -0.15D;
                }

                boolean flag = this.isSneaking() && this instanceof EntityPlayer;

                if (flag && this.motionY < 0.0D)
                {
                    this.motionY = 0.0D;
                }
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && this.isOnLadder())
            {
                this.motionY = 0.2D;
            }

            if (this.worldObj.isRemote && (!this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ) || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded))
            {
                if (this.posY > 0.0D)
                {
                    this.motionY = -0.1D;
                }
                else
                {
                    this.motionY = 0.0D;
                }
            }
            else
            {
                this.motionY -= 0.08D;
            }

            this.motionY *= 0.9800000190734863D;
            this.motionX *= (double)f2;
            this.motionZ *= (double)f2;
        }

        this.prevLimbYaw = this.limbYaw;
        d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f6 > 1.0F)
        {
            f6 = 1.0F;
        }

        this.limbYaw += (f6 - this.limbYaw) * 0.4F;
        this.limbSwing += this.limbYaw;
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        int l = this.worldObj.getBlockId(i, j, k);
        return ForgeHooks.isLivingOnLadder(Block.blocksList[l], worldObj, i, j, k);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (this.health < -32768)
        {
            this.health = -32768;
        }

        par1NBTTagCompound.setShort("Health", (short)this.health);
        par1NBTTagCompound.setShort("HurtTime", (short)this.hurtTime);
        par1NBTTagCompound.setShort("DeathTime", (short)this.deathTime);
        par1NBTTagCompound.setShort("AttackTime", (short)this.attackTime);
        par1NBTTagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        par1NBTTagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.equipment.length; ++i)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            if (this.equipment[i] != null)
            {
                this.equipment[i].writeToNBT(nbttagcompound1);
            }

            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Equipment", nbttaglist);
        NBTTagList nbttaglist1;

        if (!this.activePotionsMap.isEmpty())
        {
            nbttaglist1 = new NBTTagList();
            Iterator iterator = this.activePotionsMap.values().iterator();

            while (iterator.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                nbttaglist1.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            par1NBTTagCompound.setTag("ActiveEffects", nbttaglist1);
        }

        nbttaglist1 = new NBTTagList();

        for (int j = 0; j < this.equipmentDropChances.length; ++j)
        {
            nbttaglist1.appendTag(new NBTTagFloat(j + "", this.equipmentDropChances[j]));
        }

        par1NBTTagCompound.setTag("DropChances", nbttaglist1);
        par1NBTTagCompound.setString("CustomName", this.func_94057_bL());
        par1NBTTagCompound.setBoolean("CustomNameVisible", this.func_94062_bN());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.health = par1NBTTagCompound.getShort("Health");

        if (!par1NBTTagCompound.hasKey("Health"))
        {
            this.health = this.getMaxHealth();
        }

        this.hurtTime = par1NBTTagCompound.getShort("HurtTime");
        this.deathTime = par1NBTTagCompound.getShort("DeathTime");
        this.attackTime = par1NBTTagCompound.getShort("AttackTime");
        this.setCanPickUpLoot(par1NBTTagCompound.getBoolean("CanPickUpLoot"));
        this.persistenceRequired = par1NBTTagCompound.getBoolean("PersistenceRequired");

        if (par1NBTTagCompound.hasKey("CustomName") && par1NBTTagCompound.getString("CustomName").length() > 0)
        {
            this.func_94058_c(par1NBTTagCompound.getString("CustomName"));
        }

        this.func_94061_f(par1NBTTagCompound.getBoolean("CustomNameVisible"));
        NBTTagList nbttaglist;
        int i;

        if (par1NBTTagCompound.hasKey("Equipment"))
        {
            nbttaglist = par1NBTTagCompound.getTagList("Equipment");

            for (i = 0; i < this.equipment.length; ++i)
            {
                this.equipment[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttaglist.tagAt(i));
            }
        }

        if (par1NBTTagCompound.hasKey("ActiveEffects"))
        {
            nbttaglist = par1NBTTagCompound.getTagList("ActiveEffects");

            for (i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound1);
                this.activePotionsMap.put(Integer.valueOf(potioneffect.getPotionID()), potioneffect);
            }
        }

        if (par1NBTTagCompound.hasKey("DropChances"))
        {
            nbttaglist = par1NBTTagCompound.getTagList("DropChances");

            for (i = 0; i < nbttaglist.tagCount(); ++i)
            {
                this.equipmentDropChances[i] = ((NBTTagFloat)nbttaglist.tagAt(i)).data;
            }
        }
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead && this.health > 0;
    }

    public boolean canBreatheUnderwater()
    {
        return false;
    }

    public void setMoveForward(float par1)
    {
        this.moveForward = par1;
    }

    public void setJumping(boolean par1)
    {
        this.isJumping = par1;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0)
        {
            double d0 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isClientWorld())
        {
            if (this.isAIEnabled())
            {
                this.worldObj.theProfiler.startSection("newAi");
                this.updateAITasks();
                this.worldObj.theProfiler.endSection();
            }
            else
            {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping)
        {
            if (!this.isInWater() && !this.handleLavaMovement())
            {
                if (this.onGround && this.jumpTicks == 0)
                {
                    this.jump();
                    this.jumpTicks = 10;
                }
            }
            else
            {
                this.motionY += 0.03999999910593033D;
            }
        }
        else
        {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        float f = this.landMovementFactor;
        this.landMovementFactor *= this.getSpeedModifier();
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.landMovementFactor = f;
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote)
        {
            this.func_85033_bc();
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("looting");

        if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        {
            List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityItem entityitem = (EntityItem)iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();
                    int i = getArmorPosition(itemstack);

                    if (i > -1)
                    {
                        boolean flag = true;
                        ItemStack itemstack1 = this.getCurrentItemOrArmor(i);

                        if (itemstack1 != null)
                        {
                            if (i == 0)
                            {
                                if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword))
                                {
                                    flag = true;
                                }
                                else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword)
                                {
                                    ItemSword itemsword = (ItemSword)itemstack.getItem();
                                    ItemSword itemsword1 = (ItemSword)itemstack1.getItem();

                                    if (itemsword.func_82803_g() == itemsword1.func_82803_g())
                                    {
                                        flag = itemstack.getItemDamage() > itemstack1.getItemDamage() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                                    }
                                    else
                                    {
                                        flag = itemsword.func_82803_g() > itemsword1.func_82803_g();
                                    }
                                }
                                else
                                {
                                    flag = false;
                                }
                            }
                            else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor))
                            {
                                flag = true;
                            }
                            else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor)
                            {
                                ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                                ItemArmor itemarmor1 = (ItemArmor)itemstack1.getItem();

                                if (itemarmor.damageReduceAmount == itemarmor1.damageReduceAmount)
                                {
                                    flag = itemstack.getItemDamage() > itemstack1.getItemDamage() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                                }
                                else
                                {
                                    flag = itemarmor.damageReduceAmount > itemarmor1.damageReduceAmount;
                                }
                            }
                            else
                            {
                                flag = false;
                            }
                        }

                        if (flag)
                        {
                            if (itemstack1 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[i])
                            {
                                this.entityDropItem(itemstack1, 0.0F);
                            }

                            this.setCurrentItemOrArmor(i, itemstack);
                            this.equipmentDropChances[i] = 2.0F;
                            this.persistenceRequired = true;
                            this.onItemPickup(entityitem, 1);
                            entityitem.setDead();
                        }
                    }
                }
            }
        }

        this.worldObj.theProfiler.endSection();
    }

    protected void func_85033_bc()
    {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (list != null && !list.isEmpty())
        {
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);

                if (entity.canBePushed())
                {
                    this.collideWithEntity(entity);
                }
            }
        }
    }

    protected void collideWithEntity(Entity par1Entity)
    {
        par1Entity.applyEntityCollision(this);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * Returns whether the entity is in a local (client) world
     */
    protected boolean isClientWorld()
    {
        return !this.worldObj.isRemote;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return this.health <= 0;
    }

    public boolean isBlocking()
    {
        return false;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        this.motionY = 0.41999998688697815D;

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return true;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        if (!this.persistenceRequired)
        {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (entityplayer != null)
            {
                double d0 = entityplayer.posX - this.posX;
                double d1 = entityplayer.posY - this.posY;
                double d2 = entityplayer.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.canDespawn() && d3 > 16384.0D)
                {
                    this.setDead();
                }

                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D && this.canDespawn())
                {
                    this.setDead();
                }
                else if (d3 < 1024.0D)
                {
                    this.entityAge = 0;
                }
            }
        }
    }

    protected void updateAITasks()
    {
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITick();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick() {}

    protected void updateEntityActionState()
    {
        ++this.entityAge;
        this.despawnEntity();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        float f = 8.0F;

        if (this.rand.nextFloat() < 0.02F)
        {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, (double)f);

            if (entityplayer != null)
            {
                this.currentTarget = entityplayer;
                this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
            }
            else
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (this.currentTarget != null)
        {
            this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());

            if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double)(f * f))
            {
                this.currentTarget = null;
            }
        }
        else
        {
            if (this.rand.nextFloat() < 0.05F)
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }

            this.rotationYaw += this.randomYawVelocity;
            this.rotationPitch = this.defaultPitch;
        }

        boolean flag = this.isInWater();
        boolean flag1 = this.handleLavaMovement();

        if (flag || flag1)
        {
            this.isJumping = this.rand.nextFloat() < 0.8F;
        }
    }

    /**
     * Updates the arm swing progress counters and animation progress
     */
    protected void updateArmSwingProgress()
    {
        int i = this.getArmSwingAnimationEnd();

        if (this.isSwingInProgress)
        {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= i)
            {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else
        {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / (float)i;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 40;
    }

    /**
     * Changes pitch and yaw so that the entity calling the function is facing the entity provided as an argument.
     */
    public void faceEntity(Entity par1Entity, float par2, float par3)
    {
        double d0 = par1Entity.posX - this.posX;
        double d1 = par1Entity.posZ - this.posZ;
        double d2;

        if (par1Entity instanceof EntityLiving)
        {
            EntityLiving entityliving = (EntityLiving)par1Entity;
            d2 = entityliving.posY + (double)entityliving.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        }
        else
        {
            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f3, par3);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f2, par2);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.attackEntityFrom(DamageSource.outOfWorld, 4);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args: partialTickTime
     */
    public float getSwingProgress(float par1)
    {
        float f1 = this.swingProgress - this.prevSwingProgress;

        if (f1 < 0.0F)
        {
            ++f1;
        }

        return this.prevSwingProgress + f1 * par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * interpolated position vector
     */
    public Vec3 getPosition(float par1)
    {
        if (par1 == 1.0F)
        {
            return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
        }
        else
        {
            double d0 = this.prevPosX + (this.posX - this.prevPosX) * (double)par1;
            double d1 = this.prevPosY + (this.posY - this.prevPosY) * (double)par1;
            double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par1;
            return this.worldObj.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        }
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return this.getLook(1.0F);
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(float par1)
    {
        float f1;
        float f2;
        float f3;
        float f4;

        if (par1 == 1.0F)
        {
            f1 = MathHelper.cos(-this.rotationYaw * 0.017453292F - (float)Math.PI);
            f2 = MathHelper.sin(-this.rotationYaw * 0.017453292F - (float)Math.PI);
            f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
            f4 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(f2 * f3), (double)f4, (double)(f1 * f3));
        }
        else
        {
            f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * par1;
            f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * par1;
            f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
            f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(f4 * f5), (double)f6, (double)(f3 * f5));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns render size modifier
     */
    public float getRenderSizeModifier()
    {
        return 1.0F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Performs a ray trace for the distance specified and using the partial tick time. Args: distance, partialTickTime
     */
    public MovingObjectPosition rayTrace(double par1, float par3)
    {
        Vec3 vec3 = this.getPosition(par3);
        Vec3 vec31 = this.getLook(par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return this.worldObj.rayTraceBlocks(vec3, vec32);
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 2)
        {
            this.limbYaw = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.attackedAtYaw = 0.0F;
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.attackEntityFrom(DamageSource.generic, 0);
        }
        else if (par1 == 3)
        {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.health = 0;
            this.onDeath(DamageSource.generic);
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the Icon Index of the item currently held
     */
    public Icon getItemIcon(ItemStack par1ItemStack, int par2)
    {
        return par1ItemStack.getIconIndex();
    }

    protected void updatePotionEffects()
    {
        Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext())
        {
            Integer integer = (Integer)iterator.next();
            PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.get(integer);

            try
            {
                if (!potioneffect.onUpdate(this))
                {
                    if (!this.worldObj.isRemote)
                    {
                        iterator.remove();
                        this.onFinishedPotionEffect(potioneffect);
                    }
                }
                else if (potioneffect.getDuration() % 600 == 0)
                {
                    this.onChangedPotionEffect(potioneffect);
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking mob effect instance");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Mob effect being ticked");
                crashreportcategory.addCrashSectionCallable("Effect Name", new CallableEffectName(this, potioneffect));
                crashreportcategory.addCrashSectionCallable("Effect ID", new CallableEffectID(this, potioneffect));
                crashreportcategory.addCrashSectionCallable("Effect Duration", new CallableEffectDuration(this, potioneffect));
                crashreportcategory.addCrashSectionCallable("Effect Amplifier", new CallableEffectAmplifier(this, potioneffect));
                crashreportcategory.addCrashSectionCallable("Effect is Splash", new CallableEffectIsSplash(this, potioneffect));
                crashreportcategory.addCrashSectionCallable("Effect is Ambient", new CallableEffectIsAmbient(this, potioneffect));
                throw new ReportedException(crashreport);
            }
        }

        int i;

        if (this.potionsNeedUpdate)
        {
            if (!this.worldObj.isRemote)
            {
                if (this.activePotionsMap.isEmpty())
                {
                    this.dataWatcher.updateObject(9, Byte.valueOf((byte)0));
                    this.dataWatcher.updateObject(8, Integer.valueOf(0));
                    this.setInvisible(false);
                }
                else
                {
                    i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(9, Byte.valueOf((byte)(PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0)));
                    this.dataWatcher.updateObject(8, Integer.valueOf(i));
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        i = this.dataWatcher.getWatchableObjectInt(8);
        boolean flag = this.dataWatcher.getWatchableObjectByte(9) > 0;

        if (i > 0)
        {
            boolean flag1 = false;

            if (!this.isInvisible())
            {
                flag1 = this.rand.nextBoolean();
            }
            else
            {
                flag1 = this.rand.nextInt(15) == 0;
            }

            if (flag)
            {
                flag1 &= this.rand.nextInt(5) == 0;
            }

            if (flag1 && i > 0)
            {
                double d0 = (double)(i >> 16 & 255) / 255.0D;
                double d1 = (double)(i >> 8 & 255) / 255.0D;
                double d2 = (double)(i >> 0 & 255) / 255.0D;
                this.worldObj.spawnParticle(flag ? "mobSpellAmbient" : "mobSpell", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - (double)this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
            }
        }
    }

    public void clearActivePotions()
    {
        Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext())
        {
            Integer integer = (Integer)iterator.next();
            PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.get(integer);

            if (!this.worldObj.isRemote)
            {
                iterator.remove();
                this.onFinishedPotionEffect(potioneffect);
            }
        }
    }

    public Collection getActivePotionEffects()
    {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int par1)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(par1));
    }

    public boolean isPotionActive(Potion par1Potion)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(par1Potion.id));
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    public PotionEffect getActivePotionEffect(Potion par1Potion)
    {
        return (PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1Potion.id));
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect par1PotionEffect)
    {
        if (this.isPotionApplicable(par1PotionEffect))
        {
            if (this.activePotionsMap.containsKey(Integer.valueOf(par1PotionEffect.getPotionID())))
            {
                ((PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID()))).combine(par1PotionEffect);
                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID())));
            }
            else
            {
                this.activePotionsMap.put(Integer.valueOf(par1PotionEffect.getPotionID()), par1PotionEffect);
                this.onNewPotionEffect(par1PotionEffect);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            int i = par1PotionEffect.getPotionID();

            if (i == Potion.regeneration.id || i == Potion.poison.id)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead()
    {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Remove the speified potion effect from this entity.
     */
    public void removePotionEffectClient(int par1)
    {
        this.activePotionsMap.remove(Integer.valueOf(par1));
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffect(int par1)
    {
        PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.remove(Integer.valueOf(par1));

        if (potioneffect != null)
        {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionsNeedUpdate = true;
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionsNeedUpdate = true;
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionsNeedUpdate = true;
    }

    /**
     * This method returns a value to be applied directly to entity speed, this factor is less than 1 when a slowdown
     * potion effect is applied, more than 1 when a haste potion effect is applied and 2 for fleeing entities.
     */
    public float getSpeedModifier()
    {
        float f = 1.0F;

        if (this.isPotionActive(Potion.moveSpeed))
        {
            f *= 1.0F + 0.2F * (float)(this.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        if (this.isPotionActive(Potion.moveSlowdown))
        {
            f *= 1.0F - 0.15F * (float)(this.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }

        if (f < 0.0F)
        {
            f = 0.0F;
        }

        return f;
    }

    /**
     * Move the entity to the coordinates informed, but keep yaw/pitch values.
     */
    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        this.setLocationAndAngles(par1, par3, par5, this.rotationYaw, this.rotationPitch);
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return false;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }

    /**
     * Renders broken item particles using the given ItemStack
     */
    public void renderBrokenItemStack(ItemStack par1ItemStack)
    {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int i = 0; i < 5; ++i)
        {
            Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
            vec3.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
            vec31.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
            vec31.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
            vec31 = vec31.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle("iconcrack_" + par1ItemStack.getItem().itemID, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
        }
    }

    public int func_82143_as()
    {
        if (this.getAttackTarget() == null)
        {
            return 3;
        }
        else
        {
            int i = (int)((float)this.health - (float)this.getMaxHealth() * 0.33F);
            i -= (3 - this.worldObj.difficultySetting) * 4;

            if (i < 0)
            {
                i = 0;
            }

            return i + 3;
        }
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return this.equipment[0];
    }

    /**
     * 0 = item, 1-n is armor
     */
    public ItemStack getCurrentItemOrArmor(int par1)
    {
        return this.equipment[par1];
    }

    public ItemStack getCurrentArmor(int par1)
    {
        return this.equipment[par1 + 1];
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        this.equipment[par1] = par2ItemStack;
    }

    public ItemStack[] getLastActiveItems()
    {
        return this.equipment;
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(boolean par1, int par2)
    {
        for (int j = 0; j < this.getLastActiveItems().length; ++j)
        {
            ItemStack itemstack = this.getCurrentItemOrArmor(j);
            boolean flag1 = this.equipmentDropChances[j] > 1.0F;

            if (itemstack != null && (par1 || flag1) && this.rand.nextFloat() - (float)par2 * 0.01F < this.equipmentDropChances[j])
            {
                if (!flag1 && itemstack.isItemStackDamageable())
                {
                    int k = Math.max(itemstack.getMaxDamage() - 25, 1);
                    int l = itemstack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(k) + 1);

                    if (l > k)
                    {
                        l = k;
                    }

                    if (l < 1)
                    {
                        l = 1;
                    }

                    itemstack.setItemDamage(l);
                }

                this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        if (this.rand.nextFloat() < armorProbability[this.worldObj.difficultySetting])
        {
            int i = this.rand.nextInt(2);
            float f = this.worldObj.difficultySetting == 3 ? 0.1F : 0.25F;

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++i;
            }

            for (int j = 3; j >= 0; --j)
            {
                ItemStack itemstack = this.getCurrentArmor(j);

                if (j < 3 && this.rand.nextFloat() < f)
                {
                    break;
                }

                if (itemstack == null)
                {
                    Item item = getArmorItemForSlot(j + 1, i);

                    if (item != null)
                    {
                        this.setCurrentItemOrArmor(j + 1, new ItemStack(item));
                    }
                }
            }
        }
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity par1Entity, int par2)
    {
        if (!par1Entity.isDead && !this.worldObj.isRemote)
        {
            EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();

            if (par1Entity instanceof EntityItem)
            {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityArrow)
            {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityXPOrb)
            {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }
        }
    }

    public static int getArmorPosition(ItemStack par0ItemStack)
    {
        if (par0ItemStack.itemID != Block.pumpkin.blockID && par0ItemStack.itemID != Item.skull.itemID)
        {
            if (par0ItemStack.getItem() instanceof ItemArmor)
            {
                switch (((ItemArmor)par0ItemStack.getItem()).armorType)
                {
                    case 0:
                        return 4;
                    case 1:
                        return 3;
                    case 2:
                        return 2;
                    case 3:
                        return 1;
                }
            }

            return 0;
        }
        else
        {
            return 4;
        }
    }

    /**
     * Params: Armor slot, Item tier
     */
    public static Item getArmorItemForSlot(int par0, int par1)
    {
        switch (par0)
        {
            case 4:
                if (par1 == 0)
                {
                    return Item.helmetLeather;
                }
                else if (par1 == 1)
                {
                    return Item.helmetGold;
                }
                else if (par1 == 2)
                {
                    return Item.helmetChain;
                }
                else if (par1 == 3)
                {
                    return Item.helmetIron;
                }
                else if (par1 == 4)
                {
                    return Item.helmetDiamond;
                }
            case 3:
                if (par1 == 0)
                {
                    return Item.plateLeather;
                }
                else if (par1 == 1)
                {
                    return Item.plateGold;
                }
                else if (par1 == 2)
                {
                    return Item.plateChain;
                }
                else if (par1 == 3)
                {
                    return Item.plateIron;
                }
                else if (par1 == 4)
                {
                    return Item.plateDiamond;
                }
            case 2:
                if (par1 == 0)
                {
                    return Item.legsLeather;
                }
                else if (par1 == 1)
                {
                    return Item.legsGold;
                }
                else if (par1 == 2)
                {
                    return Item.legsChain;
                }
                else if (par1 == 3)
                {
                    return Item.legsIron;
                }
                else if (par1 == 4)
                {
                    return Item.legsDiamond;
                }
            case 1:
                if (par1 == 0)
                {
                    return Item.bootsLeather;
                }
                else if (par1 == 1)
                {
                    return Item.bootsGold;
                }
                else if (par1 == 2)
                {
                    return Item.bootsChain;
                }
                else if (par1 == 3)
                {
                    return Item.bootsIron;
                }
                else if (par1 == 4)
                {
                    return Item.bootsDiamond;
                }
            default:
                return null;
        }
    }

    protected void func_82162_bC()
    {
        if (this.getHeldItem() != null && this.rand.nextFloat() < enchantmentProbability[this.worldObj.difficultySetting])
        {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), 5 + this.worldObj.difficultySetting * this.rand.nextInt(6));
        }

        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.getCurrentArmor(i);

            if (itemstack != null && this.rand.nextFloat() < armorEnchantmentProbability[this.worldObj.difficultySetting])
            {
                EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, 5 + this.worldObj.difficultySetting * this.rand.nextInt(6));
            }
        }
    }

    /**
     * Initialize this creature.
     */
    public void initCreature() {}

    /**
     * Returns an integer indicating the end point of the swing animation, used by {@link #swingProgress} to provide a
     * progress indicator. Takes dig speed enchantments into account.
     */
    private int getArmSwingAnimationEnd()
    {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        ItemStack stack = this.getHeldItem();

        if (stack != null && stack.getItem() != null)
        {
            Item item = stack.getItem();
            if (item.onEntitySwing(this, stack))
            {
                return;
            }
        }

        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0)
        {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;

            if (this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet18Animation(this, 1));
            }
        }
    }

    /**
     * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
     * by a player and the player is holding a carrot-on-a-stick
     */
    public boolean canBeSteered()
    {
        return false;
    }

    /**
     * counts the amount of arrows stuck in the entity. getting hit by arrows increases this, used in rendering
     */
    public final int getArrowCountInEntity()
    {
        return this.dataWatcher.getWatchableObjectByte(10);
    }

    /**
     * sets the amount of arrows stuck in the entity. used for rendering those
     */
    public final void setArrowCountInEntity(int par1)
    {
        this.dataWatcher.updateObject(10, Byte.valueOf((byte)par1));
    }

    public EntityLiving func_94060_bK()
    {
        return (EntityLiving)(this.field_94063_bt.func_94550_c() != null ? this.field_94063_bt.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        return this.func_94056_bM() ? this.func_94057_bL() : super.getEntityName();
    }

    public void func_94058_c(String par1Str)
    {
        this.dataWatcher.updateObject(5, par1Str);
    }

    public String func_94057_bL()
    {
        return this.dataWatcher.getWatchableObjectString(5);
    }

    public boolean func_94056_bM()
    {
        return this.dataWatcher.getWatchableObjectString(5).length() > 0;
    }

    public void func_94061_f(boolean par1)
    {
        this.dataWatcher.updateObject(6, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    public boolean func_94062_bN()
    {
        return this.dataWatcher.getWatchableObjectByte(6) == 1;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_94059_bO()
    {
        return this.func_94062_bN();
    }

    public void func_96120_a(int par1, float par2)
    {
        this.equipmentDropChances[par1] = par2;
    }

    public boolean canPickUpLoot()
    {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean par1)
    {
        this.canPickUpLoot = par1;
    }

    public boolean func_104002_bU()
    {
        return this.persistenceRequired;
    }

    /***
     * Removes all potion effects that have curativeItem as a curative item for its effect
     * @param curativeItem The itemstack we are using to cure potion effects
     */
    public void curePotionEffects(ItemStack curativeItem)
    {
        Iterator<Integer> potionKey = activePotionsMap.keySet().iterator();

        if (worldObj.isRemote)
        {
            return;
        }

        while (potionKey.hasNext())
        {
            Integer key = potionKey.next();
            PotionEffect effect = (PotionEffect)activePotionsMap.get(key);

            if (effect.isCurativeItem(curativeItem))
            {
                potionKey.remove();
                onFinishedPotionEffect(effect);
            }
        }
    }

    /**
     * Returns true if the entity's rider (EntityPlayer) should face forward when mounted.
     * currently only used in vanilla code by pigs.
     *
     * @param player The player who is riding the entity.
     * @return If the player should orient the same direction as this entity.
     */
    public boolean shouldRiderFaceForward(EntityPlayer player)
    {
        return this instanceof EntityPig;
    }
}
