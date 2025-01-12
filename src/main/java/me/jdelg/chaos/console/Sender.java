package me.jdelg.chaos.console;

public interface Sender {
    Type type();
    String name();
    void sendMessage(String message);

    enum Type {
        CONSOLE,
        SERVER,
        PLAYER
    }
}