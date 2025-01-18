package me.jdelg.chaos.server;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class Profile {
    private final Path path;
    private final String name;
    private final Platform platform;

    public Profile(Path path) throws IOException {
        this.path = path;
        this.name = path.getFileName().toString();
        this.platform = Platform.fromString(Files.readString(path.resolve("platform.txt")));
    }

    public List<Path> listing() throws IOException {
        Stream<Path> stream = Files.walk(path)
                .filter(path -> !path.getFileName().toString().startsWith("."));
        List<Path> list = stream.toList();

        stream.close();

        return list;
    }

    public void apply(Server server) throws IOException {
        if (server.running())
            throw new IllegalStateException("Server is currently running!");

        Path dir = server.path();

        for (Path file : listing())
            Files.copy(file, dir.resolve(file.getFileName()));
    }
}