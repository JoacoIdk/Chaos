package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;

public class ReloadCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        boolean force = args.length > 0 && args[0].equals("force");
        sender.sendMessage("<green>Reloading..." + (force ? " <red>(forced, may kick)</red>" : "") + "</green>");

        Chaos.get().reload(force);

        sender.sendMessage("<green>Reloaded!</green>");
    }
}