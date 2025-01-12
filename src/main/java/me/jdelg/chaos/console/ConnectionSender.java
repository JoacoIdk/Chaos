package me.jdelg.chaos.console;

public class ConnectionSender implements Sender {
    private final Sender.Type type;
    private final String name;

    public ConnectionSender(Sender.Type type, String name) {
        if (type == Type.CONSOLE)
            throw new IllegalStateException("Initializing network sender with console type");

        this.type = type;
        this.name = name;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void sendMessage(String message) {
    }
}