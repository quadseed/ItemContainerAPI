package net.quadseed.minecraft.itemcontainerapi;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class ItemContainerManager {

    private final Plugin plugin;

    private final Player player;

    public ItemContainerManager(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void registerNameSpace(String namespaceKey) {
        PersistentDataContainer data = this.getPlayer().getPersistentDataContainer();
        if (!data.has(new NamespacedKey(this.getPlugin(),namespaceKey), PersistentDataType.STRING)) {
            data.set(new NamespacedKey(this.getPlugin(), namespaceKey), PersistentDataType.STRING, "");
        }
    }

    public void clearItems(String namespaceKey) {
        PersistentDataContainer data = this.getPlayer().getPersistentDataContainer();
        data.set(new NamespacedKey(this.getPlugin(), namespaceKey), PersistentDataType.STRING, "");
    }

    public void storeItems(String namespaceKey, List<ItemStack> items) {

        PersistentDataContainer data = this.getPlayer().getPersistentDataContainer();

        if (items.size() == 0) {
            this.clearItems(namespaceKey);
            return;
        }

        try {
            ByteArrayOutputStream byteArrayIO = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(byteArrayIO);

            outputStream.writeInt(items.size());
            for (ItemStack item : items) {
                outputStream.writeObject(item);
            }
            outputStream.flush();

            data.set(new NamespacedKey(this.getPlugin(), namespaceKey), PersistentDataType.STRING, Base64.getEncoder().encodeToString(byteArrayIO.toByteArray()));

            outputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public ArrayList<ItemStack> getItems(String namespaceKey) {

        PersistentDataContainer data = this.getPlayer().getPersistentDataContainer();

        ArrayList<ItemStack> items = new ArrayList<>();

        String encodedData = data.get(new NamespacedKey(this.getPlugin(), namespaceKey), PersistentDataType.STRING);

        if ((encodedData != null) && !(encodedData.isEmpty())) {
            try {
                ByteArrayInputStream byteArrayIO = new ByteArrayInputStream(Base64.getDecoder().decode(encodedData));
                BukkitObjectInputStream inputStream = new BukkitObjectInputStream(byteArrayIO);

                int itemChunks = inputStream.readInt();

                for (int i = 0; i < itemChunks; i++) {
                    items.add((ItemStack) inputStream.readObject());
                }

                inputStream.close();

            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
        return items;
    }
}
