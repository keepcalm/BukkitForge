package keepcalm.mods.bukkit.bukkitAPI.inventory;

import java.util.Map;

import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitMetaItem.SerializableMeta;
import net.minecraft.nbt.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.MapMeta;

import com.google.common.collect.ImmutableMap;

@DelegateDeserialization(SerializableMeta.class)
class BukkitMetaMap extends BukkitMetaItem implements MapMeta {
    static final ItemMetaKey MAP_SCALING = new ItemMetaKey("map_is_scaling", "scaling");
    static final byte SCALING_EMPTY = (byte) 0;
    static final byte SCALING_TRUE = (byte) 1;
    static final byte SCALING_FALSE = (byte) 2;

    private byte scaling = SCALING_EMPTY;

    BukkitMetaMap(BukkitMetaItem meta) {
        super(meta);

        if (!(meta instanceof BukkitMetaMap)) {
            return;
        }

        BukkitMetaMap map = (BukkitMetaMap) meta;
        this.scaling = map.scaling;
    }

    BukkitMetaMap(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(MAP_SCALING.NBT)) {
            this.scaling = tag.getBoolean(MAP_SCALING.NBT) ? SCALING_TRUE : SCALING_FALSE;
        }
    }

    BukkitMetaMap(Map<String, Object> map) {
        super(map);

        if (map.containsKey(MAP_SCALING.BUKKIT)) {
            this.scaling = SerializableMeta.getBoolean(map, MAP_SCALING.BUKKIT) ? SCALING_TRUE : SCALING_FALSE;
        }
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (hasScaling()) {
            tag.setBoolean(MAP_SCALING.NBT, isScaling());
        }
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case MAP:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isMapEmpty();
    }

    boolean isMapEmpty() {
        return !hasScaling();
    }

    boolean hasScaling() {
        return scaling != SCALING_EMPTY;
    }

    public boolean isScaling() {
        return scaling == SCALING_TRUE;
    }

    public void setScaling(boolean scaling) {
        this.scaling = scaling ? SCALING_TRUE : SCALING_FALSE;
    }

    @Override
    boolean equalsCommon(BukkitMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof BukkitMetaMap) {
            BukkitMetaMap that = (BukkitMetaMap) meta;

            return (this.scaling == that.scaling);
        }
        return true;
    }

    @Override
    boolean notUncommon(BukkitMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof BukkitMetaMap || isMapEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (hasScaling()) {
            hash ^= 0x22222222 << (isScaling() ? 1 : -1);
        }

        return original != hash ? BukkitMetaMap.class.hashCode() ^ hash : hash;
    }

    public BukkitMetaMap clone() {
        return (BukkitMetaMap) super.clone();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasScaling()) {
            builder.put(MAP_SCALING.BUKKIT, scaling);
        }

        return builder;
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.MAP;
    }
}
