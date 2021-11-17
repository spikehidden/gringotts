package org.gestern.gringotts.commands;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.Permissions;
import org.gestern.gringotts.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.COMMAND_DEPOSIT;
import static org.gestern.gringotts.Permissions.COMMAND_WITHDRAW;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

public abstract class GringottsAbstractExecutor implements TabExecutor {
    static final String TAG_BALANCE = "%balance";

    static final String TAG_PLAYER = "%player";

    static final String TAG_VALUE = "%value";

    final Gringotts plugin = Gringotts.getInstance();
    final Eco eco = plugin.getEco();

    static void sendInvalidAccountMessage(CommandSender sender, String accountName) {
        sender.sendMessage(LANG.invalid_account.replace(TAG_PLAYER, accountName));
    }

    /**
     * Test permission.
     *
     * @param sender     the sender
     * @param command    the command
     * @param permission the permission
     */
    public static void testPermission(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String permission) {
        if (!testPermission(sender, permission)) {
            if (command.getPermissionMessage() == null) {
                throw new CommandException("I'm sorry, but you do not have permission to perform this command. " +
                        "Please contact the server administrators if you believe that this is a mistake.");
            } else if (command.getPermissionMessage().length() != 0) {
                throw new CommandException(command.getPermissionMessage().replace("<permission>", permission));
            }
        }
    }

    /**
     * Test permission boolean.
     *
     * @param sender     the sender
     * @param permission the permission
     * @return the boolean
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean testPermission(@NotNull CommandSender sender, @Nullable String permission) {
        if ((permission == null) || (permission.length() == 0)) {
            return true;
        }

        return Arrays.stream(permission.split(";")).anyMatch(sender::hasPermission);
    }

    boolean pay(Player player, double value, String[] args) {
        if (!Permissions.TRANSFER.isAllowed(player)) {
            player.sendMessage(LANG.noperm);

            return true;
        }

        String recipientName = args[2];

        OfflinePlayer reciepienPlayer = Bukkit.getPlayer(recipientName);

        if (reciepienPlayer == null) {
            if (Bukkit.getOfflinePlayer(recipientName).hasPlayedBefore()) {
                reciepienPlayer = Bukkit.getOfflinePlayer(recipientName);
            } else {
                try {
                    UUID targetUuid = UUID.fromString(recipientName);

                    if (Bukkit.getOfflinePlayer(targetUuid).hasPlayedBefore()) {
                        reciepienPlayer = Bukkit.getOfflinePlayer(targetUuid);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        if (reciepienPlayer == null) {
            player.spigot().sendMessage(
                    new ComponentBuilder(
                            "Player with name `" + recipientName + "` never played in this server before."
                    ).create()
            );

            return true;
        }

        PlayerAccount from = eco.player(player.getUniqueId());
        Account to = eco.account(recipientName);

        TaxedTransaction transaction = from.send(value).withTaxes();
        TransactionResult result = transaction.to(eco.player(reciepienPlayer.getUniqueId()));

        double tax = transaction.getTax();
        double valueAdded = value + tax;

        String formattedBalance = eco.currency().format(from.balance());
        String formattedValue = eco.currency().format(value);
        String formattedValuePlusTax = eco.currency().format(valueAdded);
        String formattedTax = eco.currency().format(tax);

        switch (result) {
            case SUCCESS:
                String succTaxMessage = LANG.pay_success_tax.replace(TAG_VALUE, formattedTax);
                String succSentMessage = LANG.pay_success_sender.replace(TAG_VALUE, formattedValue).replace
                        (TAG_PLAYER, recipientName);

                from.message(succSentMessage + (tax > 0 ? succTaxMessage : ""));

                String succReceivedMessage = LANG.pay_success_target.replace(TAG_VALUE, formattedValue).replace
                        (TAG_PLAYER, player.getName());

                to.message(succReceivedMessage);

                return true;
            case INSUFFICIENT_FUNDS:
                String insFMessage = LANG.pay_insufficientFunds
                        .replace(TAG_BALANCE, formattedBalance)
                        .replace(TAG_VALUE, formattedValuePlusTax);

                from.message(insFMessage);

                return true;
            case INSUFFICIENT_SPACE:
                String insSSentMessage = LANG.pay_insS_sender
                        .replace(TAG_PLAYER, recipientName)
                        .replace(TAG_VALUE, formattedValue);

                from.message(insSSentMessage);

                String insSReceiveMessage = LANG.pay_insS_target
                        .replace(TAG_PLAYER, from.id())
                        .replace(TAG_VALUE, formattedValue);

                to.message(insSReceiveMessage);

                return true;
            default:
                String error = LANG.pay_error
                        .replace(TAG_VALUE, formattedValue)
                        .replace(TAG_PLAYER, recipientName);

                from.message(error);

                return true;
        }
    }

    void deposit(Player player, double value) {
        if (COMMAND_DEPOSIT.isAllowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).deposit(value);
            String formattedValue = eco.currency().format(value);

            if (result == SUCCESS) {
                String success = LANG.deposit_success.replace(TAG_VALUE, formattedValue);

                player.sendMessage(success);
            } else {
                String error = LANG.deposit_error.replace(TAG_VALUE, formattedValue);

                player.sendMessage(error);
            }
        }
    }

    void withdraw(Player player, double value) {
        if (COMMAND_WITHDRAW.isAllowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).withdraw(value);
            String formattedValue = eco.currency().format(value);

            if (result == SUCCESS) {
                String success = LANG.withdraw_success.replace(TAG_VALUE, formattedValue);

                player.sendMessage(success);
            } else {
                String error = LANG.withdraw_error.replace(TAG_VALUE, formattedValue);

                player.sendMessage(error);
            }
        }
    }

    void sendBalanceMessage(Account account) {
        account.message(LANG.balance.replace(TAG_BALANCE, eco.currency().format(account.balance())));

        if (Configuration.CONF.balanceShowVault) {
            account.message(LANG.vault_balance.replace(TAG_BALANCE, eco.currency().format(account.vaultBalance())));
        }

        if (Configuration.CONF.balanceShowInventory) {
            account.message(LANG.inv_balance.replace(TAG_BALANCE, eco.currency().format(account.invBalance())));
        }
    }
}
