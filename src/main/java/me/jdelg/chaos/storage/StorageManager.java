package me.jdelg.chaos.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class StorageManager {
    private final Logger logger;
    private final Path path;
    private final Map<String, Storage> storageMap;

    public StorageManager(Path path) {
        this.logger = Logger.getLogger("StorageManager");
        this.path = path;
        this.storageMap = new HashMap<>();

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.severe("Unable to create storage folder.");
            e.printStackTrace();
            return;
        }

        reload(true);
    }

    public void saveAll() {
        try {
            for (Storage storage : storageMap.values())
                storage.save();
        } catch (IOException e) {
            logger.warning("Unable to save all.");
            e.printStackTrace();
        }
    }

    public void reload(boolean force) {
        if (force) {
            saveAll();
            storageMap.clear();
        }

        List<Path> storagePaths = storageMap.values()
                .stream()
                .map(Storage::path)
                .toList();

        try {
            Stream<Path> stream = Files.list(path)
                .filter(path -> path.getFileName().toString().matches("[a-zA-Z0-9-_]+"))
                .filter(path -> !storagePaths.contains(path));

            stream.forEach(file -> {
                String name = file.getFileName().toString();

                try {
                    storageMap.put(name, new Storage(file));
                } catch (IOException e) {
                    logger.warning("Unable to load storage %s.".formatted(name));
                    e.printStackTrace();
                }
            });

            stream.close();
        } catch (IOException e) {
            logger.warning("Unable to reload.");
            e.printStackTrace();
        }
    }
}