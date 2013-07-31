package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.EnumArt;
import net.minecraft.world.WorldServer;

import org.bukkit.Art;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.CraftArt;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
//import org.bukkit.craftbukkit.v1_6_R2.CraftArt;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
//import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;

public class CraftPainting extends CraftEntity implements Painting {

    public CraftPainting(CraftServer server, EntityPainting entity) {
        super(server, entity);
    }

    public Art getArt() {
        EnumArt art = getHandle().art;
        return CraftArt.NotchToCraft(art);
    }

    public boolean setArt(Art art) {
        return setArt(art, false);
    }

    public boolean setArt(Art art, boolean force) {
        EntityPainting painting = this.getHandle();
        EnumArt oldArt = painting.art;
        painting.art = CraftArt.CraftToNotch(art);
        
        if (!force && painting.isDead) {
            // Revert painting since it doesn't fit
            painting.art = oldArt;
        //    painting.setDirection(painting.direction);
            return false;
        }
        this.update();
        return true;
    }

    public BlockFace getAttachedFace() {
        return getFacing().getOppositeFace();
    }

    public void setFacingDirection(BlockFace face) {
        setFacingDirection(face, false);
    }

    public boolean setFacingDirection(BlockFace face, boolean force) {
        Block block = getLocation().getBlock().getRelative(getAttachedFace()).getRelative(face.getOppositeFace()).getRelative(getFacing());
        EntityPainting painting = getHandle();
        int x = (int) Math.round(painting.posX), y = (int) Math.round(painting.posY), z = (int) Math.round(painting.posZ), dir = painting.hangingDirection;
        painting.posX = block.getX();
        painting.posY = block.getY();
        painting.posZ = block.getZ();
        switch (face) {
        case EAST:
        default:
            getHandle().setDirection(0);
            break;
        case NORTH:
            getHandle().setDirection(1);
            break;
        case WEST:
            getHandle().setDirection(2);
            break;
        case SOUTH:
            getHandle().setDirection(3);
            break;
        }
        if (!force && painting.isDead) {
            // Revert painting since it doesn't fit
            painting.posX = x;
            painting.posY = y;
            painting.posZ = z;
            painting.setDirection(dir);
            return false;
        }
        this.update();
        return true;
    }

    public BlockFace getFacing() {
        switch (this.getHandle().hangingDirection) {
        case 0:
        default:
            return BlockFace.EAST;
        case 1:
            return BlockFace.NORTH;
        case 2:
            return BlockFace.WEST;
        case 3:
            return BlockFace.SOUTH;
        }
    }

    private void update() {
        WorldServer world = ((CraftWorld) getWorld()).getHandle();
        EntityPainting painting = new EntityPainting(world);
        painting.posX = getHandle().posX;
        painting.posY = getHandle().posY;
        painting.posZ = getHandle().posZ;
        painting.art = getHandle().art;
        painting.setDirection(getHandle().hangingDirection);
        getHandle().setDead();
        getHandle().velocityChanged = true; // because this occurs when the painting is broken, so it might be important
        world.spawnEntityInWorld(painting);
        this.entity = painting;
    }

    @Override
    public EntityPainting getHandle() {
        return (EntityPainting) entity;
    }

    @Override
    public String toString() {
        return "CraftPainting{art=" + getArt() + "}";
    }

    public EntityType getType() {
        return EntityType.PAINTING;
    }
}
