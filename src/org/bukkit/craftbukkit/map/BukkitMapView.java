package org.bukkit.craftbukkit.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.storage.MapData;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.BukkitWorld;
import org.bukkit.craftbukkit.entity.BukkitPlayer;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
//import org.bukkit.craftbukkit.BukkitWorld;
//import org.bukkit.craftbukkit.entity.BukkitPlayer;

public final class BukkitMapView implements MapView {

    private final Map<BukkitPlayer, RenderData> renderCache = new HashMap<BukkitPlayer, RenderData>();
    private final List<MapRenderer> renderers = new ArrayList<MapRenderer>();
    private final Map<MapRenderer, Map<BukkitPlayer, BukkitMapCanvas>> canvases = new HashMap<MapRenderer, Map<BukkitPlayer, BukkitMapCanvas>>();
    protected final MapData worldMap;

    public BukkitMapView(MapData worldMap) {
        this.worldMap = worldMap;
        addRenderer(new BukkitMapRenderer(this, worldMap));
    }

    public short getId() {
        String text = worldMap.mapName;
        if (text.startsWith("map_")) {
            try {
                return Short.parseShort(text.substring("map_".length()));
            }
            catch (NumberFormatException ex) {
                throw new IllegalStateException("Map has non-numeric ID");
            }
        } else {
            throw new IllegalStateException("Map has invalid ID");
        }
    }

    public boolean isVirtual() {
        return renderers.size() > 0 && !(renderers.get(0) instanceof BukkitMapRenderer);
    }

    public Scale getScale() {
        return Scale.valueOf(worldMap.scale);
    }

    public void setScale(Scale scale) {
        worldMap.scale = scale.getValue();
    }

    public World getWorld() {
        byte dimension = (byte) worldMap.dimension;
        for (World world : Bukkit.getServer().getWorlds()) {
            if (((BukkitWorld) world).getHandle().getWorldInfo().getDimension() == dimension) {
                return world;
            }
        }
        return null;
    }

    public void setWorld(World world) {
        worldMap.dimension = (int) ((BukkitWorld) world).getHandle().getWorldInfo().getDimension();
    }

    public int getCenterX() {
        return worldMap.xCenter;
    }

    public int getCenterZ() {
        return worldMap.zCenter;
    }

    public void setCenterX(int x) {
        worldMap.xCenter = x;
    }

    public void setCenterZ(int z) {
        worldMap.zCenter = z;
    }

    public List<MapRenderer> getRenderers() {
        return new ArrayList<MapRenderer>(renderers);
    }

    public void addRenderer(MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, new HashMap<BukkitPlayer, BukkitMapCanvas>());
            renderer.initialize(this);
        }
    }

    public boolean removeRenderer(MapRenderer renderer) {
        if (renderers.contains(renderer)) {
            renderers.remove(renderer);
            for (Map.Entry<BukkitPlayer, BukkitMapCanvas> entry : canvases.get(renderer).entrySet()) {
                for (int x = 0; x < 128; ++x) {
                    for (int y = 0; y < 128; ++y) {
                        entry.getValue().setPixel(x, y, (byte) -1);
                    }
                }
            }
            canvases.remove(renderer);
            return true;
        } else {
            return false;
        }
    }

    private boolean isContextual() {
        for (MapRenderer renderer : renderers) {
            if (renderer.isContextual()) return true;
        }
        return false;
    }

    public RenderData render(BukkitPlayer player) {
        boolean context = isContextual();
        RenderData render = renderCache.get(context ? player : null);

        if (render == null) {
            render = new RenderData();
            renderCache.put(context ? player : null, render);
        }

        if (context && renderCache.containsKey(null)) {
            renderCache.remove(null);
        }

        Arrays.fill(render.buffer, (byte) 0);
        render.cursors.clear();

        for (MapRenderer renderer : renderers) {
            BukkitMapCanvas canvas = canvases.get(renderer).get(renderer.isContextual() ? player : null);
            if (canvas == null) {
                canvas = new BukkitMapCanvas(this);
                canvases.get(renderer).put(renderer.isContextual() ? player : null, canvas);
            }

            canvas.setBase(render.buffer);
            renderer.render(this, canvas, player);

            byte[] buf = canvas.getBuffer();
            for (int i = 0; i < buf.length; ++i) {
                if (buf[i] >= 0) render.buffer[i] = buf[i];
            }

            for (int i = 0; i < canvas.getCursors().size(); ++i) {
                render.cursors.add(canvas.getCursors().getCursor(i));
            }
        }

        return render;
    }

}
