package org.gestern.gringotts.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.*;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.Optional;

import static org.gestern.gringotts.Language.LANG;

public class VaultCreator implements Listener {

    private final Accounting accounting = Gringotts.getInstance().getAccounting();

    /**
     * If the vault creation event was properly handled and an AccountHolder supplied, it will be created here.
     *
     * @param event event to handle
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void registerVault(PlayerVaultCreationEvent event) {
        // event has been marked invalid, ignore
        if (!event.isValid()) {
            return;
        }

        AccountHolder owner = event.getOwner();

        if (owner == null) {
            return;
        }

        GringottsAccount account = accounting.getAccount(owner);

        SignChangeEvent cause = event.getCause();
        Optional<Sign> optionalSign = Util.getBlockStateAs(
                cause.getBlock(),
                Sign.class
        );

        if (!optionalSign.isPresent()) {
            return;
        }

        // create account chest
        AccountChest accountChest = new AccountChest(optionalSign.get(), account);

        // check for existence / add to tracking
        if (accounting.addChest(accountChest)) {
            Component line0 = cause.line(0);

            if (line0 != null) {
                String line0String = PlainTextComponentSerializer.plainText().serialize(line0);

                if (line0String.length() <= 16) {
                    cause.line(0, Component.text(line0String).decorate(TextDecoration.BOLD));
                }
            }

            cause.line(2, Component.text(owner.getName()));
            cause.getPlayer().sendMessage(LANG.vault_created);
        } else {
            cause.setCancelled(true);
            cause.getPlayer().sendMessage(LANG.vault_error);
        }
    }
}
