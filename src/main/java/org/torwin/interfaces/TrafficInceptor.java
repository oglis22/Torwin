package org.torwin.interfaces;

import org.torwin.packages.Packet;

public interface TrafficInceptor {
    void onClientToServer(Packet packet);
    void onServerToClient(Packet packet);
}
