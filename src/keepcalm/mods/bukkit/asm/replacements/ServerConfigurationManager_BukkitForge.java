package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import keepcalm.mods.bukkit.CraftNetServerHandler;
import keepcalm.mods.bukkit.CraftServerConfigurationManager;
import keepcalm.mods.bukkit.ToBukkit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.IPlayerFileData;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class ServerConfigurationManager_BukkitForge {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public static final Logger logger = Logger.getLogger("Minecraft");
    private final MinecraftServer mcServer = null;
    public final List playerEntityList = new ArrayList();
    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));

    private Set ops = new HashSet();

    private Set whiteListedPlayers = new HashSet();
    public IPlayerFileData playerNBTManagerObj;
    private boolean whiteListEnforced;
    protected int maxPlayers;
    protected int viewDistance;
    private EnumGameType gameType;
    private boolean commandsAllowedForAll;
    private int playerPingIndex = 0;

    @AsmagicReplaceMethod
    public void a(INetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP)
    {
        readPlayerDataFromFile(par2EntityPlayerMP);
        par2EntityPlayerMP.setWorld(mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
        par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer)par2EntityPlayerMP.worldObj);
        String var3 = "local";

        if (par1INetworkManager.getSocketAddress() != null)
        {
            var3 = par1INetworkManager.getSocketAddress().toString();
        }

        //logger.info(par2EntityPlayerMP.username + "[" + var3 + "] logged in with entity id " + par2EntityPlayerMP.entityId + " at (" + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
        WorldServer var4 = mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
        ChunkCoordinates var5 = var4.getSpawnPoint();
        func_72381_a(par2EntityPlayerMP, (EntityPlayerMP)null, var4);
        NetServerHandler var6 = (NetServerHandler)(new CraftNetServerHandler(mcServer, par1INetworkManager, par2EntityPlayerMP));
        var6.sendPacketToPlayer(new Packet1Login(par2EntityPlayerMP.entityId, var4.getWorldInfo().getTerrainType(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), var4.getWorldInfo().isHardcoreModeEnabled(), var4.provider.dimensionId, var4.difficultySetting, var4.getHeight(), maxPlayers));
        var6.sendPacketToPlayer(new Packet6SpawnPosition(var5.posX, var5.posY, var5.posZ));
        var6.sendPacketToPlayer(new Packet202PlayerAbilities(par2EntityPlayerMP.capabilities));
        var6.sendPacketToPlayer(new Packet16BlockItemSwitch(par2EntityPlayerMP.inventory.currentItem));
        updateTimeAndWeatherForPlayer(par2EntityPlayerMP, var4);
        sendPacketToAllPlayers(new Packet3Chat("§e" + par2EntityPlayerMP.username + " joined the game."));
        playerLoggedIn(par2EntityPlayerMP);
        var6.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
        mcServer.getNetworkThread().addPlayer(var6);
        var6.sendPacketToPlayer(new Packet4UpdateTime(var4.getTotalWorldTime(), var4.getWorldTime()));

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

    public void playerLoggedIn(EntityPlayerMP par2EntityPlayerMP)
    {

    }

    private void updateTimeAndWeatherForPlayer(EntityPlayerMP par2EntityPlayerMP, WorldServer var4) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void sendPacketToAllPlayers(Packet3Chat packet3Chat) {

    }

    private void func_72381_a(EntityPlayerMP par2EntityPlayerMP, EntityPlayerMP entityPlayerMP, WorldServer var4) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void readPlayerDataFromFile(EntityPlayerMP par2EntityPlayerMP) {
    }

    /*
    @AsmagicMethodReplace
    public void a(WorldServer[] par1ArrayOfWorldServer)
    {
        CraftServerConfigurationManager.instance().setPlayerManager(par1ArrayOfWorldServer);
    }

    @AsmagicMethodReplace
    public void a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        CraftServerConfigurationManager.instance().func_72375_a(par1EntityPlayerMP, par2WorldServer);
    }

    @AsmagicMethodReplace
    public int a()
    {
        return CraftServerConfigurationManager.instance().getEntityViewDistance();
    }

    @AsmagicMethodReplace
    public void a(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().readPlayerDataFromFile(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    protected void b(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().writePlayerData(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    public void c(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().playerLoggedIn(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    public void d(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().serverUpdateMountedMovingPlayer(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    public void e(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().playerLoggedOut(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    public String a(SocketAddress par1SocketAddress, String par2Str)
    {
        return CraftServerConfigurationManager.instance().allowUserToConnect(par1SocketAddress,par2Str);
    }

    @AsmagicMethodReplace
    public EntityPlayerMP a(String par1Str)
    {
        return CraftServerConfigurationManager.instance().createPlayerForUser(par1Str);
    }

    @AsmagicMethodReplace
    public EntityPlayerMP a(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        return CraftServerConfigurationManager.instance().respawnPlayer(par1EntityPlayerMP, par2, par3);
    }

    @AsmagicMethodReplace
    public void a(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        CraftServerConfigurationManager.instance().transferPlayerToDimension(par1EntityPlayerMP, par2);
    }

    @AsmagicMethodReplace
    public void a(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
    {
        CraftServerConfigurationManager.instance().transferPlayerToDimension(par1EntityPlayerMP, par2, teleporter);
    }

    @AsmagicMethodReplace
    public void a(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
    {
        CraftServerConfigurationManager.instance().transferEntityToWorld(par1Entity,par2,par3WorldServer,par4WorldServer);
    }

    @AsmagicMethodReplace
    public void a(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter)
    {
        CraftServerConfigurationManager.instance().transferEntityToWorld(par1Entity,par2,par3WorldServer,par4WorldServer,teleporter);
    }

    @AsmagicMethodReplace
    public void b()
    {
        CraftServerConfigurationManager.instance().sendPlayerInfoToAllPlayers();
    }

    @AsmagicMethodReplace
    public void a(Packet par1Packet)
    {
        CraftServerConfigurationManager.instance().sendPacketToAllPlayers(par1Packet);
    }

    @AsmagicMethodReplace
    public void a(Packet par1Packet, int par2)
    {
        CraftServerConfigurationManager.instance().sendPacketToAllPlayersInDimension(par1Packet,par2);
    }

    @AsmagicMethodReplace
    public String c()
    {
        return CraftServerConfigurationManager.instance().getPlayerListAsString();
    }

    @AsmagicMethodReplace
    public String[] d()
    {
        return CraftServerConfigurationManager.instance().getAllUsernames();
    }

    @AsmagicMethodReplace
    public BanList e()
    {
        return CraftServerConfigurationManager.instance().getBannedPlayers();
    }

    @AsmagicMethodReplace
    public BanList f()
    {
        return CraftServerConfigurationManager.instance().getBannedIPs();
    }

    @AsmagicMethodReplace
    public void b(String par1Str)
    {
        CraftServerConfigurationManager.instance().addOp(par1Str);
    }

    @AsmagicMethodReplace
    public void c(String par1Str)
    {
        CraftServerConfigurationManager.instance().removeOp(par1Str);
    }

    @AsmagicMethodReplace
    public boolean d(String par1Str)
    {
        return CraftServerConfigurationManager.instance().isAllowedToLogin(par1Str);
    }

    @AsmagicMethodReplace
    public boolean e(String par1Str)
    {
        return CraftServerConfigurationManager.instance().areCommandsAllowed(par1Str);
    }

    @AsmagicMethodReplace
    public EntityPlayerMP f(String par1Str)
    {
        return CraftServerConfigurationManager.instance().getPlayerForUsername(par1Str);
    }

    @AsmagicMethodReplace
    public List a(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        return CraftServerConfigurationManager.instance().findPlayers(par1ChunkCoordinates,par2,par3,par4,par5,par6,par7);
    }

    @AsmagicMethodReplace
    public void a(double par1, double par3, double par5, double par7, int par9, Packet par10Packet)
    {
        CraftServerConfigurationManager.instance().sendToAllNear(par1,par3,par5,par7,par9,par10Packet);
    }

    @AsmagicMethodReplace
    public void a(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet)
    {
        CraftServerConfigurationManager.instance().sendToAllNearExcept(par1EntityPlayer,par2,par4, par6,par8,par10,par11Packet);
    }

    @AsmagicMethodReplace
    public void g()
    {
        CraftServerConfigurationManager.instance().saveAllPlayerData();
    }

    @AsmagicMethodReplace
    public void h(String par1Str)
    {
        CraftServerConfigurationManager.instance().addToWhiteList(par1Str);
    }

    @AsmagicMethodReplace
    public void i(String par1Str)
    {
        CraftServerConfigurationManager.instance().removeFromWhitelist(par1Str);
    }

    @AsmagicMethodReplace
    public Set h()
    {
        return CraftServerConfigurationManager.instance().getWhiteListedPlayers();
    }

    @AsmagicMethodReplace
    public Set i()
    {
        return CraftServerConfigurationManager.instance().getOps();
    }

    @AsmagicMethodReplace
    public void j()
    {
        CraftServerConfigurationManager.instance().loadWhiteList();
    }

    @AsmagicMethodReplace
    public void b(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        CraftServerConfigurationManager.instance().updateTimeAndWeatherForPlayer(par1EntityPlayerMP,par2WorldServer);
    }

    @AsmagicMethodReplace
    public void f(EntityPlayerMP par1EntityPlayerMP)
    {
        CraftServerConfigurationManager.instance().syncPlayerInventory(par1EntityPlayerMP);
    }

    @AsmagicMethodReplace
    public int k()
    {
        return CraftServerConfigurationManager.instance().getCurrentPlayerCount();
    }

    @AsmagicMethodReplace
    public int l()
    {
        return CraftServerConfigurationManager.instance().getMaxPlayers();
    }

    @AsmagicMethodReplace
    public String[] m()
    {
        return CraftServerConfigurationManager.instance().getAvailablePlayerDat();
    }

    @AsmagicMethodReplace
    public boolean n()
    {
        return CraftServerConfigurationManager.instance().isWhiteListEnabled();
    }

    @AsmagicMethodReplace
    public void a(boolean par1)
    {
        CraftServerConfigurationManager.instance().setWhiteListEnabled(par1);
    }

    @AsmagicMethodReplace
    public List j(String par1Str)
    {
        return CraftServerConfigurationManager.instance().getPlayerList(par1Str);
    }

    @AsmagicMethodReplace
    public int o()
    {
        return CraftServerConfigurationManager.instance().getViewDistance();
    }

    @AsmagicMethodReplace
    public MinecraftServer p()
    {
        return CraftServerConfigurationManager.instance().getServerInstance();
    }

    @AsmagicMethodReplace
    public NBTTagCompound q()
    {
        return CraftServerConfigurationManager.instance().getTagsFromLastWrite();
    }

    @AsmagicMethodReplace
    private void a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World)
    {
        CraftServerConfigurationManager.instance().func_72381_a(par1EntityPlayerMP, par2EntityPlayerMP, par3World);
    }

    @AsmagicMethodReplace
    public void r()
    {
        CraftServerConfigurationManager.instance().removeAllPlayers();
    }

    @AsmagicMethodReplace
    public void k(String par1Str)
    {
        CraftServerConfigurationManager.instance().sendChatMsg(par1Str);
    }                  */
}
