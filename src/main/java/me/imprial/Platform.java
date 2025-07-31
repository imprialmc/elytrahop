package me.imprial;

import java.util.ArrayList;
import java.util.List;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public class Platform {
    private final PlatformManager platformManager;
    private final Point pos;
    private final List<Point> blocks = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private EventListener<PlayerMoveEvent> eventListener;
    public Platform(PlatformManager platformManager, Point pos) {
        this.platformManager = platformManager;
        this.pos = pos;
        setBlocks(Block.QUARTZ_BLOCK);
        initListener();
    }

    private void initListener() {
        eventListener = EventListener.builder(PlayerMoveEvent.class)
        .handler(e -> {
            if (inPlat(e.getNewPosition())) {
                enterPlat(e.getPlayer());
            }
        }).build();
        platformManager.instance().eventNode().addListener(eventListener);
    }

    private boolean inPlat(Pos pLoc) {
        if (pLoc.x()>= pos.x()-1 && pLoc.x()<=pos.x()+1) {
            if (pLoc.z()>= pos.z()-1 && pLoc.z()<=pos.z()+1) {
                return true;
            }
        }
        return false;
    }

    private void enterPlat(Player p) {
        p.sendPacket(new ParticlePacket(Particle.FIREWORK, p.getPosition(), Vec.ZERO, 0.3f, 50));
        platformManager.instance().eventNode().removeListener(eventListener);
        platformManager.enteredPlat(this);
    }

    private void setBlocks(Block block) {
        int posX = (int) Math.floor(pos.x());
        int posZ = (int) Math.floor(pos.z());
        for(int x = posX-1; x<=posX+1; x++) {
            for(int z = posZ-1; z<=posZ+1; z++) {
                var loc = new Pos(x, pos.y(), z);
                platformManager.instance().setBlock(loc, Block.BARRIER);
                blocks.add(loc);
                
                var entity = new Entity(EntityType.BLOCK_DISPLAY);
                var meta = (BlockDisplayMeta) entity.getEntityMeta();
                entity.setNoGravity(true);
                meta.setPosRotInterpolationDuration(5);
                meta.setBrightnessOverride(20);;
                meta.setGlowColorOverride(0);
                meta.setBlockState(block);
                entity.setInstance(platformManager.instance(), loc.sub(0, 6, 0));
                MinecraftServer.getSchedulerManager().scheduleNextTick(() -> entity.teleport(loc));
                entities.add(entity);
            }
        }
    }

    public void remove() {
        blocks.forEach(block -> platformManager.instance().setBlock(block,Block.AIR));
        entities.forEach(entity -> entity.remove());
    }
}
