package com.nopluginsfound;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.HashSet;
import java.util.Set;

public class NoPluginsFoundListener implements Listener {

    private final Set<String> allowedCommands;

    public NoPluginsFoundListener() {
        // Define ONLY the safe commands players are allowed to autocomplete or use
        this.allowedCommands = new HashSet<>();
        allowedCommands.add("spawn");
        allowedCommands.add("lobby");
        allowedCommands.add("play");
        allowedCommands.add("slots");
        allowedCommands.add("casino");
        allowedCommands.add("msg");
        allowedCommands.add("r");
        allowedCommands.add("help");
        allowedCommands.add("tpb");
        allowedCommands.add("border");
    }

    /**
     * Layer 1: Blocks client-side brute-force auto-completion.
     * When Meteor tries to tab-complete /a through /z, it will find nothing.
     */
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();

        // Exempt founders and admins so they can still see and autocomplete plugins
        if (player.hasPermission("group.founder") || player.hasPermission("casino.admin")) {
            return;
        }

        // Filter command packet
        event.getCommands().removeIf(command -> {
            // Hide commands containing colons (namespaces like bukkit:plugins)
            if (command.contains(":")) {
                return true;
            }

            // Hide everything that is not explicitly inside our clean allowed list
            return !allowedCommands.contains(command.toLowerCase());
        });
    }

    /**
     * Layer 2: Blocks bypass attempts if they manually guess command names.
     */
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("group.founder") || player.hasPermission("casino.admin")) {
            return;
        }

        String message = event.getMessage();
        String[] args = message.split(" ");
        if (args.length == 0) {
            return;
        }

        String rawCommand = args[0].substring(1).toLowerCase();

        // Block players trying to use namespace shortcuts
        if (rawCommand.contains(":")) {
            event.setCancelled(true);
            player.sendMessage("§cUnknown command. Type \"/help\" for help.");
            return;
        }

        // Cancel execution and send vanilla-looking 'Unknown command' error
        if (!allowedCommands.contains(rawCommand)) {
            event.setCancelled(true);
            player.sendMessage("§cUnknown command. Type \"/help\" for help.");
        }
    }
}
