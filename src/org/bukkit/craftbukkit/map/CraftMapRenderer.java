package org.bukkit.craftbukkit.map;

import net.minecraft.world.storage.MapCoord;
import net.minecraft.world.storage.MapData;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CraftMapRenderer extends MapRenderer {

    private final MapData worldMap;

    public CraftMapRenderer(BukkitMapView mapView, MapData worldMap) {
        super(false);
        this.worldMap = worldMap;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        // Map
        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                canvas.setPixel(x, y, worldMap.colors[y * 128 + x]);
            }
        }

        // Cursors
        MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0) {
            cursors.removeCursor(cursors.getCursor(0));
        }
        // playersVisibleOnMap is a MapCoord - and this is working out the cursors.
        for (int i = 0; i < worldMap.playersVisibleOnMap.size(); ++i) {
            MapCoord decoration = (MapCoord) worldMap.playersVisibleOnMap.get(i);
            cursors.addCursor(decoration.centerX, decoration.centerZ, (byte) (decoration.iconRotation & 15), (byte) (decoration.iconSize));
        }
    }

}
