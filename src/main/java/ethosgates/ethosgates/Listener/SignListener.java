package ethosgates.ethosgates.Listener;

import ethosgates.ethosgates.EthosGates;

import com.sk89q.worldedit.math.BlockVector3;

import ethosgates.ethosgates.utils.WorldGuardHook;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;

public class SignListener implements Listener {

    @EventHandler
    public void onSignChange(final SignChangeEvent e) {
        if (!Objects.requireNonNull(e.getLine(0)).equalsIgnoreCase("[TOR]")) return;
        Player player = e.getPlayer();
        String gateName = e.getLine(1);
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^.* " + player.getUniqueId() + gateName + "$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            player.sendMessage("§cDu hast kein Tor mit dem Namen §4" + gateName + "§c!");
            return;
        }
        dir = Objects.requireNonNull(files)[0];
        String pattern = ".[/\\\\]plugins[/\\\\]EthosGates[/\\\\]";
        String ID = dir.toString().replaceAll(pattern, "");
        pattern = " " + player.getUniqueId() + gateName;
        ID = ID.replaceAll(pattern, "");
        e.setLine(0,  "§b[TOR]");
        e.setLine(1, "§3" + gateName);
        e.setLine(2, ID);
        e.setLine(3, Boolean.toString(e.getBlock().isBlockPowered()));
    }

    @EventHandler
    public void onRedstoneChange(final BlockPhysicsEvent e) {
        if (!(e.getBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getBlock().getState();
        if (!sign.getLine(0).equals(("§b[TOR]"))) return;
        boolean wasPowered = Boolean.parseBoolean(sign.getLine(3
        ));
        if (sign.getBlock().isBlockPowered() && !wasPowered) {
            sign.setLine(3, "true");
            sign.update();
            int ID = Integer.parseInt(sign.getLine(2));
            World world = sign.getWorld();
            BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
            EthosGates.getGateManager().redstoneOpenGate(ID, world, pos);
        } else {
            if (!sign.getBlock().isBlockPowered() && wasPowered) {
                sign.setLine(3, "false");
                sign.update();
                int ID = Integer.parseInt(sign.getLine(2));
                World world = sign.getWorld();
                BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
                EthosGates.getGateManager().redstoneCloseGate(ID, world, pos);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        if (!sign.getLine(0).equalsIgnoreCase("§b[TOR]")) return;
        World world = sign.getWorld();
        Player player = e.getPlayer();

        if (!player.isOp()) {
            if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                if (!new WorldGuardHook().testState(player, world, sign)) {
                    player.sendMessage("§cDu hast in diesem Gebiet nicht die Berechtigung dazu mit dem Tor zu interagieren!");
                    return;
                }
            }

            if (EthosGates.getInstance().getServer().getPluginManager().isPluginEnabled("Lands")) {
                me.angeschossen.lands.api.LandsIntegration api = me.angeschossen.lands.api.LandsIntegration.of(EthosGates.getInstance());
                org.bukkit.Location location = sign.getLocation();
                me.angeschossen.lands.api.land.Area area = api.getArea(location);
                if (!(area == null)) {
                    if (!(area.hasRoleFlag(player.getUniqueId(), me.angeschossen.lands.api.flags.type.Flags.INTERACT_GENERAL))) {
                        player.sendMessage("§cDu hast in diesem Land nicht die Berechtigung dazu mit dem Tor zu interagieren!");
                        return;
                    }
                }
            }
        }

        int ID = Integer.parseInt(sign.getLine(2));
        BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
        if (!EthosGates.getGateManager().toggleGate(ID, world, pos, player)) {
            player.sendMessage("§cEs kann mit dem Tor mit der TorID: §4" + ID + " §cnicht interagiert werden!");
        }
    }
}
