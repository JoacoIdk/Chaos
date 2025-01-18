package me.jdelg.chaos.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtil {
    private FileUtil() {}

    public static void deleteRecursive(Path path) {
        try {
            if (Files.isDirectory(path)) {
                Stream<Path> stream = Files.list(path);

                stream.forEach(FileUtil::deleteRecursive);
                stream.close();
            }

            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}