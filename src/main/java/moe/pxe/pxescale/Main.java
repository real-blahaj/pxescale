package moe.pxe.pxescale;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import moe.pxe.pxescale.command.ScaleCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ScaleCommand.getCommand());
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return INSTANCE;
    }
}
