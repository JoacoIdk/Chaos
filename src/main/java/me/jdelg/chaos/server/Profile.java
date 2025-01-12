package me.jdelg.chaos.server;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class Profile {
    private final Path path;
    private final String name;
    private final Platform platform;

    @SneakyThrows
    public Profile(Path path) {
        this.path = path;
        this.name = path.getFileName().toString();
        this.platform = Platform.fromString(Files.readString(path.resolve("platform.txt")));
    }

    @SneakyThrows
    public List<Path> listing() {
        Stream<Path> stream = Files.walk(path);
        List<Path> list = stream.toList();

        stream.close();

        return list;
    }

    @SneakyThrows
    public void apply(Server server) {
        if (server.running())
            throw new IllegalStateException("Server is currently running!");

        Path dir = server.path();

        for (Path file : listing())
            Files.copy(file, dir.resolve(file.getFileName()));
    }
}