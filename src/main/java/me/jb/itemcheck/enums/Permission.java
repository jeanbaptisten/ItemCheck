package me.jb.itemcheck.enums;

import com.sun.istack.internal.NotNull;

public enum Permission {

    USE,
    RELOAD,
    ADD,
    REMOVE,
    LIST,
    GET,
    GIVE,
    EXECUTE,

    ;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    @NotNull
    public String getPermission() {
        return "itemcheck." + this.permission;
    }
}