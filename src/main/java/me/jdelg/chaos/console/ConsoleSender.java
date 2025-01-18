package me.jdelg.chaos.console;

import me.jdelg.hermes.type.EntityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;

import java.util.logging.Logger;

public class ConsoleSender implements Sender {
    private final Logger logger;

    public ConsoleSender() {
        this.logger = Logger.getLogger("Console");
    }

    @Override
    public EntityType type() {
        return EntityType.CONSOLE;
    }

    @Override
    public String name() {
        return "Console";
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    @Override
    public void sendMessage(Component component) {
        logger.info(ANSIComponentSerializer.ansi().serialize(component));
    }
}