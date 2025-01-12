package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;

public class StopCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        sender.sendMessage("Stopping...");
        Chaos.get().shutdown();
    }
}