package me.jb.itemcheck.itemstorage;

import me.jb.itemcheck.item.CustomItem;
import me.jb.itemcheck.utils.async.AsyncCallback;

public interface ItemStorageService {

    void loadAllItems(AsyncCallback<Boolean> asyncCallback);

    void registerItem(CustomItem customItem);

    void unregisterItem(String id);

    void reload();

}
