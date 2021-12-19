import java.io.*;
import java.util.*;

/**
 * Класс обработчик команд
 */
public class Commander {
    private CollectionManager manager;
    private String userCommand;
    private String[] finalUserCommand;

    /**
     * Конструктор который дает Commander manager для выполнения команд
     * @param manager Объект класса CollectionManager
     */
    public Commander(CollectionManager manager) {
        this.manager = manager;

    }
    List<String> info=new ArrayList<String>();
    {
        userCommand = " ";
    }

    /**
     * Метод переводящий консоль в режим ввода команд
     * @param command команда из консоли
     */
    public void interactiveMod(String command){
        finalUserCommand = command.trim().split(" +", 3);
        switch (finalUserCommand[0]) {
            case "":
                break;
            case "help":
                System.out.println("help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "insert null {element} : добавить новый элемент с заданным ключом\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_key null : удалить элемент из коллекции по его ключу\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого\n" +
                        "remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный\n" +
                        "max_by_coordinates : вывести любой объект из коллекции, значение поля coordinates которого является максимальным\n" +
                        "print_descending : вывести элементы коллекции в порядке убывания\n" +
                        "print_field_descending_position : вывести значения поля position всех элементов в порядке убывания");
                break;
            case "exit":
                break;
            case "info":
                manager.getInfoOfCollecttion();
                break;
            case "show":
                manager.show();
                break;
            case "insert":
                try {
                    if (finalUserCommand.length<2 | finalUserCommand.length>2) throw new IndexOutOfBoundsException();

                    manager.insert(Utils.integerConverter(finalUserCommand[1]));
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("Ключ должен быть целым число");
                }
                break;
            case "update":
                try {
                    if (finalUserCommand.length<3 | finalUserCommand.length>3) throw new IndexOutOfBoundsException();
                    manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2]);
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("ID должно быть целым число");
                }
                break;
            case "remove_key":
                try{
                    if (finalUserCommand.length<2| finalUserCommand.length>2) throw new IndexOutOfBoundsException();
                    manager.removeKey(Utils.integerConverter(finalUserCommand[1]));
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("Ключ должен быть целым число");
                }
                break;
            case "clear":
                manager.clear();
                break;
            case "save":
                manager.save();
                break;
            case "execute_script":
                try {
                    executeScript(finalUserCommand[1]);
                }catch (ArrayIndexOutOfBoundsException e){
                    System.err.println("Команда введена неверно.");
                }
                break;
            case "remove_greater":
                try{
                    if (finalUserCommand.length<2 | finalUserCommand.length>2) throw new IndexOutOfBoundsException();
                    manager.removeGreater(finalUserCommand[1]);
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("Зарплата должна быть целым число");
                }
                break;
            case "replace_if_greater":
                try {
                    if (finalUserCommand.length<3 | finalUserCommand.length>3) throw new IndexOutOfBoundsException();
                    manager.replaceIfGreater(Utils.integerConverter(finalUserCommand[1]), Utils.integerConverter(finalUserCommand[2]));
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("Проверьте ключ или зарплату они дожны быть целыми числами");
                }
                break;
            case "remove_greater_key":
                try{
                    if (finalUserCommand.length<2 | finalUserCommand.length>2) throw new IndexOutOfBoundsException();
                    manager.removeGreaterKey(Utils.integerConverter(finalUserCommand[1]));
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Вы ввели команду не правильно.");
                }catch (NullPointerException e){
                    System.err.println("Ключ должен быть целым число");
                }
                break;
            case "max_by_coordinates":
                manager.maxByCoordinates();
                break;
            case "print_descending":
                manager.printDescending();
                break;
            case "print_field_descending_position":
                manager.printFieldDescendingPosition();
                break;
            default:
                    System.out.println("Неопознанная команда. Наберите 'help' для справки.");
        }

    }

    /**
     * Запуск скрипта из интерактивного режима
     * @param path путь, по-которому находится скрипт
     */
    public void executeScript(String path) {
        File file = new File(path);
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
                    manager.insert(Utils.integerConverter(finalUserCommand[1]), info);
                } else if (finalUserCommand[0].equals("update")) {
                    if (finalUserCommand[1].equals("coordinates")) {
                        for (int i = 0; i < 2; i++) {
                            String scn1 = scanner.nextLine();
                            info.add(scn1);
                        }
                        manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info);
                    } else if (finalUserCommand[1].equals("person")) {
                        for (int i = 0; i < 3; i++) {
                            String scn1 = scanner.nextLine();
                            info.add(scn1);
                        }
                        manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info);
                    } else {
                        String scn1 = scanner.nextLine();
                        info.add(scn1);
                        manager.update(Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2], info);
                    }
                } else if (finalUserCommand[0].equals("save")) {
                    String scn1 = scanner.nextLine();
                    info.add(scn1);
                    manager.save(info);
                } else {
                    interactiveMod(scn);
                }
                info.clear();
            }
            scanner.close();
        }catch (FileNotFoundException e){
            System.err.println("Неправильный путь или формат файла.");
        }
    }

    /**
     * Запуск интерактивного режима в консоли
     */
    public void run(){
        manager.parse();
        try {
            while (!userCommand.equals("exit")) {
                userCommand = Utils.scanner().nextLine();
                interactiveMod(userCommand);
            }
        }catch (NoSuchElementException e) {
            System.out.println("Вы вышли из консольного приложения.");
        }
    }
}