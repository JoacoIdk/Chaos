package me.jdelg.chaos;

import lombok.Getter;
import me.jdelg.chaos.console.ConsoleManager;
import me.jdelg.chaos.command.CreateCommand;
import me.jdelg.chaos.network.NetworkManager;
import me.jdelg.chaos.server.ServerManager;
import me.jdelg.chaos.storage.StorageManager;

import java.nio.file.Path;
import java.util.logging.Logger;

@Getter
public class Chaos {
    private static Chaos chaos;

    private final Logger logger;
    private final Path path;
    private final ServerManager serverManager;
    private final StorageManager storageManager;
    private final NetworkManager networkManager;
    private final ConsoleManager consoleManager;

    public Chaos(Logger logger) {
        this.logger = logger;
        this.path = Path.of(".");
        this.serverManager = new ServerManager(
                path.resolve("servers"),
                path.resolve("profiles")
        );
        this.storageManager = new StorageManager(
                path.resolve("storage")
        );
        this.consoleManager = new ConsoleManager();

        logger.info("Starting...");

        consoleManager.registerCommand("create", new CreateCommand());
        consoleManager.start();
    }

    public void reload(boolean force) {
    }

    public void shutdown() {
        consoleManager.stop();
        serverManager.stop();
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("Chaos");
        chaos = new Chaos(logger);
    }

    public static Chaos get() {
        return chaos;
    }
}