package me.jb.itemcheck.item;

import com.sun.istack.internal.NotNull;
import org.bukkit.inventory.ItemStack;

public class CustomItem {

    private final String id;
    private final ItemStack itemStack;

    public CustomItem(String id, @NotNull ItemStack itemStack) {
        if (id.trim().equals(""))
            throw new IllegalArgumentException("Item id cannot be empty");

        this.id = id;
        this.itemStack = itemStack;
    }

    public String getId() {
        return this.id;
    }

    public ItemStack getItemStack() {
        return this.itemStack.clone();
    }

}
