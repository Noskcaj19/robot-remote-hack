package frc.robot;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

interface NetworkKV {
    void put(String key, String value);

    void put(String key, int value);

    void put(String key, double value);

    void put(String key, boolean value);

    String getString(String key);

    int getInt(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    void addListener(String key, Consumer<NetworkKVUpdate> listener);
}

/**
 * A simple NetworkTables but faster and with first class websockets
 */
public class NetworkKVServer extends WebSocketServer implements NetworkKV {
    private static NetworkKVServer instance;
    private final HashMap<String, JsonNode> table = new HashMap<>();
    private final HashMap<String, ArrayList<Consumer<NetworkKVUpdate>>> listeners = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper(new JsonFactory());

    NetworkKVServer(int port) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        start();
    }

    public static NetworkKVServer getInstance() {
        if (instance == null) {
            instance = new NetworkKVServer(8887);
        }
        return instance;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected " + conn.getRemoteSocketAddress());
        for (var entry : table.entrySet()) {
            pushUpdateToConn(conn, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnected " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        NetworkKVUpdate update;
        try {
            update = mapper.readValue(message, NetworkKVUpdate.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        doUpdate(update);
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Websocket error:");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Websocket server started on port: " + this.getPort());
    }


    private void pushUpdateToConn(WebSocket conn, String key, JsonNode value) {
        NetworkKVUpdate update = new NetworkKVUpdate(key, value);
        try {
            conn.send(mapper.writeValueAsString(update));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void doUpdate(NetworkKVUpdate update) {
        commitUpdate(update);
        pushUpdate(update);
    }

    private void doUpdate(String key, JsonNode value) {
        doUpdate(new NetworkKVUpdate(key, value));
    }

    private void pushUpdate(NetworkKVUpdate update) {
        try {
            broadcast(mapper.writeValueAsString(update));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void commitUpdate(NetworkKVUpdate update) {
        table.put(update.key, update.value);
        Optional.ofNullable(listeners.get(update.key))
                .ifPresent(listeners -> listeners.forEach(l -> l.accept(update)));
    }

    @Override
    public void put(String key, String value) {
        doUpdate(key, new TextNode(value));
    }

    @Override
    public void put(String key, int value) {
        doUpdate(key, new IntNode(value));
    }

    @Override
    public void put(String key, double value) {
        doUpdate(key, new DoubleNode(value));
    }

    @Override
    public void put(String key, boolean value) {
        doUpdate(key, BooleanNode.valueOf(value));
    }

    @Override
    public String getString(String key) {
        return table.get(key).asText();
    }

    @Override
    public int getInt(String key) {
        return table.get(key).asInt();
    }

    @Override
    public double getDouble(String key) {
        return table.get(key).asDouble();
    }

    @Override
    public boolean getBoolean(String key) {
        return table.get(key).asBoolean();
    }

    @Override
    public void addListener(String key, Consumer<NetworkKVUpdate> listener) {
        System.out.println("Listener added for \"" + key + "\"");
        listeners.computeIfAbsent(key, k -> new ArrayList<>()).add(listener);
    }
}
