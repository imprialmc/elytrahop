package me.imprial;

import java.util.LinkedList;
import java.util.Queue;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;


public class PlatformManager {
    private final Player player;
    private InstanceContainer instance;
    private final Queue<Platform> platQueue = new LinkedList<>();

    public PlatformManager(Player player) {
        this.player = player;

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instance = instanceManager.createInstanceContainer(DimensionType.THE_END);
        instance.setChunkSupplier(LightingChunk::new);

        instance.getDimensionType();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 1, Block.WHITE_STAINED_GLASS));

        spawnPlatform(ElytraHop.spawn);
    }

    public InstanceContainer instance() {
        return instance;
    }

    private void spawnPlatform(Point pos) {
        platQueue.add(new Platform(this, pos));
    }

    private void spawnNext() {
        var pPos = player.getPosition().withY(player.getPosition().blockY());
        var power = 6;
        var newLoc = pPos.add(pPos.direction().normalize().mul(power).withY(0));
        spawnPlatform(newLoc);
    }

    public void enteredPlat(Platform platform) {
        if (platQueue.size() > 3) {
            var oldPlat = platQueue.poll();
            if (oldPlat!=null) oldPlat.remove();
        }
        spawnNext();
    }
}
