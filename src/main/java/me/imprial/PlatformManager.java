package me.imprial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        var power = (int) ((player.getPosition().sub(player.getPreviousPosition()).asVec().length()*10) + 5);
        var oldLoc = lastPlat.getPos();
        var newDiff = 0;
        var r = new Random();
        if (heightDiff > 0) {
            newDiff = (int) (r.nextDouble(0, heightDiff));
        } else if (heightDiff < 0) {
            newDiff = (int) (heightDiff*1.5);
        }
        var newLoc = oldLoc.add(r.nextInt(power-5, power+5), 0, r.nextInt(power-5, power+5)).withY(lastPlat.getPos().blockY()+newDiff);
        spawnPlatform(newLoc);
    }

    public void enteredPlat(Platform platform) {
        while (!platform.equals(platQueue.peek())) {
            platQueue.poll().remove();
            var diff = player.getPosition().blockY() - platform.getPos().blockY();
            spawnNext(diff);
        }
    }
}
