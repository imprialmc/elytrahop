package me.imprial;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
    private Platform lastPlat;

    public PlatformManager(Player player) {
        this.player = player;

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instance = instanceManager.createInstanceContainer(DimensionType.THE_END);
        instance.setChunkSupplier(LightingChunk::new);

        instance.getDimensionType();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 1, Block.WHITE_STAINED_GLASS));

        spawnPlatform(ElytraHop.spawn);
        spawnNext();
        spawnNext();
    }

    public InstanceContainer instance() {
        return instance;
    }

    private void spawnPlatform(Point pos) {
        lastPlat = new Platform(this, pos);
        platQueue.add(lastPlat);
    }

    private void spawnNext() {
        spawnNext(0);
    }
    private void spawnNext(double heightDiff) {
        var power = (player.getPosition().sub(player.getPreviousPosition()).asVec().length()*10) + 5;
        var oldLoc = lastPlat.getPos();
        var newDiff = 0;
        if (heightDiff >=0) {
            newDiff = (int) (new Random().nextDouble(-4, heightDiff-1));
        } else {
            newDiff = (int) (heightDiff*1.5);
        }
        var newLoc = oldLoc.add(power, 0, power).withY(lastPlat.getPos().blockY()+newDiff);
        spawnPlatform(newLoc);
    }

    public void enteredPlat(Platform platform) {
        if (platQueue.size() > 3) {
            var oldPlat = platQueue.poll();
            if (oldPlat!=null) oldPlat.remove();
        }
        var diff = player.getPosition().blockY() - platform.getPos().blockY();
        spawnNext(diff);
    }
}
