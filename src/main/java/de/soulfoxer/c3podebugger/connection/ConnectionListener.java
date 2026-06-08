package de.soulfoxer.c3podebugger.connection;

public interface ConnectionListener {

    void onStateChanged(ConnectionState state);

    void onMessage(String line);
}
