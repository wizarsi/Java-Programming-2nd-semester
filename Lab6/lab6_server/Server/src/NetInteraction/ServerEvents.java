package NetInteraction;

import Application.CollectionManager;
import Application.Commander;
import Application.ServerConsole;
import Dependency.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ServerEvents {
    int  port;
    public void run() throws IOException, ClassNotFoundException {
        File file =new File("Server/config.txt");
        Scanner scanner = new Scanner(file);
        String scn = scanner.nextLine();
        String[] strings = scn.trim().split(" +");
        port = Integer.parseInt (strings[0]);
        SocketAddress address = new InetSocketAddress(port);
        DatagramChannel channel = DatagramChannel.open();
        Selector selector = Selector.open();
        channel.socket().bind(address);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, new Client());
        Answer answer = new Answer(channel);
        Ask ask = new Ask(channel);
        ServerManager serverManager = new ServerManager(answer,ask, selector,port);
        Commander commander = new Commander(serverManager);
        commander.runCommander();
    }
}
