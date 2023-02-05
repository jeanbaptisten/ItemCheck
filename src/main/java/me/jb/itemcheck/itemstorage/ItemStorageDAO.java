package me.jb.itemcheck.itemstorage;

import me.jb.itemcheck.exceptions.ItemStorageException;
import me.jb.itemcheck.item.CustomItem;

import java.util.Collection;

public interface ItemStorageDAO {

    Collection<CustomItem> loadAllItems() throws ItemStorageException;

    void saveItem(CustomItem customItem);

    void deleteItem(CustomItem customItem);

    void deleteItem(String id);

}
