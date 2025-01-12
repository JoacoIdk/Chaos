package me.jdelg.chaos.storage;

import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    public static final int VERSION = Integer.MIN_VALUE; // + 0

    private final Path path;
    private final Map<String, String> data;

    @SneakyThrows
    public Storage(Path path) {
        this.path = path;
        this.data = new HashMap<>();

        InputStream stream = Files.newInputStream(path);
        DataInputStream input = new DataInputStream(stream);

        int entries = input.readInt();

        for (int i = 0; i < entries; i++)
            data.put(input.readUTF(), input.readUTF());

        input.close();
        stream.close();
    }

    @SneakyThrows
    public void save() {
        OutputStream stream = Files.newOutputStream(path);
        DataOutputStream output = new DataOutputStream(stream);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            output.writeUTF(entry.getKey());
            output.writeUTF(entry.getValue());
        }

        output.flush();
        output.close();
        stream.close();
    }
}