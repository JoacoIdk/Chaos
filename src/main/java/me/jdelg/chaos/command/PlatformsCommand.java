package me.jdelg.chaos.command;

import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Platform;

import java.util.Arrays;

public class PlatformsCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            String message = "<blue>Platforms (Use \"platforms <name>\" for more info)";

            for (Platform platform : Platform.values())
                message += "\n- %s".formatted(platform.name());

            message += "</blue>";

            sender.sendMessage(message);

            return;
        }

        String name = args[0];
        Platform platform = Platform.fromString(name);

        if (platform == Platform.UNKNOWN) {
            sender.sendMessage("<red>That platform is not supported!</red> <yellow>Use \"platforms\" for a list.</yellow>");
            return;
        }

        sender.sendMessage(
                "<blue>Platform %s\nParameters: %s\nEULA: %b\nDownload: %s</blue>"
                .formatted(
                        name,
                        Arrays.toString(platform.parameters()),
                        platform.eula(),
                        platform.download()
                )
        );
    }
}