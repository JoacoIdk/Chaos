package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Platform;

import java.util.Arrays;

public class CreateCommand implements Command {
    // create <name> <platform> <parameters...>
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Wrong usage! Use \"create <name> <platform> <parameters...>\"");
            return;
        }

        String name = args[0];
        String platformName = args[1];
        Platform platform = Platform.fromString(platformName);

        if (platform == Platform.UNKNOWN) {
            sender.sendMessage("That platform is not supported! Use \"platforms\" for a list.");
            return;
        }

        String[] parameters = Arrays.copyOfRange(args, 2, args.length);

        if (platform.parameters().length > parameters.length) {
            sender.sendMessage("Missing parameters! Use \"platforms <platform>\" for a list.");
            return;
        }

        Chaos.get().serverManager().create(name, platform, parameters);
        sender.sendMessage("Created server %s!".formatted(name));
    }
}