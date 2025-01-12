package me.jdelg.chaos.storage;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StorageManager {
    private final Path path;
    private final Map<String, Storage> storageMap;

    @SneakyThrows
    public StorageManager(Path path) {
        this.path = path;
        this.storageMap = new HashMap<>();

        Files.createDirectories(path);
    }

    @SneakyThrows
    public void reload(boolean force) {
        Stream<Path> stream = Files.list(path);

        stream.forEach(file -> storageMap.put(file.getFileName().toString(), new Storage(file)));
        stream.close();
    }
}