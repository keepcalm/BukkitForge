package keepcalm.mods.bukkit;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import keepcalm.mods.bukkit.asm.replacements.ServerConfigurationManager_BukkitForge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.*;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class CraftServerConfigurationManager {

    static public CraftServerConfigurationManager instance( MinecraftServer mc ) {
        if(instance == null)
        {
            instance = new CraftServerConfigurationManager(mc);
        }
        return instance;
    }

    static public CraftServerConfigurationManager instance() {
        return instance;
    }


    private ServerConfigurationManager_BukkitForge scm = null;

    static protected CraftServerConfigurationManager  instance = null;//new CraftServerConfigurationManager()

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public static final Logger logger = Logger.getLogger("Minecraft");
    private final MinecraftServer mcServer;
    public final List playerEntityList = new ArrayList();


    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));

    private Set ops = new HashSet();

    private Set whiteListedPlayers = new HashSet();

    private boolean whiteListEnforced;

    protected int maxPlayers = 0;

    protected int viewDistance;
    private EnumGameType gameType;
    private boolean commandsAllowedForAll;
    private int playerPingIndex = 0;
    IPlayerFileData playerNBTManagerObj;

    public CraftServerConfigurationManager(MinecraftServer par1MinecraftServer)
    {
       // scm = mgr;
        mcServer = par1MinecraftServer;
        bannedPlayers.setListActive(false);
        bannedIPs.setListActive(false);
        maxPlayers = 8;
        viewDistance = 10;
    }

    public void initializeConnectionToPlayer(INetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP)
    {
        readPlayerDataFromFile(par2EntityPlayerMP);
        par2EntityPlayerMP.setWorld(mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
        par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer)par2EntityPlayerMP.worldObj);
        String var3 = "local";

        if (par1INetworkManager.getSocketAddress() != null)
        {
            var3 = par1INetworkManager.getSocketAddress().toString();
        }

        logger.info(par2EntityPlayerMP.username + "[" + var3 + "] logged in with entity id " + par2EntityPlayerMP.entityId + " at (" + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
        WorldServer var4 = mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
        ChunkCoordinates var5 = var4.getSpawnPoint();
        func_72381_a(par2EntityPlayerMP, (EntityPlayerMP)null, var4);
        NetServerHandler var6 = new CraftNetServerHandler(mcServer, par1INetworkManager, par2EntityPlayerMP);
        var6.sendPacketToPlayer(new Packet1Login(par2EntityPlayerMP.entityId, var4.getWorldInfo().getTerrainType(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), var4.getWorldInfo().isHardcoreModeEnabled(), var4.provider.dimensionId, var4.difficultySetting, var4.getHeight(), maxPlayers));
        var6.sendPacketToPlayer(new Packet6SpawnPosition(var5.posX, var5.posY, var5.posZ));
        var6.sendPacketToPlayer(new Packet202PlayerAbilities(par2EntityPlayerMP.capabilities));
        var6.sendPacketToPlayer(new Packet16BlockItemSwitch(par2EntityPlayerMP.inventory.currentItem));
        updateTimeAndWeatherForPlayer(par2EntityPlayerMP, var4);
        sendPacketToAllPlayers(new Packet3Chat("§e" + par2EntityPlayerMP.username + " joined the game."));
        playerLoggedIn(par2EntityPlayerMP);
        var6.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
        mcServer.getNetworkThread().addPlayer(var6);
        var6.sendPacketToPlayer(new Packet4UpdateTime(var4.getTotalWorldTime(), var4.getWorldTime(), true));

        if (mcServer.getTexturePack().length() > 0)
        {
            par2EntityPlayerMP.requestTexturePackLoad(mcServer.getTexturePack(), mcServer.textureSize());
        }

        Iterator var7 = par2EntityPlayerMP.getActivePotionEffects().iterator();

        while (var7.hasNext())
        {
            PotionEffect var8 = (PotionEffect)var7.next();
            var6.sendPacketToPlayer(new Packet41EntityEffect(par2EntityPlayerMP.entityId, var8));
        }

        par2EntityPlayerMP.addSelfToInternalCraftingInventory();

        FMLNetworkHandler.handlePlayerLogin(par2EntityPlayerMP, var6, par1INetworkManager);
    }

    public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer)
    {
        playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getSaveHandler();
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }


    public void func_72375_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        WorldServer var3 = par1EntityPlayerMP.getServerForPlayer();

        if (par2WorldServer != null)
        {
            par2WorldServer.getPlayerManager().removePlayer(par1EntityPlayerMP);
        }

        var3.getPlayerManager().addPlayer(par1EntityPlayerMP);
        var3.theChunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);
    }

    public int getEntityViewDistance()
    {
        return PlayerManager.getFurthestViewableBlock(getViewDistance());
    }

    public void readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP)
    {
        NBTTagCompound var2 = mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();

        if ((par1EntityPlayerMP.getCommandSenderName().equals(mcServer.getServerOwner())) && (var2 != null))
        {
            par1EntityPlayerMP.readFromNBT(var2);
        }
        else
        {
            playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
        }
    }

    public void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
    }

    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP)
    {
        sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.username, true, 1000));
        playerEntityList.add(par1EntityPlayerMP);
        WorldServer var2 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        var2.spawnEntityInWorld(par1EntityPlayerMP);
        func_72375_a(par1EntityPlayerMP, (WorldServer)null);

        for (int var3 = 0; var3 < playerEntityList.size(); var3++)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)playerEntityList.get(var3);
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(var4.username, true, var4.ping));
        }
    }

    public void serverUpdateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(par1EntityPlayerMP);
    }

    public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP)
    {
        GameRegistry.onPlayerLogout(par1EntityPlayerMP);
        writePlayerData(par1EntityPlayerMP);
        WorldServer var2 = par1EntityPlayerMP.getServerForPlayer();
        var2.removeEntity(par1EntityPlayerMP);
        var2.getPlayerManager().removePlayer(par1EntityPlayerMP);
        playerEntityList.remove(par1EntityPlayerMP);
        sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.username, false, 9999));
    }

    public String allowUserToConnect(SocketAddress par1SocketAddress, String par2Str)
    {
        if (bannedPlayers.isBanned(par2Str))
        {
            BanEntry var6 = (BanEntry)bannedPlayers.getBannedList().get(par2Str);
            String var7 = "You are banned from this server!\nReason: " + var6.getBanReason();

            if (var6.getBanEndDate() != null)
            {
                var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
            }

            return var7;
        }
        if (!isAllowedToLogin(par2Str))
        {
            return "You are not white-listed on this server!";
        }

        String var3 = par1SocketAddress.toString();
        var3 = var3.substring(var3.indexOf("/") + 1);
        var3 = var3.substring(0, var3.indexOf(":"));

        if (bannedIPs.isBanned(var3))
        {
            BanEntry var4 = (BanEntry)bannedIPs.getBannedList().get(var3);
            String var5 = "Your IP address is banned from this server!\nReason: " + var4.getBanReason();

            if (var4.getBanEndDate() != null)
            {
                var5 = var5 + "\nYour ban will be removed on " + dateFormat.format(var4.getBanEndDate());
            }

            return var5;
        }

        return playerEntityList.size() >= maxPlayers ? "The server is full!" : null;
    }

    public EntityPlayerMP createPlayerForUser(String par1Str)
    {
        ArrayList var2 = new ArrayList();

        for (int var3 = 0; var3 < playerEntityList.size(); var3++)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)playerEntityList.get(var3);

            if (var4.username.equalsIgnoreCase(par1Str))
            {
                var2.add(var4);
            }
        }

        Iterator var5 = var2.iterator();

        while (var5.hasNext())
        {
            EntityPlayerMP var4 = (EntityPlayerMP)var5.next();
            var4.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }
        Object var6;
        if (mcServer.isDemo())
        {
            var6 = new DemoWorldManager(mcServer.worldServerForDimension(0));
        }
        else
        {
            var6 = new ItemInWorldManager(mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(mcServer, mcServer.worldServerForDimension(0), par1Str, (ItemInWorldManager)var6);
    }

    public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        World world = mcServer.worldServerForDimension(par2);
        if (world == null)
        {
            par2 = 0;
        }
        else if (!world.provider.canRespawnHere())
        {
            par2 = world.provider.getRespawnDimension(par1EntityPlayerMP);
        }

        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
        playerEntityList.remove(par1EntityPlayerMP);
        mcServer.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);
        ChunkCoordinates var4 = par1EntityPlayerMP.getBedLocation();
        boolean var5 = par1EntityPlayerMP.isSpawnForced();
        par1EntityPlayerMP.dimension = par2;
        Object var6;
        if (mcServer.isDemo())
        {
            var6 = new DemoWorldManager(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }
        else
        {
            var6 = new ItemInWorldManager(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }

        EntityPlayerMP var7 = new EntityPlayerMP(mcServer, mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), par1EntityPlayerMP.username, (ItemInWorldManager)var6);
        var7.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
        var7.clonePlayer(par1EntityPlayerMP, par3);
        var7.dimension = par2;
        var7.entityId = par1EntityPlayerMP.entityId;
        WorldServer var8 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        func_72381_a(var7, par1EntityPlayerMP, var8);

        if (var4 != null)
        {
            ChunkCoordinates var9 = EntityPlayer.verifyRespawnCoordinates(mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), var4, var5);

            if (var9 != null)
            {
                var7.setLocationAndAngles(var9.posX + 0.5F, var9.posY + 0.1F, var9.posZ + 0.5F, 0.0F, 0.0F);
                var7.setSpawnChunk(var4, var5);
            }
            else
            {
                var7.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(0, 0));
            }
        }

        var8.theChunkProviderServer.loadChunk((int)var7.posX >> 4, (int)var7.posZ >> 4);

        while (!var8.getCollidingBoundingBoxes(var7, var7.boundingBox).isEmpty())
        {
            var7.setPosition(var7.posX, var7.posY + 1.0D, var7.posZ);
        }

        var7.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(var7.dimension, (byte)var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.worldObj.getHeight(), var7.theItemInWorldManager.getGameType()));
        ChunkCoordinates var9 = var8.getSpawnPoint();
        var7.playerNetServerHandler.setPlayerLocation(var7.posX, var7.posY, var7.posZ, var7.rotationYaw, var7.rotationPitch);
        var7.playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(var9.posX, var9.posY, var9.posZ));
        var7.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(var7.experience, var7.experienceTotal, var7.experienceLevel));
        updateTimeAndWeatherForPlayer(var7, var8);
        var8.getPlayerManager().addPlayer(var7);
        var8.spawnEntityInWorld(var7);
        playerEntityList.add(var7);
        var7.addSelfToInternalCraftingInventory();
        GameRegistry.onPlayerRespawn(var7);
        return var7;
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        transferPlayerToDimension(par1EntityPlayerMP, par2, mcServer.worldServerForDimension(par2).getDefaultTeleporter());
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
    {
        int var3 = par1EntityPlayerMP.dimension;
        WorldServer var4 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer var5 = mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);

        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(par1EntityPlayerMP.dimension, (byte)par1EntityPlayerMP.worldObj.difficultySetting, var5.getWorldInfo().getTerrainType(), var5.getHeight(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        var4.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        transferEntityToWorld((Entity)par1EntityPlayerMP, var3, var4, var5, teleporter);
        func_72375_a(par1EntityPlayerMP, var4);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(var5);
        updateTimeAndWeatherForPlayer(par1EntityPlayerMP, var5);
        syncPlayerInventory(par1EntityPlayerMP);
        Iterator var6 = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (var6.hasNext())
        {
            PotionEffect var7 = (PotionEffect)var6.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(par1EntityPlayerMP.entityId, var7));
        }

        GameRegistry.onPlayerChangedDimension(par1EntityPlayerMP);
    }

    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
    {
        transferEntityToWorld(par1Entity, par2, par3WorldServer, par4WorldServer, par4WorldServer.getDefaultTeleporter());
    }

    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter)
    {
        WorldProvider pOld = par3WorldServer.provider;
        WorldProvider pNew = par4WorldServer.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double var5 = par1Entity.posX * moveFactor;
        double var7 = par1Entity.posZ * moveFactor;
        double var11 = par1Entity.posX;
        double var13 = par1Entity.posY;
        double var15 = par1Entity.posZ;
        float var17 = par1Entity.rotationYaw;
        par3WorldServer.theProfiler.startSection("moving");

        if (par1Entity.dimension == 1)
        {
            ChunkCoordinates var18;
            if (par2 == 1)
            {
                var18 = par4WorldServer.getSpawnPoint();
            }
            else
            {
                var18 = par4WorldServer.getEntrancePortalLocation();
            }

            var5 = var18.posX;
            par1Entity.posY = var18.posY;
            var7 = var18.posZ;
            par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (par2 != 1)
        {
            par3WorldServer.theProfiler.startSection("placing");
            var5 = MathHelper.clamp_int((int) var5, -29999872, 29999872);
            var7 = MathHelper.clamp_int((int)var7, -29999872, 29999872);

            if (par1Entity.isEntityAlive())
            {
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par1Entity.setLocationAndAngles(var5, par1Entity.posY, var7, par1Entity.rotationYaw, par1Entity.rotationPitch);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
                teleporter.placeInPortal(par1Entity, var11, var13, var15, var17);
            }

            par3WorldServer.theProfiler.endSection();
        }

        par1Entity.setWorld(par4WorldServer);
    }

    public void sendPlayerInfoToAllPlayers()
    {
        if (++playerPingIndex > 600)
        {
            playerPingIndex = 0;
        }

        if (playerPingIndex < playerEntityList.size())
        {
            EntityPlayerMP var1 = (EntityPlayerMP)playerEntityList.get(playerPingIndex);
            sendPacketToAllPlayers(new Packet201PlayerInfo(var1.username, true, var1.ping));
        }
    }

    public void sendPacketToAllPlayers(Packet par1Packet)
    {
        for (int var2 = 0; var2 < playerEntityList.size(); var2++)
        {
            ((EntityPlayerMP)playerEntityList.get(var2)).playerNetServerHandler.sendPacketToPlayer(par1Packet);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet par1Packet, int par2)
    {
        for (int var3 = 0; var3 < playerEntityList.size(); var3++)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)playerEntityList.get(var3);

            if (var4.dimension == par2)
            {
                var4.playerNetServerHandler.sendPacketToPlayer(par1Packet);
            }
        }
    }

    public String getPlayerListAsString()
    {
        String var1 = "";

        for (int var2 = 0; var2 < playerEntityList.size(); var2++)
        {
            if (var2 > 0)
            {
                var1 = var1 + ", ";
            }

            var1 = var1 + ((EntityPlayerMP)playerEntityList.get(var2)).username;
        }

        return var1;
    }

    public String[] getAllUsernames()
    {
        String[] var1 = new String[playerEntityList.size()];

        for (int var2 = 0; var2 < playerEntityList.size(); var2++)
        {
            var1[var2] = ((EntityPlayerMP)playerEntityList.get(var2)).username;
        }

        return var1;
    }

    public BanList getBannedPlayers()
    {
        return bannedPlayers;
    }

    public BanList getBannedIPs()
    {
        return bannedIPs;
    }

    public void addOp(String par1Str)
    {
        ops.add(par1Str.toLowerCase());
    }

    public void removeOp(String par1Str)
    {
        ops.remove(par1Str.toLowerCase());
    }

    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return (!whiteListEnforced) || (ops.contains(par1Str)) || (whiteListedPlayers.contains(par1Str));
    }

    public boolean areCommandsAllowed(String par1Str)
    {
        return (ops.contains(par1Str.trim().toLowerCase())) || ((mcServer.isSinglePlayer()) && (mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()) && (mcServer.getServerOwner().equalsIgnoreCase(par1Str))) || (commandsAllowedForAll);
    }

    public EntityPlayerMP getPlayerForUsername(String par1Str)
    {
        Iterator var2 = playerEntityList.iterator();
        EntityPlayerMP var3;
        do
        {
            if (!var2.hasNext())
            {
                return null;
            }

            var3 = (EntityPlayerMP)var2.next();
        }
        while (!var3.username.equalsIgnoreCase(par1Str));

        return var3;
    }

    public List findPlayers(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        if (playerEntityList.isEmpty())
        {
            return null;
        }

        Object var8 = new ArrayList();
        boolean var9 = par4 < 0;
        int var10 = par2 * par2;
        int var11 = par3 * par3;
        par4 = MathHelper.abs_int(par4);

        for (int var12 = 0; var12 < playerEntityList.size(); var12++)
        {
            EntityPlayerMP var13 = (EntityPlayerMP)playerEntityList.get(var12);

            if ((par1ChunkCoordinates != null) && ((par2 > 0) || (par3 > 0)))
            {
                float var14 = par1ChunkCoordinates.getDistanceSquaredToChunkCoordinates(var13.getPlayerCoordinates());

                if (((par2 > 0) && (var14 < var10)) || ((par3 > 0) && (var14 > var11)));
            }
            else if (((par5 == EnumGameType.NOT_SET.getID()) || (par5 == var13.theItemInWorldManager.getGameType().getID())) && ((par6 <= 0) || (var13.experienceLevel >= par6)) && (var13.experienceLevel <= par7))
            {
                ((List)var8).add(var13);
            }
        }

        if (par1ChunkCoordinates != null)
        {
            Collections.sort((List)var8, new PlayerPositionComparator(par1ChunkCoordinates));
        }

        if (var9)
        {
            Collections.reverse((List)var8);
        }

        if (par4 > 0)
        {
            var8 = ((List)var8).subList(0, Math.min(par4, ((List)var8).size()));
        }

        return (List)var8;
    }

    public void sendToAllNear(double par1, double par3, double par5, double par7, int par9, Packet par10Packet)
    {
        sendToAllNearExcept((EntityPlayer)null, par1, par3, par5, par7, par9, par10Packet);
    }

    public void sendToAllNearExcept(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet)
    {
        for (int var12 = 0; var12 < playerEntityList.size(); var12++)
        {
            EntityPlayerMP var13 = (EntityPlayerMP)playerEntityList.get(var12);

            if ((var13 != par1EntityPlayer) && (var13.dimension == par10))
            {
                double var14 = par2 - var13.posX;
                double var16 = par4 - var13.posY;
                double var18 = par6 - var13.posZ;

                if (var14 * var14 + var16 * var16 + var18 * var18 < par8 * par8)
                {
                    var13.playerNetServerHandler.sendPacketToPlayer(par11Packet);
                }
            }
        }
    }

    public void saveAllPlayerData()
    {
        for (int var1 = 0; var1 < playerEntityList.size(); var1++)
        {
            writePlayerData((EntityPlayerMP)playerEntityList.get(var1));
        }
    }

    public void addToWhiteList(String par1Str)
    {
        whiteListedPlayers.add(par1Str);
    }

    public void removeFromWhitelist(String par1Str)
    {
        whiteListedPlayers.remove(par1Str);
    }

    public Set getWhiteListedPlayers()
    {
        return whiteListedPlayers;
    }

    public Set getOps()
    {
        return ops;
    }

    public void loadWhiteList()
    {
    }

    public void updateTimeAndWeatherForPlayer(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet4UpdateTime(par2WorldServer.getTotalWorldTime(), par2WorldServer.getWorldTime(), true));

        if (par2WorldServer.isRaining())
        {
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(1, 0));
        }
    }

    public void syncPlayerInventory(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.sendContainerToPlayer(par1EntityPlayerMP.inventoryContainer);
        par1EntityPlayerMP.setPlayerHealthUpdated();
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet16BlockItemSwitch(par1EntityPlayerMP.inventory.currentItem));
    }

    public int getCurrentPlayerCount()
    {
        return playerEntityList.size();
    }

    public String[] getAvailablePlayerDat()
    {
        return mcServer.worldServers[0].getSaveHandler().getSaveHandler().getAvailablePlayerDat();
    }

    public boolean isWhiteListEnabled()
    {
        return whiteListEnforced;
    }

    public void setWhiteListEnabled(boolean par1)
    {
        whiteListEnforced = par1;
    }

    public List getPlayerList(String par1Str)
    {
        ArrayList var2 = new ArrayList();
        Iterator var3 = playerEntityList.iterator();

        while (var3.hasNext())
        {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();

            if (var4.getPlayerIP().equals(par1Str))
            {
                var2.add(var4);
            }
        }

        return var2;
    }

    public int getViewDistance()
    {
        return viewDistance;
    }

    public MinecraftServer getServerInstance()
    {
        return mcServer;
    }

    public NBTTagCompound getTagsFromLastWrite()
    {
        return null;
    }

    public void func_72381_a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World)
    {
        if (par2EntityPlayerMP != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(par2EntityPlayerMP.theItemInWorldManager.getGameType());
        }
        else if (gameType != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(gameType);
        }

        par1EntityPlayerMP.theItemInWorldManager.initializeGameType(par3World.getWorldInfo().getGameType());
    }

    public void removeAllPlayers()
    {
        while (!playerEntityList.isEmpty())
        {
            ((EntityPlayerMP)playerEntityList.get(0)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    public void sendChatMsg(String par1Str)
    {
        mcServer.logInfo(par1Str);
        sendPacketToAllPlayers(new Packet3Chat(par1Str));
    }


}
