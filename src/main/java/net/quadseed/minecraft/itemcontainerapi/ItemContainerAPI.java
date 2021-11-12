package net.quadseed.minecraft.itemcontainerapi;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemContainerAPI extends JavaPlugin {

    private static final String version = "1.0-Stable";

    @Override
    public void onEnable() {
        getLogger().info( ChatColor.DARK_AQUA + "ItemContainerAPI " + "v" + version + " has been loaded");
    }

}
