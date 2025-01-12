package me.jdelg.chaos.console;

import java.util.logging.Logger;

public class ConsoleSender implements Sender {
    private final Logger logger;

    public ConsoleSender() {
        this.logger = Logger.getLogger("Console");
    }

    @Override
    public Type type() {
        return Type.CONSOLE;
    }

    @Override
    public String name() {
        return "Console";
    }

    @Override
    public void sendMessage(String message) {
        logger.info(message);
    }
}