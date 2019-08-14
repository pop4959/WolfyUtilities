package me.wolfyscript.utilities.api.inventory;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuiHandler implements Listener {

    private WolfyUtilities api;
    private Player player;
    private boolean changingInv = false;
    private int testChatID = -1;
    private ChatInputAction chatInputAction = null;
    private String uuid;

    private List<String> pageHistory = new ArrayList<>();
    private boolean helpEnabled = false;

    private HashMap<String, HashMap<Integer, String>> cachedButtons = new HashMap<>();

    public GuiHandler(Player player, WolfyUtilities api) {
        this.api = api;
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    public boolean isChangingInv() {
        return changingInv;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public Player getPlayer() {
        return player;
    }

    public void testForNewPlayerInstance() {
        this.player = Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public boolean verifyInv() {
        if (!pageHistory.isEmpty()) {
            return !pageHistory.get(pageHistory.size() - 1).equals("none");
        }
        return false;
    }

    public void reloadInv(String inv) {
        if (!pageHistory.isEmpty()) {
            pageHistory.remove(pageHistory.size() - 1);
        }
        changeToInv(inv);
    }

    public GuiWindow getCurrentInv() {
        if (!pageHistory.isEmpty()) {
            return getApi().getInventoryAPI().getGuiWindow(pageHistory.get(pageHistory.size() - 1));
        }
        return null;
    }

    public GuiWindow getLastInv() {
        if (!pageHistory.isEmpty() && pageHistory.size() > 1) {
            return getApi().getInventoryAPI().getGuiWindow(pageHistory.get(pageHistory.size() - 2));
        }
        return null;
    }

    public void changeToInv(String inv) {
        Bukkit.getScheduler().runTask(getApi().getPlugin(), () -> {
            changingInv = true;
            player.closeInventory();
            if (WolfyUtilities.hasPermission(player, getApi().getPlugin().getDescription().getName().toLowerCase() + ".inv." + inv.toLowerCase())) {
                if (!pageHistory.isEmpty()) {
                    if (!pageHistory.get(pageHistory.size() - 1).equals(inv)) {
                        if (pageHistory.get(pageHistory.size() - 1).equals("none")) {
                            pageHistory.remove(pageHistory.size() - 1);
                        }
                        pageHistory.add(inv);
                    }
                } else {
                    pageHistory.add(inv);
                }
                if (api.getInventoryAPI().getGuiWindow(inv) != null) {
                    GuiUpdateEvent event = new GuiUpdateEvent(this, api.getInventoryAPI().getGuiWindow(inv));
                    Bukkit.getPluginManager().callEvent(event);
                    api.getInventoryAPI().getGuiWindow(inv).setCachedInventorie(this, event.getInventory());
                    player.openInventory(event.getInventory());
                }
            } else {
                api.sendPlayerMessage(player, "§4You don't have the permission §c" + getApi().getPlugin().getDescription().getName().toLowerCase() + ".inv." + inv.toLowerCase());
            }
            changingInv = false;
        });

    }

    public void openLastInv() {
        if (!pageHistory.isEmpty()) {
            String inv;
            if (getLastInv() != null) {
                inv = getLastInv().getNamespace();
            } else {
                inv = pageHistory.get(0);
            }
            pageHistory.remove(pageHistory.size() - 1);
            changeToInv(inv);
        }
    }

    public boolean isChatEventActive() {
        return (getTestChatID() > -1) || getChatInputAction() != null;
    }

    public ChatInputAction getChatInputAction() {
        return chatInputAction;
    }

    public void setChatInputAction(ChatInputAction chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    public void cancelChatEvent() {
        setTestChatID(-1);
    }

    public void close() {
        changeToInv("none");
    }

    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setButton(GuiWindow guiWindow, int slot, String id) {
        HashMap<Integer, String> buttons = cachedButtons.getOrDefault(guiWindow.getNamespace(), new HashMap<>());
        buttons.put(slot, id);
        cachedButtons.put(guiWindow.getNamespace(), buttons);
    }

    public Button getButton(GuiWindow guiWindow, int slot) {
        String id = cachedButtons.getOrDefault(guiWindow.getNamespace(), new HashMap<>()).get(slot);
        if (id != null && !id.isEmpty() && id.contains(":")) {
            return api.getInventoryAPI().getButton(id.split(":")[0], id.split(":")[1]);
        }
        return guiWindow.getButton(id);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        Player eventPlayer = (Player) event.getPlayer();
        if (player.equals(eventPlayer)) {
            if (!pageHistory.isEmpty() && verifyInv()) {
                if (!changingInv) {
                    pageHistory.add("none");
                }
            }
        }
    }

    @Deprecated
    public String verifyItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                String[] splitted = item.getItemMeta().getDisplayName().split("§:§:");
                if (splitted.length >= 3) {
                    if (WolfyUtilities.unhideString(splitted[splitted.length - 1]).equals(Main.getMainConfig().getString("securityCode"))) {
                        return WolfyUtilities.unhideString(splitted[splitted.length - 2]);
                    }
                }
            }
        }
        return "";
    }

    @Deprecated
    public ItemStack getItem(String namespace, String id) {
        return getApi().getInventoryAPI().getItem(namespace, id, helpEnabled);
    }

    @Deprecated
    public int getTestChatID() {
        return testChatID;
    }

    @Deprecated
    public void setTestChatID(int testChatID) {
        this.testChatID = testChatID;
    }
}
