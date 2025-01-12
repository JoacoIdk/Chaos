package me.jdelg.chaos.server;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
public enum Platform {
    PAPER(
            "https://api.papermc.io/v2/projects/paper/versions/<version>/builds/<build>/downloads/paper-<version>-<build>.jar",
            new String[]{"version", "build"},
            "org.bukkit.Bukkit",
            true
    ),
    VELOCITY(
            "https://api.papermc.io/v2/projects/velocity/versions/<version>/builds/<build>/downloads/velocity-<version>-<build>.jar",
            new String[]{"version", "build"},
            "com.velocitypowered.api.proxy.ProxyServer",
            false
    ),
    //SPONGE(
    //        "https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/sponge<type>/<version>-<build>/sponge<type>-<version>-<build>-universal.jar",
    //        new String[]{"version", "build", "type"},
    //        true
    //),
    UNKNOWN(null, null, null, false);

    private final String download;
    private final String[] parameters;
    private final String identifier;
    private final boolean eula;

    Platform(String download, String[] parameters, String identifier, boolean eula) {
        this.download = download;
        this.parameters = parameters;
        this.identifier = identifier;
        this.eula = eula;
    }

    @SneakyThrows
    public static Platform fromPath(Path path) {
        List<String> classNames = new ArrayList<>();
        InputStream stream = Files.newInputStream(path);
        ZipInputStream zip = new ZipInputStream(stream);

        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.isDirectory())
                continue;

            String className = entry.getName();

            if (!className.endsWith(".class"))
                continue;

            className = className.replace(File.separatorChar, '.');
            className = className.substring(0, className.length() - ".class".length());

            classNames.add(className);
        }

        zip.close();
        stream.close();

        for (Platform platform : values())
            if (classNames.contains(platform.identifier()))
                return platform;

        return UNKNOWN;
    }

    public static Platform fromString(String name) {
        return Arrays.stream(values()).filter(platform -> platform.name().equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
    }
}