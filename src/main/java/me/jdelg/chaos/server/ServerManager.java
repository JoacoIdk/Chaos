package me.jdelg.chaos.server;

import lombok.Getter;
import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.util.FileUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Getter
public class ServerManager {
    private final Logger logger;
    private final Path serversFolder;
    private final Path profilesFolder;
    private final List<Server> servers;
    private final List<Profile> profiles;

    public ServerManager(Path serversFolder, Path profilesFolder) {
        this.logger = Logger.getLogger("ServerManager");
        this.serversFolder = serversFolder;
        this.profilesFolder = profilesFolder;
        this.servers = new ArrayList<>();
        this.profiles = new ArrayList<>();

        try {
            Files.createDirectories(serversFolder);
            Files.createDirectories(profilesFolder);
        } catch (IOException e) {
            logger.severe("Unable to create server and/or profile folders.");
            e.printStackTrace();
            return;
        }

        reload(true);
    }

    public Server create(String name, Platform platform, String[] parameters) throws IOException, URISyntaxException {
        if (name.equals(Chaos.NAME) || !name.matches("[a-zA-Z0-9-_]+"))
            throw new IllegalStateException("Cannot create server");

        Path path = serversFolder.resolve(name);
        Server server = new Server(path, platform, parameters);

        servers.add(server);

        return server;
    }

    public void delete(Server server) {
        FileUtil.deleteRecursive(server.path());

        servers.remove(server);
    }

    public Server serverByName(String name) {
        return servers.stream()
                .filter(server -> server.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Server serverByAddress(InetAddress address) {
        return servers.stream()
                .filter(server -> server.address().equals(address))
                .findFirst()
                .orElse(null);
    }

    public Profile profileByName(String name) {
        return profiles.stream()
                .filter(profile -> profile.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<InetAddress> getAddresses() {
        return servers.stream()
                .map(Server::address)
                .filter(Objects::nonNull)
                .toList();
    }

    public void reload(boolean force) {
        Stream<Path> stream;

        if (force)
            stopAll();

        // Servers

        servers.removeIf(server -> !server.running());

        List<Path> serverPaths = servers.stream()
                .map(Server::path)
                .toList();

        try {
            stream = Files.list(serversFolder)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"))
                .filter(path -> !serverPaths.contains(path));

            stream.forEach(path -> {
                try {
                    servers.add(new Server(path));
                } catch (IOException e) {
                    logger.warning("Unable to load server %s.".formatted(path.getFileName().toString()));
                    e.printStackTrace();
                }
            });
            stream.close();
        } catch (IOException e) {
            logger.severe("Unable to reload servers.");
            e.printStackTrace();
        }

        // Profiles

        profiles.clear();

        try {
            stream = Files.list(profilesFolder)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"));

            stream.forEach(path -> {
                try {
                    profiles.add(new Profile(path));
                } catch (IOException e) {
                    logger.warning("Unable to load profile %s.".formatted(path.getFileName().toString()));
                    e.printStackTrace();
                }
            });

            stream.close();
        } catch (IOException e) {
            logger.severe("Unable to reload profiles.");
            e.printStackTrace();
        }
    }

    public void stopAll() {
        servers.stream()
                .filter(Server::running)
                .forEach(server -> {
                    try {
                        server.stop();
                    } catch (IOException e) {
                        logger.warning("Error while stopping server %s, killing...".formatted(server.name()));
                        e.printStackTrace();
                        server.kill();
                    }
                });
    }
}