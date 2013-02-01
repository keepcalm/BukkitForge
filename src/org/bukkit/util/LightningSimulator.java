package org.bukkit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.weather.ThunderChangeEvent;

public class LightningSimulator {

    private static final int MAX_LIGHTNING_BRANCHES = 5;
    final net.minecraft.world.World/*was:World*/ world;
    final HashMap<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/, Integer> playerCountdown = new HashMap<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/, Integer>();
    Intensity stormIntensity = null;
    boolean canceled = false;

    public LightningSimulator(net.minecraft.world.World/*was:World*/ world) {
        this.world = world;
    }

    public static void configure(YamlConfiguration configuration) {
        Bukkit.getLogger().info("--------Setting up Storm Configuration--------");
        for (Intensity intensity : Intensity.values()) {
            String nameFormatted = intensity.name().toLowerCase().replaceAll("_", "-");
            intensity.chance = configuration.getInt("storm-settings." + nameFormatted + ".chance", intensity.chance);
            intensity.baseTicks = configuration.getInt("storm-settings." + nameFormatted + ".lightning-delay", intensity.baseTicks);
            intensity.randomTicks = configuration.getInt("storm-settings." + nameFormatted + ".lightning-random-delay", intensity.randomTicks);
            Bukkit.getLogger().info("    Storm Type: " + nameFormatted);
            Bukkit.getLogger().info("        Chance: " + intensity.chance);
            Bukkit.getLogger().info("        Lightning Delay Ticks: " + intensity.baseTicks);
            Bukkit.getLogger().info("        Lightning Random Delay Ticks: " + intensity.randomTicks);
        }
        Bukkit.getLogger().info("--------Finished Storm Configuration--------");
    }

    public void onTick() {
        try {
            updatePlayerTimers();
        } catch (Exception e) {
            System.out.println("Spigot failed to calculate lightning for the server");
            System.out.println("Please report this to md_5");
            System.out.println("Spigot Version: " + Bukkit.getBukkitVersion());
            e.printStackTrace();
        }
    }

    public void updatePlayerTimers() {
        if (world.getWorld().hasStorm()) {
            if (canceled) {
                return;
            }
            if (stormIntensity == null) {
                ThunderChangeEvent thunder = new ThunderChangeEvent(world.getWorld(), true);
                Bukkit.getPluginManager().callEvent(thunder);
                if (thunder.isCancelled()) {
                    canceled = true;
                    return;
                }
                stormIntensity = Intensity.getRandomIntensity(world.rand/*was:random*/);
                System.out.println("Started a storm of type " + stormIntensity.name() + " in world [" + world.getWorldInfo().getWorldName/*was:getName*/() + "]");
            }
            List<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/> toStrike = new ArrayList<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/>();
            for (Object o : world.playerEntities/*was:players*/) {
                if (o instanceof net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/) {
                    net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ player = (net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/) o;
                    Integer ticksLeft = playerCountdown.get(player);
                    if (ticksLeft == null) {
                        playerCountdown.put(player, getTicksBeforeNextLightning(world.rand/*was:random*/));
                    } else if (ticksLeft == 1) {
                        //weed out dc'd players
                        if (!player.playerNetServerHandler/*was:playerConnection*/.connectionClosed/*was:disconnected*/) {
                            toStrike.add(player);
                            playerCountdown.put(player, getTicksBeforeNextLightning(world.rand/*was:random*/));
                        }
                    } else {
                        playerCountdown.put(player, ticksLeft - 1);
                    }
                }
            }
            strikePlayers(toStrike);
        } else {
            stormIntensity = null;
            canceled = false;
        }
    }

    public void strikePlayers(List<net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/> toStrike) {
        for (net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ player : toStrike) {
            final int posX = net.minecraft.util.MathHelper/*was:MathHelper*/.floor_double/*was:floor*/(player.posX/*was:locX*/);
            final int posY = net.minecraft.util.MathHelper/*was:MathHelper*/.floor_double/*was:floor*/(player.posY/*was:locY*/);
            final int posZ = net.minecraft.util.MathHelper/*was:MathHelper*/.floor_double/*was:floor*/(player.posZ/*was:locZ*/);
            for (int tries = 0; tries < 10; tries++) {
                //pick a random chunk between -4, -4, to 4, 4 relative to the player's position to strike at
                int cx = (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(5);
                int cz = (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(5);

                //pick random coords to try to strike at inside the chunk (0, 0) to (15, 15)
                int rx = world.rand/*was:random*/.nextInt(16);
                int rz = world.rand/*was:random*/.nextInt(16);

                //pick a offset from the player's y position to strike at (-15 - +15) of their position
                int offsetY = (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(15);

                int x = cx * 16 + rx + posX;
                int y = posY + offsetY;
                int z = cz * 16 + rz + posZ;

                if (isRainingAt(x, y, z)) {
                    int lightning = 1;
                    //30% chance of extra lightning at the spot
                    if (world.rand/*was:random*/.nextInt(10) < 3) {
                        lightning += world.rand/*was:random*/.nextInt(MAX_LIGHTNING_BRANCHES);
                    }
                    for (int strikes = 0; strikes < lightning; strikes++) {
                        double adjustX = 0.5D;
                        double adjustY = 0.0D;
                        double adjustZ = 0.5D;
                        //if there are extra strikes, tweak their placement slightly
                        if (strikes > 0) {
                            adjustX += (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(2);
                            adjustY += (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(8);
                            adjustZ += (world.rand/*was:random*/.nextBoolean() ? -1 : 1) * world.rand/*was:random*/.nextInt(2);
                        }
                        net.minecraft.entity.effect.EntityLightningBolt/*was:EntityLightning*/ lightningStrike = new net.minecraft.entity.effect.EntityLightningBolt/*was:EntityLightning*/(world, x + adjustX, y + adjustY, z + adjustZ);
                        world.addWeatherEffect/*was:strikeLightning*/(lightningStrike);
                    }
                    //success, go to the next player
                    break;
                }
            }
        }
    }

    public int getTicksBeforeNextLightning(Random rand) {
        return stormIntensity.baseTicks + rand.nextInt(stormIntensity.randomTicks);
    }

    public boolean isRainingAt(int x, int y, int z) {
        return world.canLightningStrikeAt/*was:D*/(x, y, z);
    }
}

enum Intensity {

    STRONG_ELECTRICAL_STORM(5, 10, 20),
    ELECTRICAL_STORM(15, 40, 150),
    STRONG_THUNDERSTORM(30, 60, 250),
    THUNDERSTORM(50, 100, 500),
    WEAK_THUNDERSTORM(75, 300, 1000),
    RAINSTORM(100, 500, 2000);
    int chance, baseTicks, randomTicks;

    Intensity(int chance, int baseTicks, int randomTicks) {
        this.chance = chance;
        this.baseTicks = baseTicks;
        this.randomTicks = randomTicks;
    }

    public static Intensity getRandomIntensity(Random rand) {
        int r = rand.nextInt(100);
        if (r < STRONG_ELECTRICAL_STORM.chance) {
            return STRONG_ELECTRICAL_STORM;
        }
        if (r < ELECTRICAL_STORM.chance) {
            return ELECTRICAL_STORM;
        }
        if (r < STRONG_THUNDERSTORM.chance) {
            return STRONG_THUNDERSTORM;
        }
        if (r < THUNDERSTORM.chance) {
            return THUNDERSTORM;
        }
        if (r < WEAK_THUNDERSTORM.chance) {
            return WEAK_THUNDERSTORM;
        }
        return RAINSTORM;
    }
}
