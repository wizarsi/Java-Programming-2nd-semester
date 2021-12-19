package Application;

import Dependency.Command;
import Dependency.Position;
import Dependency.Status;
import Dependency.Utils;
import NetInteraction.ServerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для работы с коллекцией TreeMap-...
 */
public class CollectionManager {
    Long id;
    Date initDate;
    TreeMap<Integer, Worker> workerList;
    String path = "Server/file.xml";
    String savePath = "Server/file.txt";
    File file;
    DocumentBuilder builder = null;
    Document document = null;
    ServerManager serverManager;
    ObjectCheckerForServer checker = new ObjectCheckerForServer();

    /**
     * Конструктор,в котором создается объект document "для его парсинга по дереву"(DOM)
     */
    public CollectionManager(ServerManager serverManager) throws IOException {
        this.serverManager = serverManager;
    }

    /**
     * Метод осуществляющий парсинг средствами DOM
     */
    public ArrayList<String> parse() {
        ArrayList<String> message = new ArrayList<>();
        while (document == null) {
            try {
                if (path.isEmpty()) throw new NullPointerException();
                file = new File(path);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    builder = factory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    message.add("Ошибка средства парсинга.");
                }
                try {
                    document = builder.parse(file);
                } catch (FileNotFoundException e) {
                    message.add("Неравильный путь.");
                } catch (IOException e) {
                    message.add("Ошибка доступа к файлу.");
                } catch (SAXException e) {
                    message.add(" Нельзя выполнить парсинг. Файл должен быть в формате .xml");
                }

            } catch (NullPointerException e) {
                message.add("Нельзя передавать в качестве пути null. Проверьте правильность введенего пути.");
            }
        }
        Element employersElement = (Element) document.getElementsByTagName("employers").item(0);
        NodeList employeeNodeList = null;
        employeeNodeList = document.getElementsByTagName("employee");
        workerList = new TreeMap<>();
        initDate = new Date();
        for (int i = 0; i < employeeNodeList.getLength(); i++) {
            if (employeeNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element employeeElement = (Element) employeeNodeList.item(i);
                Worker worker = new Worker();
                LocalDateTime date = LocalDateTime.now();
                worker.setCreationDate(date);
                id = Utils.longConverter(i + 1);
                worker.setId(id);
                NodeList childNodes = employeeElement.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNodes.item(j);
                        String text = childElement.getTextContent();
                        String[] textArray = text.trim().split(" +");
                        switch (childElement.getNodeName()) {
                            case "name": {
                                if (text != null && (text.length() != 0 | textArray[0].length() != 0)) {
                                    worker.setName(childElement.getTextContent());
                                } else {
                                    message.add("name в xml.Строка не может быть пустой");
                                }
                            }
                            break;
                            case "coordinates": {
                                Integer x = 0;
                                Double y = 0.0;
                                NodeList coordinatesList = childElement.getChildNodes();
                                for (int k = 0; k < coordinatesList.getLength(); k++) {
                                    if (coordinatesList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                        switch (coordinatesList.item(k).getNodeName()) {
                                            case "x":
                                                x = Utils.integerConverter(coordinatesList.item(k).getTextContent());
                                                if (x == null) {
                                                    message.add("X XML файл.Значение не может быть пустым.");
                                                } else if (x <= -716) {
                                                    message.add("X XML файл.Значение поля должно быть больше -716");
                                                }
                                                break;
                                            case "y":
                                                y = Utils.doubleConverter(coordinatesList.item(k).getTextContent());
                                                if (y == null) {
                                                    message.add("Y XML файл.Значение не может быть пустым.");
                                                } else if (y >= 943) {
                                                    message.add("Y XML файл.Значение поля должно быть меньше, либо = 943");
                                                }
                                                break;
                                        }
                                    }
                                }
                                worker.setupCoordinates(x, y);
                            }
                            break;
                            case "salary": {
                                Integer salary = Utils.integerConverter(childElement.getTextContent());
                                if (salary != null && salary > 0) {
                                    worker.setSalary(salary);
                                } else {
                                    message.add("salary XML файл.Зараплата должна быть больше 0");
                                }
                            }
                            break;
                            case "position": {
                                Position position = null;
                                switch (childElement.getTextContent()) {
                                    case "DIRECTOR": {
                                        position = Position.DIRECTOR;
                                    }
                                    break;
                                    case "LABORER": {
                                        position = Position.LABORER;
                                    }
                                    break;
                                    case "HUMAN_RESOURCES": {
                                        position = Position.HUMAN_RESOURCES;
                                    }
                                    break;
                                    case "HEAD_OF_DEPARTMENT": {
                                        position = Position.HEAD_OF_DEPARTMENT;
                                    }
                                    break;
                                    case "MANAGER_OF_CLEANING": {
                                        position = Position.MANAGER_OF_CLEANING;
                                    }
                                    break;
                                }
                                if (position != null) {
                                    worker.setPosition(position);
                                } else {
                                    message.add("position XMl файл");
                                }
                            }
                            break;
                            case "status": {
                                Status status = null;
                                switch (childElement.getTextContent()) {
                                    case "HIRED": {
                                        status = Status.HIRED;
                                    }
                                    break;
                                    case "RECOMMENDED_FOR_PROMOTION": {
                                        status = Status.RECOMMENDED_FOR_PROMOTION;
                                    }
                                    break;
                                    case "REGULAR": {
                                        status = Status.REGULAR;
                                    }
                                    break;
                                    case "PROBATION": {
                                        status = Status.PROBATION;
                                    }
                                    break;
                                }
                                if (status != null) {
                                    worker.setStatus(status);
                                } else {
                                    message.add("status XMl файл");
                                }
                            }
                            break;
                            case "person": {
                                LocalDateTime birthday = null; //Поле может быть null
                                float height = 0; //Значение поля должно быть больше 0
                                String passportID = null; //Длина строки не должна быть больше 44, Строка не может быть пустой,
                                // Значение этого поля должно быть уникальным, Поле не может быть null
                                NodeList personNodeList = childElement.getChildNodes();
                                for (int m = 0; m < personNodeList.getLength(); m++) {
                                    if (personNodeList.item(m).getNodeType() == Node.ELEMENT_NODE) {
                                        Element person = (Element) personNodeList.item(m);
                                        switch (person.getNodeName()) {
                                            case "birthday": {
                                                try {
                                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                                                    birthday = LocalDateTime.parse(person.getTextContent(), formatter);
                                                } catch (DateTimeParseException e) {
                                                    message.add("Проверьте дату рождения в XML файле.\nMMMM d, yyyy hh:mm AM/PM");
                                                }
                                            }
                                            break;
                                            case "height": {
                                                height = Utils.floatConverter(person.getTextContent());
                                                if (height <= 0) {
                                                    message.add("height XML файл");
                                                }
                                            }
                                            break;
                                            case "pasportID": {
                                                passportID = person.getTextContent();
                                                String[] passport = passportID.trim().split(" +");
                                                boolean isEqually = false;
                                                for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                                                    if (entry.getValue().getPerson().getPassportID().equals(passportID)) {
                                                        isEqually = true;
                                                    }
                                                }
                                                if (passportID.length() > 45 && isEqually && (passport.length != 0 || passport[0].length() == 0)) {
                                                    message.add("passportID xml файл.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                                worker.setupPersonInfo(birthday, height, passportID);
                            }
                            break;
                        }
                    }
                }
                if (checker.checkWorker(worker, workerList)) {
                    workerList.put(i + 1, worker);

                } else {
                    message.add("Если отсутствуют ошибки,вам нужно внести недостоющие данные в XML файл.");
                    message.add("Невозможно внести данные о работнике с ID:" + worker.getId());
                }
            }
        }
        message.add("Парсинг выполнен успешно!");
        return message;

    }

    /**
     * Выводит информацию о коллекции
     */
    public String getInfoOfCollecttion() {
        String info = "Тип коллекции: " + workerList.getClass() +
                "\nДата инициализации: " + initDate +
                "\nКоличество элементов: " + workerList.size();
        return info;
    }

    /**
     * Выводит информацию о объектах коллекции
     */
    public ArrayList<String> show() {
        ArrayList<String> strings = new ArrayList<String>();
        workerList.forEach((key, value) -> strings.add("Key: " + key + ". Value: " + value));
        return strings;
    }

    /**
     * Очищает коллекцию
     */
    public String clear() {
        workerList.clear();
        return "Коллекция очищена.";
    }

    /**
     * Добовляет новый объект в коллекцию, пользователь сам вводит его
     *
     * @param key ключ под которым объект добовляется  в TreeMap
     */
    public String insert(int key, Command command) {
        String message = "Неверные данные, сотрудник не внесен в базу данных.";
        Worker worker = new Worker();
        worker.setName(command.getName());
        worker.setPosition(command.getPosition());
        worker.setStatus(command.getStatus());
        worker.setSalary(command.getSalary());
        worker.setId(id + 1);
        worker.setupCoordinates(command.getX(), command.getY());
        worker.setupPersonInfo(command.getBirthday(), command.getHeight(), command.getPassportID());
        LocalDateTime date = LocalDateTime.now();
        worker.setCreationDate(date);
        if (checker.checkWorker(worker, workerList)) {
            id += 1;
            workerList.put(key, worker);
            message = "Данные о сотруднике внесены успешно.";
        }
        return message;
    }

    public TreeMap<Integer, Worker> getWorkerList() {
        return workerList;
    }

    /**
     * Добовляет новый объект в коллекцию, информация о нем читается из скрипта
     *
     * @param key  ключ под которым объект добовляется  в TreeMap
     * @param info информация из скрипта
     */
    public ArrayList<String> insert(int key, List<String> info) {
        ArrayList<String> list = new ArrayList<>();
        LocalDateTime birthday = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        Worker worker = new Worker();
        String[] values = {"имя", "координату x", "координату y", "заработную плату", "день рождения, исапользуя формат(MMMM d, yyyy hh:mm AM/PM)", "рост", "паспортные данные", "должность", "статус"};
        for (int i = 0; i < values.length; i++) {
            switch (values[i]) {
                case "имя":
                    String scn = info.get(i);
                    String[] scnArray = scn.trim().split(" +");
                    if (scn != null && (scn.length() != 0 | scnArray[0].length() != 0)) {
                        worker.setName(scn);
                    } else {
                        list.add("Имя не может быть пустой строкой, повторите попытку.");
                    }
                    break;
                case "должность":
                    Position position = null;
                    switch (info.get(i)) {
                        case "DIRECTOR":
                            position = Position.DIRECTOR;
                            break;
                        case "LABORER":
                            position = Position.LABORER;
                            break;
                        case "HUMAN_RESOURCES":
                            position = Position.HUMAN_RESOURCES;
                            break;
                        case "HEAD_OF_DEPARTMENT":
                            position = Position.HEAD_OF_DEPARTMENT;
                            break;
                        case "MANAGER_OF_CLEANING":
                            position = Position.MANAGER_OF_CLEANING;
                            break;
                    }
                    if (position == null) {
                        list.add("Нужно было выбрать значение из списка\n" +
                                "DIRECTOR,\n" +
                                "LABORER,\n" +
                                "HUMAN_RESOURCES,\n" +
                                "HEAD_OF_DEPARTMENT,\n" +
                                "MANAGER_OF_CLEANING");
                    } else {
                        worker.setPosition(position);
                    }
                    break;
                case "статус":
                    Status status = null;
                    switch (info.get(i)) {
                        case "HIRED":
                            status = Status.HIRED;
                            break;
                        case "RECOMMENDED_FOR_PROMOTION":
                            status = Status.RECOMMENDED_FOR_PROMOTION;
                            break;
                        case "REGULAR":
                            status = Status.REGULAR;
                            break;
                        case "PROBATION":
                            status = Status.PROBATION;
                            break;
                    }
                    if (status == null) {
                        list.add("Нужно было выбрать значение из списка\n" +
                                "HIRED,\n" +
                                "RECOMMENDED_FOR_PROMOTION,\n" +
                                "REGULAR,\n" +
                                "PROBATION");
                    } else {
                        worker.setStatus(status);
                    }
                    break;
                case "координату x":
                    x = Utils.integerConverter(info.get(i));
                    if (x == null && x <= -716) {
                        list.add("Значение поля должно быть больше -716");
                    }
                    break;
                case "координату y":
                    y = Utils.doubleConverter(info.get(i));
                    if (y != null && y > 943) {
                        list.add("Максимальное значение поля: 943");
                    }
                    break;
                case "заработную плату":
                    Integer salary = null;
                    salary = Utils.integerConverter(info.get(i));
                    if (salary != null) {
                        if (salary <= 0) {
                            list.add("Зараплата должна быть больше 0");
                        } else {
                            worker.setSalary(salary);
                        }
                    } else {
                        list.add("Зарплата введена неверно");
                    }
                    break;
                case "день рождения, исапользуя формат(MMMM d, yyyy hh:mm AM/PM)":
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                        birthday = LocalDateTime.parse(info.get(i), formatter);
                    } catch (DateTimeParseException e) {
                        list.add("Проверьте введенную дату рождения.\nОна должна соответствовать формату: MMMM d, yyyy hh:mm AM/PM");
                    }
                    break;
                case "рост":
                    height = Utils.floatConverter(info.get(i));
                    if (height <= 0) {

                        list.add("Также рост не может быть меньше или равным 0.");
                    }
                    break;
                case "паспортные данные":
                    passportID = info.get(i);
                    String[] passport = passportID.trim().split(" +");
                    boolean isEqually = false;
                    for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                        if (entry.getValue().getPerson().getPassportID().equals(passportID)) {
                            isEqually = true;
                        }
                    }
                    if (passportID.length() > 44 && isEqually && (passport.length != 0 || passport[0].length() == 0)) {
                        list.add("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                    }
                    break;
            }
        }
        worker.setId(id + 1);
        worker.setupCoordinates(x, y);
        worker.setupPersonInfo(birthday, height, passportID);
        LocalDateTime date = LocalDateTime.now();
        worker.setCreationDate(date);
        if (checker.checkWorker(worker, workerList)) {
            list.add("Данные о сотруднике внесены успешно.");
            workerList.put(key, worker);
        } else {
            list.add("Проверьте порядок данных в скрипте.\n" +
                    "name\nx\ny\nsalary\nbirthday\nheight\npasportID\nposition\nstatus");
        }
        return list;
    }

    /**
     * Обновляет поле объекта из коллекции, посредством пользавательского ввода
     *
     * @param id      признак, по-которому выбирается элемент
     * @param command поле, которое хотим обновить
     */
    public String update(int id, Command command) {
        String element = command.getElement();
        String message;
        LocalDateTime birthday = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        String type = "x";
        String typeOfPerson = "birthday";
        boolean isAlive = workerList.entrySet().stream().filter((worker) -> worker.getValue().getId() == id).findFirst().map(obj -> true).orElse(false);
        boolean isAll = false;
        boolean isCorrect = false;
        if (isAlive) {
            Worker findWorker = workerList.get(id);
            while (!isAll) {
                if (element.equals("position")) {
                    Position position = null;
                    switch (String.valueOf(command.getPosition())) {
                        case "DIRECTOR":
                            position = Position.DIRECTOR;
                            break;
                        case "LABORER":
                            position = Position.LABORER;
                            break;
                        case "HUMAN_RESOURCES":
                            position = Position.HUMAN_RESOURCES;
                            break;
                        case "HEAD_OF_DEPARTMENT":
                            position = Position.HEAD_OF_DEPARTMENT;
                            break;
                        case "MANAGER_OF_CLEANING":
                            position = Position.MANAGER_OF_CLEANING;
                            break;
                    }
                    if (position != null) {
                        isCorrect = true;
                        isAll = true;
                        findWorker.setPosition(position);
                        setupCreationDate(findWorker);
                    } else {
                        isAll = true;
                    }
                } else if (element.equals("status")) {
                    Status status = null;
                    switch (String.valueOf(command.getStatus())) {
                        case "HIRED":
                            status = Status.HIRED;
                            break;
                        case "RECOMMENDED_FOR_PROMOTION":
                            status = Status.RECOMMENDED_FOR_PROMOTION;
                            break;
                        case "REGULAR":
                            status = Status.REGULAR;
                            break;
                        case "PROBATION":
                            status = Status.PROBATION;
                            break;
                    }
                    if (status != null) {
                        isCorrect = true;
                        isAll = true;
                        findWorker.setStatus(status);
                        setupCreationDate(findWorker);
                    } else {
                        isAll = true;
                    }

                } else if (element.equals("coordinates")) {
                    switch (type) {
                        case "x":
                            x = command.getX();
                            if (x != null && x > -716) {
                                type = "y";
                            } else {
                                isAll = true;
                            }
                            break;
                        case "y":
                            y = command.getY();
                            if (y != null && y <= 943) {
                                findWorker.setupCoordinates(x, y);
                                setupCreationDate(findWorker);
                                isCorrect = true;
                                isAll = true;
                            } else {
                                isAll = true;
                            }
                            break;
                    }
                } else if (element.equals("person")) {
                    switch (typeOfPerson) {
                        case "birthday":
                            birthday = command.getBirthday();
                            if (birthday != null) {
                                typeOfPerson = "height";
                            } else {
                                isAll = true;
                            }
                            break;
                        case "height":
                            height = command.getHeight();
                            if (height > 0) {
                                typeOfPerson = "passportID";
                            } else {
                                isAll = true;
                            }
                            break;
                        case "passportID":
                            passportID = command.getPassportID();
                            findWorker.setupPersonInfo(birthday, height, passportID);
                            setupCreationDate(findWorker);
                            isCorrect = true;
                            isAll = true;
                            break;
                    }
                } else if (element.equals("name") | element.equals("salary")) {
                    switch (element) {
                        case "name":
                            String name = null;
                            name = command.getName();
                            String[] nameArray = name.trim().split("/s+");
                            if (name != null && (name.length() != 0 | nameArray[0].length() != 0)) {
                                findWorker.setName(name);
                                setupCreationDate(findWorker);
                                isCorrect = true;
                                isAll = true;
                            } else {
                                isAll = true;
                            }
                            break;
                        case "salary":
                            Integer salary = null;
                            salary = command.getSalary();
                            if (salary != null) {
                                if (salary > 0) {
                                    findWorker.setSalary(salary);
                                    setupCreationDate(findWorker);
                                    isCorrect = true;
                                    isAll = true;
                                }
                            }
                            if (isCorrect != true) {
                                isAll = true;
                            }
                            break;
                    }
                }
            }
        }
        if (isCorrect) {
            message = "Данные сотрудника с id:" + command.getId() + ", успешно обновлены.";
        } else {
            message = "Cотрудник не найден в базе";
        }
        return message;
    }

    public boolean checkPassport(String passportID) {
        String[] passport = passportID.trim().split(" +");
        boolean isCorrect = false;
        boolean isEqually = workerList.entrySet().stream().filter(entry -> entry.getValue().getPerson().getPassportID()
                .equals(passportID)).findFirst().map(o -> true).orElse(false);
        if (passportID.length() < 45 && !isEqually && (passport.length == 0 || passport[0].length() != 0)) {
            isCorrect = true;
        }
        return isCorrect;
    }

    /**
     * Обновляет поле объекта из коллекции, посредством чтения из скрипта
     *
     * @param id      признак, по-которому выбирается элемент
     * @param element поле, которое хотим обновить
     * @param info    информация из скрипта
     */
    public ArrayList<String> update(int id, String element, List<String> info) {
        ArrayList<String> list = new ArrayList<>();
        LocalDateTime birthday = null;
        Worker findWorker = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        String type = "x";
        String typeOfPerson = "birthday";
        boolean isAlive = workerList.entrySet().stream().filter((worker) -> worker.getValue().getId() == id).findFirst().map(obj -> true).orElse(false);
        if(isAlive) {
            findWorker = workerList.get(id);
        }
        if (element.equals("position")) {
            Position position = null;
            switch (info.get(0)) {
                case "DIRECTOR":
                    position = Position.DIRECTOR;
                    break;
                case "LABORER":
                    position = Position.LABORER;
                    break;
                case "HUMAN_RESOURCES":
                    position = Position.HUMAN_RESOURCES;
                    break;
                case "HEAD_OF_DEPARTMENT":
                    position = Position.HEAD_OF_DEPARTMENT;
                    break;
                case "MANAGER_OF_CLEANING":
                    position = Position.MANAGER_OF_CLEANING;
                    break;
            }
            if (position == null) {
                list.add("Вы ввели непраильный position\n" +
                        "DIRECTOR,\n" +
                        "LABORER,\n" +
                        "HUMAN_RESOURCES,\n" +
                        "HEAD_OF_DEPARTMENT,\n" +
                        "MANAGER_OF_CLEANING");
            } else {
                findWorker.setPosition(position);
                setupCreationDate(findWorker);
                list.add("Элемент position обновлен успешно");
            }
        } else if (element.equals("status")) {
            Status status = null;
            switch (info.get(0)) {
                case "HIRED":
                    status = Status.HIRED;
                    break;
                case "RECOMMENDED_FOR_PROMOTION":
                    status = Status.RECOMMENDED_FOR_PROMOTION;
                    break;
                case "REGULAR":
                    status = Status.REGULAR;
                    break;
                case "PROBATION":
                    status = Status.PROBATION;
                    break;
            }
            if (status == null) {
                list.add("status. Нужно было выбрать значение из списка\n" +
                        "HIRED,\n" +
                        "RECOMMENDED_FOR_PROMOTION,\n" +
                        "REGULAR,\n" +
                        "PROBATION");
            } else {
                findWorker.setStatus(status);
                setupCreationDate(findWorker);
                list.add("Элемент status обновлен успешно");
            }

        } else if (element.equals("coordinates")) {
            switch (type) {
                case "x":
                    x = Utils.integerConverter(info.get(0));
                    if (x != null && x > -716) {
                        type = "y";
                    } else {
                        list.add("Значение поля должно быть больше -716");
                    }
                    break;
                case "y":
                    y = Utils.doubleConverter(info.get(1));
                    if (y != null && y <= 943) {
                        findWorker.setupCoordinates(x, y);
                        setupCreationDate(findWorker);
                        list.add("Элемент coordinates обновлен успешно");
                    } else {
                        list.add("Значение поля должно быть меньше, либо = 943");
                    }
                    break;
            }
        } else if (element.equals("person")) {
            switch (typeOfPerson) {
                case "birthday":
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                        birthday = LocalDateTime.parse(info.get(0), formatter);
                        if (birthday != null) {
                            typeOfPerson = "height";
                        }
                    } catch (DateTimeParseException e) {
                        list.add("Проверьте введенную дату рождения.\nMMMM d, yyyy hh:mm AM/PM");
                    }
                    break;
                case "height":
                    height = Utils.floatConverter(info.get(1));
                    if (height > 0) {
                        typeOfPerson = "passportID";
                    } else {
                        list.add("Также рост не может быть равен 0.");
                    }
                    break;
                case "passportID":
                    passportID = info.get(2);
                    String[] passport = passportID.trim().split(" +");
                    boolean isEqually = false;
                    for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                        if (entry.getValue().getPerson().getPassportID().equals(passportID)) {
                            isEqually = true;
                        }
                    }
                    if (passportID.length() < 45 && !isEqually && (passport.length == 0 || passport[0].length() != 0)) {
                        findWorker.setupPersonInfo(birthday, height, passportID);
                        setupCreationDate(findWorker);
                        list.add("Элемент person обновлен успешно");
                    } else {
                        list.add("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                    }
                    break;
            }
        } else if (element.equals("name") | element.equals("salary")) {
            switch (element) {
                case "name":
                    String name = null;
                    name = info.get(0);
                    String[] nameArray = name.trim().split(" +");
                    if (name != null && (name.length() != 0 | nameArray[0].length() != 0)) {
                        findWorker.setName(name);
                        setupCreationDate(findWorker);
                        list.add("Элемент name обновлен успешно");
                    } else {
                        list.add("Имя не может быть пустой строкой, повторите попытку.");
                    }
                    break;
                case "salary":
                    Integer salary = null;
                    salary = Utils.integerConverter(info.get(0));
                    if (salary != null) {
                        if (salary > 0) {
                            findWorker.setSalary(salary);
                            setupCreationDate(findWorker);
                            list.add("Элемент salary обновлен успешно");
                        } else {
                            list.add("Зараплата должна быть больше 0");
                        }
                    } else {
                        list.add("Вы неправильно ввели зарплату");
                    }
                    break;
            }
        } else {
            list.add("Такого элемента нет. Поэтому обновить данные не получится");
        }
        return list;
    }

    /**
     * Используется в update, чтобы уведомить пользователя о успешности операции
     *
     * @param element поле, обновление которого произошло
     */
    public void printInfoAboutOperation(String element) {
        System.out.println("Данные " + element + " успешно обновлены.");
    }

    /**
     * Устанавливает время создания объекта класса Worker
     *
     * @param worker объект Worker
     */
    public void setupCreationDate(Worker worker) {
        LocalDateTime date = LocalDateTime.now();
        worker.setCreationDate(date);
    }

    /**
     * Размер TreeMap
     *
     * @param map TreeMap
     * @return размер TreeMap
     */
    public int checkMapSize(TreeMap map) {
        return map.size();
    }

    /**
     * Удаляет элемент коллекции по ключу
     *
     * @param key ключ
     */
    public String removeKey(int key) {
        String message = workerList.entrySet().stream().filter((worker) -> worker.getKey() == key)
                .findFirst().map(o -> "Элемент с ключом " + key + " успешно удалён.")
                .orElse("Нет элемента с таким ключом.");
        workerList.remove(key);
        return message;
    }

    /**
     * Сохранет коллекцию по пути, который введет пользователь
     */
    public void save() {
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(savePath));
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                String str = "Key: " + entry.getKey() + ". Value: " + entry.getValue();
                buff.write(str + "\n");
            }
            System.out.println("Коллекция сохранена в: " + savePath);
            buff.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
        } catch (IOException e) {
            System.out.println("Ошибка доступа к файлу.");
        }
    }

    /**
     * Сохранет коллекцию по пути, который получен из скрипта
     *
     * @param info путь
     */
    public String save(List<String> info) {
        String message = null;
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(info.get(0)));
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                String str = "Key: " + entry.getKey() + ". Value: " + entry.getValue();
                buff.write(str + "\n");
            }
            buff.close();
            message = "Сохранение данных прошло успешно.";
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден.");
        } catch (IOException e) {
            message = "Ошибка доступа к файлу.";
        }
        return message;
    }

    /**
     * Удаляет объекты Worker коллекции, зарплата которых превышает введеную
     *
     * @param salary введеная пользователем зарплата
     */
    public String removeGreater(String salary) {
        List<Integer> list = new ArrayList<>();
        String message;
        if (!checkMapOnEmpty()) {
            int beginSize = checkMapSize(workerList);
            workerList.entrySet().removeIf(worker -> worker.getValue().compareSalaryValues(Utils.integerConverter(salary)) == 1);
            int calc = (beginSize - checkMapSize(workerList));
            message = "Из коллекции удален(о) " + calc + " элементов.";
        } else {
            message = "Элемент не с чем сравнивать. Коллекция пуста.";
        }
        return message;
    }

    /**
     * Меняет зраплату рабочего по ключу, если она больше введенной
     *
     * @param key   ключ для коллеции
     * @param value введенная зарплата
     */
    public String replaceIfGreater(int key, int value) {
        /*String message;
        if (workerList.get(key).compareSalaryValues(Utils.integerConverter(String.valueOf(value))) == -1) {
            workerList.get(key).setSalary(value);
            message="Зарплата рабочего изменена.";
        } else {
            message="Зарплата рабочего не может быть уменьшена.";
        }*/
        workerList.entrySet().stream().filter(o -> o.getKey() == key)
                .filter(o -> o.getValue().compareSalaryValues(Utils.integerConverter(String.valueOf(value))) == -1)
                .forEach(o -> o.getValue().setSalary(value));
        return workerList.entrySet().stream().filter(o -> o.getKey() == key)
                .filter(o -> o.getValue().compareSalaryValues(Utils.integerConverter(String.valueOf(value))) == 0)
                .findFirst().map(o -> "Зарплата рабочего изменена.").orElse("Зарплата рабочего не может быть уменьшена.");
    }

    /**
     * Удаляет элементы коллекции, ключ которых больше
     *
     * @param key введенный ключ
     */
    public String removeGreaterKey(int key) {
        List<Integer> list = new ArrayList<>();
        String message;
        if (!checkMapOnEmpty()) {
            int beginSize = checkMapSize(workerList);
            workerList.entrySet().removeIf(worker -> worker.getKey() > key);
            int calc = (beginSize - checkMapSize(workerList));
            message = "Из коллекции удален(о) " + calc + " элементов.";
        } else {
            message = "Элемент не с чем сравнивать. Коллекция пуста.";
        }
        return message;
    }

    /**
     * Проверяет коолекцию на пустоту
     *
     * @return true-пустая, false-есть элементы
     */
    public boolean checkMapOnEmpty() {
        return (checkMapSize(workerList) == 0);
    }

    /**
     * Выводит объект Worker коллекции, сумма координат x,y которого максимальная
     */
    public String maxByCoordinates() {
        Double buff = 0.0;
        int findKey = 0;
        String message;
        if (!checkMapOnEmpty()) {
            message = workerList.entrySet().stream().max((o1, o2) ->
                    (int) Math.round((o1.getValue().getCoordinates().getX() + o1.getValue().getCoordinates().getY())
                            - (o2.getValue().getCoordinates().getX() + o2.getValue().getCoordinates().getY()))).map(Object::toString).orElse("");
        } else {
            message = "Элемент не с чем сравнивать. Коллекция пуста.";
        }
        return message;
    }

    /**
     * Выводит элементы коллекции в порядке убывания ключа
     */
    public ArrayList<String> getDescending() {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> list;
        if (!checkMapOnEmpty()) {
            list = (ArrayList<Integer>) workerList.entrySet().stream().map(o -> o.getKey()).collect(Collectors.toList());
            list.sort(Collections.reverseOrder());
            list.stream().map(o -> workerList.get(o).toString()).forEach(o -> strings.add(o));
        } else {
            strings.add("Коллекция пуста.");
        }
        return strings;
    }

    /**
     * Выводит поля position в порядке убывания ключей коллекции
     */
    public ArrayList<String> getFieldDescendingPosition() {
        ArrayList<Integer> list;
        ArrayList<String> strings = new ArrayList<>();

        if (!checkMapOnEmpty()) {
            list = (ArrayList<Integer>) workerList.entrySet().stream().map(o -> o.getKey()).collect(Collectors.toList());
            list.sort(Collections.reverseOrder());
            list.stream().map(o -> o + ":" + workerList.get(o).getPosition().toString()).forEach(o -> strings.add(o));
        } else {
            strings.add("Коллекция пуста.");
        }
        return strings;
    }

    public String help() {
        String help = "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "insert null {element} : добавить новый элемент с заданным ключом\n" +
                "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_key null : удалить элемент из коллекции по его ключу\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                "replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого\n" +
                "remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный\n" +
                "max_by_coordinates : вывести любой объект из коллекции, значение поля coordinates которого является максимальным\n" +
                "print_descending : вывести элементы коллекции в порядке убывания\n" +
                "print_field_descending_position : вывести значения поля position всех элементов в порядке убывания";
        return help;
    }
}
