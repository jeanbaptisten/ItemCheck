package me.jb.itemcheck.command.tabcompleter;

import me.jb.itemcheck.ItemCheck;
import me.jb.itemcheck.enums.Permission;
import me.jb.itemcheck.itemstorage.ItemStorageModel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TBItemCheck implements TabCompleter {

    /*
   /itemcheck <pseudo> <identifiant_item> <quantité> <commande>
   /itemcheck additem <nom_de_l’item>
   /itemcheck removeitem <nom_de_l’item>
   /itemcheck get <id>
   /itemcheck list
     */

    private static final List<String> ALL_CMD_LIST = Arrays.asList("add", "remove", "get", "list", "reload");

    private final ItemStorageModel itemStorageModel;

    public TBItemCheck(ItemCheck itemCheck) {
        this.itemStorageModel = itemCheck.getItemStorageModel();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> cmdList = new ArrayList<>();

        switch (args.length) {
            case 1:
                if (sender.hasPermission(Permission.ADD.getPermission()))
                    cmdList.add("add");
                if (sender.hasPermission(Permission.REMOVE.getPermission()))
                    cmdList.add("remove");
                if (sender.hasPermission(Permission.GET.getPermission()))
                    cmdList.add("get");
                if (sender.hasPermission(Permission.LIST.getPermission()))
                    cmdList.add("list");
                if (sender.hasPermission(Permission.GIVE.getPermission()))
                    cmdList.add("give");
                if (sender.hasPermission(Permission.RELOAD.getPermission()))
                    cmdList.add("reload");
                if (sender.hasPermission(Permission.EXECUTE.getPermission()))
                    Bukkit.getOnlinePlayers().forEach(player -> cmdList.add(player.getName()));
                return cmdList;

            case 2:
                if (args[0].equalsIgnoreCase("remove") ||
                        args[0].equalsIgnoreCase("get"))
                    return this.itemStorageModel.getAllId();
                if (!ALL_CMD_LIST.contains(args[0]) && sender.hasPermission(Permission.EXECUTE.getPermission()))
                    return this.itemStorageModel.getAllId();

            case 3:
                if (!args[0].equalsIgnoreCase("add")
                        && !args[0].equalsIgnoreCase("remove")
                        && !args[0].equalsIgnoreCase("get"))
                    return IntStream.rangeClosed(0, 100).mapToObj(Integer::toString).collect(Collectors.toList());
        }

        return cmdList;
    }
}
