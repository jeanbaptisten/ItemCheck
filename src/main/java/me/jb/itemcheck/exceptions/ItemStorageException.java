package me.jb.itemcheck.exceptions;

public class ItemStorageException extends Exception {

    public ItemStorageException(String message) {
        super(message);
    }

    public ItemStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
