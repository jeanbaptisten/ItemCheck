package me.jb.itemcheck.itemstorage;

import me.jb.itemcheck.item.CustomItem;

import java.util.List;
import java.util.Optional;

public interface ItemStorageModel {

    void addItem(CustomItem customItem);

    void removeItem(CustomItem customItem);

    void removeItem(String id);

    boolean containItem(String id);

    Optional<CustomItem> getById(String id);

    List<String> getAllId();

    void clearData();

}
