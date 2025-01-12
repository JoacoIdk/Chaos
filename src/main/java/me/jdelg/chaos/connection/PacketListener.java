package me.jdelg.chaos.connection;

import me.jdelg.hermes.HermesPacket;

public interface PacketListener {
    void receive(HermesPacket packet);
}