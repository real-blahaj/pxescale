package moe.pxe.pxescale.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import moe.pxe.pxescale.Main;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScaleCommand {

    private static final Main PLUGIN_INSTANCE = Main.getInstance();

    private static final Pattern MIN_PERM_FORMAT = Pattern.compile("scale\\.min\\.(?<num>[\\d.]+)");
    private static final Pattern MAX_PERM_FORMAT = Pattern.compile("scale\\.max\\.(?<num>[\\d.]+)");

    private static int command(CommandContext<CommandSourceStack> ctx, double scale, Entity[] entities) {

        double minScale = PLUGIN_INSTANCE.getConfig().getDouble("minimum-scale");
        double maxScale = PLUGIN_INSTANCE.getConfig().getDouble("maximum-scale");

        if (ctx.getSource().getSender().hasPermission("scale.bypass") || ctx.getSource().getSender().isOp()) {
            minScale = 0.0625;
            maxScale = 16.0;
        } else {
            OptionalDouble max = ctx.getSource().getSender().getEffectivePermissions().stream()
                    .map(perm -> MAX_PERM_FORMAT.matcher(perm.getPermission()))
                    .filter(Matcher::matches)
                    .map(match -> match.group("num"))
                    .mapToDouble(Double::valueOf)
                    .max();
            if (max.isPresent()) maxScale = max.getAsDouble();

            OptionalDouble min = ctx.getSource().getSender().getEffectivePermissions().stream()
                    .map(perm -> MIN_PERM_FORMAT.matcher(perm.getPermission()))
                    .filter(Matcher::matches)
                    .map(match -> match.group("num"))
                    .mapToDouble(Double::valueOf)
                    .max();
            if (min.isPresent()) minScale = min.getAsDouble();
        }

        scale = Math.clamp(scale, minScale, maxScale);

        Entity[] attributableEntities = Arrays.stream(entities)
                .filter(e -> e instanceof Attributable)
                .toList()
                .toArray(new Entity[0]);
        for (Entity e : attributableEntities) {
            Attributable att = (Attributable)e;
            AttributeInstance scaleAttribute = att.getAttribute(Attribute.GENERIC_SCALE);
            if (scaleAttribute == null) {
                att.registerAttribute(Attribute.GENERIC_SCALE);
                att.getAttribute(Attribute.GENERIC_SCALE);
            }
            if (scaleAttribute == null) continue; // ← because IDE yells if i don't
            scaleAttribute.setBaseValue(scale);
        }
        if (attributableEntities.length > 1) ctx.getSource().getSender().sendRichMessage("Set scale of <entities> entities to <scale>",
                Placeholder.unparsed("entities", String.valueOf(attributableEntities.length)),
                Placeholder.unparsed("scale", String.valueOf(scale)));
        else if (attributableEntities.length == 1) ctx.getSource().getSender().sendRichMessage("Set scale of <entity> to <scale>",
                Placeholder.component("entity", attributableEntities[0].name().hoverEvent(attributableEntities[0].asHoverEvent())),
                Placeholder.unparsed("scale", String.valueOf(scale)));
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("scale")
                .requires(ctx -> ctx.getSender().hasPermission("scale.use") || ctx.getSender().isOp())
                .then(Commands.argument("scale", DoubleArgumentType.doubleArg(0.0625, 16.0))
                        .then(Commands.argument("selector", ArgumentTypes.entities())
                                .requires(ctx -> ctx.getSender().hasPermission("scale.others") || ctx.getSender().isOp())
                                .executes(ctx -> command(
                                        ctx, ctx.getArgument("scale", Double.class),
                                        ctx.getArgument("selector", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource())
                                                .toArray(new Entity[]{})
                                        )))
                        .executes(ctx -> command(
                                ctx, ctx.getArgument("scale", Double.class),
                                new Entity[]{ctx.getSource().getExecutor()}
                                )))
                .then(Commands.literal("reload")
                        .requires(ctx -> ctx.getSender().hasPermission("scale.reload") || ctx.getSender().isOp())
                        .executes(ctx -> {
                            PLUGIN_INSTANCE.reloadConfig();
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(ctx -> command(ctx, 1, new Entity[]{ctx.getSource().getExecutor()}))
                .build();
    }

}
