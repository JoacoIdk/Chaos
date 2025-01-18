package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;

public class HelpCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        String message = "<blue>Commands";

        for (String name : Chaos.get().consoleManager().commandMap().keySet())
            message += "\n- %s".formatted(name);

        message += "</blue>";

        sender.sendMessage(message);
    }
}