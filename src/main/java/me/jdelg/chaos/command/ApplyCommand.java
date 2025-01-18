package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;
import me.jdelg.chaos.server.Profile;
import me.jdelg.chaos.server.Server;

public class ApplyCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("<red>Wrong usage!</red> <yellow>Use \"apply <server> <profile>\"</yellow>");
            return;
        }

        Server server = Chaos.get().serverManager().serverByName(args[0]);
        Profile profile = Chaos.get().serverManager().profileByName(args[1]);

        if (server == null) {
            sender.sendMessage("<red>That server does not exist!</red> <yellow>Use \"servers\" for a list of servers.</yellow>");
            return;
        }

        if (server.running()) {
            sender.sendMessage("<red>That server is already running!</red> <yellow>Use \"stop <name>\" to stop the server.</yellow>");
            return;
        }

        if (profile == null) {
            sender.sendMessage("<red>That profile does not exist!</red> <yellow>Use \"profiles\" for a list.</yellow>");
            return;
        }

        if (profile.platform() != server.platform()) {
            sender.sendMessage("<red>That profile is not compatible with the server!</red> <yellow>Use \"profiles <profile>\" for more info.</yellow>");
            return;
        }

        profile.apply(server);
        sender.sendMessage("<green>Applied profile %s to server %s.</green>".formatted(profile.name(), server.name()));
    }
}