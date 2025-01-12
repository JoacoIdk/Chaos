package me.jdelg.chaos.network;

import lombok.Getter;
import lombok.SneakyThrows;
import me.jdelg.hermes.HermesPacket;
import me.jdelg.hermes.packets.ConnectPacket;
import me.jdelg.hermes.packets.DisconnectPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;

@Getter
public class Connection {
    private final NetworkManager networkManager;
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;
    private boolean identified = false;
    private int lastId = -1;

    @SneakyThrows
    public Connection(NetworkManager networkManager, Socket socket) {
        this.networkManager = networkManager;
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());

        new Thread(this::loop).start();
    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    @SneakyThrows
    public void sendPacket(HermesPacket packet) {
        if (!identified)
            return;

        int id = networkManager.getHermes().getPacketId(packet.getClass());

        if (id == -1)
            throw new IllegalArgumentException("Tried sending non-registered packet");

        output.writeInt(id);
        packet.write(output);
        output.flush();
    }

    @SneakyThrows
    public void receivePacket(int id) {
        Class<? extends HermesPacket> packetClass = networkManager.getHermes().getPacket(id);

        if (packetClass == null)
            throw new IllegalStateException("Received non-registered packet");

        Constructor<? extends HermesPacket> constructor = packetClass.getDeclaredConstructor();
        HermesPacket packet = constructor.newInstance();

        if (packet instanceof ConnectPacket)
            identified = true;

        if (!identified)
            return;

        packet.read(input);

        for (PacketReceiver receiver : networkManager.getPacketReceivers())
            receiver.onPacketReceived(this, packet);

        lastId = id;
    }

    @SneakyThrows
    public void loop() {
        if (!networkManager.isEnabled()) {
            sendPacket(new DisconnectPacket("Chaos service stopped"));
            socket.close();
            return;
        }

        if (socket.isConnected()) {
            int id = input.readInt();

            receivePacket(id);

            loop();
            return;
        }

        if (lastId == networkManager.getHermes().getPacketId(DisconnectPacket.class))
            return;

        DisconnectPacket packet = new DisconnectPacket();

        for (PacketReceiver receiver : networkManager.getPacketReceivers())
            receiver.onPacketReceived(this, packet);

        networkManager.getConnections().remove(this);
    }
}