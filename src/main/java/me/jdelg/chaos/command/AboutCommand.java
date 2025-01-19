package me.jdelg.chaos.command;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.Command;
import me.jdelg.chaos.console.Sender;

public class AboutCommand implements Command {
    @Override
    public void execute(Sender sender, String[] args) {
        sender.sendMessage(
                "<blue>Running %s version %s on %s:%d</blue>"
                .formatted(
                        Chaos.NAME,
                        Chaos.VERSION,
                        Chaos.HOST,
                        Chaos.PORT
                )
        );
    }
}