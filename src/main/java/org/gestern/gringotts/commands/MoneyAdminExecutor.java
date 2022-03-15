package org.gestern.gringotts.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TransactionResult;
import org.gestern.gringotts.event.VaultCreationEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

/**
 * Admin commands for managing ingame aspects.
 */
public class MoneyAdminExecutor extends GringottsAbstractExecutor {
    private static final List<String> commands = Arrays.asList("balance", "add", "remove");

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
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String commandLabel,
                             String[] args) {
        testPermission(sender, cmd, "gringotts.admin");

        if (args.length < 2) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "balance":
            case "bal":
            case "b": {
                if (args.length > 2) {
                    return false;
                }

                String targetAccount = args[1];

                Account target = eco.getAccount(targetAccount);

                if (!target.exists()) {
                    sendInvalidAccountMessage(sender, targetAccount);

                    return false;
                }

                String formattedBalance = eco.currency().format(target.balance());
                String senderMessage = LANG.moneyadmin_b
                        .replace(TAG_BALANCE, formattedBalance)
                        .replace(TAG_PLAYER, targetAccount);

                sender.sendMessage(senderMessage);

                return true;
            }
            case "add": {
                if (args.length != 3) {
                    return false;
                }

                String targetAccount = args[1];

                Account target = eco.getAccount(targetAccount);

                if (!target.exists()) {
                    sendInvalidAccountMessage(sender, targetAccount);

                    return false;
                }

                double amount;

                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    return false;
                }

                String formattedAmount = eco.currency().format(amount);
                TransactionResult added = target.add(amount);

                if (added == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_add_sender
                            .replace(TAG_VALUE, formattedAmount)
                            .replace(TAG_PLAYER, targetAccount);

                    sender.sendMessage(senderMessage);

                    String targetMessage = LANG.moneyadmin_add_target
                            .replace(TAG_VALUE, formattedAmount);

                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_add_error
                            .replace(TAG_VALUE, targetAccount)
                            .replace(TAG_PLAYER, targetAccount);

                    sender.sendMessage(errorMessage);
                }

                return true;
            }
            case "remove":
            case "rm": {
                if (args.length != 3) {
                    return false;
                }

                String targetAccount = args[1];

                Account target = eco.getAccount(targetAccount);

                if (!target.exists()) {
                    sendInvalidAccountMessage(sender, targetAccount);

                    return false;
                }

                double amount;

                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    return false;
                }

                String formattedAmount = eco.currency().format(amount);
                TransactionResult removed = target.remove(amount);

                if (removed == SUCCESS) {
                    String senderMessage = LANG.moneyadmin_rm_sender
                            .replace(TAG_VALUE, formattedAmount)
                            .replace(TAG_PLAYER, targetAccount);

                    sender.sendMessage(senderMessage);

                    String targetMessage = LANG.moneyadmin_rm_target
                            .replace(TAG_VALUE, formattedAmount);

                    target.message(targetMessage);
                } else {
                    String errorMessage = LANG.moneyadmin_rm_error
                            .replace(TAG_VALUE, formattedAmount)
                            .replace(TAG_PLAYER, targetAccount);

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
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {
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
                switch (cmd) {
                    case "b":
                    case "bal":
                    case "balance":
                    case "add":
                    case "remove":
                    case "rm": {
                        String[] steps = (args[1] + " ").split(":");

                        if (steps.length == 1) {
                            return Stream.of(Bukkit.getOfflinePlayers())
                                    .map(OfflinePlayer::getName)
                                    .filter(Objects::nonNull)
                                    .filter(name -> name.startsWith(args[1]))
                                    .collect(Collectors.toList());
                        }

                        try {
                            VaultCreationEvent.Type type = VaultCreationEvent.Type.valueOf(steps[0].toUpperCase());

                            Optional<AccountHolderProvider> providerOptional = Gringotts.getInstance()
                                    .getAccountHolderFactory()
                                    .getProvider(type);

                            if (providerOptional.isPresent()) {
                                return providerOptional.get().getAccountNames().stream()
                                        .filter(Objects::nonNull)
                                        .map(s -> type.getId() + ":" + s)
                                        .filter(name -> name.startsWith(args[1]))
                                        .collect(Collectors.toList());
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }

        return Lists.newArrayList();
    }
}
