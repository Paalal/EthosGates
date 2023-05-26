package ethosgates.ethosgates.utils;

import ethosgates.ethosgates.EthosGates;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.math.BlockVector3;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public class GateClickCreator implements Listener {
    private final Player player;
    private int[] clickInfo = null;
    GateClickCreator gateClickCreator = this;

    public GateClickCreator(Player player) {
        this.player = player;
    }

    public void clickCreateGate(int overhang, @NotNull File dir) {
        player.sendMessage("§7Klicke zwei diagonal gegenüberliegende Ecken des Tors an");
        new BukkitRunnable() {
            int repetitions = 0;
            final String gateDir = dir.toString().replace("/schematics", "").replace("\\schematics", "");
            int[] x = null;
            int[] y = null;
            int[] z = null;
            @Override
            public void run() {

                if (!(clickInfo == null)) {
                    if (x == null) {
                        x = new int[] {clickInfo[0], 0};
                        y = new int[] {clickInfo[1], 0};
                        z = new int[] {clickInfo[2], 0};
                        clickInfo = null;
                    } else {
                        x[1] = clickInfo[0];
                        y[1] = clickInfo[1];
                        z[1] = clickInfo[2];
                        Arrays.sort(x);
                        Arrays.sort(y);
                        Arrays.sort(z);
                        BlockVector3 max = BlockVector3.at(x[1], y[1], z[1]);
                        BlockVector3 min = BlockVector3.at(x[0], y[0], z[0]);

                        if (!(overhang < max.getY() - min.getY() - 1)) {
                            player.sendMessage("§cIm offenen Zustand muss das Tor mindestens §43 Blöcke hoch frei §csein. ");
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            cancel();
                            return;
                        }

                        if (!(((max.getX() == min.getX() && max.getZ() - min.getZ() > 0 && (max.getZ() - min.getZ()) * (max.getY() - min.getY()) < 100) || (max.getZ() == min.getZ() && max.getX() - min.getX() > 0 && (max.getX() - min.getX()) * (max.getY() - min.getY()) < 100)) && max.getY() - min.getY() > 1)) {
                            player.sendMessage("§cDas Tor muss mindestens §43 Blöcke hoch §cund §42 Blöcke breit §csein, aber §4nicht größer als 100 Blöcke§c!");
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            cancel();
                            return;
                        }

                        if (EthosGates.getGateManager().createGate(player, max, min, overhang, dir)) {
                            player.sendMessage("§7Das Tor wurde erfolgreich erstellt.");
                        } else {
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            player.sendMessage("§cUnerwarteter Fehler beim erstellen des Tors. §4§lVersuche es bitte erneut und achte darauf, dass das Tor beim Erstellen geschlossen ist. §r§cSollte dieser Fehler trotzdem wieder auftreten, wende dich bitte an den Support (/ch s)");
                        }
                        cancel();
                        EthosGates.getInstance().unregisterGateClickCreator(gateClickCreator);
                    }
                }

                if (++repetitions == 200) {
                    EthosGates.getGateManager().deleteGate(new File(gateDir));
                    player.sendMessage("§cTor Erstellung abgelaufen");
                    EthosGates.getInstance().unregisterGateClickCreator(gateClickCreator);
                    cancel();
                }
            }
        }.runTaskTimer(EthosGates.getInstance(), 0, 1);
    }

    @EventHandler
    public void playerClickEvent(PlayerInteractEvent e) {
        if (e.getPlayer() != player) return;
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK) || !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        clickInfo = new int[] {block.getX(), block.getY(), block.getZ()};
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }
}
