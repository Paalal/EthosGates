package ethosgates3.ethosgates3.Listener;

import ethosgates3.ethosgates3.Main;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bukkit.ChatColor;
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
    public void onSignChange(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[TOR]")) {
            Player p = e.getPlayer();
            String gateName = e.getLine(1);
            File dir = new File("./plugins/EthosGates3/");
            FileFilter fileFilter = new RegexFileFilter("^.* " + p.getUniqueId() + gateName + "$");
            File[] files = dir.listFiles(fileFilter);
            if (Objects.requireNonNull(files).length != 1) {
                p.sendMessage("§cDu hast kein Tor mit dem Namen §4" + gateName + "§c!");
                return;
            }
            dir = Objects.requireNonNull(files)[0];
            String pattern = ".[/\\\\]plugins[/\\\\]EthosGates3[/\\\\]";
            String ID = dir.toString().replaceAll(pattern, "");
            pattern = " " + p.getUniqueId() + gateName;
            ID = ID.replaceAll(pattern, "");
            e.setLine(0, ChatColor.DARK_RED + "[TOR]");
            e.setLine(1, ID);
            e.setLine(2, Boolean.toString(e.getBlock().isBlockPowered()));
        }
    }

    @EventHandler
    public void onRedstoneChange(BlockPhysicsEvent e) {
        if(e.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getBlock().getState();
            if (sign.getLine(0).equals((ChatColor.DARK_RED + "[TOR]"))) {
                boolean oldPower = Boolean.parseBoolean(sign.getLine(2));
                if (sign.getBlock().isBlockPowered() && !oldPower) {
                    sign.setLine(2, "true");
                    sign.update();
                    int ID = Integer.parseInt(sign.getLine(1));
                    World world = BukkitAdapter.adapt(sign.getWorld());
                    BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
                    Main.getGateManager().redstone_open(ID, world, pos);
                } else {
                    if (!sign.getBlock().isBlockPowered() && oldPower) {
                        sign.setLine(2, Boolean.toString(false));
                        sign.update();
                        int ID = Integer.parseInt(sign.getLine(1));
                        World world = BukkitAdapter.adapt(sign.getWorld());
                        BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
                        Main.getGateManager().redstone_close(ID, world, pos);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)return;
        if(e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "[TOR]")) {
                Player p = e.getPlayer();
                int ID = Integer.parseInt(sign.getLine(1));
                World world = BukkitAdapter.adapt(e.getClickedBlock().getWorld());
                BlockVector3 pos = BlockVector3.at(e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ());
                if (!Main.getGateManager().toggleGate(ID, world, pos, p)) {
                    p.sendMessage("§cEs kann mit dem Tor mit der TorID: §4" + ID + " §cnicht interagiert werden!");
                }
            }
        }
    }
}
