package NetInteraction;

import Dependency.Command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Ask {
    private DatagramChannel channel;
    public static final Logger logger = Logger.getLogger(Ask.class.getName());

    public Ask(DatagramChannel channel) throws IOException {
        this.channel = channel;
        Handler handler = new FileHandler("Server/logs/log2.txt");
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public Command askForClient(SelectionKey key) throws IOException, ClassNotFoundException {
        //Получаем данные у клиента
        byte[] b = new byte[65536];
        ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.clear();
        DatagramChannel channel = (DatagramChannel) key.channel();
        Client client = (Client) key.attachment();
        client.clientAddress=channel.receive(buffer);
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.array());
        ObjectInputStream is = new ObjectInputStream(in);
        Command command = (Command) is.readObject();
        if(command.isCollection()) {
            StringBuilder str = new StringBuilder();
            for(String string:command.getStrings()){
                str.append(string).append("\n");
            }
            logger.info("Сообщение, получено: " + str.toString() + " - Адрес клиента:" + client.clientAddress);
        }else if(command.getUserCommand().equals("connect")){
            logger.info("Новое подключение - Адрес клиента:" + client.clientAddress);
        }else {
            logger.info("Сообщение, получено: " + command.getUserCommand() + " - Адрес клиента:" + client.clientAddress);
        }
        if(client.clientAddress != null){
            key.interestOps(SelectionKey.OP_WRITE);
        }
        return command;
    }



}
