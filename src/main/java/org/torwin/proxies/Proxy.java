package org.torwin.proxies;

import org.torwin.interfaces.TrafficInceptor;
import org.torwin.packages.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy {

    private int PROXY_PORT;
    private String SERVER_HOST;
    private int SERVER_PORT;
    private ServerSocket serverSocket;
    private TrafficInceptor trafficInceptor;

    public Proxy(int PROXY_PORT, String SERVER_HOST, int SERVER_PORT, TrafficInceptor trafficInceptor) {

        this.PROXY_PORT = PROXY_PORT;
        this.SERVER_HOST = SERVER_HOST;
        this.SERVER_PORT = SERVER_PORT;
        this.trafficInceptor = trafficInceptor;

        try {
            this.serverSocket = new ServerSocket(PROXY_PORT);
            System.out.println("[ SYSTEM ] >> Server is running on port: " + PROXY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start() {

        System.out.println("[ SYSTEM ] >> Server is listening...");
        try {
            while (true) {
                Socket clientSocket = this.serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleClient(Socket clientSocket) {

        try (
                Socket serverSocket = new Socket(SERVER_HOST, SERVER_PORT);
                InputStream clientIn = clientSocket.getInputStream();
                OutputStream clientOut = clientSocket.getOutputStream();
                InputStream serverIn = serverSocket.getInputStream();
                OutputStream serverOut = serverSocket.getOutputStream();
        ) {

            Thread clientToServer = new Thread(() -> {
                try {
                    transferClientServer(clientIn, serverOut);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            Thread serverToClient = new Thread(() -> {
                try {
                    transferServerClient(serverIn, clientOut);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            clientToServer.start();
            serverToClient.start();
            clientToServer.join();
            serverToClient.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void transferClientServer(InputStream in, OutputStream out) throws IOException {

        while (true) {
            try {

                Packet packet = new Packet(in, out);
                trafficInceptor.onClientToServer(packet);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

    }

    private void transferServerClient(InputStream in, OutputStream out) throws IOException {

        while (true) {
            try {

                Packet packet = new Packet(in, out);
                trafficInceptor.onServerToClient(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }


}
