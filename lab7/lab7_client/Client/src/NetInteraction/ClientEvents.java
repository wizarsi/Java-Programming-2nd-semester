package NetInteraction;

import Dependency.Command;
import Dependency.Utils;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;

public class ClientEvents {
    ClientManager clientManager;
    boolean isLogIn=false;
    public ClientEvents(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public void run() throws IOException, ClassNotFoundException {
        clientManager.send(new Command("connect"));
        Command commandIn = clientManager.receiveCommand();
        if (commandIn.isCollection()) {
            clientManager.printCollection(commandIn);
        } else {
            clientManager.printString(commandIn);
        }
        System.out.println("Для авторизации введите auth, а для регистрации reg:");
        commandMode();

    }


    public void commandMode() throws IOException, ClassNotFoundException {
        Command commandIn;
        while (true) {
            commandIn = clientManager.interact();
            if (commandIn.isCollection()) {
                clientManager.printCollection(commandIn);
            } else {
                clientManager.printString(commandIn);
            }
        }
    }


}
