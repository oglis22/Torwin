package org.torwin.example;

import org.torwin.proxies.Proxy;

public class Main {
    public static void main(String[] args) {

        Proxy proxy = new Proxy(4444, "127.0.0.1", 25565, new Inceptor());
        proxy.start();

    }
}