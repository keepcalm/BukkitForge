package keepcalm.mods.bukkitforge;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.ComponentStrongholdRightTurn;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_6_R2.generator.CustomChunkGenerator;

public class BukkitForgeWorldProvider extends WorldProvider {

    protected WorldProvider wp;

    public BukkitForgeWorldProvider(WorldProvider providerToWrap, WorldCreator worldCreator)
    {
        wp = providerToWrap;
        wc = worldCreator;

        worldObj = wp.worldObj;
        terrainType = wp.terrainType;
        field_82913_c = wp.field_82913_c;
        worldChunkMgr = wp.worldChunkMgr;
        isHellWorld = wp.isHellWorld;
        hasNoSky = wp.hasNoSky;
        lightBrightnessTable = wp.lightBrightnessTable;
        dimensionId = wp.dimensionId;
    }

    private String name;
    private WorldCreator wc;

    public void setDimensionName( String dimName )
    {
        name = dimName;
    }

    @Override
    public String getDimensionName() {
        return name;
    }

    @Override
    public java.lang.String getSaveFolder() {
        return "DIM_BF" + dimensionId;
    }

    @Override
    public IChunkProvider createChunkGenerator()
    {
        return new CustomChunkGenerator( this.worldObj, dimensionId, wc.generator());
    }


}
