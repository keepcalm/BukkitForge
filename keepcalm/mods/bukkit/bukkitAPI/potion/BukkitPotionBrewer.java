package keepcalm.mods.bukkit.bukkitAPI.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import net.minecraft.src.PotionEffect;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Maps;

public class BukkitPotionBrewer implements PotionBrewer {
    private static final Map<Integer, Collection<PotionEffect>> cache = Maps.newHashMap();

    public Collection<PotionEffect> getEffectsFromDamage(int damage) {
        if (cache.containsKey(damage))
            return cache.get(damage);

        List<?> mcEffects = net.minecraft.src.PotionHelper.getPotionEffects(damage, false);
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (mcEffects == null)
            return effects;

        for (Object raw : mcEffects) {
            if (raw == null || !(raw instanceof net.minecraft.src.PotionEffect))
                continue;
            net.minecraft.src.PotionEffect mcEffect = (net.minecraft.src.PotionEffect) raw;
            PotionEffect effect = new PotionEffect(PotionEffectType.getById(mcEffect.getPotionID()),
                    mcEffect.getDuration(), mcEffect.getAmplifier());
            // Minecraft PotionBrewer applies duration modifiers automatically.
            effects.add(effect);
        }

        cache.put(damage, effects);

        return effects;
    }

    public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
        return new PotionEffect(potion, potion.isInstant() ? 1 : (int) (duration * potion.getDurationModifier()),
                amplifier);
    }
}
