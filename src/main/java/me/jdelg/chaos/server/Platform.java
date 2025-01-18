package me.jdelg.chaos.server;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
public enum Platform {
    PAPER(
            "https://api.papermc.io/v2/projects/paper/versions/<version>/builds/<build>/downloads/paper-<version>-<build>.jar",
            new String[]{"version", "build"},
            "io/papermc/paperclip/Main.class",
            true,
            "stop",
            true
    ),
    VELOCITY(
            "https://api.papermc.io/v2/projects/velocity/versions/<version>/builds/<build>/downloads/velocity-<version>-<build>.jar",
            new String[]{"version", "build"},
            "com/velocitypowered/api/proxy/ProxyServer.class",
            false,
            "stop",
            true
    ),
    BUNGEECORD(
            "https://ci.md-5.net/job/BungeeCord/<build>/artifact/bootstrap/target/BungeeCord.jar",
            new String[]{"build"},
            "net/md_5/bungee/BungeeCord.class",
            false,
            "end",
            true
    ),
    SPONGE(
            "https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/sponge<type>/<version>-<build>/sponge<type>-<version>-<build>-universal.jar",
            new String[]{"version", "build", "type"},
            "org/spongepowered/configurate",
            true,
            "stop",
            true
    ),
    PURPUR(
            "https://api.purpurmc.org/v2/purpur/<version>/<build>/download",
            new String[]{"version", "build"},
            "META-INF/libraries/org/purpurmc/purpur/purpur-api",
            true,
            "stop",
            true
    ),
    UNKNOWN(
            "",
            new String[]{},
            "",
            false,
            "stop",
            false
    );

    private final String download;
    private final String[] parameters;
    private final String identifier;
    private final boolean eula;
    private final String stop;
    private final boolean supported; // Name's kinda confusing, but is more of a "will attempt to download" than a "supported".

    Platform(String download, String[] parameters, String identifier, boolean eula, String stop, boolean supported) {
        this.download = download;
        this.parameters = parameters;
        this.identifier = identifier;
        this.eula = eula;
        this.stop = stop;
        this.supported = supported;
    }

    public static Platform fromPath(Path path) throws IOException {
        JarFile jar = new JarFile(path.toString());
        Platform result = UNKNOWN;

        for (Platform platform : values())
            if (jar.getJarEntry(platform.identifier) != null)
                result = platform;

        jar.close();

        return result;
    }

    public static Platform fromString(String name) {
        return Arrays.stream(values()).filter(platform -> platform.name().equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
    }
}