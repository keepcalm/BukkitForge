package org.bukkit.craftbukkit.v1_5_R2.block;

import net.minecraft.block.Block;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R2.CraftChunk;

public class CraftBlockFake extends CraftBlock {
	
	private int blockID;
	private int data;
	private Block block;
	
	public CraftBlockFake(CraftChunk chunk, int x, int y, int z, int blockID, int data) {
		super(chunk, x, y, z);
		
		this.data = data;
		this.blockID = blockID;
		
		this.block = Block.blocksList[blockID];
		
		if (block == null) {
			throw new IllegalArgumentException("Block ID " + blockID + " is not occupied!");
		}
	}
	
	@Override
	public void setData(final byte data) {
		this.data = data;
	}
	
	public byte getData() {
		return (byte) this.data;
	}
	
	@Override
	public void setData(final byte data, boolean applyPhysics) {
		setData(data);
	}
	
	public void setType(final Material type) {
		setTypeId(type.getId());
	}

	public boolean setTypeId(final int type) {
		this.blockID = type;
		return true;
	}

	public boolean setTypeId(final int type, final boolean applyPhysics) {
			return setTypeId(type);
	}

	public boolean setTypeIdAndData(final int type, final byte data, final boolean applyPhysics) {
		setTypeId(type);
		setData(data);
		
		return true;
	}

	public Material getType() {
		return Material.getMaterial(getTypeId());
	}

	public int getTypeId() {
		return blockID;
	}
	
	
	
	
	
}