package ethosgates.ethosgates.Commands;

import ethosgates.ethosgates.EthosGates;
import ethosgates.ethosgates.utils.ClickInfo;
import ethosgates.ethosgates.utils.PlayerClickCordsGetter;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.io.filefilter.RegexFileFilter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.*;


public class Commands implements CommandExecutor {
    private static final List<ClickInfo> clickInfoList = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (command.getName().equalsIgnoreCase("tor")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage("§cAnwendung: §6/tor §e<§6erstellen§e|§6löschen§e|§6auflisten§e|§6hilfe§e>");
                    return false;
                }
                switch (args[0].toLowerCase()) {
                    case "erstellen": {
                        if (args.length != 2 && args.length != 3 && args.length != 8 && args.length != 9) {
                            player.sendMessage("§cAnwendung: §6/tor erstellen x1 y1 z1 x2 y2 z2 §e[Torname] <Überhanghöhe> §6oder /tor erstellen §e[Torname] <Überhanghöhe>");
                            return false;
                        }
                        int overhang = 0;
                        if (args.length < 4 ) {
                            if (args.length == 3) {
                                try {
                                    overhang = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    player.sendMessage("§cDie Überhanghöhe muss eine positive Ganzzahl sein");
                                }
                            }
                            String gateName = args[1];
                            File dir = new File("./plugins/EthosGates/");
                            //check if gateName already exists
                            FileFilter fileFilter = new RegexFileFilter("^\\d* " + player.getUniqueId() + gateName + "$");
                            File[] files = dir.listFiles(fileFilter);
                            if (files == null) return true;
                            if (files.length == 0) {
                                dir = new File(dir, EthosGates.getCurrentGateID() + " " + player.getUniqueId() + gateName + "/schematics/");
                                dir.mkdirs();
                            } else {
                                player.sendMessage("§cDu hats schon ein Tor mit dem Namen §4" + gateName + " §cerstellt.");
                                return false;
                            }
                            new PlayerClickCordsGetter(player).clickCreateGate(overhang, dir);
                            return true;
                        }

                        //sort min and max BlockVector3
                        int[] x = new int[2];
                        int[] y = new int[2];
                        int[] z = new int[2];
                        try {
                            x = new int[]{Integer.parseInt(args[1]), Integer.parseInt(args[4])};
                            y = new int[]{Integer.parseInt(args[2]), Integer.parseInt(args[5])};
                            z = new int[]{Integer.parseInt(args[3]), Integer.parseInt(args[6])};
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cAnwendung: §6/tor erstellen x1 y1 z1 x2 y2 z2 §e[Torname] <Überhanghöhe>");
                            e.printStackTrace();
                        }
                        Arrays.sort(x);
                        Arrays.sort(y);
                        Arrays.sort(z);
                        BlockVector3 max = BlockVector3.at(x[1], y[1], z[1]);
                        BlockVector3 min = BlockVector3.at(x[0], y[0], z[0]);

                        //check if gate has valid dimensions
                        if (!(((max.getX() == min.getX() && max.getZ() - min.getZ() > 0 && (max.getZ() - min.getZ()) * (max.getY() - min.getY()) < 100) || (max.getZ() == min.getZ() && max.getX() - min.getX() > 0 && (max.getX() - min.getX()) * (max.getY() - min.getY()) < 100)) && max.getY() - min.getY() > 1)) {
                            player.sendMessage("§cDas Tor muss mindestens §43 Blöcke hoch §cund §42 Blöcke breit §csein, aber §4nicht größer als 100 Blöcke§c!");
                            return false;
                        }

                        String gateName = args[7];
                        File dir = new File("./plugins/EthosGates/");
                        //check if gateName already exists
                        FileFilter fileFilter = new RegexFileFilter("^\\d* " + player.getUniqueId() + gateName + "$");
                        File[] files = dir.listFiles(fileFilter);
                        assert files != null;
                        if (files.length == 0) {
                            dir = new File(dir, EthosGates.getCurrentGateID() + " " + player.getUniqueId() + gateName + "/schematics/");
                            dir.mkdirs();
                        } else {
                            player.sendMessage("§cDu hats schon ein Tor mit dem Namen §4" + gateName + " §cerstellt.");
                            return false;
                        }

                        final String gateDir = dir.toString().replace("/schematics", "").replace("\\schematics", "");
                        if (args.length == 9) {
                            try {
                                if (!((overhang = Integer.parseInt(args[8])) < max.getY() - min.getY() - 1)) {
                                    player.sendMessage("§cIm offenen Zustand muss das Tor mindestens §43 Blöcke hoch frei §csein. ");
                                    EthosGates.getGateManager().deleteGate(new File(gateDir));
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                player.sendMessage("§cDie Überhanghöhe muss eine positive Ganzzahl sein");
                                EthosGates.getGateManager().deleteGate(new File(gateDir));
                                return true;
                            }
                        }

                        if (EthosGates.getGateManager().createGate(player, max, min, overhang, dir)) {
                            EthosGates.increaseCurrentGateID();
                            player.sendMessage("§7Das Tor §8" + gateName + " §7wurde erfolgreich erstellt.");
                        } else {
                            EthosGates.getGateManager().deleteGate(new File(gateDir));
                            player.sendMessage("§cUnerwarteter Fehler beim erstellen des Tors. §4§lVersuche es bitte erneut und achte darauf, dass das Tor beim Erstellen geschlossen ist und nur aus legalen Blöcken besteht. §r§cSollte dieser Fehler trotzdem wieder auftreten, wende dich bitte an den Support (/ch s)");
                        }
                        break;
                    }
                    case "auflisten": {
                        File dir = new File("./plugins/EthosGates/");
                        FileFilter fileFilter = new RegexFileFilter("^.* " + player.getUniqueId() + ".*$");
                        File[] files = dir.listFiles(fileFilter);
                        if (Objects.requireNonNull(dir.listFiles(fileFilter)).length == 0) {
                            player.sendMessage(ChatColor.YELLOW + "Du hast noch kein Tor erstellt!");
                        }
                        if (files == null) return false;
                        if (files.length == 0) return true;
                        if (files.length == 1) {
                            player.sendMessage("§6Dein Tor:");
                        } else {
                            player.sendMessage("§6Deine Tore:");
                        }
                        String pattern = ".*" + player.getUniqueId();
                        for (File file : files) {
                            String gateName = file.toString().replaceAll(pattern, "");
                            player.sendMessage(ChatColor.GRAY + gateName);
                        }
                    }
                    break;
                    case "löschen": {
                        if (args.length != 2) {
                            player.sendMessage("§cAnwendung: §6/tor löschen §e[Torname]");
                            return false;
                        }
                        String gateName = args[1];
                        File dir = new File("./plugins/EthosGates/");
                        FileFilter fileFilter = new RegexFileFilter("^\\d* " + player.getUniqueId() + gateName + "$");
                        File[] files = dir.listFiles(fileFilter);
                        if (Objects.requireNonNull(files).length == 0) {
                            player.sendMessage("§cDas Tor §4" + gateName + " §cwurde nicht gefunden!");
                        } else {
                            EthosGates.getGateManager().deleteGate(Objects.requireNonNull(files)[0]);
                            player.sendMessage("§7Erfolgreich Tor §8" + gateName + " §7gelöscht.");
                        }
                    }
                    break;
                    case "hilfe": {
                        player.sendMessage("§eVerwende den §6/tor erstellen §eBefehl, um ein Tor zu speichern. Platziere nun ein Schild in die Nähe des Tors und schreibe in die erste Zeile §6[TOR] §eund in die zweite Zeile den §6Tornamen§e, den du zuvor vergeben hast. Jetzt kannst du mit einem §6Klick auf das Schild §edas Tor öffnen, oder schließen. Das Tor schließt und öffnet sich auch per §6Redstonesignal auf das Schild§e.");
                    }
                    break;
                    default: {
                        player.sendMessage("§cAnwendung: §6/tor §e<§6erstellen§e|§6löschen§e|§6auflisten§e|§6hilfe§e>");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
