package Application;

import Dependency.Command;
import Dependency.Utils;
import NetInteraction.ServerEvents;
import NetInteraction.ServerManager;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Класс обработчик команд
 */
public class Commander {
    private CollectionManager manager;
    private String userCommand;
    private String[] finalUserCommand;
    private ServerManager serverManager;
    private Command commandFromClient;
    private Selector selector;
    private Command commandForClient;
    boolean parsingIsComplete = false;
    public static final Logger logger = Logger.getLogger(Commander.class.getName());

    /**
     * Конструктор который дает Commander manager для выполнения команд
     *
     * @param
     */
    public Commander(ServerManager serverManager) throws IOException {
        this.serverManager = serverManager;
        manager = new CollectionManager(serverManager);
        selector = serverManager.getSelector();
        Handler handler = new FileHandler("Server/logs/log4.txt");
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    List<String> info = new ArrayList<String>();

    {
        userCommand = " ";
    }

    /**
     * Метод переводящий консоль в режим ввода команд
     *
     * @param command команда из консоли
     */
    public void interactiveMod(String command) throws IOException, ClassNotFoundException {
        finalUserCommand = command.trim().split(" +", 3);
        switch (finalUserCommand[0]) {
            case "help":
                commandForClient = new Command(manager.help());
                break;
            case "info":
                commandForClient = new Command(manager.getInfoOfCollecttion());
                break;
            case "show":
                commandForClient = new Command(manager.show());
                break;
            case "insert":
                commandForClient = new Command(manager.insert(commandFromClient.getKey(), commandFromClient));
                break;
            case "update":
                commandForClient = new Command(manager.update(commandFromClient.getId(), commandFromClient));
                break;
            case "remove_key":
                commandForClient = new Command(manager.removeKey(Utils.integerConverter(finalUserCommand[1])));
                break;
            case "clear":
                commandForClient = new Command(manager.clear());
                break;
            case "execute_script":
                commandForClient = new Command(executeScript(finalUserCommand[1]));
                break;
            case "remove_greater":
                commandForClient = new Command(manager.removeGreater(finalUserCommand[1]));
                break;
            case "replace_if_greater":
                commandForClient = new Command(manager.replaceIfGreater(Utils.integerConverter(finalUserCommand[1]), Utils.integerConverter(finalUserCommand[2])));
                break;
            case "remove_greater_key":
                commandForClient = new Command(manager.removeGreaterKey(Utils.integerConverter(finalUserCommand[1])));
                break;
            case "max_by_coordinates":
                commandForClient = new Command(manager.maxByCoordinates());
                break;
            case "print_descending":
                commandForClient = new Command(manager.getDescending());
                break;
            case "print_field_descending_position":
                commandForClient = new Command(manager.getFieldDescendingPosition());
                break;
            case "check_passport":
                if (manager.checkPassport(commandFromClient.getPassportID())) {
                    commandForClient = new Command("correct");
                } else {
                    commandForClient = new Command("not correct");
                }
                break;
            default:
                commandForClient = new Command("Неопознанная команда. Наберите 'help' для справки.");
                break;
        }

    }

    /**
     * Запуск скрипта из интерактивного режима
     *
     * @param path путь, по-которому находится скрипт
     */
    public ArrayList<String> executeScript(String path) throws IOException {
        File file = new File(path);
        ArrayList<String> message = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String scn = scanner.nextLine();
                String[] finalUserCommand = scn.trim().split(" +", 3);
                if (finalUserCommand[0].equals("insert")) {
                    for (int i = 0; i < 9; i++) {
                        String scn1 = scanner.nextLine();
                        info.add(scn1);
                    }
                    ArrayList<String> answer = manager.insert(Utils.integerConverter(finalUserCommand[1]), info);
                    for (String string : answer) {
                        message.add(string);
                    }
                } else if (finalUserCommand[0].equals("update")) {
                    if (finalUserCommand[1].equals("coordinates")) {
                        for (int i = 0; i < 2; i++) {
                            String scn1 = scanner.nextLine();
                            info.add(scn1);
                        }
                        for (String string : manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info)) {
                            message.add(string);
                        }
                    } else if (finalUserCommand[1].equals("person")) {
                        for (int i = 0; i < 3; i++) {
                            String scn1 = scanner.nextLine();
                            info.add(scn1);
                        }
                        for (String string : manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info)) {
                            message.add(string);
                        }

                    } else {
                        String scn1 = scanner.nextLine();
                        info.add(scn1);
                        ArrayList<String> answer = manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info);
                        for (String string : answer) {
                            message.add(string);
                        }
                    }
                } else if (finalUserCommand[0].equals("save")) {
                    String scn1 = scanner.nextLine();
                    info.add(scn1);
                    message.add(manager.save(info));
                } else {
                    switch (finalUserCommand[0]) {
                        case "help":
                            message.add(manager.help());
                            break;
                        case "exit":
                            manager.save();
                            message.add("exit");
                            break;
                        case "info":
                            message.add(manager.getInfoOfCollecttion());
                            break;
                        case "show":
                            for (String string : manager.show()) {
                                message.add(string);
                            }
                            break;
                        case "remove_key":
                            message.add(manager.removeKey(Utils.integerConverter(finalUserCommand[1])));
                            break;
                        case "clear":
                            message.add(manager.clear());
                            break;
                        case "execute_script":
                            for (String string : executeScript(finalUserCommand[1])) {
                                message.add(string);
                            }
                            break;
                        case "remove_greater":
                            message.add(manager.removeGreater(finalUserCommand[1]));
                            break;
                        case "replace_if_greater":
                            message.add(manager.replaceIfGreater(Utils.integerConverter(finalUserCommand[1]), Utils.integerConverter(finalUserCommand[2])));
                            break;
                        case "remove_greater_key":
                            message.add(manager.removeGreaterKey(Utils.integerConverter(finalUserCommand[1])));
                            break;
                        case "max_by_coordinates":
                            message.add(manager.maxByCoordinates());
                            break;
                        case "print_descending":
                            for (String string : manager.getDescending()) {
                                message.add(string);
                            }
                            break;
                        case "print_field_descending_position":
                            for (String string : manager.getFieldDescendingPosition()) {
                                message.add(string);
                            }
                            break;
                        default:
                            message.add("Неопознанная команда. Наберите 'help' для справки.");
                            break;
                    }
                }
                info.clear();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            message.add("Неправильный путь или формат файла.");
        } catch (IOException e) {
            message.add("Ошибка доступа");
        }
        return message;
    }

    /**
     * Запуск интерактивного режима в консоли
     */
    public void runCommander() throws IOException, ClassNotFoundException {
        logger.info("Сервер запущен c портом:"+serverManager.getPort());
        ServerConsole console = new ServerConsole(manager);
        console.start();
        while (true) { // Run forever, receiving and echoing datagrams
            if (selector.select() == 0) {
                continue;
            }
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();// Key is bit mask
                if (key.isReadable()) {
                    commandFromClient = serverManager.receiveCommand(key);
                    userCommand = commandFromClient.getUserCommand();
                    if (userCommand.equals("connect")) {
                        commandForClient = new Command("Вы подключены к серверу, парсинг уже выполнен.");
                        continue;
                    }
                    try {
                        interactiveMod(userCommand);
                    } catch (NullPointerException e) {
                        parsingIsComplete = true;
                        commandForClient = new Command(manager.parse());
                    }
                } else if (!parsingIsComplete && key.isValid() && key.isWritable()) {
                    parsingIsComplete = true;
                    ArrayList<String> message = manager.parse();
                    serverManager.sendStrings(message, key);
                } else if (key.isValid() && key.isWritable() && commandForClient.isCollection()) {
                    serverManager.sendStrings(commandForClient.getStrings(), key);
                } else if (key.isValid() && key.isWritable() && !commandForClient.isCollection()) {
                    serverManager.sendString(commandForClient.getUserCommand(), key);
                }
                keyIter.remove();
            }

        }
    }
}