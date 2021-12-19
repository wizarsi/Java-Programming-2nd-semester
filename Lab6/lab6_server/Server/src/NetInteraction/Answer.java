package NetInteraction;

import Application.Commander;
import Dependency.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Answer{
    DatagramChannel channel;
    public static final Logger logger = Logger.getLogger(Answer.class.getName());

    public  Answer(DatagramChannel channel) throws IOException {
        this.channel = channel;
        Handler handler = new FileHandler("Server/logs/log1.txt");
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public void answerForClient(Command command, SelectionKey key) throws IOException {
        //Отправляем данные клиенту
        DatagramChannel channel = (DatagramChannel) key.channel();
        Client client = (Client) key.attachment();
        client.buffer.flip(); // Prepare buffer for sending
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(command);
        client.buffer = ByteBuffer.wrap(outputStream.toByteArray());
        int bytesSent= channel.send(client.buffer, client.clientAddress);
        if(command.isCollection()) {
            StringBuilder str = new StringBuilder();
            for(String string:command.getStrings()){
                str.append(string).append("\n");
            }
            logger.info("Сообщение, отпрвлено: " + str.toString() + " - Адрес клиента:" + client.clientAddress);
        }else{
            logger.info("Сообщение, отпрвлено: " + command.getUserCommand() + " - Адрес клиента:" + client.clientAddress);
        }
        if (bytesSent != 0) { // Buffer completely written?
            // No longer interested in writes
            key.interestOps(SelectionKey.OP_READ);
        }
    }

}
