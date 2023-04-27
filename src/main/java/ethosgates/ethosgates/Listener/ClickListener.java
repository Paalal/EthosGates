package ethosgates.ethosgates.Listener;

import ethosgates.ethosgates.utils.PlayerClickCordsGetter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickListener implements Listener {
    
private int activations = 0;
private static Boolean activated = false;
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (!activated) return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if(block.getState().getBlockData().getMaterial().equals(Material.AIR)) return;
        PlayerClickCordsGetter.addClickInfo(player, block.getX(), block.getY(), block.getZ());
    }

    public static void activate() {
        activatons++;
        activated = true;
    }

    public static void deactivate() {
        if (--activations == 0) {
            activated = false;
        }
    }
}
