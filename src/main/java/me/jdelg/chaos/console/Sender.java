package me.jdelg.chaos.console;

import me.jdelg.hermes.type.EntityType;
import net.kyori.adventure.text.Component;

public interface Sender {
    EntityType type();
    String name();
    void sendMessage(String message);
    void sendMessage(Component component);
}