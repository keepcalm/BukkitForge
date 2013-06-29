package net.minecraft.entity.player;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public abstract class EntityPlayer extends EntityLiving implements ICommandSender
{
    public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
    public int maxHealth = 20;

    //public int Bukkit;

    /** Inventory of the player */
    public InventoryPlayer inventory = new InventoryPlayer(this);
    private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

    /**
     * The Container for the player's inventory (which opens when they press E)
     */
    public Container inventoryContainer;

    /** The Container the player has open. */
    public Container openContainer;

    /** The player's food stats. (See class FoodStats) */
    protected FoodStats foodStats = new FoodStats();

    /**
     * Used to tell if the player pressed jump twice. If this is at 0 and it's pressed (And they are allowed to fly, as
     * defined in the player's movementInput) it sets this to 7. If it's pressed and it's greater than 0 enable fly.
     */
    protected int flyToggleTimer = 0;
    public byte field_71098_bD = 0;
    public float prevCameraYaw;
    public float cameraYaw;
    public String username;

    /**
     * Used by EntityPlayer to prevent too many xp orbs from getting absorbed at once.
     */
    public int xpCooldown = 0;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;

    /** Boolean value indicating weather a player is sleeping or not */
    protected boolean sleeping;

    /**
     * The chunk coordinates of the bed the player is in (null if player isn't in a bed).
     */
    public ChunkCoordinates playerLocation;
    public int sleepTimer;
    public float field_71079_bU;
    @SideOnly(Side.CLIENT)
    public float field_71082_cx;
    public float field_71089_bV;

    /**
     * Holds the last coordinate to spawn based on last bed that the player sleep.
     */
    private ChunkCoordinates spawnChunk;

    /**
     * Whether this player's spawn point is forced, preventing execution of bed checks.
     */
    private boolean spawnForced;

    /** Holds the coordinate of the player when enter a minecraft to ride. */
    private ChunkCoordinates startMinecartRidingCoordinate;

    /** The player's capabilities. (See class PlayerCapabilities) */
    public PlayerCapabilities capabilities = new PlayerCapabilities();

    /** The current experience level the player is on. */
    public int experienceLevel;

    /**
     * The total amount of experience the player has. This also includes the amount of experience within their
     * Experience Bar.
     */
    public int experienceTotal;

    /**
     * The current amount of experience the player has within their Experience Bar.
     */
    public float experience;

    /**
     * This is the item that is in use when the player is holding down the useItemButton (e.g., bow, food, sword)
     */
    private ItemStack itemInUse;

    /**
     * This field starts off equal to getMaxItemUseDuration and is decremented on each tick
     */
    private int itemInUseCount;
    protected float speedOnGround = 0.1F;
    protected float speedInAir = 0.02F;
    private int field_82249_h = 0;

    /**
     * An instance of a fishing rod's hook. If this isn't null, the icon image of the fishing rod is slightly different
     */
    public EntityFishHook fishEntity = null;

    public EntityPlayer(World par1World)
    {
        super(par1World);
        this.inventoryContainer = new ContainerPlayer(this.inventory, !par1World.isRemote, this);
        this.openContainer = this.inventoryContainer;
        this.yOffset = 1.62F;
        ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
        this.setLocationAndAngles((double)chunkcoordinates.posX + 0.5D, (double)(chunkcoordinates.posY + 1), (double)chunkcoordinates.posZ + 0.5D, 0.0F, 0.0F);
        this.entityType = "humanoid";
        this.field_70741_aB = 180.0F;
        this.fireResistance = 20;
        this.texture = "/mob/char.png";
    }

    public int getMaxHealth()
    {
        return maxHealth <= 0 ? 20 : maxHealth;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(18, Integer.valueOf(0));
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns the ItemStack containing the itemInUse
     */
    public ItemStack getItemInUse()
    {
        return this.itemInUse;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the item in use count
     */
    public int getItemInUseCount()
    {
        return this.itemInUseCount;
    }

    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword) by holding down the useItemButton
     */
    public boolean isUsingItem()
    {
        return this.itemInUse != null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * gets the duration for how long the current itemInUse has been in use
     */
    public int getItemInUseDuration()
    {
        return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
    }

    public void stopUsingItem()
    {
        if (this.itemInUse != null)
        {
            this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
        }

        this.clearItemInUse();
    }

    public void clearItemInUse()
    {
        this.itemInUse = null;
        this.itemInUseCount = 0;

        if (!this.worldObj.isRemote)
        {
            this.setEating(false);
        }
    }

    public boolean isBlocking()
    {
        return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemUseAction(this.itemInUse) == EnumAction.block;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        FMLCommonHandler.instance().onPlayerPreTick(this);
        if (this.itemInUse != null)
        {
            ItemStack itemstack = this.inventory.getCurrentItem();

            if (itemstack == this.itemInUse)
            {
                itemInUse.getItem().onUsingItemTick(itemInUse, this, itemInUseCount);
                if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0)
                {
                    this.updateItemUse(itemstack, 5);
                }

                if (--this.itemInUseCount == 0 && !this.worldObj.isRemote)
                {
                    this.onItemUseFinish();
                }
            }
            else
            {
                this.clearItemInUse();
            }
        }

        if (this.xpCooldown > 0)
        {
            --this.xpCooldown;
        }

        if (this.isPlayerSleeping())
        {
            ++this.sleepTimer;

            if (this.sleepTimer > 100)
            {
                this.sleepTimer = 100;
            }

            if (!this.worldObj.isRemote)
            {
                if (!this.isInBed())
                {
                    this.wakeUpPlayer(true, true, false);
                }
                else if (this.worldObj.isDaytime())
                {
                    this.wakeUpPlayer(false, true, true);
                }
            }
        }
        else if (this.sleepTimer > 0)
        {
            ++this.sleepTimer;

            if (this.sleepTimer >= 110)
            {
                this.sleepTimer = 0;
            }
        }

        super.onUpdate();

        if (!this.worldObj.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this))
        {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        if (this.isBurning() && this.capabilities.disableDamage)
        {
            this.extinguish();
        }

        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        double d0 = this.posX - this.field_71094_bP;
        double d1 = this.posY - this.field_71095_bQ;
        double d2 = this.posZ - this.field_71085_bR;
        double d3 = 10.0D;

        if (d0 > d3)
        {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d2 > d3)
        {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d1 > d3)
        {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }

        if (d0 < -d3)
        {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d2 < -d3)
        {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d1 < -d3)
        {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }

        this.field_71094_bP += d0 * 0.25D;
        this.field_71085_bR += d2 * 0.25D;
        this.field_71095_bQ += d1 * 0.25D;
        this.addStat(StatList.minutesPlayedStat, 1);

        if (this.ridingEntity == null)
        {
            this.startMinecartRidingCoordinate = null;
        }

        if (!this.worldObj.isRemote)
        {
            this.foodStats.onUpdate(this);
        }
        FMLCommonHandler.instance().onPlayerPostTick(this);
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return this.capabilities.disableDamage ? 0 : 80;
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 10;
    }

    public void playSound(String par1Str, float par2, float par3)
    {
        this.worldObj.playSoundToNearExcept(this, par1Str, par2, par3);
    }

    /**
     * Plays sounds and makes particles for item in use state
     */
    protected void updateItemUse(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack.getItemUseAction() == EnumAction.drink)
        {
            this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (par1ItemStack.getItemUseAction() == EnumAction.eat)
        {
            for (int j = 0; j < par2; ++j)
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

            this.playSound("random.eat", 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        if (this.itemInUse != null)
        {
            this.updateItemUse(this.itemInUse, 16);
            int i = this.itemInUse.stackSize;
            ItemStack itemstack = this.itemInUse.onFoodEaten(this.worldObj, this);

            if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i)
            {
                this.inventory.mainInventory[this.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0)
                {
                    this.inventory.mainInventory[this.inventory.currentItem] = null;
                }
            }

            this.clearItemInUse();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 9)
        {
            this.onItemUseFinish();
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return this.getHealth() <= 0 || this.isPlayerSleeping();
    }

    /**
     * sets current screen to null (used on escape buttons of GUIs)
     */
    public void closeScreen()
    {
        this.openContainer = this.inventoryContainer;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        if (this.ridingEntity == par1Entity)
        {
            this.unmountEntity(par1Entity);

            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            super.mountEntity(par1Entity);
        }
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        float f = this.rotationYaw;
        float f1 = this.rotationPitch;
        super.updateRidden();
        this.prevCameraYaw = this.cameraYaw;
        this.cameraYaw = 0.0F;
        this.addMountedMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);

        if (this.ridingEntity instanceof EntityLiving && ((EntityLiving)ridingEntity).shouldRiderFaceForward(this))
        {
            this.rotationPitch = f1;
            this.rotationYaw = f;
            this.renderYawOffset = ((EntityLiving)this.ridingEntity).renderYawOffset;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    public void preparePlayerToSpawn()
    {
        this.yOffset = 1.62F;
        this.setSize(0.6F, 1.8F);
        super.preparePlayerToSpawn();
        this.setEntityHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    protected void updateEntityActionState()
    {
        this.updateArmSwingProgress();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.flyToggleTimer > 0)
        {
            --this.flyToggleTimer;
        }

        if (this.worldObj.difficultySetting == 0 && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 * 12 == 0)
        {
            this.heal(1);
        }

        this.inventory.decrementAnimations();
        this.prevCameraYaw = this.cameraYaw;
        super.onLivingUpdate();
        this.landMovementFactor = this.capabilities.getWalkSpeed();
        this.jumpMovementFactor = this.speedInAir;

        if (this.isSprinting())
        {
            this.landMovementFactor = (float)((double)this.landMovementFactor + (double)this.capabilities.getWalkSpeed() * 0.3D);
            this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
        }

        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f1 = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;

        if (f > 0.1F)
        {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0)
        {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0)
        {
            f1 = 0.0F;
        }

        this.cameraYaw += (f - this.cameraYaw) * 0.4F;
        this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;

        if (this.getHealth() > 0)
        {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 0.5D, 1.0D));

            if (list != null)
            {
                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (!entity.isDead)
                    {
                        this.collideWithPlayer(entity);
                    }
                }
            }
        }
    }

    private void collideWithPlayer(Entity par1Entity)
    {
        par1Entity.onCollideWithPlayer(this);
    }

    public int getScore()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * Set player's score
     */
    public void setScore(int par1)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Add to player's score
     */
    public void addScore(int par1)
    {
        int j = this.getScore();
        this.dataWatcher.updateObject(18, Integer.valueOf(j + par1));
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612D;

        captureDrops = true;
        capturedDrops.clear();

        if (this.username.equals("Notch"))
        {
            this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
        }

        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            this.inventory.dropAllItems();
        }

        captureDrops = false;

        if (!worldObj.isRemote)
        {
            PlayerDropsEvent event = new PlayerDropsEvent(this, par1DamageSource, capturedDrops, recentlyHit > 0);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                for (EntityItem item : capturedDrops)
                {
                    joinEntityItemWithWorld(item);
                }
            }
        }

        if (par1DamageSource != null)
        {
            this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
            this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
        }
        else
        {
            this.motionX = this.motionZ = 0.0D;
        }

        this.yOffset = 0.1F;
        this.addStat(StatList.deathsStat, 1);
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(Entity par1Entity, int par2)
    {
        this.addScore(par2);
        Collection collection = this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96640_e);

        if (par1Entity instanceof EntityPlayer)
        {
            this.addStat(StatList.playerKillsStat, 1);
            collection.addAll(this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96639_d));
        }
        else
        {
            this.addStat(StatList.mobKillsStat, 1);
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext())
        {
            ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
            Score score = this.getWorldScoreboard().func_96529_a(this.getEntityName(), scoreobjective);
            score.func_96648_a();
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem(boolean par1)
    {
        ItemStack stack = inventory.getCurrentItem();

        if (stack == null)
        {
            return null;
        }

        if (stack.getItem().onDroppedByPlayer(stack, this))
        {
            int count = par1 && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1;
            return ForgeHooks.onPlayerTossEvent(this, inventory.decrStackSize(inventory.currentItem, count));
        }

        return null;
    }

    /**
     * Args: itemstack - called when player drops an item stack that's not in his inventory (like items still placed in
     * a workbench while the workbench'es GUI gets closed)
     */
    public EntityItem dropPlayerItem(ItemStack par1ItemStack)
    {
        return ForgeHooks.onPlayerTossEvent(this, par1ItemStack);
    }

    /**
     * Args: itemstack, flag
     */
    public EntityItem dropPlayerItemWithRandomChoice(ItemStack par1ItemStack, boolean par2)
    {
        if (par1ItemStack == null)
        {
            return null;
        }
        else
        {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY - 0.30000001192092896D + (double)this.getEyeHeight(), this.posZ, par1ItemStack);
            entityitem.delayBeforeCanPickup = 40;
            float f = 0.1F;
            float f1;

            if (par2)
            {
                f1 = this.rand.nextFloat() * 0.5F;
                float f2 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                entityitem.motionX = (double)(-MathHelper.sin(f2) * f1);
                entityitem.motionZ = (double)(MathHelper.cos(f2) * f1);
                entityitem.motionY = 0.20000000298023224D;
            }
            else
            {
                f = 0.3F;
                entityitem.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
                entityitem.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
                entityitem.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f + 0.1F);
                f = 0.02F;
                f1 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                f *= this.rand.nextFloat();
                entityitem.motionX += Math.cos((double)f1) * (double)f;
                entityitem.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                entityitem.motionZ += Math.sin((double)f1) * (double)f;
            }

            this.joinEntityItemWithWorld(entityitem);
            this.addStat(StatList.dropStat, 1);
            return entityitem;
        }
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    public void joinEntityItemWithWorld(EntityItem par1EntityItem)
    {
        if (captureDrops)
        {
            capturedDrops.add(par1EntityItem);
            return;
        }
        this.worldObj.spawnEntityInWorld(par1EntityItem);
    }

    /**
     * Returns how strong the player is against the specified block at this moment
     * Deprecated in favor of the more sensitive version
     */
    @Deprecated
    public float getCurrentPlayerStrVsBlock(Block par1Block, boolean par2)
    {
        return getCurrentPlayerStrVsBlock(par1Block, par2, 0);
    }

    public float getCurrentPlayerStrVsBlock(Block par1Block, boolean par2, int meta)
    {
        ItemStack stack = inventory.getCurrentItem();
        float f = (stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, par1Block, meta));

        if (f > 1.0F)
        {
            int i = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack itemstack = this.inventory.getCurrentItem();

            if (i > 0 && itemstack != null)
            {
                float f1 = (float)(i * i + 1);

                boolean canHarvest = ForgeHooks.canToolHarvestBlock(par1Block, meta, itemstack);

                if (!canHarvest && f <= 1.0F)
                {
                    f += f1 * 0.08F;
                }
                else
                {
                    f += f1;
                }
            }
        }

        if (this.isPotionActive(Potion.digSpeed))
        {
            f *= 1.0F + (float)(this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (this.isPotionActive(Potion.digSlowdown))
        {
            f *= 1.0F - (float)(this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
        }

        if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this))
        {
            f /= 5.0F;
        }

        if (!this.onGround)
        {
            f /= 5.0F;
        }

        f = ForgeEventFactory.getBreakSpeed(this, par1Block, meta, f);
        return (f < 0 ? 0 : f);
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean canHarvestBlock(Block par1Block)
    {
        return ForgeEventFactory.doPlayerHarvestCheck(this, par1Block, inventory.canHarvestBlock(par1Block));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Inventory");
        this.inventory.readFromNBT(nbttaglist);
        this.inventory.currentItem = par1NBTTagCompound.getInteger("SelectedItemSlot");
        this.sleeping = par1NBTTagCompound.getBoolean("Sleeping");
        this.sleepTimer = par1NBTTagCompound.getShort("SleepTimer");
        this.experience = par1NBTTagCompound.getFloat("XpP");
        this.experienceLevel = par1NBTTagCompound.getInteger("XpLevel");
        this.experienceTotal = par1NBTTagCompound.getInteger("XpTotal");
        this.setScore(par1NBTTagCompound.getInteger("Score"));

        int tmp = par1NBTTagCompound.getInteger("MaxHealth");
        maxHealth = (tmp <= 0 ? 20 : tmp);

        if (this.sleeping)
        {
            this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.wakeUpPlayer(true, true, false);
        }

        if (par1NBTTagCompound.hasKey("SpawnX") && par1NBTTagCompound.hasKey("SpawnY") && par1NBTTagCompound.hasKey("SpawnZ"))
        {
            this.spawnChunk = new ChunkCoordinates(par1NBTTagCompound.getInteger("SpawnX"), par1NBTTagCompound.getInteger("SpawnY"), par1NBTTagCompound.getInteger("SpawnZ"));
            this.spawnForced = par1NBTTagCompound.getBoolean("SpawnForced");
        }

        this.foodStats.readNBT(par1NBTTagCompound);
        this.capabilities.readCapabilitiesFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("EnderItems"))
        {
            NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("EnderItems");
            this.theInventoryEnderChest.loadInventoryFromNBT(nbttaglist1);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        par1NBTTagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
        par1NBTTagCompound.setBoolean("Sleeping", this.sleeping);
        par1NBTTagCompound.setShort("SleepTimer", (short)this.sleepTimer);
        par1NBTTagCompound.setFloat("XpP", this.experience);
        par1NBTTagCompound.setInteger("XpLevel", this.experienceLevel);
        par1NBTTagCompound.setInteger("XpTotal", this.experienceTotal);
        par1NBTTagCompound.setInteger("Score", this.getScore());
        par1NBTTagCompound.setInteger("MaxHealth", maxHealth);

        if (this.spawnChunk != null)
        {
            par1NBTTagCompound.setInteger("SpawnX", this.spawnChunk.posX);
            par1NBTTagCompound.setInteger("SpawnY", this.spawnChunk.posY);
            par1NBTTagCompound.setInteger("SpawnZ", this.spawnChunk.posZ);
            par1NBTTagCompound.setBoolean("SpawnForced", this.spawnForced);
        }

        this.foodStats.writeNBT(par1NBTTagCompound);
        this.capabilities.writeCapabilitiesToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(IInventory par1IInventory) {}

    public void displayGUIHopper(TileEntityHopper par1TileEntityHopper) {}

    public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper) {}

    public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str) {}

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void displayGUIAnvil(int par1, int par2, int par3) {}

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayGUIWorkbench(int par1, int par2, int par3) {}

    public float getEyeHeight()
    {
        return 0.12F;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight()
    {
        this.yOffset = 1.62F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (this.capabilities.disableDamage && !par1DamageSource.canHarmInCreative())
        {
            return false;
        }
        else
        {
            this.entityAge = 0;

            if (this.getHealth() <= 0)
            {
                return false;
            }
            else
            {
                if (this.isPlayerSleeping() && !this.worldObj.isRemote)
                {
                    this.wakeUpPlayer(true, true, false);
                }

                if (par1DamageSource.isDifficultyScaled())
                {
                    if (this.worldObj.difficultySetting == 0)
                    {
                        par2 = 0;
                    }

                    if (this.worldObj.difficultySetting == 1)
                    {
                        par2 = par2 / 2 + 1;
                    }

                    if (this.worldObj.difficultySetting == 3)
                    {
                        par2 = par2 * 3 / 2;
                    }
                }

                if (par2 == 0)
                {
                    return false;
                }
                else
                {
                    Entity entity = par1DamageSource.getEntity();

                    if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity != null)
                    {
                        entity = ((EntityArrow)entity).shootingEntity;
                    }

                    if (entity instanceof EntityLiving)
                    {
                        this.alertWolves((EntityLiving)entity, false);
                    }

                    this.addStat(StatList.damageTakenStat, par2);
                    return super.attackEntityFrom(par1DamageSource, par2);
                }
            }
        }
    }

    public boolean func_96122_a(EntityPlayer par1EntityPlayer)
    {
        ScorePlayerTeam scoreplayerteam = this.getTeam();
        ScorePlayerTeam scoreplayerteam1 = par1EntityPlayer.getTeam();
        return scoreplayerteam != scoreplayerteam1 ? true : (scoreplayerteam != null ? scoreplayerteam.func_96665_g() : true);
    }

    /**
     * Called when the player attack or gets attacked, it's alert all wolves in the area that are owned by the player to
     * join the attack or defend the player.
     */
    protected void alertWolves(EntityLiving par1EntityLiving, boolean par2)
    {
        if (!(par1EntityLiving instanceof EntityCreeper) && !(par1EntityLiving instanceof EntityGhast))
        {
            if (par1EntityLiving instanceof EntityWolf)
            {
                EntityWolf entitywolf = (EntityWolf)par1EntityLiving;

                if (entitywolf.isTamed() && this.username.equals(entitywolf.getOwnerName()))
                {
                    return;
                }
            }

            if (!(par1EntityLiving instanceof EntityPlayer) || this.func_96122_a((EntityPlayer)par1EntityLiving))
            {
                List list = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getAABBPool().getAABB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    EntityWolf entitywolf1 = (EntityWolf)iterator.next();

                    if (entitywolf1.isTamed() && entitywolf1.getEntityToAttack() == null && this.username.equals(entitywolf1.getOwnerName()) && (!par2 || !entitywolf1.isSitting()))
                    {
                        entitywolf1.setSitting(false);
                        entitywolf1.setTarget(par1EntityLiving);
                    }
                }
            }
        }
    }

    protected void damageArmor(int par1)
    {
        this.inventory.damageArmor(par1);
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return this.inventory.getTotalArmorValue();
    }

    public float func_82243_bO()
    {
        int i = 0;
        ItemStack[] aitemstack = this.inventory.armorInventory;
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k)
        {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null)
            {
                ++i;
            }
        }

        return (float)i / (float)this.inventory.armorInventory.length;
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

            if (!par1DamageSource.isUnblockable() && this.isBlocking())
            {
                par2 = 1 + par2 >> 1;
            }

            par2 = ArmorProperties.ApplyArmor(this, inventory.armorInventory, par1DamageSource, par2);
            if (par2 <= 0)
            {
                return;
            }
            par2 = this.applyPotionDamageCalculations(par1DamageSource, par2);
            this.addExhaustion(par1DamageSource.getHungerDamage());
            int j = this.getHealth();
            this.setEntityHealth(this.getHealth() - par2);
            this.field_94063_bt.func_94547_a(par1DamageSource, j, par2);
        }
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {}

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser) {}

    /**
     * Displays the GUI for editing a sign. Args: tileEntitySign
     */
    public void displayGUIEditSign(TileEntity par1TileEntity) {}

    /**
     * Displays the GUI for interacting with a brewing stand.
     */
    public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand) {}

    /**
     * Displays the GUI for interacting with a beacon.
     */
    public void displayGUIBeacon(TileEntityBeacon par1TileEntityBeacon) {}

    public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str) {}

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook(ItemStack par1ItemStack) {}

    public boolean interactWith(Entity par1Entity)
    {
        if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(this, par1Entity)))
        {
            return false;
        }
        if (par1Entity.interact(this))
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.getCurrentEquippedItem();

            if (itemstack != null && par1Entity instanceof EntityLiving)
            {
                if (this.capabilities.isCreativeMode)
                {
                    itemstack = itemstack.copy();
                }

                if (itemstack.interactWith((EntityLiving)par1Entity))
                {
                    if (itemstack.stackSize <= 0 && !this.capabilities.isCreativeMode)
                    {
                        this.destroyCurrentEquippedItem();
                    }

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Returns the currently being used item by the player.
     */
    public ItemStack getCurrentEquippedItem()
    {
        return this.inventory.getCurrentItem();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem()
    {
        ItemStack orig = getCurrentEquippedItem();
        this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, orig));
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return (double)(this.yOffset - 0.5F);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
     * called on it. Args: targetEntity
     */
    public void attackTargetEntityWithCurrentItem(Entity par1Entity)
    {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(this, par1Entity)))
        {
            return;
        }
        ItemStack stack = getCurrentEquippedItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, this, par1Entity))
        {
            return;
        }
        if (par1Entity.canAttackWithItem())
        {
            if (!par1Entity.func_85031_j(this))
            {
                int i = this.inventory.getDamageVsEntity(par1Entity);

                if (this.isPotionActive(Potion.damageBoost))
                {
                    i += 3 << this.getActivePotionEffect(Potion.damageBoost).getAmplifier();
                }

                if (this.isPotionActive(Potion.weakness))
                {
                    i -= 2 << this.getActivePotionEffect(Potion.weakness).getAmplifier();
                }

                int j = 0;
                int k = 0;

                if (par1Entity instanceof EntityLiving)
                {
                    k = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLiving)par1Entity);
                    j += EnchantmentHelper.getKnockbackModifier(this, (EntityLiving)par1Entity);
                }

                if (this.isSprinting())
                {
                    ++j;
                }

                if (i > 0 || k > 0)
                {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && par1Entity instanceof EntityLiving;

                    if (flag && i > 0)
                    {
                        i += this.rand.nextInt(i / 2 + 2);
                    }

                    i += k;
                    boolean flag1 = false;
                    int l = EnchantmentHelper.getFireAspectModifier(this);

                    if (par1Entity instanceof EntityLiving && l > 0 && !par1Entity.isBurning())
                    {
                        flag1 = true;
                        par1Entity.setFire(1);
                    }

                    boolean flag2 = par1Entity.attackEntityFrom(DamageSource.causePlayerDamage(this), i);

                    if (flag2)
                    {
                        if (j > 0)
                        {
                            par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F));
                            this.motionX *= 0.6D;
                            this.motionZ *= 0.6D;
                            this.setSprinting(false);
                        }

                        if (flag)
                        {
                            this.onCriticalHit(par1Entity);
                        }

                        if (k > 0)
                        {
                            this.onEnchantmentCritical(par1Entity);
                        }

                        if (i >= 18)
                        {
                            this.triggerAchievement(AchievementList.overkill);
                        }

                        this.setLastAttackingEntity(par1Entity);

                        if (par1Entity instanceof EntityLiving)
                        {
                            EnchantmentThorns.func_92096_a(this, (EntityLiving)par1Entity, this.rand);
                        }
                    }

                    ItemStack itemstack = this.getCurrentEquippedItem();
                    Object object = par1Entity;

                    if (par1Entity instanceof EntityDragonPart)
                    {
                        IEntityMultiPart ientitymultipart = ((EntityDragonPart)par1Entity).entityDragonObj;

                        if (ientitymultipart != null && ientitymultipart instanceof EntityLiving)
                        {
                            object = (EntityLiving)ientitymultipart;
                        }
                    }

                    if (itemstack != null && object instanceof EntityLiving)
                    {
                        itemstack.hitEntity((EntityLiving)object, this);

                        if (itemstack.stackSize <= 0)
                        {
                            this.destroyCurrentEquippedItem();
                        }
                    }

                    if (par1Entity instanceof EntityLiving)
                    {
                        if (par1Entity.isEntityAlive())
                        {
                            this.alertWolves((EntityLiving)par1Entity, true);
                        }

                        this.addStat(StatList.damageDealtStat, i);

                        if (l > 0 && flag2)
                        {
                            par1Entity.setFire(l * 4);
                        }
                        else if (flag1)
                        {
                            par1Entity.extinguish();
                        }
                    }

                    this.addExhaustion(0.3F);
                }
            }
        }
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity par1Entity) {}

    public void onEnchantmentCritical(Entity par1Entity) {}

    @SideOnly(Side.CLIENT)
    public void respawnPlayer() {}

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();
        this.inventoryContainer.onCraftGuiClosed(this);

        if (this.openContainer != null)
        {
            this.openContainer.onCraftGuiClosed(this);
        }
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        return !this.sleeping && super.isEntityInsideOpaqueBlock();
    }

    public boolean func_71066_bF()
    {
        return false;
    }

    /**
     * Attempts to have the player sleep in a bed at the specified location.
     */
    public EnumStatus sleepInBedAt(int par1, int par2, int par3)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(this, par1, par2, par3);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.result != null)
        {
            return event.result;
        }
        if (!this.worldObj.isRemote)
        {
            if (this.isPlayerSleeping() || !this.isEntityAlive())
            {
                return EnumStatus.OTHER_PROBLEM;
            }

            if (!this.worldObj.provider.isSurfaceWorld())
            {
                return EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (this.worldObj.isDaytime())
            {
                return EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.posX - (double)par1) > 3.0D || Math.abs(this.posY - (double)par2) > 2.0D || Math.abs(this.posZ - (double)par3) > 3.0D)
            {
                return EnumStatus.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB((double)par1 - d0, (double)par2 - d1, (double)par3 - d0, (double)par1 + d0, (double)par2 + d1, (double)par3 + d0));

            if (!list.isEmpty())
            {
                return EnumStatus.NOT_SAFE;
            }
        }

        this.setSize(0.2F, 0.2F);
        this.yOffset = 0.2F;

        if (this.worldObj.blockExists(par1, par2, par3))
        {
            int l = this.worldObj.getBlockMetadata(par1, par2, par3);
            int i1 = BlockBed.getDirection(l);
            Block block = Block.blocksList[worldObj.getBlockId(par1, par2, par3)];
            if (block != null)
            {
                i1 = block.getBedDirection(worldObj, par1, par2, par3);
            }
            float f = 0.5F;
            float f1 = 0.5F;

            switch (i1)
            {
                case 0:
                    f1 = 0.9F;
                    break;
                case 1:
                    f = 0.1F;
                    break;
                case 2:
                    f1 = 0.1F;
                    break;
                case 3:
                    f = 0.9F;
            }

            this.func_71013_b(i1);
            this.setPosition((double)((float)par1 + f), (double)((float)par2 + 0.9375F), (double)((float)par3 + f1));
        }
        else
        {
            this.setPosition((double)((float)par1 + 0.5F), (double)((float)par2 + 0.9375F), (double)((float)par3 + 0.5F));
        }

        this.sleeping = true;
        this.sleepTimer = 0;
        this.playerLocation = new ChunkCoordinates(par1, par2, par3);
        this.motionX = this.motionZ = this.motionY = 0.0D;

        if (!this.worldObj.isRemote)
        {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        return EnumStatus.OK;
    }

    private void func_71013_b(int par1)
    {
        this.field_71079_bU = 0.0F;
        this.field_71089_bV = 0.0F;

        switch (par1)
        {
            case 0:
                this.field_71089_bV = -1.8F;
                break;
            case 1:
                this.field_71079_bU = 1.8F;
                break;
            case 2:
                this.field_71089_bV = 1.8F;
                break;
            case 3:
                this.field_71079_bU = -1.8F;
        }
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    {
        this.setSize(0.6F, 1.8F);
        this.resetHeight();
        ChunkCoordinates chunkcoordinates = this.playerLocation;
        ChunkCoordinates chunkcoordinates1 = this.playerLocation;

        Block block = (chunkcoordinates == null ? null : Block.blocksList[worldObj.getBlockId(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ)]);

        if (chunkcoordinates != null && block != null && block.isBed(worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this))
        {
            block.setBedOccupied(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this, false);
            chunkcoordinates1 = block.getBedSpawnPosition(worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this);

            if (chunkcoordinates1 == null)
            {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ);
            }

            this.setPosition((double)((float)chunkcoordinates1.posX + 0.5F), (double)((float)chunkcoordinates1.posY + this.yOffset + 0.1F), (double)((float)chunkcoordinates1.posZ + 0.5F));
        }

        this.sleeping = false;

        if (!this.worldObj.isRemote && par2)
        {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        if (par1)
        {
            this.sleepTimer = 0;
        }
        else
        {
            this.sleepTimer = 100;
        }

        if (par3)
        {
            this.setSpawnChunk(this.playerLocation, false);
        }
    }

    /**
     * Checks if the player is currently in a bed
     */
    private boolean isInBed()
    {
        ChunkCoordinates c = playerLocation;
        int blockID = worldObj.getBlockId(c.posX, c.posY, c.posZ);
        return Block.blocksList[blockID] != null && Block.blocksList[blockID].isBed(worldObj, c.posX, c.posY, c.posZ, this);
    }

    /**
     * Ensure that a block enabling respawning exists at the specified coordinates and find an empty space nearby to
     * spawn.
     */
    public static ChunkCoordinates verifyRespawnCoordinates(World par0World, ChunkCoordinates par1ChunkCoordinates, boolean par2)
    {
        IChunkProvider ichunkprovider = par0World.getChunkProvider();
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);

        ChunkCoordinates c = par1ChunkCoordinates;
        Block block = Block.blocksList[par0World.getBlockId(c.posX, c.posY, c.posZ)];

        if (block != null && block.isBed(par0World, c.posX, c.posY, c.posZ, null))
        {
            ChunkCoordinates chunkcoordinates1 = block.getBedSpawnPosition(par0World, c.posX, c.posY, c.posZ, null);
            return chunkcoordinates1;
        }
        else
        {
            Material material = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ);
            Material material1 = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY + 1, par1ChunkCoordinates.posZ);
            boolean flag1 = !material.isSolid() && !material.isLiquid();
            boolean flag2 = !material1.isSolid() && !material1.isLiquid();
            return par2 && flag1 && flag2 ? par1ChunkCoordinates : null;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the orientation of the bed in degrees.
     */
    public float getBedOrientationInDegrees()
    {
        if (this.playerLocation != null)
        {
            int x = playerLocation.posX;
            int y = playerLocation.posY;
            int z = playerLocation.posZ;
            Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
            int i = (block == null ? 0 : block.getBedDirection(worldObj, x, y, z));

            switch (i)
            {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return this.sleeping;
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully faded.
     */
    public boolean isPlayerFullyAsleep()
    {
        return this.sleeping && this.sleepTimer >= 100;
    }

    @SideOnly(Side.CLIENT)
    public int getSleepTimer()
    {
        return this.sleepTimer;
    }

    @SideOnly(Side.CLIENT)
    protected boolean getHideCape(int par1)
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1 << par1) != 0;
    }

    protected void setHideCape(int par1, boolean par2)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (par2)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1 << par1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & ~(1 << par1))));
        }
    }

    /**
     * Add a chat message to the player
     */
    public void addChatMessage(String par1Str) {}

    /**
     * Returns the location of the bed the player will respawn at, or null if the player has not slept in a bed.
     */
    public ChunkCoordinates getBedLocation()
    {
        return this.spawnChunk;
    }

    public boolean isSpawnForced()
    {
        return this.spawnForced;
    }

    /**
     * Defines a spawn coordinate to player spawn. Used by bed after the player sleep on it.
     */
    public void setSpawnChunk(ChunkCoordinates par1ChunkCoordinates, boolean par2)
    {
        if (par1ChunkCoordinates != null)
        {
            this.spawnChunk = new ChunkCoordinates(par1ChunkCoordinates);
            this.spawnForced = par2;
        }
        else
        {
            this.spawnChunk = null;
            this.spawnForced = false;
        }
    }

    /**
     * Will trigger the specified trigger.
     */
    public void triggerAchievement(StatBase par1StatBase)
    {
        this.addStat(par1StatBase, 1);
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2) {}

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        super.jump();
        this.addStat(StatList.jumpStat, 1);

        if (this.isSprinting())
        {
            this.addExhaustion(0.8F);
        }
        else
        {
            this.addExhaustion(0.2F);
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;

        if (this.capabilities.isFlying && this.ridingEntity == null)
        {
            double d3 = this.motionY;
            float f2 = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed();
            super.moveEntityWithHeading(par1, par2);
            this.motionY = d3 * 0.6D;
            this.jumpMovementFactor = f2;
        }
        else
        {
            super.moveEntityWithHeading(par1, par2);
        }

        this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    /**
     * Adds a value to a movement statistic field - like run, walk, swin or climb.
     */
    public void addMovementStat(double par1, double par3, double par5)
    {
        if (this.ridingEntity == null)
        {
            int i;

            if (this.isInsideOfMaterial(Material.water))
            {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

                if (i > 0)
                {
                    this.addStat(StatList.distanceDoveStat, i);
                    this.addExhaustion(0.015F * (float)i * 0.01F);
                }
            }
            else if (this.isInWater())
            {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 0)
                {
                    this.addStat(StatList.distanceSwumStat, i);
                    this.addExhaustion(0.015F * (float)i * 0.01F);
                }
            }
            else if (this.isOnLadder())
            {
                if (par3 > 0.0D)
                {
                    this.addStat(StatList.distanceClimbedStat, (int)Math.round(par3 * 100.0D));
                }
            }
            else if (this.onGround)
            {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 0)
                {
                    this.addStat(StatList.distanceWalkedStat, i);

                    if (this.isSprinting())
                    {
                        this.addExhaustion(0.099999994F * (float)i * 0.01F);
                    }
                    else
                    {
                        this.addExhaustion(0.01F * (float)i * 0.01F);
                    }
                }
            }
            else
            {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 25)
                {
                    this.addStat(StatList.distanceFlownStat, i);
                }
            }
        }
    }

    /**
     * Adds a value to a mounted movement statistic field - by minecart, boat, or pig.
     */
    private void addMountedMovementStat(double par1, double par3, double par5)
    {
        if (this.ridingEntity != null)
        {
            int i = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

            if (i > 0)
            {
                if (this.ridingEntity instanceof EntityMinecart)
                {
                    this.addStat(StatList.distanceByMinecartStat, i);

                    if (this.startMinecartRidingCoordinate == null)
                    {
                        this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
                    }
                    else if ((double)this.startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000000.0D)
                    {
                        this.addStat(AchievementList.onARail, 1);
                    }
                }
                else if (this.ridingEntity instanceof EntityBoat)
                {
                    this.addStat(StatList.distanceByBoatStat, i);
                }
                else if (this.ridingEntity instanceof EntityPig)
                {
                    this.addStat(StatList.distanceByPigStat, i);
                }
            }
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (!this.capabilities.allowFlying)
        {
            if (par1 >= 2.0F)
            {
                this.addStat(StatList.distanceFallenStat, (int)Math.round((double)par1 * 100.0D));
            }

            super.fall(par1);
        }
        else
        {
            MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(this, par1));
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLiving par1EntityLiving)
    {
        if (par1EntityLiving instanceof IMob)
        {
            this.triggerAchievement(AchievementList.killEnemy);
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        if (!this.capabilities.isFlying)
        {
            super.setInWeb();
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the Icon Index of the item currently held
     */
    public Icon getItemIcon(ItemStack par1ItemStack, int par2)
    {
        Icon icon = super.getItemIcon(par1ItemStack, par2);

        if (par1ItemStack.itemID == Item.fishingRod.itemID && this.fishEntity != null)
        {
            icon = Item.fishingRod.func_94597_g();
        }
        else
        {
            if (par1ItemStack.getItem().requiresMultipleRenderPasses())
            {
                return par1ItemStack.getItem().getIcon(par1ItemStack, par2);
            }

            if (this.itemInUse != null && par1ItemStack.itemID == Item.bow.itemID)
            {
                int j = par1ItemStack.getMaxItemUseDuration() - this.itemInUseCount;

                if (j >= 18)
                {
                    return Item.bow.getItemIconForUseDuration(2);
                }

                if (j > 13)
                {
                    return Item.bow.getItemIconForUseDuration(1);
                }

                if (j > 0)
                {
                    return Item.bow.getItemIconForUseDuration(0);
                }
            }
            icon = par1ItemStack.getItem().getIcon(par1ItemStack, par2, this, itemInUse, itemInUseCount);
        }

        return icon;
    }

    public ItemStack getCurrentArmor(int par1)
    {
        return this.inventory.armorItemInSlot(par1);
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor() {}

    protected void func_82162_bC() {}

    /**
     * This method increases the player's current amount of experience.
     */
    public void addExperience(int par1)
    {
        this.addScore(par1);
        int j = Integer.MAX_VALUE - this.experienceTotal;

        if (par1 > j)
        {
            par1 = j;
        }

        this.experience += (float)par1 / (float)this.xpBarCap();

        for (this.experienceTotal += par1; this.experience >= 1.0F; this.experience /= (float)this.xpBarCap())
        {
            this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
            this.addExperienceLevel(1);
        }
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int par1)
    {
        this.experienceLevel += par1;

        if (this.experienceLevel < 0)
        {
            this.experienceLevel = 0;
            this.experience = 0.0F;
            this.experienceTotal = 0;
        }

        if (par1 > 0 && this.experienceLevel % 5 == 0 && (float)this.field_82249_h < (float)this.ticksExisted - 100.0F)
        {
            float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
            this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
            this.field_82249_h = this.ticksExisted;
        }
    }

    /**
     * This method returns the cap amount of experience that the experience bar can hold. With each level, the
     * experience cap on the player's experience bar is raised by 10.
     */
    public int xpBarCap()
    {
        return this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7 : (this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3 : 17);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void addExhaustion(float par1)
    {
        if (!this.capabilities.disableDamage)
        {
            if (!this.worldObj.isRemote)
            {
                this.foodStats.addExhaustion(par1);
            }
        }
    }

    /**
     * Returns the player's FoodStats object.
     */
    public FoodStats getFoodStats()
    {
        return this.foodStats;
    }

    public boolean canEat(boolean par1)
    {
        return (par1 || this.foodStats.needFood()) && !this.capabilities.disableDamage;
    }

    /**
     * Checks if the player's health is not full and not zero.
     */
    public boolean shouldHeal()
    {
        return this.getHealth() > 0 && this.getHealth() < this.getMaxHealth();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void setItemInUse(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack != this.itemInUse)
        {
            this.itemInUse = par1ItemStack;
            this.itemInUseCount = par2;

            if (!this.worldObj.isRemote)
            {
                this.setEating(true);
            }
        }
    }

    /**
     * Returns true if the item the player is holding can harvest the block at the given coords. Args: x, y, z.
     */
    public boolean canCurrentToolHarvestBlock(int par1, int par2, int par3)
    {
        if (this.capabilities.allowEdit)
        {
            return true;
        }
        else
        {
            int l = this.worldObj.getBlockId(par1, par2, par3);

            if (l > 0)
            {
                Block block = Block.blocksList[l];

                if (block.blockMaterial.isAlwaysHarvested())
                {
                    return true;
                }

                if (this.getCurrentEquippedItem() != null)
                {
                    ItemStack itemstack = this.getCurrentEquippedItem();

                    if (itemstack.canHarvestBlock(block) || itemstack.getStrVsBlock(block) > 1.0F)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean canPlayerEdit(int par1, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
        return this.capabilities.allowEdit ? true : (par5ItemStack != null ? par5ItemStack.func_82835_x() : false);
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            return 0;
        }
        else
        {
            int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        }
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer()
    {
        return true;
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        return this.username;
    }

    public boolean func_94062_bN()
    {
        return super.func_94062_bN();
    }

    @SideOnly(Side.CLIENT)
    public boolean func_94059_bO()
    {
        return true;
    }

    public boolean canPickUpLoot()
    {
        return false;
    }

    /**
     * Copies the values from the given player into this player if boolean par2 is true. Always clones Ender Chest
     * Inventory.
     */
    public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2)
    {
        if (par2)
        {
            this.inventory.copyInventory(par1EntityPlayer.inventory);
            this.health = par1EntityPlayer.health;
            this.foodStats = par1EntityPlayer.foodStats;
            this.experienceLevel = par1EntityPlayer.experienceLevel;
            this.experienceTotal = par1EntityPlayer.experienceTotal;
            this.experience = par1EntityPlayer.experience;
            this.setScore(par1EntityPlayer.getScore());
            this.teleportDirection = par1EntityPlayer.teleportDirection;
        }
        else if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            this.inventory.copyInventory(par1EntityPlayer.inventory);
            this.experienceLevel = par1EntityPlayer.experienceLevel;
            this.experienceTotal = par1EntityPlayer.experienceTotal;
            this.experience = par1EntityPlayer.experience;
            this.setScore(par1EntityPlayer.getScore());
        }

        this.theInventoryEnderChest = par1EntityPlayer.theInventoryEnderChest;

        //Copy over a section of the Entity Data from the old player.
        //Allows mods to specify data that persists after players respawn.
        NBTTagCompound old = par1EntityPlayer.getEntityData();
        if (old.hasKey(PERSISTED_NBT_TAG))
        {
            getEntityData().setCompoundTag(PERSISTED_NBT_TAG, old.getCompoundTag(PERSISTED_NBT_TAG));
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return !this.capabilities.isFlying;
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities() {}

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(EnumGameType par1EnumGameType) {}

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.username;
    }

    public StringTranslate getTranslator()
    {
        return StringTranslate.getInstance();
    }

    /**
     * Translates and formats the given string key with the given arguments.
     */
    public String translateString(String par1Str, Object ... par2ArrayOfObj)
    {
        return this.getTranslator().translateKeyFormat(par1Str, par2ArrayOfObj);
    }

    /**
     * Returns the InventoryEnderChest of this player.
     */
    public InventoryEnderChest getInventoryEnderChest()
    {
        return this.theInventoryEnderChest;
    }

    /**
     * 0 = item, 1-n is armor
     */
    public ItemStack getCurrentItemOrArmor(int par1)
    {
        return par1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[par1 - 1];
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return this.inventory.getCurrentItem();
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        if (par1 == 0)
        {
            this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
        }
        else
        {
            this.inventory.armorInventory[par1 - 1] = par2ItemStack;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_98034_c(EntityPlayer par1EntityPlayer)
    {
        if (!this.isInvisible())
        {
            return false;
        }
        else
        {
            ScorePlayerTeam scoreplayerteam = this.getTeam();
            return scoreplayerteam == null || par1EntityPlayer == null || par1EntityPlayer.getTeam() != scoreplayerteam || !scoreplayerteam.func_98297_h();
        }
    }

    public ItemStack[] getLastActiveItems()
    {
        return this.inventory.armorInventory;
    }

    @SideOnly(Side.CLIENT)
    public boolean getHideCape()
    {
        return this.getHideCape(1);
    }

    public boolean func_96092_aw()
    {
        return !this.capabilities.isFlying;
    }

    public Scoreboard getWorldScoreboard()
    {
        return this.worldObj.getScoreboard();
    }

    public ScorePlayerTeam getTeam()
    {
        return this.getWorldScoreboard().getPlayersTeam(this.username);
    }

    /**
     * Returns the translated name of the entity.
     */
    public String getTranslatedEntityName()
    {
        return ScorePlayerTeam.func_96667_a(this.getTeam(), this.username);
    }

    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z)
    {
        FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
    }
}
