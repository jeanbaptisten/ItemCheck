package me.jb.itemcheck.itemstorage.impl;

import com.sun.istack.internal.NotNull;
import me.jb.itemcheck.ItemCheck;
import me.jb.itemcheck.exceptions.ItemStorageException;
import me.jb.itemcheck.item.CustomItem;
import me.jb.itemcheck.itemstorage.ItemStorageDAO;
import me.jb.itemcheck.itemstorage.ItemStorageModel;
import me.jb.itemcheck.itemstorage.ItemStorageService;
import me.jb.itemcheck.utils.async.AsyncCallback;
import me.jb.itemcheck.utils.async.AsyncRequest;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.logging.Level;

public class CraftItemStorageService implements ItemStorageService {

    private final ItemCheck itemCheck;
    private final ItemStorageModel itemStorageModel;
    private final ItemStorageDAO itemStorageDAO;

    public CraftItemStorageService(ItemCheck itemCheck, ItemStorageModel itemStorageModel, ItemStorageDAO itemStorageDAO) {
        this.itemCheck = itemCheck;
        this.itemStorageModel = itemStorageModel;
        this.itemStorageDAO = itemStorageDAO;
    }

    @Override
    public void loadAllItems(AsyncCallback<Boolean> asyncCallback) {
        new AsyncRequest(this.itemCheck).executeTask(() -> {
            try {
                Collection<CustomItem> customItems = this.itemStorageDAO.loadAllItems();

                if (customItems.isEmpty())
                    return;

                customItems.forEach(this.itemStorageModel::addItem);

                this.runSync(() -> asyncCallback.onResponse(true));
            } catch (ItemStorageException e) {
                this.runSync(() -> asyncCallback.onResponse(false));
                e.printStackTrace();
            }
        });
    }

    @Override
    public void registerItem(CustomItem customItem) {
        new AsyncRequest(this.itemCheck).executeTask(() -> this.itemStorageDAO.saveItem(customItem));
        this.itemStorageModel.addItem(customItem);
    }

    @Override
    public void unregisterItem(String id) {
        new AsyncRequest(this.itemCheck).executeTask(() -> this.itemStorageDAO.deleteItem(id));
        this.itemStorageModel.removeItem(id);
    }

    @Override
    public void reload() {
        this.itemStorageModel.clearData();

        this.loadAllItems(bool -> {
            if (!bool) this.itemCheck.getLogger().log(Level.SEVERE, "Error while set up item storage service.");
            else this.itemCheck.getLogger().log(Level.INFO, "Item storage service reload.");
        });
    }

    private void runSync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(this.itemCheck, runnable);
    }
}
