package ethosgates.ethosgates.Listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import com.sk89q.worldedit.math.BlockVector3;

import ethosgates.ethosgates.EthosGates;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;

import org.apache.commons.io.filefilter.RegexFileFilter;

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
    LandsIntegration api = LandsIntegration.of(EthosGates.getInstance());

    @EventHandler
    public void onSignChange(final SignChangeEvent e) {
        if (!e.getLine(0).equalsIgnoreCase("[TOR]")) return;
        Player p = e.getPlayer();
        String gateName = e.getLine(1);
        File dir = new File("./plugins/EthosGates/");
        FileFilter fileFilter = new RegexFileFilter("^.* " + p.getUniqueId() + gateName + "$");
        File[] files = dir.listFiles(fileFilter);
        if (Objects.requireNonNull(files).length != 1) {
            p.sendMessage("§cDu hast kein Tor mit dem Namen §4" + gateName + "§c!");
            return;
        }
        dir = Objects.requireNonNull(files)[0];
        String pattern = ".[/\\\\]plugins[/\\\\]EthosGates[/\\\\]";
        String ID = dir.toString().replaceAll(pattern, "");
        pattern = " " + p.getUniqueId() + gateName;
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
        Player p = e.getPlayer();
        {
            com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(world), sign.getX(), sign.getY(), sign.getZ());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(location);
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
            if (!set.testState(localPlayer, Flags.BUILD)) {
                p.sendMessage("§cDu darfst das hier nicht tun!");
                return;
            }
        }

        org.bukkit.Location location = sign.getLocation();
        Area area = api.getArea(location);
        if (!(area == null)) {
            if (!(area.hasRoleFlag(p.getUniqueId(), me.angeschossen.lands.api.flags.type.Flags.INTERACT_GENERAL))) {
                p.sendMessage("§cDu darfst das hier nicht tun!");
                return;
            }
        }

        int ID = Integer.parseInt(sign.getLine(2));
        BlockVector3 pos = BlockVector3.at(sign.getX(), sign.getY(), sign.getZ());
        if (!EthosGates.getGateManager().toggleGate(ID, world, pos, p)) {
            p.sendMessage("§cEs kann mit dem Tor mit der TorID: §4" + ID + " §cnicht interagiert werden!");
        }
    }
}
