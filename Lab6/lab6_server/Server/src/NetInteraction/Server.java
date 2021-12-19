package NetInteraction;

import Application.ServerConsole;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerEvents serverEvents = new ServerEvents();
        serverEvents.run();
    }
}
