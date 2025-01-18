package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Platform;
import me.jdelg.chaos.server.Server;

import java.util.Arrays;

public class CreateCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"create <name> <platform> <parameters...>\"</yellow>");
            return;
        }

        String name = args[0];
        String platformName = args[1];
        Platform platform = Platform.fromString(platformName);

        if (platform == Platform.UNKNOWN) {
            sender.sendMessage("<red>That platform is not supported!</red> <yellow>Use \"platforms\" for a list.</yellow>");
            return;
        }

        String[] parameters = Arrays.copyOfRange(args, 2, args.length);

        if (platform.parameters().length > parameters.length) {
            sender.sendMessage("<red>Missing parameters!</red> <yellow>Use \"platforms <platform>\" for a list.</yellow>");
            return;
        }

        Server server = Chaos.get().serverManager().create(name, platform, parameters);

        sender.sendMessage(
                "<green>Created server %s on path %s</green>"
                .formatted(
                        name,
                        server.path()
                )
        );
    }
}