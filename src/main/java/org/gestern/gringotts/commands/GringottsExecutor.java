package org.gestern.gringotts.commands;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.api.dependency.Dependency;
import org.gestern.gringotts.currency.Denomination;
import org.gestern.gringotts.currency.GringottsCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.gestern.gringotts.Language.LANG;

/**
 * Administrative commands not related to ingame money.
 */
public class GringottsExecutor extends GringottsAbstractExecutor {
    private static final List<String> commands = Arrays.asList("reload", "dependencies", "denominations");
    private final Gringotts gringotts;

    /**
     * Instantiates a new Gringotts executor.
     *
     * @param gringotts the gringotts
     */
    public GringottsExecutor(Gringotts gringotts) {
        this.gringotts = gringotts;
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args) {
        testPermission(sender, command, "gringotts.admin");

        if (args.length < 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload": {
                plugin.reloadConfig();
                sender.sendMessage(LANG.reload);

                return true;
            }
            case "deps":
            case "dependencies": {
                if (sender instanceof Player) {
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

                    BookMeta meta = (BookMeta) book.getItemMeta();


                    meta = meta.author(Component.text("Gringotts"))
                            .title(Component.text("Gringotts Dependencies"));

                    Component builder = Component.text("Gringotts Dependencies")
                            .decorate(TextDecoration.BOLD)
                            .style(Style.empty())
                            .append(Component.newline())
                            .append(Component.newline());

                    for (Dependency dependency : this.gringotts.getDependencies()) {
                        builder = builder.style(Style.empty()).append(Component.text(" - ").append(
                                Component.text(dependency.getName()).color(
                                        dependency.isEnabled() ? NamedTextColor.DARK_GREEN : NamedTextColor.RED
                                ).decorate(TextDecoration.UNDERLINED).hoverEvent(HoverEvent.showText(Component.text(
                                        "Identification: "
                                ).append(Component.text(dependency.getId())
                                        .decorate(TextDecoration.BOLD)
                                ).append(Component.newline()).style(Style.empty()).append(Component.text(
                                                "Version: "
                                        ).append(Component.text(dependency.getVersion())
                                                .decorate(TextDecoration.BOLD)
                                        )
                                )))
                        ).append(Component.newline()));
                    }

                    meta.addPages(builder);

                    book.setItemMeta(meta);

                    ((Player) sender).openBook(book);
                }
                break;
            }
            case "den":
            case "denominations": {
                if (sender instanceof Player) {
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

                    BookMeta meta = (BookMeta) book.getItemMeta();

                    meta = meta.author(Component.text("Gringotts"))
                            .title(Component.text("Gringotts Denominations"));

                    GringottsCurrency currency = Configuration.CONF.getCurrency();

                    meta.addPages(Component.text("Gringotts Denominations")
                            .decorate(TextDecoration.BOLD)
                            .style(Style.empty())
                            .append(Component.newline())
                            .append(Component.newline())
                            .append(Component.text("Singular Name: "))
                            .append(Component.text(currency.getName())
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.newline())
                            .style(Style.empty())
                            .append(Component.text("Plural Name: "))
                            .append(Component.text(currency.getNamePlural())
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.newline())
                            .style(Style.empty())
                            .append(Component.text("Digits: "))
                            .append(Component.text(String.valueOf(currency.getDigits()))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.newline()));

                    Component builder = Component.text("Denominations: ")
                            .append(Component.newline());

                    for (Denomination denomination : currency.getDenominations()) {
                        builder = builder.style(Style.empty()).append(Component.text(" - ").append(
                                Component.text(
                                        denomination.getUnitName()
                                ).decorate(TextDecoration.UNDERLINED).hoverEvent(HoverEvent.showText(
                                        Component.text(
                                                "Value: "
                                        ).append(Component.text(String.valueOf(currency.getDisplayValue(
                                                        denomination.getValue()
                                                ))).decorate(TextDecoration.BOLD)
                                        ).append(Component.newline()).style(Style.empty()).append(Component.text(
                                                        "Material: "
                                                ).append(Component.text(
                                                                denomination.getKey().type.getType().getKey().toString()
                                                        ).decorate(TextDecoration.BOLD)
                                                )
                                        )
                                ))
                        ).append(Component.newline()));
                    }

                    meta.addPages(builder);

                    book.setItemMeta(meta);

                    ((Player) sender).openBook(book);
                }
                break;
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

        if (args.length == 1) {
            return commands.stream()
                    .filter(com -> com.startsWith(args[0]))
                    .collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }
}
