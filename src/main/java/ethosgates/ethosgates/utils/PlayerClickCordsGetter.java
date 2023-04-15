package ethosgates.ethosgates.utils;

import ethosgates.ethosgates.EthosGates;

import ethosgates.ethosgates.Listener.ClickListener;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.math.BlockVector3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerClickCordsGetter {
    private static List<ClickInfo> clickInfoList;
    private final Player player;

    public PlayerClickCordsGetter(Player player) {
        this.player = player;
    }

    public void clickCreateGate(int overhang, File dir) {
        player.sendMessage("§7Klicke zwei diagonal gegenüberliegende Ecken des Tors an");
        EthosGates.increaseCurrentGateID();
        clickInfoList = new ArrayList<>();
        new BukkitRunnable() {
            int repetitions = 0;
            int[] x = null;
            int[] y = null;
            int[] z = null;
            final String gateDir = dir.toString().replace("/schematics", "").replace("\\schematics", "");
            @Override
            public void run() {
                ClickListener.activate();
                for(int i = 0; i<clickInfoList.size();i++) {
                    ClickInfo clickInfo = clickInfoList.get(i);
                    if (!clickInfo.getPlayer().equals(player)) return;
                    if (x == null) {
                        x = new int[]{clickInfo.getX(), 0};
                        y = new int[]{clickInfo.getY(), 0};
                        z = new int[]{clickInfo.getZ(), 0};
                        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                    } else {
                        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                        x[1] = clickInfo.getX();
                        y[1] = clickInfo.getY();
                        z[1] = clickInfo.getZ();
                        Arrays.sort(x);
                        Arrays.sort(y);
                        Arrays.sort(z);
                        BlockVector3 max = BlockVector3.at(x[1], y[1], z[1]);
                        BlockVector3 min = BlockVector3.at(x[0], y[0], z[0]);
                        if (!(((max.getX() == min.getX() && max.getZ() - min.getZ() > 0 && (max.getZ() - min.getZ()) * (max.getY() - min.getY()) < 100) || (max.getZ() == min.getZ() && max.getX() - min.getX() > 0 && (max.getX() - min.getX()) * (max.getY() - min.getY()) < 100)) && max.getY() - min.getY() > 1)) {
                            player.sendMessage("§cDas Tor muss mindestens §43 Blöcke hoch §cund §42 Blöcke breit §csein, aber §4nicht größer als 100 Blöcke§c!");
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            ClickListener.deactivate();
                            cancel();
                            return;
                        }
                        if (EthosGates.getGateManager().createGate(player, max, min, overhang, dir)) {
                            player.sendMessage("§7Das Tor wurde erfolgreich erstellt.");
                        } else {
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            player.sendMessage("§cUnerwarteter Fehler beim erstellen des Tors. §4§lVersuche es bitte erneut und achte darauf, dass das Tor beim Erstellen geschlossen ist. §r§cSollte dieser Fehler trotzdem wieder auftreten, wende dich bitte an den Support (/ch s)");
                        }
                        ClickListener.deactivate();
                        cancel();
                    }
                    clickInfoList.remove(clickInfo);
                }
                if (repetitions++ >= 200) {
                    EthosGates.getGateManager().deleteGate(new File(gateDir));
                    player.sendMessage("§cTor Erstellung abgebrochen");
                    ClickListener.deactivate();
                    cancel();
                }
            }
        }.runTaskTimer(EthosGates.getInstance(), 0, 1);
    }

    public static void addClickInfo(Player p, int x, int y, int z) {
        ClickInfo clickInfo = new ClickInfo(p, x, y, z);
        for (ClickInfo clIn : clickInfoList) {
            if (Arrays.equals(new int[]{clickInfo.getX(), clickInfo.getY(), clickInfo.getZ()}, new int[]{clIn.getX(), clIn.getY(), clIn.getZ()})) return;
        }
        clickInfoList.add(clickInfo);
    }
}
