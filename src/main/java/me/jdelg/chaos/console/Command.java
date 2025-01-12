package me.jdelg.chaos.console;

public interface Command {
    void execute(Sender sender, String[] args);
}