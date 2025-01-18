package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Server;

import java.io.IOException;

public class StartCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"start <name>\"</yellow>");
            return;
        }

        String name = args[0];
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"servers\" for a list of servers.</yellow>");
            return;
        }

        if (server.running()) {
            sender.sendMessage("<red>That server is already running!</red> <yellow>Use \"stop <name>\" to stop the server.</yellow>");
            return;
        }

        sender.sendMessage("<green>Starting server %s.</green>".formatted(server.name()));

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("<red>Could not start server!</red> <yellow>Information has been printed to the console.</yellow>");
        }
    }
}