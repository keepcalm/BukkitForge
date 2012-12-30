package keepcalm.mods.bukkit.bukkitAPI.inventory;

import java.util.Map;

import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitMetaItem.SerializableMeta;
import net.minecraft.nbt.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class BukkitMetaSkull extends BukkitMetaItem implements SkullMeta {
    static final ItemMetaKey SKULL_OWNER = new ItemMetaKey("SkullOwner", "skull-owner");
    static final int MAX_OWNER_LENGTH = 16;

    private String player;

    BukkitMetaSkull(BukkitMetaItem meta) {
        super(meta);
        if (!(meta instanceof BukkitMetaSkull)) {
            return;
        }
        BukkitMetaSkull skullMeta = (BukkitMetaSkull) meta;
        this.player = skullMeta.player;
    }

    BukkitMetaSkull(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(SKULL_OWNER.NBT)) {
            player = tag.getString(SKULL_OWNER.NBT);
        }
    }

    BukkitMetaSkull(Map<String, Object> map) {
        super(map);
        setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (hasOwner()) {
            tag.setString(SKULL_OWNER.NBT, player);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSkullEmpty();
    }

    boolean isSkullEmpty() {
        return !(hasOwner());
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case SKULL_ITEM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public BukkitMetaSkull clone() {
        return (BukkitMetaSkull) super.clone();
    }

    public boolean hasOwner() {
        return !Strings.isNullOrEmpty(player);
    }

    public String getOwner() {
        return player;
    }

    public boolean setOwner(String name) {
        if (name != null && name.length() > MAX_OWNER_LENGTH) {
            return false;
        }
        player = name;
        return true;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasOwner()) {
            hash = 61 * hash + player.hashCode();
        }
        return original != hash ? BukkitMetaSkull.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(BukkitMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof BukkitMetaSkull) {
            BukkitMetaSkull that = (BukkitMetaSkull) meta;

            return (this.hasOwner() ? that.hasOwner() && this.player.equals(that.player) : !that.hasOwner());
        }
        return true;
    }

    @Override
    boolean notUncommon(BukkitMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof BukkitMetaSkull || isSkullEmpty());
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);
        if (hasOwner()) {
            return builder.put(SKULL_OWNER.BUKKIT, this.player);
        }
        return builder;
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.SKULL;
    }
}
