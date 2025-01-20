package me.jdelg.chaos.connection;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.server.Server;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.Receiver;
import me.jdelg.hermes.packets.StatusPacket;
import me.jdelg.hermes.type.Status;

import java.net.InetAddress;

public class StatusReceiver implements Receiver {
    @Override
    public void receivePacket(InetAddress address, Packet packet) {
        if (Chaos.get().serverManager().serverByAddress(address) == null)
            return;

        if (!(packet instanceof StatusPacket statusPacket))
            return;

        String name = statusPacket.source();
        Status status = statusPacket.status();
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            Chaos.get().logger().severe("Detected unknown server %s with open connection at %s.".formatted(name, address));
            return;
        }

        server.status(status);
    }
}