package me.jdelg.chaos.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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

    public static void downloadFile(String link, Path path) throws URISyntaxException, MalformedURLException {
        try {
            URL url = new URI(link).toURL();
            InputStream stream = url.openStream();
            ReadableByteChannel channel = Channels.newChannel(stream);
            FileOutputStream file = new FileOutputStream(path.resolve("server.jar").toFile());

            file.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

            file.close();
            channel.close();
            stream.close();
        } catch (IOException e) {
            if (e instanceof MalformedURLException ue)
                throw ue;

            throw new RuntimeException(e);
        }
    }
}