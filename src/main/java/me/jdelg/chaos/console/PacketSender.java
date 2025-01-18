package me.jdelg.chaos.console;

import me.jdelg.hermes.Hermes;
import me.jdelg.hermes.packets.MessagePacket;
import me.jdelg.hermes.type.EntityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PacketSender implements Sender {
    private final EntityType type;
    private final String name;
    private final Hermes hermes;

    public PacketSender(EntityType type, String name, Hermes hermes) {
        if (type == EntityType.CONSOLE)
            throw new IllegalStateException("Initializing network sender with console type");

        this.type = type;
        this.name = name;
        this.hermes = hermes;
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
        sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    @Override
    public void sendMessage(Component component) {
        MessagePacket packet = new MessagePacket();

        packet.entityType(type);
        packet.entityName(name);
        packet.component(component);

        hermes.broadcast(packet);
    }
}