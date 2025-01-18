package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;

public class ExitCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        sender.sendMessage("<green>Exiting...</green>");
        Chaos.get().shutdown();
    }
}