package me.jdelg.chaos.storage;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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

        reload(true);
    }

    public void saveAll() {
        for (Storage storage : storageMap.values())
            storage.save();
    }

    @SneakyThrows
    public void reload(boolean force) {
        if (force) {
            saveAll();
            storageMap.clear();
        }

        List<Path> storagePaths = storageMap.values()
                .stream()
                .map(Storage::path)
                .toList();

        Stream<Path> stream = Files.list(path)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"))
                .filter(path -> !storagePaths.contains(path));

        stream.forEach(file -> storageMap.put(file.getFileName().toString(), new Storage(file)));
        stream.close();
    }
}