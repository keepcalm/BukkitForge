package keepcalm.mods.bukkitforge;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import keepcalm.mods.bukkit.BukkitEventRouter;
import keepcalm.mods.bukkit.BukkitEventRouters;
import keepcalm.mods.bukkit.ToBukkit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.TextWrapper;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.LazyPlayerSet;
import org.bukkit.util.Waitable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 3/9/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class BukkitForgeNetHandler extends NetServerHandler {


    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** incremented each tick */
    private int currentTicks;

    /**
     * player is kicked if they float for over 80 ticks without flying enabled
     */
    public int ticksForFloatKick;
    private boolean field_72584_h;
    private int keepAliveRandomID;
    private long keepAliveTimeSent;
    private static Random randomGenerator = new Random();
    private long ticksOfLastKeepAlive;
    private volatile int chatSpamThresholdCount = 0;
    // MCPC - IMPORTANT: UPDATE THIS MAPPING FOR TESTING IN ECLIPSE
    // eclipse mapping = chatSpamThresholdCount
    // obf mapping = m
    private static final AtomicIntegerFieldUpdater chatSpamField = AtomicIntegerFieldUpdater.newUpdater(NetServerHandler.class, "m"); // CraftBukkit - multithreaded field
    //private static final AtomicIntegerFieldUpdater chatSpamField = AtomicIntegerFieldUpdater.newUpdater(NetServerHandler.class, "chatSpamThresholdCount"); // CraftBukkit - multithreaded field
    private int creativeItemCreationSpamThresholdTally = 0;

    /** The last known x position for this connection. */
    private double lastPosX;

    /** The last known y position for this connection. */
    private double lastPosY;

    /** The last known z position for this connection. */
    private double lastPosZ;

    /** is true when the player has moved since his last movement packet */
    public boolean hasMoved = true; // CraftBukkit - private -> public
    private IntHashMap field_72586_s = new IntHashMap();

    CraftServer server = null;

    public BukkitForgeNetHandler(MinecraftServer par1, INetworkManager par2, EntityPlayerMP par3)
    {
        super(par1, par2, par3);
        this.mcServer = par1;
        this.netManager = par2;
        par2.setNetHandler(this);
        this.playerEntity = par3;
        par3.playerNetServerHandler = this;
        // CraftBukkit start
        this.server = CraftServer.instance();
    }

    private int lastTick = 0;//MinecraftServer.currentTick;
    private int lastDropTick = 0;//MinecraftServer.currentTick;
    private int dropCount = 0;
    private static final int PLACE_DISTANCE_SQUARED = 6 * 6;

    // Get position of last block hit for BlockDamageLevel.STOPPED
    private double lastPosX__ForEvent_CB = Double.MAX_VALUE;
    private double lastPosY__ForEvent_CB = Double.MAX_VALUE;
    private double lastPosZ__ForEvent_CB = Double.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;
    private boolean justTeleported = false;

    // For the packet15 hack :(
    Long lastPacket;

    // Store the last block right clicked and what type it was
    private int lastMaterial;

    // MCPC+ - rename getPlayer -> getPlayerB() to disambiguate with FML's getPlayer() method of the same name (below)
    // Plugins calling this method will be remapped appropriately, but CraftBukkit code should be updated
    public CraftPlayer getPlayerB()
    {
        return (this.playerEntity == null) ? null : (CraftPlayer)ToBukkit.player(playerEntity);
    }
    private final static HashSet<Integer> invalidItems = new HashSet<Integer>(java.util.Arrays.asList(8, 9, 10, 11, 26, 34, 36, 43, 51, 52, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 95, 104, 105, 115, 117, 118, 119, 125, 127, 132, 137, 140, 141, 142, 144)); // TODO: Check after every update.
    // CraftBukkit end

    /**
     * run once each game tick
     */
    public void networkTick()
    {
        this.field_72584_h = false;
        ++this.currentTicks;
        this.mcServer.theProfiler.startSection("packetflow");
        this.netManager.processReadPackets();
        this.mcServer.theProfiler.endStartSection("keepAlive");

        if ((long)this.currentTicks - this.ticksOfLastKeepAlive > 20L)
        {
            this.ticksOfLastKeepAlive = (long)this.currentTicks;
            this.keepAliveTimeSent = System.nanoTime() / 1000000L;
            this.keepAliveRandomID = randomGenerator.nextInt();
            this.sendPacketToPlayer(new Packet0KeepAlive(this.keepAliveRandomID));
        }

        // CraftBukkit start
        for (int spam; (spam = this.chatSpamThresholdCount) > 0 && !chatSpamField.compareAndSet(this, spam, spam - 1);) ;

        /* Use thread-safe field access instead
        if (this.m > 0) {
            --this.m;
        }
        */
        // CraftBukkit end

        if (this.creativeItemCreationSpamThresholdTally > 0)
        {
            --this.creativeItemCreationSpamThresholdTally;
        }

        this.mcServer.theProfiler.endStartSection("playerTick");
        this.mcServer.theProfiler.endSection();
    }

    public void kickPlayerFromServer(String par1Str)
    {
        if (!this.connectionClosed)
        {
            // CraftBukkit start
            String leaveMessage = "\u00A7e" + this.playerEntity.username + " left the game.";
            PlayerKickEvent event = new PlayerKickEvent(ToBukkit.player(this.playerEntity), par1Str, leaveMessage);

            if (mcServer.isServerRunning())
            {
                this.server.getPluginManager().callEvent(event);
            }

            if (event.isCancelled())
            {
                // Do not kick the player
                return;
            }

            // Send the possibly modified leave message
            par1Str = event.getReason();
            // CraftBukkit end
            this.playerEntity.mountEntityAndWakeUp();
            this.sendPacketToPlayer(new Packet255KickDisconnect(par1Str));
            this.netManager.serverShutdown();
            // CraftBukkit start
            leaveMessage = event.getLeaveMessage();

            if (leaveMessage != null && leaveMessage.length() > 0)
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(leaveMessage));
            }

            super.kickPlayerFromServer(par1Str);
        }
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        this.field_72584_h = true;

        if (!this.playerEntity.playerConqueredTheEnd)
        {
            double var3;

            if (!this.hasMoved)
            {
                var3 = par1Packet10Flying.yPosition - this.lastPosY;

                if (par1Packet10Flying.xPosition == this.lastPosX && var3 * var3 < 0.01D && par1Packet10Flying.zPosition == this.lastPosZ)
                {
                    this.hasMoved = true;
                }
            }

            // CraftBukkit start
            Player player = this.getPlayerB();
            Location from = new Location(player.getWorld(), lastPosX__ForEvent_CB, lastPosY__ForEvent_CB, lastPosZ__ForEvent_CB, lastYaw, lastPitch); // Get the Players previous Event location.
            Location to = player.getLocation().clone(); // Start off the To location as the Players current location.

            // If the packet contains movement information then we update the To location with the correct XYZ.
            if (par1Packet10Flying.moving && !(par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D))
            {
                to.setX(par1Packet10Flying.xPosition);
                to.setY(par1Packet10Flying.yPosition);
                to.setZ(par1Packet10Flying.zPosition);
            }

            // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
            if (par1Packet10Flying.rotating)
            {
                to.setYaw(par1Packet10Flying.yaw);
                to.setPitch(par1Packet10Flying.pitch);
            }

            // Prevent 40 event-calls for less than a single pixel of movement >.>
            double delta = Math.pow(this.lastPosX__ForEvent_CB - to.getX(), 2) + Math.pow(this.lastPosY__ForEvent_CB - to.getY(), 2) + Math.pow(this.lastPosZ__ForEvent_CB - to.getZ(), 2);
            float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());

            if ((delta > 1f / 256 || deltaAngle > 10f) && (this.hasMoved && !this.playerEntity.isDead))
            {
                this.lastPosX__ForEvent_CB = to.getX();
                this.lastPosY__ForEvent_CB = to.getY();
                this.lastPosZ__ForEvent_CB = to.getZ();
                this.lastYaw = to.getYaw();
                this.lastPitch = to.getPitch();

                // Skip the first time we do this
                if (from.getX() != Double.MAX_VALUE)
                {
                    PlayerMoveEvent event = BukkitEventRouters.Player.PlayerMove.callEvent(false, null, player, from, to);

                    // If the event is cancelled we move the player back to their old location.
                    if (event.isCancelled())
                    {
                        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet13PlayerLookMove(from.getX(), from.getY() + 1.6200000047683716D, from.getY(), from.getZ(), from.getYaw(), from.getPitch(), false));
                        return;
                    }

                    /* If a Plugin has changed the To destination then we teleport the Player
                    there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                    We only do this if the Event was not cancelled. */
                    if (!to.equals(event.getTo()) && !event.isCancelled())
                    {
                        ToBukkit.player(playerEntity).teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                        return;
                    }

                    /* Check to see if the Players Location has some how changed during the call of the event.
                    This can happen due to a plugin teleporting the player instead of using .setTo() */
                    if (!from.equals(this.getPlayerB().getLocation()) && this.justTeleported)
                    {
                        this.justTeleported = false;
                        return;
                    }
                }
            }

            if (Double.isNaN(par1Packet10Flying.xPosition) || Double.isNaN(par1Packet10Flying.yPosition) || Double.isNaN(par1Packet10Flying.zPosition) || Double.isNaN(par1Packet10Flying.stance))
            {
                player.teleport(player.getWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                System.err.println(player.getName() + " was caught trying to crash the server with an invalid position.");
                player.kickPlayer("Nope!");
                return;
            }

            if (this.hasMoved && !this.playerEntity.isDead)
            {
                // CraftBukkit end
                double var5;
                double var7;
                double var9;
                double var13;

                if (this.playerEntity.ridingEntity != null)
                {
                    float var34 = this.playerEntity.rotationYaw;
                    float var4 = this.playerEntity.rotationPitch;
                    this.playerEntity.ridingEntity.updateRiderPosition();
                    var5 = this.playerEntity.posX;
                    var7 = this.playerEntity.posY;
                    var9 = this.playerEntity.posZ;
                    double var35 = 0.0D;
                    var13 = 0.0D;

                    if (par1Packet10Flying.rotating)
                    {
                        var34 = par1Packet10Flying.yaw;
                        var4 = par1Packet10Flying.pitch;
                    }

                    if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                    {
                        if (Math.abs(par1Packet10Flying.xPosition) > 1.0D || Math.abs(par1Packet10Flying.zPosition) > 1.0D)
                        {
                            System.err.println(this.playerEntity.username + " was caught trying to crash the server with an invalid position.");
                            this.kickPlayerFromServer("Nope!");
                            return;
                        }

                        var35 = par1Packet10Flying.xPosition;
                        var13 = par1Packet10Flying.zPosition;
                    }

                    this.playerEntity.onGround = par1Packet10Flying.onGround;
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.moveEntity(var35, 0.0D, var13);
                    this.playerEntity.setPositionAndRotation(var5, var7, var9, var34, var4);
                    this.playerEntity.motionX = var35;
                    this.playerEntity.motionZ = var13;

                    if (this.playerEntity.ridingEntity != null)
                    {
                        var2.uncheckedUpdateEntity(this.playerEntity.ridingEntity, true);
                    }

                    if (this.playerEntity.ridingEntity != null)
                    {
                        this.playerEntity.ridingEntity.updateRiderPosition();
                    }

                    if (!this.hasMoved) //Fixes teleportation kick while riding entities
                    {
                        return;
                    }

                    this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                    this.lastPosX = this.playerEntity.posX;
                    this.lastPosY = this.playerEntity.posY;
                    this.lastPosZ = this.playerEntity.posZ;
                    var2.updateEntity(this.playerEntity);
                    return;
                }

                if (this.playerEntity.isPlayerSleeping())
                {
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    var2.updateEntity(this.playerEntity);
                    return;
                }

                var3 = this.playerEntity.posY;
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                var5 = this.playerEntity.posX;
                var7 = this.playerEntity.posY;
                var9 = this.playerEntity.posZ;
                float var11 = this.playerEntity.rotationYaw;
                float var12 = this.playerEntity.rotationPitch;

                if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                {
                    par1Packet10Flying.moving = false;
                }

                if (par1Packet10Flying.moving)
                {
                    var5 = par1Packet10Flying.xPosition;
                    var7 = par1Packet10Flying.yPosition;
                    var9 = par1Packet10Flying.zPosition;
                    var13 = par1Packet10Flying.stance - par1Packet10Flying.yPosition;

                    if (!this.playerEntity.isPlayerSleeping() && (var13 > 1.65D || var13 < 0.1D))
                    {
                        this.kickPlayerFromServer("Illegal stance");
                        this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " had an illegal stance: " + var13);
                        return;
                    }

                    if (Math.abs(par1Packet10Flying.xPosition) > 3.2E7D || Math.abs(par1Packet10Flying.zPosition) > 3.2E7D)
                    {
                        // CraftBukkit - teleport to previous position instead of kicking, players get stuck
                        this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                        return;
                    }
                }

                if (par1Packet10Flying.rotating)
                {
                    var11 = par1Packet10Flying.yaw;
                    var12 = par1Packet10Flying.pitch;
                }

                this.playerEntity.onUpdateEntity();
                this.playerEntity.ySize = 0.0F;
                this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);

                if (!this.hasMoved)
                {
                    return;
                }

                var13 = var5 - this.playerEntity.posX;
                double var15 = var7 - this.playerEntity.posY;
                double var17 = var9 - this.playerEntity.posZ;
                // CraftBukkit start - min to max
                double d8 = Math.max(Math.abs(var13), Math.abs(this.playerEntity.motionX));
                double d9 = Math.max(Math.abs(var15), Math.abs(this.playerEntity.motionY));
                double d10 = Math.max(Math.abs(var17), Math.abs(this.playerEntity.motionZ));
                // CraftBukkit end
                double var19 = d8 * d8 + d9 * d9 + d10 * d10;

                if (var19 > 100.0D && this.hasMoved && (!this.mcServer.isSinglePlayer() || !this.mcServer.getServerOwner().equals(this.playerEntity.username)))   // CraftBukkit - Added this.checkMovement condition to solve this check being triggered by teleports
                {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + d8 + ", " + d9 + ", " + d10 + ")");
                    return;
                }

                float var21 = 0.0625F;
                boolean var23 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double) var21, (double) var21, (double) var21)).isEmpty();

                if (this.playerEntity.onGround && !par1Packet10Flying.onGround && var15 > 0.0D)
                {
                    this.playerEntity.addExhaustion(0.2F);
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.moveEntity(var13, var15, var17);
                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.playerEntity.addMovementStat(var13, var15, var17);
                double var25 = var15;
                var13 = var5 - this.playerEntity.posX;
                var15 = var7 - this.playerEntity.posY;

                if (var15 > -0.5D || var15 < 0.5D)
                {
                    var15 = 0.0D;
                }

                var17 = var9 - this.playerEntity.posZ;
                var19 = var13 * var13 + var15 * var15 + var17 * var17;
                boolean var27 = false;

                if (var19 > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative())
                {
                    var27 = true;
                    this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " moved wrongly!");
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
                boolean var28 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double) var21, (double) var21, (double) var21)).isEmpty();

                if (var23 && (var27 || !var28) && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.noClip)   // Forge
                {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
                    return;
                }

                AxisAlignedBB var29 = this.playerEntity.boundingBox.copy().expand((double) var21, (double) var21, (double) var21).addCoord(0.0D, -0.55D, 0.0D);

                if (!this.mcServer.isFlightAllowed() && !this.playerEntity.capabilities.allowFlying && !var2.checkBlockCollision(var29))   // CraftBukkit - check abilities instead of creative mode
                {
                    if (var25 >= -0.03125D)
                    {
                        ++this.ticksForFloatKick;

                        if (this.ticksForFloatKick > 80)
                        {
                        	this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " was kicked for floating too long!");
                            this.kickPlayerFromServer("Flying is not enabled on this server");
                            return;
                        }
                    }
                }
                else
                {
                    this.ticksForFloatKick = 0;
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);

                if (this.playerEntity.theItemInWorldManager.isCreative())
                {
                    return;    // CraftBukkit - fixed fall distance accumulating while being in Creative mode.
                }

                this.playerEntity.updateFlyingState(this.playerEntity.posY - var3, par1Packet10Flying.onGround);
            }
        }
    }

    /**
     * Moves the player to the specified destination and rotation
     */
    public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8)
    {
        // CraftBukkit start - Delegate to teleport(Location)
        Player player = this.getPlayerB();
        Location from = player.getLocation();
        Location to = new Location(this.getPlayerB().getWorld(), par1, par3, par5, par7, par8);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to, PlayerTeleportEvent.TeleportCause.UNKNOWN);
        this.server.getPluginManager().callEvent(event);
        from = event.getFrom();
        to = event.isCancelled() ? from : event.getTo();
        this.teleport(to);
    }

    public void teleport(Location dest)
    {
        double d0, d1, d2;
        float f, f1;
        d0 = dest.getX();
        d1 = dest.getY();
        d2 = dest.getZ();
        f = dest.getYaw();
        f1 = dest.getPitch();

        // TODO: make sure this is the best way to address this.
        if (Float.isNaN(f))
        {
            f = 0;
        }

        if (Float.isNaN(f1))
        {
            f1 = 0;
        }

        this.lastPosX__ForEvent_CB = d0;
        this.lastPosY__ForEvent_CB = d1;
        this.lastPosZ__ForEvent_CB = d2;
        this.lastYaw = f;
        this.lastPitch = f1;
        this.justTeleported = true;
        // CraftBukkit end
        this.hasMoved = false;
        this.lastPosX = d0;
        this.lastPosY = d1;
        this.lastPosZ = d2;
        this.playerEntity.setPositionAndRotation(d0, d1, d2, f, f1);
        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet13PlayerLookMove(d0, d1 + 1.6200000047683716D, d1, d2, f, f1, false));
    }

    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);

        if (par1Packet14BlockDig.status == 4)
        {
            // CraftBukkit start
            // If the ticks aren't the same then the count starts from 0 and we update the lastDropTick.
            if (this.lastDropTick != mcServer.getTickCounter())
            {
                this.dropCount = 0;
                this.lastDropTick = mcServer.getTickCounter();
            }
            else
            {
                // Else we increment the drop count and check the amount.
                this.dropCount++;

                if (this.dropCount >= 20)
                {
                	this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " dropped their items too quickly!");
                    this.kickPlayerFromServer("You dropped your items too quickly (Hacking?)");
                    return;
                }
            }

            // CraftBukkit end
            this.playerEntity.dropOneItem(false);
        }
        else if (par1Packet14BlockDig.status == 3)
        {
            this.playerEntity.dropOneItem(true);
        }
        else if (par1Packet14BlockDig.status == 5)
        {
            this.playerEntity.stopUsingItem();
        }
        else
        {
            int var3 = this.mcServer.getSpawnProtectionSize();
            boolean var4 = var2.provider.dimensionId != 0 || this.mcServer.getConfigurationManager().getOps().isEmpty() || this.mcServer.getConfigurationManager().areCommandsAllowed(this.playerEntity.username) || var3 <= 0 || this.mcServer.isSinglePlayer();
            boolean var5 = false;

            if (par1Packet14BlockDig.status == 0)
            {
                var5 = true;
            }

            if (par1Packet14BlockDig.status == 1)
            {
                var5 = true;
            }

            if (par1Packet14BlockDig.status == 2)
            {
                var5 = true;
            }

            int var6 = par1Packet14BlockDig.xPosition;
            int var7 = par1Packet14BlockDig.yPosition;
            int var8 = par1Packet14BlockDig.zPosition;

            if (var5)
            {
                double var9 = this.playerEntity.posX - ((double)var6 + 0.5D);
                double var11 = this.playerEntity.posY - ((double)var7 + 0.5D) + 1.5D;
                double var13 = this.playerEntity.posZ - ((double)var8 + 0.5D);
                double var15 = var9 * var9 + var11 * var11 + var13 * var13;

                double dist = playerEntity.theItemInWorldManager.getBlockReachDistance() + 1;
                dist *= dist;

                if (var15 > dist)
                {
                    return;
                }

                if (var7 >= this.mcServer.getBuildLimit())
                {
                    return;
                }
            }

            ChunkCoordinates var17 = var2.getSpawnPoint();
            int var10 = MathHelper.abs_int(var6 - var17.posX);
            int var18 = MathHelper.abs_int(var8 - var17.posZ);

            if (var10 > var18)
            {
                var18 = var10;
            }

            if (par1Packet14BlockDig.status == 0)
            {
                // CraftBukkit start
                if (var18 < this.server.getSpawnRadius() && !var4)
                {
                    CraftEventFactory.callPlayerInteractEvent(this.playerEntity, org.bukkit.event.block.Action.LEFT_CLICK_BLOCK, var6, var7, var8, var10, ToBukkit.itemStack(this.playerEntity.inventory.getCurrentItem()));
                    ForgeEventFactory.onPlayerInteract(this.playerEntity, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, var6, var7, var8, 0); // Forge
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var6, var7, var8, var2));
                    // Update any tile entity data for this block
                    TileEntity tileentity = var2.getBlockTileEntity(var6, var7, var8);

                    if (tileentity != null)
                    {
                        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(tileentity.getDescriptionPacket());
                    }

                    // CraftBukkit end
                }
                else
                {
                    this.playerEntity.theItemInWorldManager.onBlockClicked(var6, var7, var8, par1Packet14BlockDig.face);
                }
            }
            else if (par1Packet14BlockDig.status == 2)
            {
                this.playerEntity.theItemInWorldManager.uncheckedTryHarvestBlock(var6, var7, var8);

                if (var2.getBlockId(var6, var7, var8) != 0)
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var6, var7, var8, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 1)
            {
                this.playerEntity.theItemInWorldManager.cancelDestroyingBlock(var6, var7, var8);

                if (var2.getBlockId(var6, var7, var8) != 0)
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var6, var7, var8, var2));
                }
            }
        }
    }

    public void handlePlace(Packet15Place par1Packet15Place)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);

        // CraftBukkit start
        if (this.playerEntity.isDead)
        {
            return;
        }

        // This is a horrible hack needed because the client sends 2 packets on 'right mouse click'
        // aimed at a block. We shouldn't need to get the second packet if the data is handled
        // but we cannot know what the client will do, so we might still get it
        //
        // If the time between packets is small enough, and the 'signature' similar, we discard the
        // second one. This sadly has to remain until Mojang makes their packets saner. :(
        //  -- Grum

        if (par1Packet15Place.getDirection() == 255)
        {
            if (par1Packet15Place.getItemStack() != null && par1Packet15Place.getItemStack().itemID == this.lastMaterial && this.lastPacket != null && par1Packet15Place.creationTimeMillis - this.lastPacket < 100)
            {
                this.lastPacket = null;
                return;
            }
        }
        else
        {
            this.lastMaterial = par1Packet15Place.getItemStack() == null ? -1 : par1Packet15Place.getItemStack().itemID;
            this.lastPacket = par1Packet15Place.creationTimeMillis;
        }

        // CraftBukkit - if rightclick decremented the item, always send the update packet.
        // this is not here for CraftBukkit's own functionality; rather it is to fix
        // a notch bug where the item doesn't update correctly.
        boolean always = false;
        // CraftBukkit end
        ItemStack var3 = this.playerEntity.inventory.getCurrentItem();
        boolean var4 = false;
        int var5 = par1Packet15Place.getXPosition();
        int var6 = par1Packet15Place.getYPosition();
        int var7 = par1Packet15Place.getZPosition();
        int var8 = par1Packet15Place.getDirection();
        int var9 = this.mcServer.getSpawnProtectionSize();
        boolean var10 = var2.provider.dimensionId != 0 || this.mcServer.getConfigurationManager().getOps().isEmpty() || this.mcServer.getConfigurationManager().areCommandsAllowed(this.playerEntity.username) || var9 <= 0 || this.mcServer.isSinglePlayer();

        if (par1Packet15Place.getDirection() == 255)
        {
            if (var3 == null)
            {
                return;
            }

            // CraftBukkit start
            int itemstackAmount = var3.stackSize;
            org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.playerEntity, org.bukkit.event.block.Action.RIGHT_CLICK_AIR, var5,var6, var7,var8, ToBukkit.itemStack(var3));
            // Forge start
            PlayerInteractEvent forgeEvent = ForgeEventFactory.onPlayerInteract(this.playerEntity, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1);

            if (event.useItemInHand() != org.bukkit.event.Event.Result.DENY && forgeEvent.useItem != net.minecraftforge.event.Event.Result.DENY)
            {
                this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, this.playerEntity.worldObj, var3);
            }

            // Forge end
            // CraftBukkit - notch decrements the counter by 1 in the above method with food,
            // snowballs and so forth, but he does it in a place that doesn't cause the
            // inventory update packet to get sent
            always = (var3.stackSize != itemstackAmount);
            // CraftBukkit end
        }
        else if (par1Packet15Place.getYPosition() >= this.mcServer.getBuildLimit() - 1 && (par1Packet15Place.getDirection() == 1 || par1Packet15Place.getYPosition() >= this.mcServer.getBuildLimit()))
        {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat("\u00A77Height limit for building is " + this.mcServer.getBuildLimit()));
            var4 = true;
        }
        else
        {
            ChunkCoordinates var11 = var2.getSpawnPoint();
            int var12 = MathHelper.abs_int(var5 - var11.posX);
            int var13 = MathHelper.abs_int(var7 - var11.posZ);

            if (var12 > var13)
            {
                var13 = var12;
            }

            // CraftBukkit start - Check if we can actually do something over this large a distance
            Location eyeLoc = this.getPlayerB().getEyeLocation();

            if (Math.pow(eyeLoc.getX() - var5, 2) + Math.pow(eyeLoc.getY() - var6, 2) + Math.pow(eyeLoc.getZ() - var7, 2) > PLACE_DISTANCE_SQUARED)
            {
                return;
            }

            var10 = true; // spawn protection moved to ItemBlock!!!

            if (var12 > var9 || var10)
            {
                // CraftBukkit end
                this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, var2, var3, var5, var6, var7, var8, par1Packet15Place.getXOffset(), par1Packet15Place.getYOffset(), par1Packet15Place.getZOffset());
            }

            var4 = true;
        }

        if (var4)
        {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));

            if (var8 == 0)
            {
                --var6;
            }

            if (var8 == 1)
            {
                ++var6;
            }

            if (var8 == 2)
            {
                --var7;
            }

            if (var8 == 3)
            {
                ++var7;
            }

            if (var8 == 4)
            {
                --var5;
            }

            if (var8 == 5)
            {
                ++var5;
            }

            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
        }

        var3 = this.playerEntity.inventory.getCurrentItem();

        if (var3 != null && var3.stackSize == 0)
        {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            var3 = null;
        }

        if (var3 == null || var3.getMaxItemUseDuration() == 0)
        {
            this.playerEntity.playerInventoryBeingManipulated = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot var14 = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            if (var14 == null) return; // MCPC+ - abort if no slot, fixes RP2 timer crash block place - see #181
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.playerInventoryBeingManipulated = false;

            // CraftBukkit - TODO CHECK IF NEEDED -- new if structure might not need 'always'. Kept it in for now, but may be able to remove in future
            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), par1Packet15Place.getItemStack()) || always)
            {
                this.sendPacketToPlayer(new Packet103SetSlot(this.playerEntity.openContainer.windowId, var14.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        if (this.connectionClosed)
        {
            return;    // CraftBukkit - rarely it would send a playerLoggedOut line twice
        }
        
        this.mcServer.getLogAgent().logInfo(this.playerEntity.username + " lost connection: " + par1Str);
        // CraftBukkit start - we need to handle custom quit messages
        /*String quitMessage = this.mcServer.getConfigurationManager().disconnect(this.playerEntity);

        if ((quitMessage != null) && (quitMessage.length() > 0))
        {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(quitMessage));
        }  */

        // CraftBukkit end
        this.connectionClosed = true;

        if (this.mcServer.isSinglePlayer() && this.playerEntity.username.equals(this.mcServer.getServerOwner()))
        {
        	this.mcServer.getLogAgent().logInfo("Stopping singleplayer server as player logged out");
            this.mcServer.initiateShutdown();
        }
    }

    /**
     * Default handler called for packets that don't have their own handlers in NetClientHandler; currentlly does
     * nothing.
     */
    public void unexpectedPacket(Packet par1Packet)
    {
        if (this.connectionClosed)
        {
            return;    // CraftBukkit
        }
        
        this.mcServer.getLogAgent().logWarning(this.getClass() + " wasn\'t prepared to deal with a " + par1Packet.getClass());
        this.kickPlayerFromServer("Protocol error, unexpected packet");
    }

    /**
     * addToSendQueue. if it is a chat packet, check before sending it
     */
    public void sendPacketToPlayer(Packet par1Packet)
    {
        if (par1Packet instanceof Packet3Chat)
        {
            Packet3Chat var2 = (Packet3Chat)par1Packet;
            int var3 = this.playerEntity.getChatVisibility();

            if (var3 == 2)
            {
                return;
            }

            if (var3 == 1 && !var2.getIsServer())
            {
                return;
            }

            // CraftBukkit start
            String message = var2.message;

            for (final String line : TextWrapper.wrapText(message))
            {
                this.netManager.addToSendQueue(new Packet3Chat(line));
            }

            return;
            // CraftBukkit end
        }

        // CraftBukkit start
        if (par1Packet == null)
        {
            return;
        }
        else if (par1Packet instanceof Packet6SpawnPosition)
        {
            Packet6SpawnPosition packet6 = (Packet6SpawnPosition) par1Packet;
            //this.playerEntity.compassTarget = new Location(this.getPlayerB().getWorld(), packet6.xPosition, packet6.yPosition, packet6.zPosition);
        }

        // CraftBukkit end
        this.netManager.addToSendQueue(par1Packet);
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        // CraftBukkit start
        if (this.playerEntity.isDead)
        {
            return;
        }

        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize())
        {
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayerB(), this.playerEntity.inventory.currentItem, par1Packet16BlockItemSwitch.id);
            this.server.getPluginManager().callEvent(event);
            // CraftBukkit end
            this.playerEntity.inventory.currentItem = par1Packet16BlockItemSwitch.id;
        }
        else
        {
        	this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " tried to set an invalid carried item");
            this.kickPlayerFromServer("Nope!"); // CraftBukkit
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        par1Packet3Chat = FMLNetworkHandler.handleChatMessage(this, par1Packet3Chat);
        if (this.playerEntity.getChatVisibility() == 2)
        {
            this.sendPacketToPlayer(new Packet3Chat("Cannot send chat message."));
        }
        else
        {
            String s = par1Packet3Chat.message;

            if (s.length() > 100)
            {
                System.out.println("var2 " + s + " is > 100");
                // CraftBukkit start
                /*if (par1Packet3Chat.canProcessAsync())
                {
                    Waitable waitable = new Waitable()
                    {
                        @Override
                        protected Object evaluate()
                        {
                            kickPlayerFromServer("Chat message too long");
                            return null;
                        }
                    };
                    this.mcServer.processQueue.add(waitable);

                    try
                    {
                        waitable.get();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    catch (ExecutionException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else  */
                {
                    this.kickPlayerFromServer("Chat message too long");
                }

                // CraftBukkit end
            }
            else
            {
                s = s.trim();
                for (int var3 = 0; var3 < s.length(); ++var3)
                {
                    if (!ChatAllowedCharacters.isAllowedCharacter(s.charAt(var3)))
                    {
                        // CraftBukkit start
                        /*if (par1Packet3Chat.canProcessAsync())
                        {
                            Waitable waitable = new Waitable()
                            {
                                @Override
                                protected Object evaluate()
                                {
                                    kickPlayerFromServer("Illegal characters in chat");
                                    return null;
                                }
                            };
                            this.mcServer.processQueue.add(waitable);

                            try
                            {
                                waitable.get();
                            }
                            catch (InterruptedException e)
                            {
                                Thread.currentThread().interrupt();
                            }
                            catch (ExecutionException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                        else  */
                        {
                            this.kickPlayerFromServer("Illegal characters in chat");
                        }

                        // CraftBukkit end
                        return;
                    }
                }

                // CraftBukkit start
                if (this.playerEntity.getChatVisibility() == 1 && !s.startsWith("/"))
                {
                    this.sendPacketToPlayer(new Packet3Chat("Cannot send chat message."));
                    return;
                }
                this.chat(s, par1Packet3Chat.canProcessAsync());
                // Spigot start
                boolean isCounted = true;

                /*if (server.spamGuardExclusions != null)
                {
                    for (String excluded : server.spamGuardExclusions)
                    {
                        if (s.startsWith(excluded))
                        {
                            isCounted = false;
                            break;
                        }
                    }
                }  */

                // This section stays because it is only applicable to packets
                if (isCounted && chatSpamField.addAndGet(this, 20) > 200 && !this.mcServer.getConfigurationManager().areCommandsAllowed(this.playerEntity.username))   // CraftBukkit use thread-safe spam
                {
                    // Spigot end
                    // CraftBukkit start
                    /*if (par1Packet3Chat.canProcessAsync())
                    {
                        Waitable waitable = new Waitable()
                        {
                            @Override
                            protected Object evaluate()
                            {
                                kickPlayerFromServer("playerLoggedOut.spam");
                                return null;
                            }
                        };
                        this.mcServer.processQueue.add(waitable);

                        try
                        {
                            waitable.get();
                        }
                        catch (InterruptedException e)
                        {
                            Thread.currentThread().interrupt();
                        }
                        catch (ExecutionException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    else*/
                    {
                        this.kickPlayerFromServer("playerLoggedOut.spam");
                    }

                    // CraftBukkit end
                }
            }
        }
    }

    public void chat(String s, boolean async)
    {
        if (!this.playerEntity.isDead)
        {
            if (s.length() == 0)
            {
            	this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " tried to send an empty message");
                return;
            }

            if (getPlayerB().isConversing())
            {
                getPlayerB().acceptConversationInput(s);
                return;
            }

            if (s.startsWith("/"))
            {
                this.handleSlashCommand(s);
                return;
            }
            else
            {
                Player player = this.getPlayerB();
                AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet());
                this.server.getPluginManager().callEvent(event);
                ServerChatEvent forgeEvent = new ServerChatEvent(this.playerEntity, s, "<" + this.playerEntity.username + "> " + s);
                if (MinecraftForge.EVENT_BUS.post(forgeEvent))
                {
                    return;
                }
                if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length != 0)
                {
                    // Evil plugins still listening to deprecated event
                    final PlayerChatEvent queueEvent = new PlayerChatEvent(player, event.getMessage(), event.getFormat(), event.getRecipients());
                    queueEvent.setCancelled(event.isCancelled());
                    //Waitable waitable = new Waitable()
                    {
                        //@Override
                        //protected Object evaluate()
                        {
                            Bukkit.getPluginManager().callEvent(queueEvent);

                            if (!queueEvent.isCancelled())
                            {
                                //return null;


                            String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
                            //mcServer. .console.sendMessage(message);

                            if (((LazyPlayerSet) queueEvent.getRecipients()).isLazy())
                            {
                                for (Object pplayer : mcServer.getConfigurationManager().playerEntityList)
                                {
                                    ((EntityPlayerMP) pplayer).sendChatToPlayer(message);
                                }
                            }
                            else
                            {
                                for (Player pplayer : queueEvent.getRecipients())
                                {
                                    pplayer.sendMessage(message);
                                }
                            }

                            //return null;
                            }
                        }
                    };

                    /*if (async)
                    {
                        mcServer.processQueue.add(waitable);
                    }
                    else
                    {
                        waitable.run();
                    }

                    try
                    {
                        waitable.get();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt(); // This is proper habit for java. If we aren't handling it, pass it on!
                    }
                    catch (ExecutionException e)
                    {
                        throw new RuntimeException("Exception processing chat event", e.getCause());
                    }    */
                }
                else
                {
                    if (event.isCancelled())
                    {
                        return;
                    }

                    s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
                    //mcServer.console.sendMessage(s);

                    if (((LazyPlayerSet) event.getRecipients()).isLazy())
                    {
                        for (Object recipient : mcServer.getConfigurationManager().playerEntityList)
                        {
                            ((EntityPlayerMP) recipient).sendChatToPlayer(s);
                        }
                    }
                    else
                    {
                        for (Player recipient : event.getRecipients())
                        {
                            recipient.sendMessage(s);
                        }
                    }
                }
            }
        }

        return;
    }
    // CraftBukkit end

    /**
     * Processes a / command
     */
    private void handleSlashCommand(String par1Str)
    {
        // CraftBukkit start
        CraftPlayer player = this.getPlayerB();
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, par1Str, new LazyPlayerSet());
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled())
        {
            return;
        }

        try
        {
            //if (server.logCommands)
            {
           //     logger.info(event.getPlayer().getName() + " issued server command: " + event.getMessage());    // Spigot
            }
            // MCPC+ start - handle bukkit/vanilla commands
            int space = event.getMessage().indexOf(" ");
            // if bukkit command exists then execute it over vanilla
            if (this.server.getCommandMap().getCommand(event.getMessage().substring(1, space != -1 ? space : event.getMessage().length())) != null)
            {
                this.server.dispatchCommand(event.getPlayer(), event.getMessage().substring(1));
                return;
            }
            else // process vanilla command
            {
                this.server.dispatchVanillaCommand(event.getPlayer(), event.getMessage().substring(1));
                return;
            }
        }
        catch (org.bukkit.command.CommandException ex)
        {
            player.sendMessage(org.bukkit.ChatColor.RED + "An internal error occurred while attempting to perform this command");
            Logger.getLogger(NetServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // CraftBukkit end
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        if (par1Packet18Animation.animate == 1)
        {
            // CraftBukkit start - raytrace to look for 'rogue armswings'
            float f = 1.0F;
            float f1 = this.playerEntity.prevRotationPitch + (this.playerEntity.rotationPitch - this.playerEntity.prevRotationPitch) * f;
            float f2 = this.playerEntity.prevRotationYaw + (this.playerEntity.rotationYaw - this.playerEntity.prevRotationYaw) * f;
            double d0 = this.playerEntity.prevPosX + (this.playerEntity.posX - this.playerEntity.prevPosX) * (double) f;
            double d1 = this.playerEntity.prevPosY + (this.playerEntity.posY - this.playerEntity.prevPosY) * (double) f + 1.62D - (double) this.playerEntity.yOffset;
            double d2 = this.playerEntity.prevPosZ + (this.playerEntity.posZ - this.playerEntity.prevPosZ) * (double) f;
            Vec3 vec3d = this.playerEntity.worldObj.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
            float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
            float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            double d3 = 5.0D;
            Vec3 vec3d1 = vec3d.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
            MovingObjectPosition movingobjectposition = this.playerEntity.worldObj.rayTraceBlocks_do(vec3d, vec3d1, true);

            if (movingobjectposition == null || movingobjectposition.typeOfHit != EnumMovingObjectType.TILE)
            {
                CraftEventFactory.callPlayerInteractEvent(playerEntity, org.bukkit.event.block.Action.LEFT_CLICK_AIR, (int)playerEntity.posX, (int)playerEntity.posY, (int)playerEntity.posZ, 0, ToBukkit.itemStack(this.playerEntity.inventory.getCurrentItem()));
            }

            // Arm swing animation
            PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayerB());
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled())
            {
                return;
            }

            // CraftBukkit end
            this.playerEntity.swingItem();
        }
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
    {
        // CraftBukkit start
        if (this.playerEntity.isDead)
        {
            return;
        }

        if (par1Packet19EntityAction.state == 1 || par1Packet19EntityAction.state == 2)
        {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getPlayerB(), par1Packet19EntityAction.state == 1);
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled())
            {
                return;
            }
        }

        if (par1Packet19EntityAction.state == 4 || par1Packet19EntityAction.state == 5)
        {
            PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(this.getPlayerB(), par1Packet19EntityAction.state == 4);
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled())
            {
                return;
            }
        }

        // CraftBukkit end

        if (par1Packet19EntityAction.state == 1)
        {
            this.playerEntity.setSneaking(true);
        }
        else if (par1Packet19EntityAction.state == 2)
        {
            this.playerEntity.setSneaking(false);
        }
        else if (par1Packet19EntityAction.state == 4)
        {
            this.playerEntity.setSprinting(true);
        }
        else if (par1Packet19EntityAction.state == 5)
        {
            this.playerEntity.setSprinting(false);
        }
        else if (par1Packet19EntityAction.state == 3)
        {
            this.playerEntity.wakeUpPlayer(false, true, true);
            // this.checkMovement = false; // CraftBukkit - this is handled in teleport
        }
    }

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
    }

    /**
     * returns 0 for memoryMapped connections
     */
    public int packetSize()
    {
        return this.netManager.packetSize();
    }

    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        Entity var3 = var2.getEntityByID(par1Packet7UseEntity.targetEntity);

        if (var3 != null)
        {
            boolean var4 = this.playerEntity.canEntityBeSeen(var3);
            double var5 = 36.0D;

            if (!var4)
            {
                var5 = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(var3) < var5)
            {
                ItemStack itemInHand = this.playerEntity.inventory.getCurrentItem(); // CraftBukkit

                if (par1Packet7UseEntity.isLeftClick == 0)
                {
                    // CraftBukkit start
                    PlayerInteractEntityEvent event = new PlayerInteractEntityEvent((Player) this.getPlayerB(), ToBukkit.entity(var3));
                    this.server.getPluginManager().callEvent(event);

                    if (event.isCancelled())
                    {
                        return;
                    }

                    // CraftBukkit end
                    this.playerEntity.interactWith(var3);

                    // CraftBukkit start - update the client if the item is an infinite one
                    if (itemInHand != null && itemInHand.stackSize <= -1)
                    {
                        this.playerEntity.sendContainerToPlayer(this.playerEntity.openContainer);
                    }
                }
                else if (par1Packet7UseEntity.isLeftClick == 1)
                {
                    if ((var3 instanceof EntityItem) || (var3 instanceof EntityXPOrb) || (var3 instanceof EntityArrow))
                    {
                        String type = var3.getClass().getSimpleName();
                        kickPlayerFromServer("Attacking an " + type + " is not permitted");
                        System.out.println("Player " + playerEntity.username + " tried to attack an " + type + ", so I have playerLoggedOuted them for exploiting.");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(var3);

                    if (itemInHand != null && itemInHand.stackSize <= -1)
                    {
                        this.playerEntity.sendContainerToPlayer(this.playerEntity.openContainer);
                    }

                    // CraftBukkit end
                }
            }
        }
    }

    public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand)
    {
        if (par1Packet205ClientCommand.forceRespawn == 1)
        {
            if (this.playerEntity.playerConqueredTheEnd)
            {
                this.mcServer.getConfigurationManager().transferPlayerToDimension(this.playerEntity, 0, null );//PlayerTeleportEvent.TeleportCause.END_PORTAL); // CraftBukkit - reroute logic through custom portal management
            }
            else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
            {
                if (this.mcServer.isSinglePlayer() && this.playerEntity.username.equals(this.mcServer.getServerOwner()))
                {
                    this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    this.mcServer.deleteWorldAndStopServer();
                }
                else
                {
                    BanEntry var2 = new BanEntry(this.playerEntity.username);
                    var2.setBanReason("Death in Hardcore");
                    this.mcServer.getConfigurationManager().getBannedPlayers().put(var2);
                    this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                }
            }
            else
            {
                if (this.playerEntity.getHealth() > 0)
                {
                    return;
                }

                this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, playerEntity.dimension, false);
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return true;
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn) {}

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        // MCPC+ start - vanilla compatibility
        try
        {
            if ( ToBukkit.view(playerEntity, (ContainerPlayer)playerEntity.openContainer) != null)
            {
                // CraftBukkit start - INVENTORY_CLOSE hook
                InventoryCloseEvent event = new InventoryCloseEvent( ToBukkit.view( playerEntity, (ContainerPlayer)playerEntity.openContainer));
                server.getPluginManager().callEvent(event);
                //this.playerEntity.openContainer.transferTo(this.playerEntity.inventoryContainer, getPlayerB());
                // CraftBukkit end
            }
        }
        catch (AbstractMethodError e)
        {
            // do nothing
        }
        // MCPC+ end
        this.playerEntity.closeInventory();
    }

    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }
        ItemStack itemstack = null;
        InventoryType.SlotType type = null;
        if (this.playerEntity.openContainer.windowId == par1Packet102WindowClick.window_Id && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            if( ToBukkit.view(playerEntity, (ContainerPlayer)playerEntity.openContainer) != null) // MCPC - allow vanilla to bypass
            {
                // CraftBukkit start - fire InventoryClickEvent
                InventoryView inventory = ToBukkit.view(playerEntity, (ContainerPlayer)playerEntity.openContainer);
                type = CraftInventoryView.getSlotType(inventory, par1Packet102WindowClick.inventorySlot);
                InventoryClickEvent event = new InventoryClickEvent(inventory, type, par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick != 0, par1Packet102WindowClick.holdingShift == 1);
                org.bukkit.inventory.Inventory top = inventory.getTopInventory();

                if (par1Packet102WindowClick.inventorySlot == 0 && top instanceof CraftingInventory)
                {
                    // MCPC+ start - vanilla compatibility
                    org.bukkit.inventory.Recipe recipe = null;
                    try {
                        recipe = ((CraftingInventory) top).getRecipe();
                    }
                    catch (AbstractMethodError e)
                    {
                        // do nothing
                    }
                    // MCPC+ end
                    if (recipe != null)
                    {
                        event = new CraftItemEvent(recipe, inventory, type, par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick != 0, par1Packet102WindowClick.holdingShift == 1);
                    }
                }

                server.getPluginManager().callEvent(event);

                itemstack = null;
                boolean defaultBehaviour = false;

                switch (event.getResult())
                {
                    case DEFAULT:
                        itemstack = this.playerEntity.openContainer.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, this.playerEntity);
                        defaultBehaviour = true;
                        break;

                    case DENY: // Deny any change, including changes from the event
                        break;

                    case ALLOW: // Allow changes unconditionally
                        org.bukkit.inventory.ItemStack cursor = event.getCursor();

                        if (cursor == null)
                        {
                            this.playerEntity.inventory.setItemStack((ItemStack) null);
                        }
                        else
                        {
                            this.playerEntity.inventory.setItemStack(CraftItemStack.asNMSCopy(cursor));
                        }

                        org.bukkit.inventory.ItemStack item = event.getCurrentItem();

                        if (item != null)
                        {
                            itemstack = CraftItemStack.asNMSCopy(item);

                            if (par1Packet102WindowClick.inventorySlot == -999)
                            {
                                this.playerEntity.dropPlayerItem(itemstack);
                            }
                            else
                            {
                                this.playerEntity.openContainer.getSlot(par1Packet102WindowClick.inventorySlot).putStack(itemstack);
                            }
                        }
                        else if (par1Packet102WindowClick.inventorySlot != -999)
                        {
                            this.playerEntity.openContainer.getSlot(par1Packet102WindowClick.inventorySlot).putStack((ItemStack) null);
                        }

                        break;
                }
                // CraftBukkit end
            } else {
                itemstack = this.playerEntity.openContainer.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, this.playerEntity);;
            }


            if (ItemStack.areItemStacksEqual(par1Packet102WindowClick.itemStack, itemstack))
            {
                this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, true));
                this.playerEntity.playerInventoryBeingManipulated = true;
                this.playerEntity.openContainer.detectAndSendChanges();
                this.playerEntity.updateHeldItem();
                this.playerEntity.playerInventoryBeingManipulated = false;
            }
            else
            {
                this.field_72586_s.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(par1Packet102WindowClick.action));
                this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, false));
                this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, false);
                ArrayList var2 = new ArrayList();

                for (int var3 = 0; var3 < this.playerEntity.openContainer.inventorySlots.size(); ++var3)
                {
                    var2.add(((Slot) this.playerEntity.openContainer.inventorySlots.get(var3)).getStack());
                }

                this.playerEntity.sendContainerAndContentsToPlayer(this.playerEntity.openContainer, var2);
                // CraftBukkit start - send a Set Slot to update the crafting result slot
                if (type == InventoryType.SlotType.RESULT && itemstack != null)
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer((Packet)(new Packet103SetSlot(this.playerEntity.openContainer.windowId, 0, itemstack)));
                }

                // CraftBukkit end
            }
        }
    }

    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem)
    {
        if (this.playerEntity.openContainer.windowId == par1Packet108EnchantItem.windowId && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, par1Packet108EnchantItem.enchantment);
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        if (this.playerEntity.theItemInWorldManager.isCreative())
        {
            boolean var2 = par1Packet107CreativeSetSlot.slot < 0;
            ItemStack var3 = par1Packet107CreativeSetSlot.itemStack;
            boolean var4 = par1Packet107CreativeSetSlot.slot >= 1 && par1Packet107CreativeSetSlot.slot < 36 + InventoryPlayer.getHotbarSize();
            // CraftBukkit

            boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null && !invalidItems.contains(var3.itemID);
            boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;
            // CraftBukkit start - Fire INVENTORY_CLICK event
            org.bukkit.entity.HumanEntity player = ToBukkit.player(this.playerEntity);
            InventoryView inventory = new CraftInventoryView(player, player.getInventory(), this.playerEntity.inventoryContainer);

            InventoryType.SlotType slot = InventoryType.SlotType.QUICKBAR;

            if (par1Packet107CreativeSetSlot.slot == -1)
            {
                slot = InventoryType.SlotType.OUTSIDE;
            }

            InventoryClickEvent event = new InventoryClickEvent(inventory, slot, slot == InventoryType.SlotType.OUTSIDE ? -999 : par1Packet107CreativeSetSlot.slot, false, false);
            server.getPluginManager().callEvent(event);
            org.bukkit.inventory.ItemStack item = event.getCurrentItem();

            switch (event.getResult())
            {
                case ALLOW:
                    if (slot == InventoryType.SlotType.QUICKBAR)
                    {
                        if (item == null)
                        {
                            this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack) null);
                        }
                        else
                        {
                            this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, CraftItemStack.asNMSCopy(item));
                        }
                    }
                    else if (item != null)
                    {
                        this.playerEntity.dropPlayerItem(CraftItemStack.asNMSCopy(item));
                    }

                    return;

                case DENY:

                    // TODO: Will this actually work?
                    if (par1Packet107CreativeSetSlot.slot > -1)
                    {
                        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(this.playerEntity.inventoryContainer.windowId, par1Packet107CreativeSetSlot.slot, CraftItemStack.asNMSCopy(item)));
                    }

                    return;

                case DEFAULT:
                    // We do the stuff below
                    break;

                default:
                    return;
            }

            // CraftBukkit end

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack)null);
                }
                else
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, var3);
                }

                this.playerEntity.inventoryContainer.setPlayerIsPresent(this.playerEntity, true);
            }
            else if (var2 && var5 && var6 && this.creativeItemCreationSpamThresholdTally < 200)
            {
                this.creativeItemCreationSpamThresholdTally += 20;
                EntityItem var7 = this.playerEntity.dropPlayerItem(var3);

                if (var7 != null)
                {
                    var7.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        Short var2 = (Short)this.field_72586_s.lookup(this.playerEntity.openContainer.windowId);

        if (var2 != null && par1Packet106Transaction.shortWindowId == var2.shortValue() && this.playerEntity.openContainer.windowId == par1Packet106Transaction.windowId && !this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, true);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        if (this.playerEntity.isDead)
        {
            return;    // CraftBukkit
        }

        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);

        if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.isEditable())
                {
                    this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
                    this.sendPacketToPlayer(new Packet130UpdateSign(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition, var4.signText)); // CraftBukkit
                    return;
                }
            }

            int var6;
            int var8;

            for (var8 = 0; var8 < 4; ++var8)
            {
                boolean var5 = true;

                if (par1Packet130UpdateSign.signLines[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < par1Packet130UpdateSign.signLines[var8].length(); ++var6)
                    {
                        if (!ChatAllowedCharacters.isAllowedCharacter(par1Packet130UpdateSign.signLines[var8].charAt(var6))) // Spigot
                        {
                            var5 = false;
                            break;
                        }
                    }
                }

                if (!var5)
                {
                    par1Packet130UpdateSign.signLines[var8] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var8 = par1Packet130UpdateSign.xPosition;
                int var9 = par1Packet130UpdateSign.yPosition;
                var6 = par1Packet130UpdateSign.zPosition;
                TileEntitySign var7 = (TileEntitySign)var3;
                // CraftBukkit start
                Player player = this.server.getPlayer( ToBukkit.player(this.playerEntity).getName());
                SignChangeEvent event = new SignChangeEvent((org.bukkit.craftbukkit.v1_5_R2.block.CraftBlock) player.getWorld().getBlockAt(var8, var9, var6), this.server.getPlayer(ToBukkit.player(this.playerEntity).getName()), par1Packet130UpdateSign.signLines);
                this.server.getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    for (int l = 0; l < 4; ++l)
                    {
                        var7.signText[l] = event.getLine(l);

                        if (var7.signText[l] == null)
                        {
                            var7.signText[l] = "";
                        }
                    }

                    var7.setEditable(false);
                }

                // CraftBukkit end
                var7.onInventoryChanged();
                var2.markBlockForUpdate(var8, var9, var6);
            }
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        if (par1Packet0KeepAlive.randomId == this.keepAliveRandomID)
        {
            int var2 = (int)(System.nanoTime() / 1000000L - this.keepAliveTimeSent);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + var2) / 4;
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return true;
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        // CraftBukkit start
        if (this.playerEntity.capabilities.allowFlying && this.playerEntity.capabilities.isFlying != par1Packet202PlayerAbilities.getFlying())
        {
            PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(this.server.getPlayer(this.playerEntity.getEntityName()), par1Packet202PlayerAbilities.getFlying());
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                this.playerEntity.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying(); // Actually set the player's flying status
            }
            else
            {
                this.playerEntity.sendPlayerAbilities(); // Tell the player their ability was reverted
            }
        }

        // CraftBukkit end
    }

    public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete)
    {
        StringBuilder var2 = new StringBuilder();
        String var4;

        for (Iterator var3 = this.mcServer.getPossibleCompletions(this.playerEntity, par1Packet203AutoComplete.getText()).iterator(); var3.hasNext(); var2.append(var4))
        {
            var4 = (String)var3.next();

            if (var2.length() > 0)
            {
                var2.append('\0'); // CraftBukkit - fix decompile issue
            }
        }

        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet203AutoComplete(var2.toString()));
    }

    public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo)
    {
        this.playerEntity.updateClientInfo(par1Packet204ClientInfo);
    }

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
    {
        FMLNetworkHandler.handlePacket250Packet(par1Packet250CustomPayload, netManager, this);
    }

    public void handleVanilla250Packet(Packet250CustomPayload packet250custompayload)   // Forge
    {
        DataInputStream datainputstream;
        ItemStack itemstack;
        ItemStack itemstack1;

        // CraftBukkit start - ignore empty payloads
        if (packet250custompayload.length <= 0)
        {
            return;
        }

        // CraftBukkit end

        if ("MC|BEdit".equals(packet250custompayload.channel))
        {
            try
            {
                datainputstream = new DataInputStream(new ByteArrayInputStream(packet250custompayload.data));
                itemstack = Packet.readItemStack(datainputstream);

                if (!ItemWritableBook.validBookTagPages(itemstack.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.playerEntity.inventory.getCurrentItem();

                if (itemstack != null && itemstack.itemID == Item.writableBook.itemID && itemstack.itemID == itemstack1.itemID)
                {
                    itemstack1.setTagInfo("pages", (NBTBase) itemstack.getTagCompound().getTagList("pages"));
                }
            }
            catch (Exception exception)
            {
                // CraftBukkit start
            	this.mcServer.getLogAgent().logSevereException(this.playerEntity.username + " sent invalid MC|BEdit data", exception);
                this.kickPlayerFromServer("Invalid book data!");
                // CraftBukkit end
            }
        }
        else if ("MC|BSign".equals(packet250custompayload.channel))
        {
            try
            {
                datainputstream = new DataInputStream(new ByteArrayInputStream(packet250custompayload.data));
                itemstack = Packet.readItemStack(datainputstream);

                if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.playerEntity.inventory.getCurrentItem();

                if (itemstack != null && itemstack.itemID == Item.writtenBook.itemID && itemstack1.itemID == Item.writableBook.itemID)
                {
                    itemstack1.setTagInfo("author", (NBTBase)(new NBTTagString("author", this.playerEntity.username)));
                    itemstack1.setTagInfo("title", (NBTBase)(new NBTTagString("title", itemstack.getTagCompound().getString("title"))));
                    itemstack1.setTagInfo("pages", (NBTBase) itemstack.getTagCompound().getTagList("pages"));
                    itemstack1.itemID = Item.writtenBook.itemID;
                }
            }
            catch (Exception exception1)
            {
                // CraftBukkit start
            	this.mcServer.getLogAgent().logSevereException(this.playerEntity.username + " sent invalid MC|BSign data", exception1);
                this.kickPlayerFromServer("Invalid book data!");
                // CraftBukkit end
            }
        }
        else
        {
            int i;

            if ("MC|TrSel".equals(packet250custompayload.channel))
            {
                try
                {
                    datainputstream = new DataInputStream(new ByteArrayInputStream(packet250custompayload.data));
                    i = datainputstream.readInt();
                    Container container = this.playerEntity.openContainer;

                    if (container instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant) container).setCurrentRecipeIndex(i);
                    }
                }
                catch (Exception exception2)
                {
                    // CraftBukkit start
                	this.mcServer.getLogAgent().logSevereException(this.playerEntity.username + " sent invalid MC|TrSel data", exception2);
                    this.kickPlayerFromServer("Invalid trade data!");
                    // CraftBukkit end
                }
            }
            else
            {
                int j;

                if ("MC|AdvCdm".equals(packet250custompayload.channel))
                {
                    if (!this.mcServer.isCommandBlockEnabled())
                    {
                        this.playerEntity.sendChatToPlayer(this.playerEntity.translateString("advMode.notEnabled", new Object[0]));
                    }
                    else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode)
                    {
                        try
                        {
                            datainputstream = new DataInputStream(new ByteArrayInputStream(packet250custompayload.data));
                            i = datainputstream.readInt();
                            j = datainputstream.readInt();
                            int k = datainputstream.readInt();
                            String s = Packet.readString(datainputstream, 256);
                            TileEntity tileentity = this.playerEntity.worldObj.getBlockTileEntity(i, j, k);

                            if (tileentity != null && tileentity instanceof TileEntityCommandBlock)
                            {
                                ((TileEntityCommandBlock) tileentity).setCommand(s);
                                this.playerEntity.worldObj.markBlockForUpdate(i, j, k);
                                this.playerEntity.sendChatToPlayer("Command set: " + s);
                            }
                        }
                        catch (Exception exception3)
                        {
                            // CraftBukkit start
                        	this.mcServer.getLogAgent().logSevereException(this.playerEntity.username + " sent invalid MC|AdvCdm data", exception3);
                            this.kickPlayerFromServer("Invalid CommandBlock data!");
                            // CraftBukkit end
                        }
                    }
                    else
                    {
                        this.playerEntity.sendChatToPlayer(this.playerEntity.translateString("advMode.notAllowed", new Object[0]));
                    }
                }
                else if ("MC|Beacon".equals(packet250custompayload.channel))
                {
                    if (this.playerEntity.openContainer instanceof ContainerBeacon)
                    {
                        try
                        {
                            datainputstream = new DataInputStream(new ByteArrayInputStream(packet250custompayload.data));
                            i = datainputstream.readInt();
                            j = datainputstream.readInt();
                            ContainerBeacon containerbeacon = (ContainerBeacon) this.playerEntity.openContainer;
                            Slot slot = containerbeacon.getSlot(0);

                            if (slot.getHasStack())
                            {
                                slot.decrStackSize(1);
                                TileEntityBeacon tileentitybeacon = containerbeacon.getBeacon();
                                tileentitybeacon.setPrimaryEffect(i);
                                tileentitybeacon.setSecondaryEffect(j);
                                tileentitybeacon.onInventoryChanged();
                            }
                        }
                        catch (Exception exception4)
                        {
                            // CraftBukkit start
                        	this.mcServer.getLogAgent().logSevereException(this.playerEntity.username + " sent invalid MC|Beacon data", exception4);
                            this.kickPlayerFromServer("Invalid beacon data!");
                            // CraftBukkit end
                        }
                    }
                }
                else if ("MC|ItemName".equals(packet250custompayload.channel) && this.playerEntity.openContainer instanceof ContainerRepair)
                {
                    ContainerRepair containeranvil = (ContainerRepair) this.playerEntity.openContainer;

                    if (packet250custompayload.data != null && packet250custompayload.data.length >= 1)
                    {
                        String s1 = ChatAllowedCharacters.filerAllowedCharacters(new String(packet250custompayload.data));

                        if (s1.length() <= 30)
                        {
                            containeranvil.updateItemName(s1);
                        }
                    }
                    else
                    {
                        containeranvil.updateItemName("");
                    }
                }
                // CraftBukkit start
                else
                {
                    server.getMessenger().dispatchIncomingMessage(ToBukkit.player(playerEntity), packet250custompayload.channel, packet250custompayload.data);
                }

                // CraftBukkit end
            }
        }
    }

    @Override

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(Packet131MapData par1Packet131MapData)
    {
        FMLNetworkHandler.handlePacket131Packet(this, par1Packet131MapData);
    }

    // modloader compat -- yuk!
    @Override
    public EntityPlayerMP getPlayer()
    {
        return playerEntity;
    }
}
