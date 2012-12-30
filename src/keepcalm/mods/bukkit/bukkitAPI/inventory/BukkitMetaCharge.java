package keepcalm.mods.bukkit.bukkitAPI.inventory;

import java.util.Map;

import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitMetaItem.SerializableMeta;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitMetaItem.SerializableMeta.Deserializers;
import net.minecraft.nbt.NBTTagCompound;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class BukkitMetaCharge extends BukkitMetaItem implements FireworkEffectMeta {
    static final ItemMetaKey EXPLOSION = new ItemMetaKey("Explosion", "firework-effect");

    private FireworkEffect effect;

    BukkitMetaCharge(BukkitMetaItem meta) {
        super(meta);

        if (meta instanceof BukkitMetaCharge) {
            effect = ((BukkitMetaCharge) meta).effect;
        }
    }

    BukkitMetaCharge(Map<String, Object> map) {
        super(map);

        effect = SerializableMeta.getObject(FireworkEffect.class, map, EXPLOSION.BUKKIT, true);
    }

    BukkitMetaCharge(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(EXPLOSION.NBT)) {
            effect = BukkitMetaFirework.getEffect(tag.getCompoundTag(EXPLOSION.NBT));
        }
    }

    public void setEffect(FireworkEffect effect) {
        this.effect = effect;
    }

    public boolean hasEffect() {
        return effect != null;
    }

    public FireworkEffect getEffect() {
        return effect;
    }

    @Override
    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasEffect()) {
            itemTag.setTag(EXPLOSION.NBT, BukkitMetaFirework.getExplosion(effect));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case FIREWORK_CHARGE:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && !hasChargeMeta();
    }

    boolean hasChargeMeta() {
        return hasEffect();
    }

    @Override
    boolean equalsCommon(BukkitMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof BukkitMetaCharge) {
            BukkitMetaCharge that = (BukkitMetaCharge) meta;

            return (hasEffect() ? that.hasEffect() && this.effect.equals(that.effect) : !that.hasEffect());
        }
        return true;
    }

    @Override
    boolean notUncommon(BukkitMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof BukkitMetaCharge || !hasChargeMeta());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (hasEffect()) {
            hash = 61 * hash + effect.hashCode();
        }

        return hash != original ? BukkitMetaCharge.class.hashCode() ^ hash : hash;
    }

    @Override
    public BukkitMetaCharge clone() {
        return (BukkitMetaCharge) super.clone();
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasEffect()) {
            builder.put(EXPLOSION.BUKKIT, effect);
        }

        return builder;
    }

    @Override
    Deserializers deserializer() {
        return Deserializers.FIREWORK_EFFECT;
    }
}
