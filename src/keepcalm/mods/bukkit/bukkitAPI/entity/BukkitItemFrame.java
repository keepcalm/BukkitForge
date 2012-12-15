package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

public class BukkitItemFrame extends BukkitHanging implements ItemFrame {
    public BukkitItemFrame(BukkitServer server, EntityItemFrame entity) {
        super(server, entity);
    }

    public void setItem(org.bukkit.inventory.ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
        	// guesses, TODO check this is right
            getHandle().getDataWatcher().addObjectByDataType(2, 5);
            getHandle().getDataWatcher().func_82708_h(2);
        } else {
            getHandle().setDisplayedItem(BukkitItemStack.createNMSItemStack(item));
        }
    }

    public org.bukkit.inventory.ItemStack getItem() {
        ItemStack i = getHandle().getDisplayedItem();
        return i == null ? new org.bukkit.inventory.ItemStack(Material.AIR) : new BukkitItemStack(i);
    }

    public Rotation getRotation() {
        return toBukkitRotation(getHandle().getRotation());
    }

    Rotation toBukkitRotation(int value) {
        // Translate NMS rotation integer to Bukkit API
        switch (value) {
        case 0:
            return Rotation.NONE;
        case 1:
            return Rotation.CLOCKWISE;
        case 2:
            return Rotation.FLIPPED;
        case 3:
            return Rotation.COUNTER_CLOCKWISE;
        default:
            throw new AssertionError("Unknown rotation " + getHandle().getRotation() + " for " + getHandle());
        }
    }

    public void setRotation(Rotation rotation) {
        Validate.notNull(rotation, "Rotation cannot be null");
        getHandle().setItemRotation(toInteger(rotation));
    }

    static int toInteger(Rotation rotation) {
        // Translate Bukkit API rotation to NMS integer
        switch (rotation) {
        case NONE:
            return 0;
        case CLOCKWISE:
            return 1;
        case FLIPPED:
            return 2;
        case COUNTER_CLOCKWISE:
            return 3;
        default:
            throw new IllegalArgumentException(rotation + " is not applicable to an ItemFrame");
        }
    }

    @Override
    public EntityItemFrame getHandle() {
        return (EntityItemFrame) entity;
    }

    @Override
    public String toString() {
        return "BukkitItemFrame{item=" + getItem() + ", rotation=" + getRotation() + "}";
    }

    public EntityType getType() {
        return EntityType.ITEM_FRAME;
    }
}
