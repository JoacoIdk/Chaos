package me.jdelg.chaos.server;

import lombok.Getter;
import lombok.Setter;
import me.jdelg.hermes.type.Status;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Getter
public class Server {
    private final Path path;
    private final String name;
    private final Logger logger;
    private final Platform platform;
    private final String secret;

    private Process process = null;
    private BufferedReader input = null;
    private BufferedWriter output = null;
    private BufferedReader error = null;

    @Setter
    private Status status = Status.OFFLINE;
    @Setter
    private InetAddress address = null;
    @Setter
    private boolean logging = true;

    public Server(Path path, Platform platform, String[] parameters) throws IOException, URISyntaxException {
        this.path = path;
        this.name = path.getFileName().toString();
        this.logger = Logger.getLogger("Server - " + name);
        this.platform = platform;
        this.secret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        Path secretPath = path.resolve("secret.txt");

        Files.createDirectories(path);

        Files.createFile(secretPath);
        Files.writeString(secretPath, secret);

        if (platform.eula()) {
            Files.writeString(
                    path.resolve("eula.txt"),
                    "eula=TRUE",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        String download = platform.download();
        String[] parameterNames = platform.parameters();

        for (int i = 0; i < parameterNames.length; i++)
            download = download.replace("<%s>".formatted(parameterNames[i]), parameters[i]);

        URL url = new URI(download).toURL();
        InputStream stream = url.openStream();
        ReadableByteChannel channel = Channels.newChannel(stream);
        FileOutputStream file = new FileOutputStream(path.resolve("server.jar").toFile());

        file.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

        file.close();
        channel.close();
        stream.close();

        logger.info("Created server.");
    }

    public Server(Path path) throws IOException {
        this.path = path;
        this.name = path.getFileName().toString();
        this.logger = Logger.getLogger("Server - " + name);
        this.platform = Platform.fromPath(path.resolve("server.jar"));
        this.secret = Files.readString(path.resolve("secret.txt"));

        logger.info("Loaded server.");
    }

    public boolean running() {
        if (process == null)
            return false;

        return process.isAlive();
    }

    public void start() throws IOException {
        if (running())
            throw new IllegalStateException("Process is already running!");

        List<String> command = new ArrayList<>();
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");

        command.add(java.toAbsolutePath().toString());

        Path starter = path.resolve("starter.txt");

        try {
            if (!Files.exists(starter))
                throw new Exception("Jump to catch block");

            command.addAll(List.of(Files.readString(starter).split(" ")));
        } catch (Exception e) {
            if (e instanceof IOException)
                logger.warning("Unable to read starter.txt file, using default command.");

            command.addAll(List.of("-Xms1G", "-Xmx2G", "-jar", "server.jar", "nogui"));
        }

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(path.toFile());
        builder.redirectInput(ProcessBuilder.Redirect.PIPE);
        builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.redirectError(ProcessBuilder.Redirect.PIPE);

        logger.info("Starting process...");

        status = Status.ONLINE;

        process = builder.start();
        input = process.inputReader();
        output = process.outputWriter();
        error = process.errorReader();

        new Thread(this::inputLoop).start();
        new Thread(this::errorLoop).start();

        process.onExit().whenCompleteAsync(this::exitCallback);
    }

    private void exitCallback(Process ignored, Throwable throwable) {
        status = Status.OFFLINE;
    }

    public void run(String command) throws IOException {
        if (!running())
            throw new IllegalStateException("Proccess is not running!");

        output.write(command);
        output.newLine();
        output.flush();
    }

    private void inputLoop() {
        if (!running())
            return;

        try {
            String line = input.readLine();
            logger.info(line);
        } catch (IOException e) {
            logger.warning("Error while reading input.");
            throw new RuntimeException(e);
        }

        inputLoop();
    }

    private void errorLoop() {
        if (!running())
            return;

        try {
            String line = error.readLine();
            logger.severe(line);
        } catch (IOException e) {
            logger.warning("Error while reading error.");
            throw new RuntimeException(e);
        }

        errorLoop();
    }

    public void stop() throws IOException {
        if (!running())
            throw new IllegalStateException("Proccess is not running!");

        logger.info("Stopping process...");

        run(platform.stop());
    }

    public void kill() {
        if (!running())
            throw new IllegalStateException("Proccess is not running!");

        logger.info("Killing process...");

        if (!process.supportsNormalTermination())
            logger.warning("Please nag the maintainers of this server software to add normal termination support, unfortunately, there is a very big chance that your data from this session will not be saved.");

        process.destroy();
    }
}