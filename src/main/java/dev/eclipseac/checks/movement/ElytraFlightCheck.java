package dev.eclipseac.checks.movement;

import dev.eclipseac.EclipseAC;
import dev.eclipseac.checks.Check;
import dev.eclipseac.managers.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class ElytraFlightCheck extends Check {

    public ElytraFlightCheck(EclipseAC plugin) {
        super(plugin, "ElytraFlight");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!p.isGliding()) return;
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        if (p.hasPermission("eclipseac.bypass")) return;
        if (!plugin.getConfig().getBoolean("checks.ElytraFlight.enabled", true)) return;

        PlayerData data = plugin.getDataManager().getData(p);

        double hSpeed = Math.sqrt(
            Math.pow(e.getTo().getX() - e.getFrom().getX(), 2) +
            Math.pow(e.getTo().getZ() - e.getFrom().getZ(), 2)
        );

        // Only flag if moving extremely fast while gliding (actual elytra hacks, not normal use)
        if (hSpeed > 5.0) {
            data.elytraVl++;
            if (data.elytraVl > 10) {
                flag(p, "speed=" + String.format("%.2f", hSpeed));
                data.elytraVl = 0;
            }
        } else {
            if (data.elytraVl > 0) data.elytraVl--;
        }
    }
}
