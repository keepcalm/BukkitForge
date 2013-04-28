package org.bukkit.craftbukkit.v1_5_R2;

import net.minecraft.util.EnumArt;

import org.bukkit.Art;

// Safety class, will break if either side changes
public class CraftArt {
    public static Art NotchToCraft(EnumArt art) {
        switch (art) {
            case Kebab: return Art.KEBAB;
            case Aztec: return Art.AZTEC;
            case Alban: return Art.ALBAN;
            case Aztec2: return Art.AZTEC2;
            case Bomb: return Art.BOMB;
            case Plant: return Art.PLANT;
            case Wasteland: return Art.WASTELAND;
            case Pool: return Art.POOL;
            case Courbet: return Art.COURBET;
            case Sea: return Art.SEA;
            case Sunset: return Art.SUNSET;
            case Creebet: return Art.CREEBET;
            case Wanderer: return Art.WANDERER;
            case Graham: return Art.GRAHAM;
            case Match: return Art.MATCH;
            case Bust: return Art.BUST;
            case Stage: return Art.STAGE;
            case Void: return Art.VOID;
            case SkullAndRoses: return Art.SKULL_AND_ROSES;
            case Fighters: return Art.FIGHTERS;
            case Pointer: return Art.POINTER;
            case Pigscene: return Art.PIGSCENE;
            case BurningSkull: return Art.BURNINGSKULL;
            case Skeleton: return Art.SKELETON;
            case DonkeyKong: return Art.DONKEYKONG;
            case Wither: return Art.WITHER;
		default:
			break;
        }
        return null;
    }

    public static EnumArt CraftToNotch(Art art) {
        switch (art) {
            case KEBAB: return EnumArt.Kebab;
            case AZTEC: return EnumArt.Aztec;
            case ALBAN: return EnumArt.Alban;
            case AZTEC2: return EnumArt.Aztec2;
            case BOMB: return EnumArt.Bomb;
            case PLANT: return EnumArt.Plant;
            case WASTELAND: return EnumArt.Wasteland;
            case POOL: return EnumArt.Pool;
            case COURBET: return EnumArt.Courbet;
            case SEA: return EnumArt.Sea;
            case SUNSET: return EnumArt.Sunset;
            case CREEBET: return EnumArt.Creebet;
            case WANDERER: return EnumArt.Wanderer;
            case GRAHAM: return EnumArt.Graham;
            case MATCH: return EnumArt.Match;
            case BUST: return EnumArt.Bust;
            case STAGE: return EnumArt.Stage;
            case VOID: return EnumArt.Void;
            case SKULL_AND_ROSES: return EnumArt.SkullAndRoses;
            case FIGHTERS: return EnumArt.Fighters;
            case POINTER: return EnumArt.Pointer;
            case PIGSCENE: return EnumArt.Pigscene;
            case BURNINGSKULL: return EnumArt.BurningSkull;
            case SKELETON: return EnumArt.Skeleton;
            case DONKEYKONG: return EnumArt.DonkeyKong;
            case WITHER: return EnumArt.Wither; 
        }
        return null;
    }

    static {
        assert (EnumArt.values().length == 25);
        assert (Art.values().length == 25);
    }
}
