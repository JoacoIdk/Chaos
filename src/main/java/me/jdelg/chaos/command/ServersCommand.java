package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Server;

public class ServersCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            String message = "<blue>Servers (Use \"servers <name>\" for more info)";

            for (Server server : Chaos.get().serverManager().servers())
                message += "\n- %s".formatted(server.name());

            message += "</blue>";

            sender.sendMessage(message);
            return;
        }

        String name = args[0];
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"create <name>\" to create a server.</yellow>");
            return;
        }

        sender.sendMessage(
                "<blue>Server %s\nPath: %s\nRunning: %b\nPlatform: %s\nStatus: %s\nAddress: %s</blue>"
                .formatted(
                        name,
                        server.path(),
                        server.running(),
                        server.platform(),
                        server.status(),
                        server.address()
                )
        );
    }
}