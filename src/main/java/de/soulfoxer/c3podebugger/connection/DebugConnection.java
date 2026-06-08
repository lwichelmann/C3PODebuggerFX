package de.soulfoxer.c3podebugger.connection;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugConnection {

    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 1000;
    private static final int HEARTBEAT_INTERVAL_MS = 2000;
    private static final int HEARTBEAT_TIMEOUT_MS = 5000;

    private static final String PING = "PING";
    private static final String PONG = "PONG";

    private final ConnectionListener listener;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private ScheduledExecutorService heartbeat;

    private volatile ConnectionState state = ConnectionState.DISCONNECTED;
    private volatile long lastPong;

    public DebugConnection(ConnectionListener listener) {
        this.listener = listener;
    }

    public ConnectionState getState() {
        return state;
    }

    public void connect(String host, int port) {
        if (state != ConnectionState.DISCONNECTED) {
            return;
        }
        setState(ConnectionState.CONNECTING);
        startDaemon("debug-connect", () -> establish(host, port));
    }

    private void establish(String host, int port) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
            s.setSoTimeout(READ_TIMEOUT_MS);

            socket = s;
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            lastPong = System.currentTimeMillis();

            setState(ConnectionState.CONNECTED);
            startDaemon("debug-reader", this::readLoop);
            startHeartbeat();
        } catch (IOException e) {
            cleanup();
            setState(ConnectionState.DISCONNECTED);
        }
    }

    private void readLoop() {
        while (state == ConnectionState.CONNECTED) {
            try {
                String line = in.readLine();
                if (line == null) {
                    disconnect();
                    return;
                }
                handleLine(line.trim());
            } catch (SocketTimeoutException e) {
                // read timeout
            } catch (IOException e) {
                disconnect();
                return;
            }
        }
    }

    private void handleLine(String line) {
        if (PONG.equals(line)) {
            lastPong = System.currentTimeMillis();
        } else {
            listener.onMessage(line);
        }
    }

    private void startHeartbeat() {
        heartbeat = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread t = new Thread(runnable, "debug-heartbeat");
            t.setDaemon(true);
            return t;
        });
        heartbeat.scheduleAtFixedRate(this::tick,
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        if (state != ConnectionState.CONNECTED) {
            return;
        }
        if (System.currentTimeMillis() - lastPong > HEARTBEAT_TIMEOUT_MS) {
            disconnect();
            return;
        }
        send(PING);
    }

    public void send(String line) {
        BufferedWriter writer = this.out;
        if (state != ConnectionState.CONNECTED || writer == null) {
            return;
        }
        try {
            writer.write(line);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            disconnect();
        }
    }

    public synchronized void disconnect() {
        if (state == ConnectionState.DISCONNECTED) {
            return;
        }
        cleanup();
        setState(ConnectionState.DISCONNECTED);
    }

    private void cleanup() {
        if (heartbeat != null) {
            heartbeat.shutdownNow();
            heartbeat = null;
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
        socket = null;
        in = null;
        out = null;
    }

    private void setState(ConnectionState newState) {
        state = newState;
        listener.onStateChanged(newState);
    }

    private static void startDaemon(String name, Runnable task) {
        Thread t = new Thread(task, name);
        t.setDaemon(true);
        t.start();
    }
}
