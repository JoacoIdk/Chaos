package me.jdelg.chaos.console;

import me.jdelg.hermes.type.EntityType;
import net.kyori.adventure.text.Component;

public class PacketSender implements Sender {
    private final EntityType type;
    private final String name;

    public PacketSender(EntityType type, String name) {
        if (type == EntityType.CONSOLE)
            throw new IllegalStateException("Initializing network sender with console type");

        this.type = type;
        this.name = name;
    }

    @Override
    public EntityType type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void sendMessage(Component component) {
    }
}