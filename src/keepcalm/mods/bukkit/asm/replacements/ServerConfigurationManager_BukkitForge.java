package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import keepcalm.mods.bukkit.CraftNetServerHandler;
import keepcalm.mods.bukkit.CraftServerConfigurationManager;
import keepcalm.mods.bukkit.ToBukkit;
import keepcalm.mods.bukkit.nmsforge.DimensionManagerImpl;
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
import net.minecraft.server.management.BanList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class ServerConfigurationManager_BukkitForge {


//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public static final Logger a = Logger.getLogger("Minecraft");
    private final MinecraftServer f = null;
    public final List b = new ArrayList();
    private final BanList getBannedIPs = new BanList(new File("banned-players.txt"));
    private final BanList h = new BanList(new File("banned-ips.txt"));

    private Set i = new HashSet();

    private Set j = new HashSet();
    public IPlayerFileData k;
    private boolean l;
    protected int c;
    protected int d;
    private EnumGameType m;
    private boolean n;
    private int o = 0;

    @AsmagicReplaceMethod
    public EntityPlayerMP a(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        World world = f.worldServerForDimension(par2);
        if (world == null)
        {
            par2 = 0;
        }
        else if (!world.provider.canRespawnHere())
        {
            par2 = world.provider.getRespawnDimension(par1EntityPlayerMP);
        }

        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeAllTrackingPlayers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
        b.remove(par1EntityPlayerMP);
        f.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);
        ChunkCoordinates var4 = par1EntityPlayerMP.getBedLocation();
        boolean var5 = par1EntityPlayerMP.isSpawnForced();
        par1EntityPlayerMP.dimension = par2;
        Object var6;
        if (f.isDemo())
        {
            var6 = new DemoWorldManager(f.worldServerForDimension(par1EntityPlayerMP.dimension));
        }
        else
        {
            var6 = new ItemInWorldManager(f.worldServerForDimension(par1EntityPlayerMP.dimension));
        }

        EntityPlayerMP var7 = new EntityPlayerMP(f, f.worldServerForDimension(par1EntityPlayerMP.dimension), par1EntityPlayerMP.username, (ItemInWorldManager)var6);
        var7.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
        var7.clonePlayer(par1EntityPlayerMP, par3);
        var7.dimension = par2;
        var7.entityId = par1EntityPlayerMP.entityId;
        WorldServer var8 = f.worldServerForDimension(par1EntityPlayerMP.dimension);
        a(var7, par1EntityPlayerMP, var8);

        if (var4 != null)
        {
            ChunkCoordinates var9 = EntityPlayer.verifyRespawnCoordinates(f.worldServerForDimension(par1EntityPlayerMP.dimension), var4, var5);

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

        int envId = var8.provider.isSurfaceWorld()?-1:0;
        int dimensionId = DimensionManagerImpl.getInstance().isCraftWorld(par2)?1:par2;

        var7.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(envId, (byte)var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.worldObj.getHeight(), var7.theItemInWorldManager.getGameType()));
        var7.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(dimensionId, (byte)var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.worldObj.getHeight(), var7.theItemInWorldManager.getGameType()));
        ChunkCoordinates var9 = var8.getSpawnPoint();
        var7.playerNetServerHandler.setPlayerLocation(var7.posX, var7.posY, var7.posZ, var7.rotationYaw, var7.rotationPitch);
        var7.playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(var9.posX, var9.posY, var9.posZ));
        var7.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(var7.experience, var7.experienceTotal, var7.experienceLevel));
        b(var7, var8);
        var8.getPlayerManager().addPlayer(var7);
        var8.spawnEntityInWorld(var7);
        b.add(var7);
        var7.addSelfToInternalCraftingInventory();
        GameRegistry.onPlayerRespawn(var7);
        return var7;
    }

    private void a(EntityPlayerMP var7, EntityPlayerMP par1EntityPlayerMP, World var8) {
    }

    public void b(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        CraftServerConfigurationManager.instance().updateTimeAndWeatherForPlayer(par1EntityPlayerMP,par2WorldServer);
    }

}
