package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Server;

public class DeleteCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"delete <name>\"</yellow>");
            return;
        }

        String name = args[0];
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"servers\" for a list of servers.</yellow>");
            return;
        }

        if (server.running()) {
            sender.sendMessage("<red>That server is running!</red> <yellow>Use \"stop <name>\" to stop the server.</yellow>");
            return;
        }

        if (args.length < 2 || !args[1].equals("CONFIRM")) {
            sender.sendMessage("<red>Are you sure?</red> <yellow>Use \"delete <name> CONFIRM\" to confirm.</yellow>");
            return;
        }

        sender.sendMessage("<green>Deleting server %s.</green>".formatted(server.name()));
        Chaos.get().serverManager().delete(server);
    }
}