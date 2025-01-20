package me.jdelg.chaos.connection;

import me.jdelg.chaos.Chaos;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.Receiver;

import java.net.InetAddress;

public class Broadcaster implements Receiver {
    @Override
    public void receivePacket(InetAddress address, Packet packet) {
        if (Chaos.get().serverManager().serverByAddress(address) == null)
            return;

        Chaos.get().hermes().send(
                Chaos.get().serverManager().getAddresses(),
                packet
        );
    }
}