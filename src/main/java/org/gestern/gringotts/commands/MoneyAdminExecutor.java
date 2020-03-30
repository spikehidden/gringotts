package org.gestern.gringotts.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TransactionResult;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

/**
 * Admin commands for managing ingame aspects.
 */
public class MoneyAdminExecutor extends GringottsAbstractExecutor {
    private static final List<String> commands = Arrays.asList("add", "rm", "b");

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender       Source of the command
     * @param cmd          Command which was executed
     * @param commandLabel Alias of the command which was used
     * @param args         Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String commandLabel,
                             String[] args) {
        testPermission(sender, cmd, "gringotts.admin");

        if (args.length < 2) {
            return false;
        }

        String command = args[0];

        // admin command: x of player / faction
        if ("b".equalsIgnoreCase(command)) {
            String targetAccountHolderStr = args[1];

            // explicit or automatic account type
            Account target;
            if (args.length == 3) {
                target = eco.custom(args[2], targetAccountHolderStr);
            } else {
                target = eco.account(targetAccountHolderStr);
            }

            if (!target.exists()) {
                sendInvalidAccountMessage(sender, targetAccountHolderStr);
                return false;
            }

            String formattedBalance = eco.currency().format(target.balance());
            String senderMessage = LANG.moneyadmin_b
                    .replace(TAG_BALANCE, formattedBalance)
                    .replace(TAG_PLAYER, targetAccountHolderStr);

            sender.sendMessage(senderMessage);

            return true;
        }

        // moneyadmin add/remove
        if (args.length >= 3) {
            String amountStr = args[1];
            double value;

            try {
                value = Double.parseDouble(amountStr);
            } catch (NumberFormatException ignored) {
                return false;
            }

            String targetAccountHolderStr = args[2];
            Account target;

            if (args.length == 4) {
                target = eco.custom(args[3], targetAccountHolderStr);
            } else {
                target = eco.account(targetAccountHolderStr);
            }

            if (!target.exists()) {
                sendInvalidAccountMessage(sender, targetAccountHolderStr);

                return false;
            }

            String formatValue = eco.currency().format(value);

            if ("add".equalsIgnoreCase(command)) {
                TransactionResult added = target.add(value);
                if (added == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_add_sender.replace(TAG_VALUE, formatValue).replace
                            (TAG_PLAYER, target.id());

                    sender.sendMessage(senderMessage);

                    String targetMessage = LANG.moneyadmin_add_target
                            .replace(TAG_VALUE, formatValue);

                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_add_error.replace(TAG_VALUE, formatValue).replace
                            (TAG_PLAYER, target.id());

                    sender.sendMessage(errorMessage);
                }

                return true;

            } else if ("rm".equalsIgnoreCase(command)) {
                TransactionResult removed = target.remove(value);
                if (removed == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_rm_sender
                            .replace(TAG_VALUE, formatValue)
                            .replace(TAG_PLAYER, target.id());

                    sender.sendMessage(senderMessage);

                    String targetMessage = LANG.moneyadmin_rm_target
                            .replace(TAG_VALUE, formatValue);

                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_rm_error
                            .replace(TAG_VALUE, formatValue)
                            .replace(TAG_PLAYER, target.id());

                    sender.sendMessage(errorMessage);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        if (!testPermission(sender, "gringotts.admin")) {
            return Lists.newArrayList();
        }

        String cmd = args[0].toLowerCase();

        switch (args.length) {
            case 1: {
                return commands.stream()
                        .filter(com -> com.startsWith(args[0]))
                        .collect(Collectors.toList());
            }
            case 2: {
                if ("b".equals(cmd)) {
                    return Stream.of(Bukkit.getOfflinePlayers())
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .filter(name -> name.startsWith(args[1]))
                            .collect(Collectors.toList());
                }
            }
            case 3: {
                switch (cmd) {
                    case "add":
                    case "rm": {
                        return Stream.of(Bukkit.getOfflinePlayers())
                                .map(OfflinePlayer::getName)
                                .filter(Objects::nonNull)
                                .filter(name -> name.startsWith(args[2]))
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        return Lists.newArrayList();
    }
}
