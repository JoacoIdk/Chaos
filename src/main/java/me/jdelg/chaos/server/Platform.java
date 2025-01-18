package me.jdelg.chaos.server;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;

@Getter
public enum Platform {
    // CraftBukkit based
    CRAFTBUKKIT(
            null,
            null,
            "org/bukkit/Bukkit.class",
            true,
            "stop",
            false
    ),
    SPIGOT(
            null,
            null,
            "org/spigotmc/SpigotConfig.class",
            true,
            "stop",
            false
    ),
    PAPER(
            "https://api.papermc.io/v2/projects/paper/versions/<version>/builds/<build>/downloads/paper-<version>-<build>.jar",
            new String[]{"version", "build"},
            "io/papermc/paperclip/Main.class",
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
    // BungeeCord based
    BUNGEECORD(
            "https://ci.md-5.net/job/BungeeCord/<build>/artifact/bootstrap/target/BungeeCord.jar",
            new String[]{"build"},
            "net/md_5/bungee/BungeeCord.class",
            false,
            "end",
            true
    ),
    WATERFALL(
            "https://api.papermc.io/v2/projects/waterfall/versions/<version>/builds/<build>/downloads/waterfall-<version>-<build>.jar",
            new String[]{"version", "build"},
            "io/github/waterfallmc/waterfall",
            false,
            "end",
            true
    ),
    // Sponge
    SPONGEVANILLA(
            "https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/<version>-<build>/spongevanilla-<version>-<build>-universal.jar",
            new String[]{"version", "build"},
            "org/spongepowered/vanilla",
            true,
            "stop",
            true
    ),
    SPONGEFORGE(
            "https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongeforge/<version>-<build>/spongeforge-<version>-<build>-universal.jar",
            new String[]{"version", "build"},
            "org/spongepowered/forge",
            true,
            "stop",
            true
    ),
    SPONGENEO(
            "https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongeneo/<version>-<build>/spongeneo-<version>-<build>-universal.jar",
            new String[]{"version", "build"},
            "org/spongepowered/neoforge",
            true,
            "stop",
            true
    ),
    // Velocity
    VELOCITY(
            "https://api.papermc.io/v2/projects/velocity/versions/<version>/builds/<build>/downloads/velocity-<version>-<build>.jar",
            new String[]{"version", "build"},
            "com/velocitypowered/api/proxy/ProxyServer.class",
            false,
            "stop",
            true
    ),
    // Other
    UNKNOWN(
            null,
            null,
            null,
            false,
            "stop",
            false
    );

    private final String link;
    private final String[] parameters;
    private final String identifier;
    private final boolean eula;
    private final String stop;
    private final boolean download;

    Platform(String link, String[] parameters, String identifier, boolean eula, String stop, boolean download) {
        this.link = link;
        this.parameters = parameters;
        this.identifier = identifier;
        this.eula = eula;
        this.stop = stop;
        this.download = download;
    }

    public static Platform fromPath(Path path) throws IOException {
        JarFile jar = new JarFile(path.toString());
        Platform result = UNKNOWN;

        for (Platform platform : values())
            if (platform.identifier != null && jar.getJarEntry(platform.identifier) != null)
                result = platform;

        jar.close();

        return result;
    }

    public static Platform fromString(String name) {
        return Arrays.stream(values()).filter(platform -> platform.name().equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
    }
}