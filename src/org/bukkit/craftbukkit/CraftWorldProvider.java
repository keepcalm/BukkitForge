package org.bukkit.craftbukkit;

import net.minecraft.world.WorldProvider;

public class CraftWorldProvider extends WorldProvider
{
    String dimName;

    public void setName( String name )
    {
        dimName = name;
    }

    @Override
    public String getDimensionName() {
        // TODO: Fix this to get stored dimension name
        return (dimName != null)?dimName:getSaveFolder();
    }

    @Override
    public String getSaveFolder()    {
        return "DIM_BF" + dimensionId;
    }

    public static int ProviderID = 101;
}
