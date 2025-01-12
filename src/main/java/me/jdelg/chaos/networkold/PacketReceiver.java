package me.jdelg.chaos.network;

import me.jdelg.hermes.HermesPacket;

public interface PacketReceiver {
    void onPacketReceived(Connection connection, HermesPacket packet);
}