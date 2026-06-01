package com.learning;

import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        // host, port, root path, config (null = defaults), then our endpoint class
        Server server = new Server("localhost", 8025, "/", null, GameEndpoint.class);

        try {
            server.start();
            System.out.println("WebSocket server running at ws://localhost:8025/echo");
            System.out.println("Press Enter to stop...");

            // Keep the program alive until you press Enter
            new BufferedReader(new InputStreamReader(System.in)).readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("Server stopped.");
        }
    }
}
