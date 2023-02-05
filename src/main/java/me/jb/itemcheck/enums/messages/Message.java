package me.jb.itemcheck.enums.messages;

import com.sun.istack.internal.NotNull;

public enum Message {

    RELOAD("reload"),
    NO_PERM("noPerm"),
    NOT_PLAYER("needPlayer"),

    ITEM_HELP("itemCheckHelp"),

    ITEM_STORED("itemStored"),
    ITEM_REMOVED("itemRemoved"),
    ITEM_LIST("itemList"),
    ITEM_EMPTY_LIST("itemEmptyList"),
    ITEM_GET("itemGet"),
    ITEM_GIVE("itemGave"),
    ITEM_RECEIVED("itemReceived"),

    UNKNOWN_ITEM("unknownItem"),
    UNKNOWN_PLAYER("unknownPlayer"),
    EXIST_ITEM("alreadyExist"),
    NO_ITEM_IN_HAND("noItemInHand"),

    ITEM_DROP("itemDropped"),

    NOT_ENOUGH_ITEM_SENDER("notEnoughItemsSender"),
    NOT_ENOUGH_ITEM_PLAYER("notEnoughItemsPlayer"),

    SENDER_ITEM_CHECK("senderItemCheck"),
    PLAYER_ITEM_CHECK("playerItemCheck"),
    ;

    private final String key;

    Message(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return this.key;
    }
}

