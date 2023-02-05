package me.jb.itemcheck;

import me.jb.itemcheck.command.CmdItemCheck;
import me.jb.itemcheck.command.tabcompleter.TBItemCheck;
import me.jb.itemcheck.configuration.FileHandler;
import me.jb.itemcheck.itemstorage.ItemStorageDAO;
import me.jb.itemcheck.itemstorage.ItemStorageModel;
import me.jb.itemcheck.itemstorage.ItemStorageService;
import me.jb.itemcheck.itemstorage.impl.CraftItemStorageModel;
import me.jb.itemcheck.itemstorage.impl.CraftItemStorageService;
import me.jb.itemcheck.itemstorage.impl.SQLItemStorageDAO;
import me.jb.itemcheck.sql.DbConnection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class ItemCheck extends JavaPlugin {

    private final FileHandler fileHandler = new FileHandler(this);
    private ItemStorageService itemStorageService;
    private ItemStorageModel itemStorageModel;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Setup points service
        try {
            this.setupItemStorageService();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Registering commands
        this.registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    public void reloadPlugin() {
        this.fileHandler.reloadFiles();
        this.itemStorageService.reload();
    }

    private void registerCommands() {
        getCommand("itemcheck").setExecutor(new CmdItemCheck(this));
        getCommand("itemcheck").setTabCompleter(new TBItemCheck(this));
    }

    public ItemStorageService getItemStorageService() {
        return this.itemStorageService;
    }

    public ItemStorageModel getItemStorageModel() {
        return this.itemStorageModel;
    }

    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    private void setupItemStorageService() throws SQLException, ClassNotFoundException {
        ConfigurationSection section = this.fileHandler.getMainConfigFile().getConfig().getConfigurationSection("mysql");
        DbConnection dbConnection = new DbConnection(
                section.getString("host"),
                section.getString("database"),
                section.getString("username"),
                section.getString("password"));

        dbConnection.open();

        ItemStorageDAO pointsDAO = new SQLItemStorageDAO(dbConnection);
        this.itemStorageModel = new CraftItemStorageModel();
        this.itemStorageService = new CraftItemStorageService(this, this.itemStorageModel, pointsDAO);

        this.itemStorageService.loadAllItems(bool -> {
            if (!bool) this.getLogger().log(Level.SEVERE, "Error while set up item storage service.");
            else this.getLogger().log(Level.INFO, "Item storage service set.");
        });
    }
}
