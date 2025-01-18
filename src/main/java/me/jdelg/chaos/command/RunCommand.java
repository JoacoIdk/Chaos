package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Server;

import java.io.IOException;
import java.util.Arrays;

public class RunCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"run <name> <command...>\"</yellow>");
            return;
        }

        String name = args[0];
        String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"servers\" for a list of servers.</yellow>");
            return;
        }

        if (!server.running()) {
            sender.sendMessage("<red>That server is not running!</red> <yellow>Use \"start <name>\" to start the server.</yellow>");
            return;
        }

        try {
            server.run(command);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("<red>Could not run command!</red> <yellow>Information has been printed to the console.</yellow>");
        }
    }
}