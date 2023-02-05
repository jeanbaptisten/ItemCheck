package me.jb.itemcheck.command;

import com.sun.istack.internal.NotNull;
import me.jb.itemcheck.ItemCheck;
import me.jb.itemcheck.configuration.FileHandler;
import me.jb.itemcheck.enums.Permission;
import me.jb.itemcheck.enums.messages.Message;
import me.jb.itemcheck.item.CustomItem;
import me.jb.itemcheck.itemstorage.ItemStorageModel;
import me.jb.itemcheck.itemstorage.ItemStorageService;
import me.jb.itemcheck.utils.MessageUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CmdItemCheck implements CommandExecutor {

    private final ItemCheck itemCheck;
    private final FileHandler fileHandler;
    private final ItemStorageService itemStorageService;
    private final ItemStorageModel itemStorageModel;

    public CmdItemCheck(@NotNull ItemCheck itemCheck) {
        this.itemCheck = itemCheck;
        this.fileHandler = itemCheck.getFileHandler();
        this.itemStorageService = itemCheck.getItemStorageService();
        this.itemStorageModel = itemCheck.getItemStorageModel();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration mainConfig = this.fileHandler.getMainConfigFile().getConfig();

        if (!sender.hasPermission(Permission.USE.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return true;
        }

        if (args.length == 0) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return true;
        }

        switch (args[0]) {
            case "add":
                this.onItemAdd(mainConfig, sender, args);
                break;
            case "remove":
                this.onItemRemove(mainConfig, sender, args);
                break;
            case "list":
                this.onItemList(mainConfig, sender, args);
                break;
            case "reload":
                this.onReload(mainConfig, sender, args);
                break;
            case "get":
                this.onGetItem(mainConfig, sender, args);
                break;
            case "give":
                this.onGive(mainConfig, sender, args);
                break;
            default:
                this.onItemCheck(mainConfig, sender, args);
                break;
        }

        return true;
    }

    // /itemcheck add <id>
    private void onItemAdd(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            String notPlayerMessage = mainConfig.getString(Message.NOT_PLAYER.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(notPlayerMessage));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(Permission.ADD.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 2) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            String alreadyExistItemMessage = mainConfig.getString(Message.NO_ITEM_IN_HAND.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }


        if (this.itemStorageModel.containItem(args[1])) {
            String alreadyExistItemMessage = mainConfig.getString(Message.EXIST_ITEM.getKey()).replace("%id%", args[1]);
            player.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }

        CustomItem newCustomItem = new CustomItem(args[1], player.getInventory().getItemInMainHand().clone());
        this.itemStorageService.registerItem(newCustomItem);

        String itemStoredMessage = mainConfig.getString(Message.ITEM_STORED.getKey()).replace("%id%", args[1]);
        player.sendMessage(MessageUtils.setColorsMessage(itemStoredMessage));
    }

    // /itemcheck remove <id>
    private void onItemRemove(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            String helpVoteMessage = mainConfig.getString(Message.NOT_PLAYER.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(Permission.REMOVE.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 2) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        if (!this.itemStorageModel.containItem(args[1])) {
            String alreadyExistItemMessage = mainConfig.getString(Message.UNKNOWN_ITEM.getKey()).replace("%id%", args[1]);
            player.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }

        this.itemStorageService.unregisterItem(args[1]);
        String itemRemovedMessage = mainConfig.getString(Message.ITEM_REMOVED.getKey()).replace("%id%", args[1]);
        player.sendMessage(MessageUtils.setColorsMessage(itemRemovedMessage));
    }

    // /itemcheck list
    public void onItemList(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permission.LIST.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 1) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        List<String> idList = this.itemStorageModel.getAllId();

        if (idList.isEmpty()) {
            String idEmptyListMessage = mainConfig.getString(Message.ITEM_EMPTY_LIST.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(idEmptyListMessage));
            return;
        }

        StringBuilder idListBuilder = new StringBuilder();

        idListBuilder.append(idList.get(0));
        idList.remove(0);
        idList.forEach(id -> idListBuilder.append(", ").append(id));

        String idListMessage = mainConfig.getString(Message.ITEM_LIST.getKey()).replace("%list%", idListBuilder.toString());
        sender.sendMessage(MessageUtils.setColorsMessage(idListMessage));
    }

    // /itemcheck reload
    private void onReload(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permission.RELOAD.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 1) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        this.itemCheck.reloadPlugin();
        String reloadMessage = mainConfig.getString(Message.RELOAD.getKey());
        sender.sendMessage(MessageUtils.setColorsMessage(reloadMessage));
    }

    // /itemcheck get <id>
    private void onGetItem(FileConfiguration mainConfig, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            String helpVoteMessage = mainConfig.getString(Message.NOT_PLAYER.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(Permission.GET.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 2) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            player.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        Optional<CustomItem> customItemOptional = this.itemStorageModel.getById(args[1]);

        if (!customItemOptional.isPresent()) {
            String alreadyExistItemMessage = mainConfig.getString(Message.UNKNOWN_ITEM.getKey()).replace("%id%", args[1]);
            player.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }

        CustomItem customItem = customItemOptional.get();

        player.getInventory().addItem(customItem.getItemStack());
        String itemGetMessage = mainConfig.getString(Message.ITEM_GET.getKey()).replace("%id%", args[1]);
        player.sendMessage(MessageUtils.setColorsMessage(itemGetMessage));
    }

    // /itemcheck give <id> <nb> <pseudo>
    private void onGive(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permission.GIVE.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length != 4) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        if (this.isntStringInt(args[2])) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        Player getter = Bukkit.getPlayer(args[3]);

        if (getter == null) {
            String unknownPlayerMessage = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%id%", args[3]);
            sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMessage));
            return;
        }

        Optional<CustomItem> customItemOptional = this.itemStorageModel.getById(args[1]);

        if (!customItemOptional.isPresent()) {
            String alreadyExistItemMessage = mainConfig.getString(Message.UNKNOWN_ITEM.getKey()).replace("%id%", args[1]);
            sender.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }

        CustomItem customItem = customItemOptional.get();
        ItemStack selectedItem = customItem.getItemStack();

        selectedItem.setAmount(Integer.parseInt(args[2]));
        if (getter.getInventory().firstEmpty() == -1) {
            getter.getWorld().dropItemNaturally(getter.getLocation(), selectedItem);

            String itemDroppedMessage = mainConfig.getString(Message.ITEM_DROP.getKey());
            getter.sendMessage(MessageUtils.setColorsMessage(itemDroppedMessage));
        } else {
            getter.getInventory().addItem(selectedItem);
        }

        String itemGaveMessage = mainConfig.getString(Message.ITEM_GIVE.getKey())
                .replace("%id%", args[1])
                .replace("%nb%", args[2]);
        if (itemGaveMessage.equals("none"))
            sender.sendMessage(MessageUtils.setupMessage(getter, itemGaveMessage));

        String itemReceivedMessage = mainConfig.getString(Message.ITEM_RECEIVED.getKey())
                .replace("%id%", args[1])
                .replace("%nb%", args[2]);
        if (itemReceivedMessage.equals("none"))
            getter.sendMessage(MessageUtils.setupMessage(getter, itemReceivedMessage));
    }

    // /itemcheck <pseudo> <identifiant_item> <quantité> <commande>
    private void onItemCheck(FileConfiguration mainConfig, CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permission.EXECUTE.getPermission())) {
            String noPermMessage = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMessage));
            return;
        }

        if (args.length < 4) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            String unknownPlayerMessage = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%id%", args[1]);
            sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMessage));
            return;
        }

        if (this.isntStringInt(args[2])) {
            String helpVoteMessage = mainConfig.getString(Message.ITEM_HELP.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(helpVoteMessage));
            return;
        }

        Optional<CustomItem> customItemOptional = this.itemStorageModel.getById(args[1]);

        if (!customItemOptional.isPresent()) {
            String alreadyExistItemMessage = mainConfig.getString(Message.UNKNOWN_ITEM.getKey()).replace("%id%", args[1]);
            sender.sendMessage(MessageUtils.setColorsMessage(alreadyExistItemMessage));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        int numberOfItems = Integer.parseInt(args[2]);
        ItemStack customItem = customItemOptional.get().getItemStack();

        if (!player.getInventory().containsAtLeast(customItem, numberOfItems)) {
            String notEnoughItemsSenderMessage = mainConfig.getString(Message.NOT_ENOUGH_ITEM_SENDER.getKey());
            assert notEnoughItemsSenderMessage != null;
            if (!notEnoughItemsSenderMessage.equals("none"))
                sender.sendMessage(MessageUtils.setupMessage(player, notEnoughItemsSenderMessage));

            String notEnoughItemsPlayerMessage = mainConfig.getString(Message.NOT_ENOUGH_ITEM_PLAYER.getKey());
            assert notEnoughItemsPlayerMessage != null;
            if (!notEnoughItemsPlayerMessage.equals("none"))
                player.sendMessage(MessageUtils.setupMessage(player, notEnoughItemsPlayerMessage));

            return;
        }

        customItem.setAmount(numberOfItems);

        player.getInventory().removeItem(new ItemStack(customItem));

        List<String> cmdList = this.getCommands(player, args);

        cmdList.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));

        String playerItemRemoveMessage = mainConfig.getString(Message.PLAYER_ITEM_CHECK.getKey()).replace("%id%", args[1]);
        if (!playerItemRemoveMessage.equals("none"))
            player.sendMessage(MessageUtils.setupMessage(player, playerItemRemoveMessage));

        String senderItemRemoveMessage = mainConfig.getString(Message.SENDER_ITEM_CHECK.getKey())
                .replace("%nb%", String.valueOf(numberOfItems))
                .replace("%id%", args[1]);
        if (!senderItemRemoveMessage.equals("none"))
            sender.sendMessage(MessageUtils.setupMessage(player, senderItemRemoveMessage));

    }

    private List<String> getCommands(Player player, String[] args) {

        List<String> allCmdStrings = Arrays.stream(args)
                .skip(3)
                .collect(Collectors.toList());

        StringBuilder cmdBuild = new StringBuilder();

        for (String cmdString : allCmdStrings)
            cmdBuild.append(cmdString).append(" ");

        String allCmd = cmdBuild.toString();

        List<String> allRowCmdList = Arrays.asList(allCmd.split(","));
        List<String> allCmdList = new ArrayList<>();
        allRowCmdList.forEach(cmd -> allCmdList.add(PlaceholderAPI.setPlaceholders(player, cmd.trim())));

        return allCmdList;
    }

    private boolean isntStringInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }
}
