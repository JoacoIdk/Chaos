package me.jdelg.chaos.connection;

import me.jdelg.chaos.Chaos;
import me.jdelg.chaos.console.PacketSender;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.Receiver;
import me.jdelg.hermes.packets.CommandPacket;

import java.net.InetAddress;

public class ConsoleReceiver implements Receiver {
    @Override
    public void receivePacket(InetAddress address, Packet packet) {
        if (Chaos.get().serverManager().serverByAddress(address) == null)
            return;

        if (!(packet instanceof CommandPacket commandPacket))
            return;

        if (!commandPacket.destination().equals(Chaos.NAME))
            return;

        PacketSender sender = new PacketSender(
                commandPacket.entityType(),
                commandPacket.entityName(),
                Chaos.get().hermes()
        );

        Chaos.get().consoleManager().run(sender, commandPacket.command());
    }
}