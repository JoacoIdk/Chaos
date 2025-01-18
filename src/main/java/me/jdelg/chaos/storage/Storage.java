package me.jdelg.chaos.storage;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Storage {
    private final Path path;
    private final Map<String, String> data;

    public Storage(Path path) throws IOException {
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
    
    public void save() throws IOException {
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