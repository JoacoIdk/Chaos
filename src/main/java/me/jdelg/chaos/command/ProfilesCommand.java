package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Profile;

import java.util.stream.Collectors;

public class ProfilesCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 1) {
            String message = "<blue>Profiles (Use \"profile <name>\" for more info)";

            for (Profile profile : Chaos.get().serverManager().profiles())
                message += "\n- %s".formatted(profile.name());

            message += "</blue>";

            sender.sendMessage(message);

            return;
        }

        String name = args[0];
        Profile profile = Chaos.get().serverManager().profileByName(name);

        if (profile == null) {
            sender.sendMessage("<red>That profile does not exist!</red> <yellow>Use \"profiles\" for a list.</yellow>");
            return;
        }

        sender.sendMessage(
                "<blue>Profile %s\nPlatform: %s\nListing: %s</blue>"
                .formatted(
                        name,
                        profile.platform(),
                        profile.listing().stream().map(path -> "\n- " + path).collect(Collectors.joining(""))
                )
        );
    }
}