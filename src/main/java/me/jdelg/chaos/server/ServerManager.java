package me.jdelg.chaos.server;

import lombok.Getter;
import lombok.SneakyThrows;
import me.jdelg.chaos.Chaos;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class ServerManager {
    private final Path serversFolder;
    private final Path profilesFolder;
    private final List<Server> servers;
    private final List<Profile> profiles;

    @SneakyThrows
    public ServerManager(Path serversFolder, Path profilesFolder) {
        this.serversFolder = serversFolder;
        this.profilesFolder = profilesFolder;
        this.servers = new ArrayList<>();
        this.profiles = new ArrayList<>();

        Files.createDirectories(serversFolder);
        Files.createDirectories(profilesFolder);

        reload(true);
    }

    public Server create(String name, Platform platform, String[] parameters) {
        if (name.equals(Chaos.NAME) || !name.matches("[a-zA-Z0-9-_]+"))
            throw new IllegalStateException("Cannot create server");

        Path path = serversFolder.resolve(name);
        Server server = new Server(path, platform, parameters);

        servers.add(server);

        return server;
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

    @SneakyThrows
    public void reload(boolean force) {
        Stream<Path> stream;

        if (force)
            stopAll();

        // Servers

        servers.removeIf(server -> !server.running());

        List<Path> serverPaths = servers.stream()
                .map(Server::path)
                .toList();

        stream = Files.list(serversFolder)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"))
                .filter(path -> !serverPaths.contains(path));

        stream.forEach(path -> servers.add(new Server(path)));
        stream.close();

        // Profiles

        profiles.clear();

        stream = Files.list(profilesFolder)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"));

        stream.forEach(path -> profiles.add(new Profile(path)));
        stream.close();
    }

    public void stopAll() {
        servers.stream()
                .filter(Server::running)
                .forEach(Server::stop);
    }
}