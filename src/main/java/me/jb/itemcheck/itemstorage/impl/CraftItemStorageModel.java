package me.jb.itemcheck.itemstorage.impl;

import me.jb.itemcheck.item.CustomItem;
import me.jb.itemcheck.itemstorage.ItemStorageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftItemStorageModel implements ItemStorageModel {

    private final List<CustomItem> customItemList = new ArrayList<>();

    @Override
    public void addItem(CustomItem customItem) {
        if (this.containItem(customItem.getId()))
            throw new IllegalArgumentException("CustomItem (id:" + customItem.getId() + ") already exist !");

        this.customItemList.add(customItem);
    }

    @Override
    public void removeItem(CustomItem customItem) {
        if (!this.containItem(customItem.getId()))
            throw new IllegalArgumentException("CustomItem (id:" + customItem.getId() + ") doesn't exist !");

        this.customItemList.remove(customItem);
    }

    @Override
    public void removeItem(String id) {
        Optional<CustomItem> customItemOptional = this.getById(id);

        if (!customItemOptional.isPresent())
            throw new IllegalArgumentException("CustomItem (id:" + id + ") doesn't exist !");

        this.customItemList.remove(customItemOptional.get());
    }

    @Override
    public boolean containItem(String id) {
        return customItemList.stream().anyMatch(customItem -> customItem.getId().equals(id));
    }

    @Override
    public Optional<CustomItem> getById(String id) {
        return customItemList.stream().filter(customItem -> customItem.getId().equals(id)).findAny();
    }

    @Override
    public List<String> getAllId() {
        List<String> idList = new ArrayList<>();
        this.customItemList.forEach(customItem -> idList.add(customItem.getId()));

        return idList;
    }

    @Override
    public void clearData() {
        this.customItemList.clear();
    }

}
