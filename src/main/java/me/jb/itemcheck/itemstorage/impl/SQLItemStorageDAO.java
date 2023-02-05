package me.jb.itemcheck.itemstorage.impl;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import me.jb.itemcheck.exceptions.ItemStorageException;
import me.jb.itemcheck.item.CustomItem;
import me.jb.itemcheck.itemstorage.ItemStorageDAO;
import me.jb.itemcheck.sql.DbConnection;
import me.jb.itemcheck.utils.nms.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLItemStorageDAO implements ItemStorageDAO {

    private final DbConnection dbConnection;
    private static final Gson GSON = new Gson();

    public SQLItemStorageDAO(@NotNull DbConnection dbConnection) {
        if (dbConnection == null)
            throw new IllegalArgumentException("Database connection cannot be null");
        this.dbConnection = dbConnection;

        this.initTable();
    }

    @Override
    public Collection<CustomItem> loadAllItems() throws ItemStorageException {
        List<CustomItem> customItemList = new ArrayList<>();

        String sqlQuery = "SELECT * FROM items;";
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String itemdbString = resultSet.getString("item_base64");

                ItemStack storedItem = ItemStackUtils.deserializeItemStack(itemdbString);
                CustomItem storedCustomItem = new CustomItem(resultSet.getString("item_id"), storedItem);

                customItemList.add(storedCustomItem);
            }


        } catch (SQLException troubles) {
            throw new ItemStorageException("Error while getting items from database.", troubles);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return customItemList;
    }

    @Override
    public void saveItem(CustomItem customItem) {
        String stringSQL = "INSERT INTO items (item_id, item_base64)" +
                "VALUES (?,?);";
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(stringSQL)) {

             String serializedItem = ItemStackUtils.serializeItemStack(customItem.getItemStack());

            statement.setString(1, customItem.getId());
            statement.setString(2, serializedItem);

            statement.execute();
        } catch (SQLException | IOException troubles) {
            troubles.printStackTrace();
        }
    }

    @Override
    public void deleteItem(CustomItem customItem) {
        String stringSQL = "DELETE FROM items WHERE item_id=?;";

        Connection connection = dbConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(stringSQL)) {

            statement.setString(1, customItem.getId());
            statement.execute();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    @Override
    public void deleteItem(String id) {
        String stringSQL = "DELETE FROM items WHERE item_id=?;";

        Connection connection = dbConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(stringSQL)) {

            statement.setString(1, id);
            statement.execute();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }

    private void initTable() {
        String sqlQueryCreate = "CREATE TABLE IF NOT EXISTS items" +
                "(" +
                "item_id VARCHAR(16) NOT NULL," +
                "item_base64 VARCHAR(8192) NOT NULL," +
                "PRIMARY KEY (item_id)" +
                ");";

        Connection connection = this.dbConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sqlQueryCreate)) {
            statement.execute();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }
}
