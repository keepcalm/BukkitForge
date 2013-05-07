package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicAddField;
import com.eoware.asm.asmagic.AsmagicAddMethod;
import com.eoware.asm.asmagic.AsmagicReplaceMethod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class Block_BukkitForge extends Block {
	
	@AsmagicAddField
	public boolean isForgeBlock; // MCPC+  
	
	@AsmagicAddMethod//(replaceType = 1)
	public Block_BukkitForge(int par1, Material par2Material)
    {
		super(par1, par2Material);
        org.bukkit.Material.addMaterial(par1); // MCPC+ - many mods do not register blocks through GameRegistry so to be safe we need to add materials here
        this.isForgeBlock = (this.getClass().getName().length() > 3 && !this.getClass().getName().startsWith("net.minecraft.block")) ? true : false; // MCPC+
    }
	
}
