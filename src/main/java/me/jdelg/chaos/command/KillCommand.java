package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Server;

public class KillCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"kill <name>\"</yellow>");
            return;
        }

        String name = args[0];
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"servers\" for a list of servers.</yellow>");
            return;
        }

        if (!server.running()) {
            sender.sendMessage("<red>That server is not running!</red> <yellow>Use \"start <name>\" to start the server.</yellow>");
            return;
        }

        server.kill();
        sender.sendMessage("<green>Killing server %s.</green>".formatted(server.name()));
    }
}