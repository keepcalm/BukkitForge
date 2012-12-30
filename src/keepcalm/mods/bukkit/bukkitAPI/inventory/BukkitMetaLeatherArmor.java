package keepcalm.mods.bukkit.bukkitAPI.inventory;

import java.util.Map;

import static keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitItemFactory.DEFAULT_LEATHER_COLOR;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitMetaItem.SerializableMeta;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class BukkitMetaLeatherArmor extends BukkitMetaItem implements LeatherArmorMeta {
    static final ItemMetaKey COLOR = new ItemMetaKey("color");

    private Color color = DEFAULT_LEATHER_COLOR;

    BukkitMetaLeatherArmor(BukkitMetaItem meta) {
        super(meta);
        if (!(meta instanceof BukkitMetaLeatherArmor)) {
            return;
        }

        BukkitMetaLeatherArmor armorMeta = (BukkitMetaLeatherArmor) meta;
        this.color = armorMeta.color;
    }

    BukkitMetaLeatherArmor(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey(DISPLAY.NBT)) {
            NBTTagCompound display = tag.getCompoundTag(DISPLAY.NBT);
            if (display.hasKey(COLOR.NBT)) {
                color = Color.fromRGB(display.getInteger(COLOR.NBT));
            }
        }
    }

    BukkitMetaLeatherArmor(Map<String, Object> map) {
        super(map);
        setColor(SerializableMeta.getObject(Color.class, map, COLOR.BUKKIT, true));
    }

    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasColor()) {
            setDisplayTag(itemTag, COLOR.NBT, new NBTTagInt(COLOR.NBT, color.asRGB()));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !(hasColor());
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public BukkitMetaLeatherArmor clone() {
        return (BukkitMetaLeatherArmor) super.clone();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color == null ? DEFAULT_LEATHER_COLOR : color;
    }

    boolean hasColor() {
        return !DEFAULT_LEATHER_COLOR.equals(color);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasColor()) {
            builder.put(COLOR.BUKKIT, color);
        }

        return builder;
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.LEATHER_ARMOR;
    }

    @Override
    boolean equalsCommon(BukkitMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof BukkitMetaLeatherArmor) {
            BukkitMetaLeatherArmor that = (BukkitMetaLeatherArmor) meta;

            return color.equals(that.color);
        }
        return true;
    }

    @Override
    boolean notUncommon(BukkitMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof BukkitMetaLeatherArmor || isLeatherArmorEmpty());
    }

    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasColor()) {
            hash ^= color.hashCode();
        }
        return original != hash ? BukkitMetaSkull.class.hashCode() ^ hash : hash;
    }
}
