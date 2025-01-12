package me.jdelg.chaos.server;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.jdelg.chaos.console.Sender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter
public class Server implements Sender {
    private final Path path;
    private final String name;
    private final Platform platform;
    private final Logger logger;

    private Process process = null;
    private BufferedReader input = null;
    private BufferedWriter output = null;
    private BufferedReader error = null;

    @Setter
    private Status status = Status.OFFLINE;
    @Setter
    private InetAddress address = null;

    @SneakyThrows
    public Server(Path path, Platform platform, String[] parameters) {
        this.path = path;
        this.name = path.getFileName().toString();
        this.platform = platform;
        this.logger = Logger.getLogger("Server " + name);

        Files.createDirectories(path);

        String download = platform.download();
        String[] parameterNames = platform.parameters();

        for (int i = 0; i < parameterNames.length; i++)
            download = download.replace(parameterNames[i], parameters[i]);

        URL url = new URI(download).toURL();
        InputStream stream = url.openStream();
        ReadableByteChannel channel = Channels.newChannel(stream);
        FileOutputStream file = new FileOutputStream("server.jar");

        file.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

        file.close();
        channel.close();
        stream.close();

        if (!platform.eula())
            return;

        Files.writeString(
                path.getParent().resolve("eula.txt"),
                "eula=TRUE",
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public Server(Path path) {
        this.path = path;
        this.name = path.getFileName().toString();
        this.platform = Platform.fromPath(path.resolve("server.jar"));
        this.logger = Logger.getLogger("Server " + name);
    }

    public boolean running() {
        return process != null && process.isAlive();
    }

    @SneakyThrows
    public void start() {
        if (running())
            throw new IllegalStateException("Process is already running!");

        List<String> command = new ArrayList<>();
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");

        command.add(java.toAbsolutePath().toString());

        Path starter = path.resolve("starter.txt");

        if (Files.exists(starter))
            command.addAll(List.of(Files.readString(starter).split(" ")));
        else
            command.addAll(List.of("-Xmx1G", "-Xmx2G", "-jar", "server.jar"));

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(path.toFile());
        builder.redirectInput(ProcessBuilder.Redirect.PIPE);
        builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.redirectError(ProcessBuilder.Redirect.PIPE);

        logger.info("Starting process...");

        process = builder.start();
        input = process.inputReader();
        output = process.outputWriter();
        error = process.errorReader();

        new Thread(this::inputLoop).start();
        new Thread(this::errorLoop).start();
    }

    @SneakyThrows
    private void inputLoop() {
        if (!running())
            return;

        String line = input.readLine();
        logger.info(line);

        inputLoop();
    }

    @SneakyThrows
    private void errorLoop() {
        if (!running())
            return;

        String line = error.readLine();
        logger.severe(line);

        errorLoop();
    }

    public void stop() {
        if (!running())
            throw new IllegalStateException("Proccess is not running!");

        logger.info("Stopping process...");

        if (!process.supportsNormalTermination())
            logger.warning("Please nag the maintainers of this server software to add normal termination support, unfortunately, there is a very big chance that your data from this session will not be saved.");

        process.destroy();

        process = null;
        input = null;
        output = null;
        error = null;
    }

    public void kill() {
        if (!running())
            throw new IllegalStateException("Proccess is not running!");

        logger.info("Killing process...");

        process.destroyForcibly();

        process = null;
        input = null;
        output = null;
        error = null;
    }

    @Override
    public Type type() {
        return Type.SERVER;
    }

    @Override
    public void sendMessage(String message) {
    }
}