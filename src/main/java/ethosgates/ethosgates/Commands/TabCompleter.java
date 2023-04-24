package ethosgates.ethosgates.Commands;

import org.apache.commons.io.filefilter.RegexFileFilter;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, final String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Block targ = player.getTargetBlock(null, 5);
            switch (args.length) {
                case 1:
                    List<String> arguments = new ArrayList<>();
                    arguments.add("erstellen");
                    arguments.add("löschen");
                    arguments.add("auflisten");
                    arguments.add("hilfe");
                    return arguments;
                case 2:
                    if (Objects.equals(args[0], "erstellen")) {
                        List<String> coordinates = new ArrayList<>();
                        coordinates.add(targ.getX() + "");
                        coordinates.add(targ.getX() + " " + targ.getY());
                        coordinates.add(targ.getX() + " " + targ.getY() + " " + targ.getZ());
                        coordinates.add("[Torname]");
                        return coordinates;
                    } else if (Objects.equals(args[0], "löschen")) {
                        List<String> gateNames = new ArrayList<>();
                        File dir = new File("./plugins/EthosGates/");
                        FileFilter fileFilter = new RegexFileFilter("^.* " + player.getUniqueId() + ".*$");
                        File[] files = dir.listFiles(fileFilter);
                        if (Objects.requireNonNull(dir.listFiles(fileFilter)).length == 0) {
                            gateNames.add(ChatColor.YELLOW + "Du hast keine Tore zum löschen.");
                            return gateNames;
                        }
                        assert files != null;
                        String pattern = ".*" + player.getUniqueId();
                        for (File file : files) {
                            String gateName = file.toString().replaceAll(pattern, "");
                            gateNames.add(gateName);
                        }
                        return gateNames;
                    } else return Collections.singletonList("");
                case 3: if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    if (!Objects.equals(args[1], targ.getX() + "")) return Collections.singletonList("[ÜberhangHöhe]");
                    List<String> coordinates = new ArrayList<>();
                    coordinates.add(targ.getY() + "");
                    coordinates.add(targ.getY() + " " + targ.getZ());
                    return coordinates;
                case 4:
                case 7:
                    if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    coordinates = new ArrayList<>();
                    coordinates.add(targ.getZ() + "");
                    return coordinates;
                case 5:
                    if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    coordinates = new ArrayList<>();
                    coordinates.add(targ.getX() + "");
                    coordinates.add(targ.getX() + " " + targ.getY());
                    coordinates.add(targ.getX() + " " + targ.getY() + " " + targ.getZ());
                    return coordinates;
                case 6:
                    if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    coordinates = new ArrayList<>();
                    coordinates.add(targ.getY() + "");
                    coordinates.add(targ.getY() + " " + targ.getZ());
                    return coordinates;
                case 8:
                    if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    return Collections.singletonList("[Torname]");
                case 9:
                    if (!Objects.equals(args[0], "erstellen")) return Collections.singletonList("");
                    return Collections.singletonList("[ÜberhangHöhe]");
                default: return Collections.singletonList("");
            }
        }
        return null;
    }
}
