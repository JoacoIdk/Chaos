package me.jdelg.chaos.network;

import lombok.Getter;
import lombok.SneakyThrows;
import me.jdelg.hermes.Hermes;
import me.jdelg.hermes.HermesPacket;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

@Getter
public class NetworkManager {
    private final Hermes hermes = new Hermes();
    private final List<PacketReceiver> packetReceivers = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();
    private boolean enabled = false;
    private ServerSocket socket;

    @SneakyThrows
    public void start(int port) {
        enabled = true;
        socket = new ServerSocket(port);

        new Thread(this::loop).start();
    }

    @SneakyThrows
    public void stop() {
        enabled = false;
        socket.close();
    }

    public Connection getByAddress(InetAddress address) {
         return connections.stream().filter(c -> c.getAddress().equals(address)).findFirst().orElse(null);
    }

    @SneakyThrows
    public void loop() {
        Connection connection = new Connection(this, socket.accept());

        connections.add(connection);

        if (enabled)
            loop();
    }

    public void broadcastPacket(HermesPacket packet) {
        for (Connection connection : connections)
            connection.sendPacket(packet);
    }

    public void registerPacketHandler(PacketReceiver handler) {
        packetReceivers.add(handler);
    }
}