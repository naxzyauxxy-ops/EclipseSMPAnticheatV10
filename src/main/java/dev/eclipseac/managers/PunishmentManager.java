package dev.eclipseac.managers;

import dev.eclipseac.EclipseAC;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentManager {

    private final EclipseAC plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String DISCORD = "https://discord.gg/zB6hQP99ZA";
    private static final int KICKS_BEFORE_BAN = 3;

    private final Map<UUID, Integer> kickCounts = new HashMap<>();

    public PunishmentManager(EclipseAC plugin) {
        this.plugin = plugin;
    }

    public void punish(Player player, String checkName, int vl) {
        if (!plugin.getConfig().getBoolean("punishments.enabled", true)) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) return;

            UUID uuid = player.getUniqueId();
            int kicks = kickCounts.getOrDefault(uuid, 0) + 1;
            kickCounts.put(uuid, kicks);

            if (kicks >= KICKS_BEFORE_BAN) {
                kickCounts.remove(uuid);
                player.kick(MM.deserialize(
                    "<red><bold>EclipseAC - Banned</bold></red>\n\n" +
                    "<gray>You have been banned for suspicious activity.\n\n" +
                    "<white>Check: <yellow>" + checkName + "\n\n" +
                    "<gray>Appeal at:\n<aqua>" + DISCORD
                ));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "ban " + player.getName() + " [EclipseAC] " + checkName +
                    " | Appeal: " + DISCORD);
                plugin.getLogger().info("[PUNISH] Banned " + player.getName() +
                    " | " + checkName + " VL:" + vl + " (kick " + kicks + "/" + KICKS_BEFORE_BAN + ")");
            } else {
                int remaining = KICKS_BEFORE_BAN - kicks;
                player.kick(MM.deserialize(
                    "<red><bold>EclipseAC</bold></red>\n\n" +
                    "<gray>Removed for suspicious activity.\n\n" +
                    "<white>Check: <yellow>" + checkName + "\n" +
                    "<gray>Kicks: <white>" + kicks + "<gray>/" + KICKS_BEFORE_BAN + "\n" +
                    "<red>" + remaining + " more kick(s) will result in a ban.\n\n" +
                    "<gray>Appeal at:\n<aqua>" + DISCORD
                ));
                plugin.getLogger().info("[PUNISH] Kicked " + player.getName() +
                    " | " + checkName + " VL:" + vl + " (kick " + kicks + "/" + KICKS_BEFORE_BAN + ")");
            }
        });
    }

    public void resetKicks(UUID uuid) { kickCounts.remove(uuid); }
    public int getKickCount(UUID uuid) { return kickCounts.getOrDefault(uuid, 0); }
}
