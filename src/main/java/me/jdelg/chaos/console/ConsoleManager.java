package me.jdelg.chaos.console;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ConsoleManager {
    private final Logger logger;
    private final ConsoleSender console;
    private final Map<String, Command> commandMap;
    private boolean enabled;

    public ConsoleManager() {
        this.logger = Logger.getLogger("ConsoleManager");
        this.console = new ConsoleSender();
        this.commandMap = new HashMap<>();
        this.enabled = false;
    }

    public void registerCommand(String name, Command command) {
        commandMap.put(name, command);
    }

    public void run(Sender sender, String input) {
        String[] args = input.split(" ");
        String name = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        Command command = commandMap.get(name);

        if (command == null) {
            sender.sendMessage("That command does not exist!");
            return;
        }

        try {
            command.execute(sender, args);
        } catch (Exception e) {
            sender.sendMessage("<red>An error has ocurred with that command!</red> <yellow>Information has been printed to the console.</yellow>");
            e.printStackTrace();
        }
    }

    public void loop() {
        String input = System.console().readLine("");

        run(console, input);

        if (enabled)
            this.loop();
    }
    
    public void start() {
        this.enabled = true;
        this.loop();
    }

    public void stop() {
        this.enabled = false;
    }
}