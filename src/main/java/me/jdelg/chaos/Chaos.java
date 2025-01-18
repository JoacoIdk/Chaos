package me.jdelg.chaos;

import lombok.Getter;
import me.jdelg.chaos.command.*;
import me.jdelg.chaos.connection.Broadcaster;
import me.jdelg.chaos.connection.ConsoleReceiver;
import me.jdelg.chaos.console.ConsoleManager;
import me.jdelg.chaos.server.ServerManager;
import me.jdelg.chaos.storage.StorageManager;
import me.jdelg.hermes.Hermes;
import me.jdelg.hermes.network.ServerNetwork;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Getter
public class Chaos {
    public static final String NAME = "Chaos";
    public static final String HOST = "0.0.0.0";
    public static final int PORT = 30060;

    private static Chaos chaos;

    private final Logger logger;
    private final Path path;
    private final ServerManager serverManager;
    private final StorageManager storageManager;
    private final Hermes hermes;
    private final ConsoleManager consoleManager;

    public Chaos(Logger logger) {
        long millis = System.currentTimeMillis();

        chaos = this;

        InetAddress address = null;

        try {
            address = InetAddress.getByName(HOST);
        } catch (UnknownHostException e) {
            logger.severe("Unable to get host address.");
            e.printStackTrace();
            System.exit(1);
        }

        this.logger = logger;
        this.path = Path.of(".");
        this.serverManager = new ServerManager(
                path.resolve("servers"),
                path.resolve("profiles")
        );
        this.storageManager = new StorageManager(
                path.resolve("storage")
        );
        this.hermes = new Hermes(
                address,
                PORT,
                new ServerNetwork()
        );
        this.consoleManager = new ConsoleManager();

        // This is pretty useless since Helios isn't done yet.
        hermes.registerReceiver(new Broadcaster());
        hermes.registerReceiver(new ConsoleReceiver());

        consoleManager.registerCommand("servers", new ServersCommand());
        consoleManager.registerCommand("create", new CreateCommand());
        consoleManager.registerCommand("start", new StartCommand());
        consoleManager.registerCommand("run", new RunCommand());
        consoleManager.registerCommand("stop", new StopCommand());
        consoleManager.registerCommand("kill", new KillCommand());
        consoleManager.registerCommand("delete", new DeleteCommand());
        consoleManager.registerCommand("platforms", new PlatformsCommand());
        consoleManager.registerCommand("profiles", new ProfilesCommand());
        consoleManager.registerCommand("apply", new ApplyCommand());
        consoleManager.registerCommand("help", new HelpCommand());
        consoleManager.registerCommand("exit", new ExitCommand());
        consoleManager.registerCommand("reload", new ReloadCommand());

        logger.info("Done! (%fs)".formatted((System.currentTimeMillis() - millis) / 1000D));

        consoleManager.start();
    }

    public void reload(boolean force) {
        logger.info("Reloading...");

        serverManager.reload(force);
        storageManager.reload(force);

        logger.info("Reload complete");
    }

    public void shutdown() {
        logger.info("Shutting down...");

        consoleManager.stop();
        hermes.stop();
        storageManager.saveAll();
        serverManager.stopAll();

        logger.info("Goodbye!");
    }

    public static void main(String[] args) {
        try (InputStream file = Chaos.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(file);
        } catch (Exception e) {
            System.out.println("Logging not configured (program will be terminated)");
            System.exit(1);
        }

        Logger logger = Logger.getLogger(NAME);
        logger.info("Creating instance...");

        new Chaos(logger);

        System.exit(0);
    }

    public static Chaos get() {
        return chaos;
    }
}