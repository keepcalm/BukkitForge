package keepcalm.mods.bukkitforge;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/8/13
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class BukkitForgeWorldServer extends WorldServer {
    public BukkitForgeWorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, Profiler par6Profiler, WorldCreator creator, WorldServer baseWorld) {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, new WorldSettings(baseWorld.getWorldInfo()), par6Profiler, null);

        super.chunkProvider = new CustomChunkGenerator( this, creator.seed(), creator.generator() );

        mapStorage = baseWorld.mapStorage;
        worldInfo = new DerivedWorldInfo(baseWorld.getWorldInfo());

    }
}
