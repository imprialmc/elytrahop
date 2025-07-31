package me.imprial;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ElytraHop {
    public static Pos spawn = new Pos(0, 50, 0);
    public static void main( String[] args ) {
        System.out.println( "Starting elytahop.." );
        startMinestom();
    }

    private static void startMinestom() {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            var platMan = new PlatformManager(player);
            event.setSpawningInstance(platMan.instance());
            player.setRespawnPoint(spawn.add(0.5, 1, 0.5));
            player.setEquipment(EquipmentSlot.CHESTPLATE, ItemStack.of(Material.ELYTRA));
        });

        globalEventHandler.addListener(PlayerStartSneakingEvent.class, e -> {
            e.getPlayer().setVelocity(e.getPlayer().getPosition().direction().normalize().mul(20));
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}