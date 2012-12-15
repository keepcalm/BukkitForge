package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import net.minecraft.tileentity.TileEntitySign;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
//import org.bukkit.craftbukkit.BukkitWorld;

public class BukkitSign extends BukkitBlockState implements Sign {
    private final TileEntitySign sign;
    private final String[] lines;

    public BukkitSign(final Block block) {
        super(block);

        BukkitWorld world = (BukkitWorld) block.getWorld();
        sign = (TileEntitySign) world.getTileEntityAt(getX(), getY(), getZ());
        lines = new String[sign.signText.length];
        System.arraycopy(sign.signText, 0, lines, 0, lines.length);
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines[index];
    }

    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        lines[index] = line;
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            for(int i = 0; i < 4; i++) {
                if(lines[i] != null) {
                    sign.signText[i] = lines[i];
                } else {
                    sign.signText[i] = "";
                }
            }
            sign.updateEntity();
        }

        return result;
    }
}
