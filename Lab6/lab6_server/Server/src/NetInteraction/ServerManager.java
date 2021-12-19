package NetInteraction;

import Dependency.Command;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class ServerManager {
    private Answer answer;
    private Ask ask;
    private Selector selector;
    private int port;
    public ServerManager(Answer answer, Ask ask,Selector selector,int port) {
        this.answer = answer;
        this.ask = ask;
        this.selector =selector;
        this.port = port;
    }
    public void sendStrings(ArrayList<String> strings, SelectionKey key) throws IOException {
        Command command = new Command(strings);
        answer.answerForClient(command,key);
    }

    public int getPort() {
        return port;
    }

    public Selector getSelector() {
        return selector;
    }

    public void sendString(String string,SelectionKey key) throws IOException {
        Command command = new Command(string);
        answer.answerForClient(command,key);
    }



    public Command receiveCommand(SelectionKey key) throws IOException, ClassNotFoundException {
        Command command = ask.askForClient(key);
        return command;
    }


}
