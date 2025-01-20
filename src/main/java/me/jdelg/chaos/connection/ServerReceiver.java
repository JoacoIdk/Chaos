package me.jdelg.chaos.connection;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.server.Server;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.Receiver;
import me.jdelg.hermes.packets.IdentifyPacket;

import java.net.InetAddress;

public class ServerReceiver implements Receiver {
    @Override
    public void receivePacket(InetAddress address, Packet packet) {
        if (Chaos.get().serverManager().serverByAddress(address) != null)
            return;

        if (!(packet instanceof IdentifyPacket identifyPacket))
            return;

        String name = identifyPacket.source();
        String secret = identifyPacket.secret();
        Server server = Chaos.get().serverManager().serverByName(name);

        if (server == null) {
            Chaos.get().logger().warning("Connection from unknown server %s at %s.".formatted(name, address));
            return;
        }

        if (!server.running()) {
            Chaos.get().logger().warning("Connection from non-running %s at %s.".formatted(name, address));
            return;
        }

        if (!server.secret().equals(secret)) {
            Chaos.get().logger().warning("Connection from %s at %s with invalid secret %s.".formatted(name, address, secret));
            return;
        }

        server.address(address);
        Chaos.get().logger().info("%s identified at %s.".formatted(name, address));
    }
}