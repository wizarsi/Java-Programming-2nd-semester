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

/**
 * Класс для работы с коллекцией TreeMap-...
 */
public class CollectionManager {
    Long id;
    Date initDate;
    TreeMap<Integer, Worker> workerList;
    Map map = new LinkedHashMap();
    String path = null;
    File file;
    DocumentBuilder builder = null;
    Document document = null;
    List<? extends Integer> sda = new ArrayList<>();
    /**
     * Конструктор,в котором создается объект document "для его парсинга по дереву"(DOM)
     */
    public CollectionManager(String pathFromArgs) {
        while (document == null) {
            try {
                path = pathFromArgs;
                if (path.isEmpty()) throw new NullPointerException();
                file = new File(path);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    builder = factory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    System.err.println("Ошибка средства парсинга.");
                }
                try {
                    document = builder.parse(file);
                } catch (FileNotFoundException e) {
                    System.err.println("Введите правильный путь.");
                    System.exit(0);
                } catch (IOException e) {
                    System.err.println("Ошибка доступа к файлу.");
                    System.exit(0);
                } catch (SAXException e) {
                    System.err.println(" Нельзя выполнить парсинг. Файл должен быть в формате .xml");
                    System.exit(0);
                }
            } catch (NullPointerException e) {
                System.err.println("Нельзя передавать в качестве пути null. Проверьте правильность введенего пути.");
            } catch (NoSuchElementException e) {
                System.out.println("Вы вышли из консольного приложения.");
                System.exit(0);
            }
        }
    }
    /**
     * Метод осуществляющий парсинг средствами DOM
     */
    public void parse() {
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
                                    System.err.println("name в xml.Строка не может быть пустой");
                                }
                            }
                            break;
                            case "coordinates": {
                                Integer x = 0;
                                Double y = 0.0;
                                NodeList coordinatesList = childElement.getChildNodes();
                                for (int k = 0; k < coordinatesList.getLength(); k++) {
                                    if (coordinatesList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                        Element coordinatesElement = (Element) coordinatesList.item(k);
                                        switch (coordinatesList.item(k).getNodeName()) {
                                            case "x":
                                                x = Utils.integerConverter(coordinatesList.item(k).getTextContent());
                                                if (x == null) {
                                                    System.err.println("X XML файл.Значение не может быть пустым.");
                                                } else if (x <= -716) {
                                                    System.err.println("X XML файл.Значение поля должно быть больше -716");
                                                }
                                                break;
                                            case "y":
                                                y = Utils.doubleConverter(coordinatesList.item(k).getTextContent());
                                                if (y == null) {
                                                    System.err.println("Y XML файл.Значение не может быть пустым.");
                                                } else if (y >= 943) {
                                                    System.err.println("Y XML файл.Значение поля должно быть меньше, либо = 943");
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
                                    System.err.println("salary XML файл.Зараплата должна быть больше 0");
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
                                    System.err.println("position XMl файл");
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
                                    System.err.println("status XMl файл");
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
                                                    System.err.println("Проверьте дату рождения в XML файле.\nMMMM d, yyyy hh:mm AM/PM");
                                                }
                                            }
                                            break;
                                            case "height": {
                                                height = Utils.floatConverter(person.getTextContent());
                                                if (height <= 0) {
                                                    System.err.println("height XML файл");
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
                                                    System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
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
                if (checkWorker(worker)) {
                    workerList.put(i + 1, worker);
                } else {
                    System.err.println("Если отсутствуют ошибки,вам нужно внести недостоющие данные в XML файл.");
                }
            }

        }
    }

    /**
     * Проверка объекта на соотввествие требованиям к полям
     *
     * @param worker проверяемый, объект
     * @return true-можно, false-нельзя, добавить в коллекцию
     */
    public boolean checkWorker(Worker worker) {
        boolean isMatch = false;
        if (checkId(worker) && checkName(worker) && checkCoordinates(worker)
                && checkSalary(worker) && checkPerson(worker) && checkStatus(worker)
                && checkPosition(worker) && worker.getCreationDate() != null) {
            isMatch = true;
        } else {
            System.err.println("Невозможно внести данные о работнике с ID:" + worker.getId());
        }
        return isMatch;
    }

    /**
     * Проверка поля id для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkId(Worker worker) {
        boolean isEquallyID = false;
        boolean id = false;
        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
            if (entry.getValue().getId().equals(worker.getId())) {
                isEquallyID = true;
            }
        }
        if (worker.getId() != null && worker.getId() > 0 && !isEquallyID) {
            id = true;
        }
        return id;
    }

    /**
     * Проверка поля name для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkName(Worker worker) {
        boolean isCorrect = false;
        String name = worker.getName();
        if (name != null) {
            String[] nameArray = name.trim().split(" +");
            if (name.length() != 0 | nameArray[0].length() != 0) {
                isCorrect = true;
            }
        }
        return isCorrect;
    }

    /**
     * Проверка объекта статического класса Coordinates для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильный,false-неправильный
     */
    public boolean checkCoordinates(Worker worker) {
        boolean isCorrect = false;
        if (worker.getCoordinates() != null) {
            Integer x = worker.getCoordinates().getX();
            Double y = worker.getCoordinates().getY();
            if (x != null && x > -716) {
                if (y != null && y <= 943) {
                    isCorrect = true;
                }
            }
        }
        return isCorrect;
    }

    /**
     * Проверка поля salary для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkSalary(Worker worker) {
        boolean isCorrect = false;
        if (worker.getSalary() != null && worker.getSalary() > 0) {
            isCorrect = true;
        }
        return isCorrect;
    }

    /**
     * Проверка поля position для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkPosition(Worker worker) {
        boolean isCorrect = false;
        if (worker.getPosition() != null) {
            isCorrect = true;
        }
        return isCorrect;
    }

    /**
     * Проверка поля status для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkStatus(Worker worker) {
        boolean isCorrect = false;
        if (worker.getStatus() != null) {
            isCorrect = true;
        }
        return isCorrect;
    }

    /**
     * Проверка объекта статического класса Person для checkWorker
     *
     * @param worker проверяемый объект
     * @return true-правильный,false-неправильный
     */
    public boolean checkPerson(Worker worker) {
        boolean isCorrect = false;
        if (worker.getPerson() != null) {
            if (worker.getPerson().getBirthday() != null) {
                if (worker.getPerson().getHeight() > 0) {
                    String[] passport = worker.getPerson().getPassportID().trim().split(" +");
                    boolean isEqually = false;
                    for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                        if (entry.getValue().getPerson().getPassportID().equals(worker.getPerson().getPassportID())) {
                            isEqually = true;
                        }
                    }
                    if (worker.getPerson().getPassportID().length() <= 44 && !isEqually && (passport.length == 0 || passport[0].length() != 0)) {
                        isCorrect = true;
                    }
                }
            }
        }
        return isCorrect;
    }

    /**
     * Выводит информацию о коллекции
     */
    public void getInfoOfCollecttion() {
        System.out.println("Тип коллекции: " + workerList.getClass() +
                "\nДата инициализации: " + initDate +
                "\nКоличество элементов: " + workerList.size());
    }

    /**
     * Выводит информацию о объектах коллекции
     */
    public void show() {
        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }

    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        workerList.clear();
        System.out.print("Коллекция очищена.");
    }

    /**
     * Добовляет новый объект в коллекцию, пользователь сам вводит его
     *
     * @param key ключ под которым объект добовляется  в TreeMap
     */
    public void insert(int key) {
        LocalDateTime birthday = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        Worker worker = new Worker();
        String[] values = {"имя", "координату x", "координату y", "заработную плату", "день рождения, исапользуя формат(MMMM d, yyyy hh:mm AM/PM)", "рост", "паспортные данные", "должность", "статус"};
        for (int i = 0; i < values.length; i++) {
            boolean isCorrect = false;
            while (!isCorrect) {
                switch (values[i]) {
                    case "имя":
                        System.out.println("Введите " + values[i] + ":");
                        String scn = Utils.scanner().nextLine();
                        String[] scnArray = scn.trim().split(" +");
                        if (scn != null && (scn.length() != 0 | scnArray[0].length() != 0)) {
                            worker.setName(scn);
                            isCorrect = true;
                        } else {
                            System.err.println("Имя не может быть пустой строкой, повторите попытку.");
                        }
                        break;
                    case "должность":
                        Position position = null;
                        System.out.println("Введите " + values[i] + ", выбрав её в предложенном списке:");
                        System.out.println("DIRECTOR,\n" +
                                "LABORER,\n" +
                                "HUMAN_RESOURCES,\n" +
                                "HEAD_OF_DEPARTMENT,\n" +
                                "MANAGER_OF_CLEANING");
                        switch (Utils.scanner().nextLine()) {
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
                            System.err.println("Нужно было выбрать значение из списка");
                        } else {
                            worker.setPosition(position);
                            isCorrect = true;
                        }
                        break;
                    case "статус":
                        System.out.println("Введите " + values[i] + ", выбрав его в предложенном списке:");
                        System.out.println("HIRED,\n" +
                                "RECOMMENDED_FOR_PROMOTION,\n" +
                                "REGULAR,\n" +
                                "PROBATION");
                        Status status = null;
                        switch (Utils.scanner().nextLine()) {
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
                            System.err.println("Нужно было выбрать значение из списка");
                        } else {
                            worker.setStatus(status);
                            isCorrect = true;
                        }
                        break;
                    case "координату x":
                        System.out.println("Введите " + values[i] + ":");
                        x = Utils.integerConverter(Utils.scanner().nextLine());
                        if (x != null && x > -716) {
                            isCorrect = true;
                        } else {
                            System.err.println("Значение поля должно быть больше -716");
                        }
                        break;
                    case "координату y":
                        System.out.println("Введите " + values[i] + ":");
                        y = Utils.doubleConverter(Utils.scanner().nextLine());
                        if (y != null && y <= 943) {
                            isCorrect = true;
                        } else {
                            System.err.println("Максимальное значение поля: 943");
                        }
                        break;
                    case "заработную плату":
                        Integer salary = null;
                        System.out.println("Введите " + values[i] + ":");
                        salary = Utils.integerConverter(Utils.scanner().nextLine());
                        if (salary != null) {
                            if (salary > 0) {
                                worker.setSalary(salary);
                                isCorrect = true;
                            } else {
                                System.err.println("Зараплата должна быть больше 0");
                            }
                        } else {
                            System.err.println("Зарплата введена неверно");
                        }
                        break;
                    case "день рождения, исапользуя формат(MMMM d, yyyy hh:mm AM/PM)":
                        System.out.println("Введите " + values[i] + ":");
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                            birthday = LocalDateTime.parse(Utils.scanner().nextLine(), formatter);
                            if (birthday != null) {
                                isCorrect = true;
                            }
                        } catch (DateTimeParseException e) {
                            System.err.println("Проверьте введенную дату рождения.\nОна должна соответствовать формату: MMMM d, yyyy hh:mm AM/PM");
                        }
                        break;
                    case "рост":
                        System.out.println("Введите " + values[i] + ":");
                        height = Utils.floatConverter(Utils.scanner().nextLine());
                        if (height > 0) {
                            isCorrect = true;
                        } else {
                            System.err.println("Также рост не может быть равен 0.");
                        }
                        break;
                    case "паспортные данные":
                        System.out.println("Введите " + values[i] + ":");
                        passportID = Utils.scanner().nextLine();
                        String[] passport = passportID.trim().split(" +");
                        boolean isEqually = false;
                        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                            if (entry.getValue().getPerson().getPassportID().equals(passportID)) {
                                isEqually = true;
                            }
                        }
                        if (passportID.length() < 45 && !isEqually && (passport.length == 0 || passport[0].length() != 0)) {
                            isCorrect = true;
                        } else {
                            System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                        }
                        break;
                }
            }
        }
        worker.setId(id + 1);
        worker.setupCoordinates(x, y);
        worker.setupPersonInfo(birthday, height, passportID);
        workerList.put(key, worker);
        LocalDateTime date = LocalDateTime.now();
        worker.setCreationDate(date);
        System.out.println("Данные о сотруднике внесены успешно.");
    }

    /**
     * Добовляет новый объект в коллекцию, информация о нем читается из скрипта
     *
     * @param key  ключ под которым объект добовляется  в TreeMap
     * @param info информация из скрипта
     */
    public void insert(int key, List<String> info) {
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
                        System.err.println("Имя не может быть пустой строкой, повторите попытку.");
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
                        System.err.println("Нужно было выбрать значение из списка");
                        System.err.println("DIRECTOR,\n" +
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
                        System.err.println("Нужно было выбрать значение из списка");
                        System.err.println("HIRED,\n" +
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
                        System.err.println("Значение поля должно быть больше -716");
                    }
                    break;
                case "координату y":
                    y = Utils.doubleConverter(info.get(i));
                    if (y != null && y > 943) {
                        System.err.println("Максимальное значение поля: 943");
                    }
                    break;
                case "заработную плату":
                    Integer salary = null;
                    salary = Utils.integerConverter(info.get(i));
                    if (salary != null) {
                        if (salary <= 0) {
                            System.err.println("Зараплата должна быть больше 0");
                        } else {
                            worker.setSalary(salary);
                        }
                    } else {
                        System.err.println("Зарплата введена неверно");
                    }
                    break;
                case "день рождения, исапользуя формат(MMMM d, yyyy hh:mm AM/PM)":
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                        birthday = LocalDateTime.parse(info.get(i), formatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Проверьте введенную дату рождения.\nОна должна соответствовать формату: MMMM d, yyyy hh:mm AM/PM");
                    }
                    break;
                case "рост":
                    height = Utils.floatConverter(info.get(i));
                    if (height <= 0) {

                        System.err.println("Также рост не может быть меньше или равным 0.");
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
                        System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                    }
                    break;
            }
        }
        worker.setId(id + 1);
        worker.setupCoordinates(x, y);
        worker.setupPersonInfo(birthday, height, passportID);
        LocalDateTime date = LocalDateTime.now();
        worker.setCreationDate(date);
        if (checkWorker(worker)) {
            workerList.put(key, worker);
            System.out.println("Данные о сотруднике внесены успешно.");
        } else {
            System.err.println("Проверьте порядок данных в скрипте.");
            System.err.println("name\nx\ny\nsalary\nbirthday\nheight\npasportID\nposition\nstatus");
        }
    }

    /**
     * Обновляет поле объекта из коллекции, посредством пользавательского ввода
     *
     * @param id      признак, по-которому выбирается элемент
     * @param element поле, которое хотим обновить
     */
    public void update(int id, String element) {
        Worker findWorker = null;
        LocalDateTime birthday = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        String type = "x";
        String typeOfPerson = "birthday";
        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
            if (entry.getValue().getId() == id) {
                findWorker = entry.getValue();
            }
        }
        boolean isCorrect = false;
        while (!isCorrect) {
            if (element.equals("position")) {
                System.out.println("Введите должность, выбрав её в предложенном списке:");
                System.out.println("DIRECTOR,\n" +
                        "LABORER,\n" +
                        "HUMAN_RESOURCES,\n" +
                        "HEAD_OF_DEPARTMENT,\n" +
                        "MANAGER_OF_CLEANING");
                Position position = null;
                switch (Utils.scanner().nextLine()) {
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
                    System.err.println("Вы ввели непраильный position");
                } else {
                    isCorrect = true;
                    findWorker.setPosition(position);
                    printInfoAboutOperation(element);
                    setupCreationDate(findWorker);
                }
            } else if (element.equals("status")) {
                System.out.println("Введите статус, выбрав его в предложенном списке:");
                System.out.println("HIRED,\n" +
                        "RECOMMENDED_FOR_PROMOTION,\n" +
                        "REGULAR,\n" +
                        "PROBATION");
                Status status = null;
                switch (Utils.scanner().nextLine()) {
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
                    System.err.println("status. Нужно было выбрать значение из списка");
                } else {
                    isCorrect = true;
                    findWorker.setStatus(status);
                }
                printInfoAboutOperation(element);
                setupCreationDate(findWorker);
            } else if (element.equals("coordinates")) {
                switch (type) {
                    case "x":
                        System.out.println("Введите координату x:");
                        x = Utils.integerConverter(Utils.scanner().nextLine());
                        if (x != null && x > -716) {
                            type = "y";
                        } else {
                            System.err.println("Значение поля должно быть больше -716");
                        }
                        break;
                    case "y":
                        System.out.println("Введите координату y:");
                        y = Utils.doubleConverter(Utils.scanner().nextLine());
                        if (y != null && y <= 943) {
                            findWorker.setupCoordinates(x, y);
                            printInfoAboutOperation(element);
                            setupCreationDate(findWorker);
                            type = "y";
                            isCorrect = true;
                        } else {
                            System.err.println("Значение поля должно быть меньше, либо = 943");
                        }
                        break;
                }
            } else if (element.equals("person")) {
                switch (typeOfPerson) {
                    case "birthday":
                        System.out.println("Введите дату рождения, используя формат(MMMM d, yyyy hh:mm AM/PM):");
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
                            birthday = LocalDateTime.parse(Utils.scanner().nextLine(), formatter);
                            if (birthday != null) {
                                typeOfPerson = "height";
                            }
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                            System.err.println("Проверьте введенную дату рождения.\nMMMM d, yyyy hh:mm AM/PM");
                        }
                        break;
                    case "height":
                        System.out.println("Введите рост:");
                        height = Utils.floatConverter(Utils.scanner().nextLine());
                        if (height > 0) {
                            typeOfPerson = "passportID";
                        } else {
                            System.err.println("Также рост не может быть равен 0.");
                        }
                        break;
                    case "passportID":
                        System.out.println("Введите паспортные данные:");
                        passportID = Utils.scanner().nextLine();
                        String[] passport = passportID.trim().split(" +");
                        boolean isEqually = false;
                        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                            if (entry.getValue().getPerson().getPassportID().equals(passportID)) {
                                isEqually = true;
                            }
                        }
                        if (passportID.length() < 45 && !isEqually && (passport.length == 0 || passport[0].length() != 0)) {
                            isCorrect = true;
                            findWorker.setupPersonInfo(birthday, height, passportID);
                            printInfoAboutOperation(element);
                            setupCreationDate(findWorker);
                        } else {
                            System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
                        }
                        break;
                }
            } else if (element.equals("name") | element.equals("salary")) {
                switch (element) {
                    case "name":
                        System.out.println("Введите имя:");
                        String name = null;
                        name = Utils.scanner().nextLine();
                        String[] nameArray = name.trim().split("/s+");
                        if (name != null && (name.length() != 0 | nameArray[0].length() != 0)) {
                            findWorker.setName(name);
                            printInfoAboutOperation(element);
                            setupCreationDate(findWorker);
                            isCorrect = true;
                        } else {
                            System.err.println("Имя не может быть пустой строкой, повторите попытку.");
                        }
                        break;
                    case "salary":
                        System.out.println("Введите зарплату:");
                        Integer salary = null;
                        salary = Utils.integerConverter(Utils.scanner().nextLine());
                        if (salary != null) {
                            if (salary > 0) {
                                findWorker.setSalary(salary);
                                printInfoAboutOperation(element);
                                setupCreationDate(findWorker);
                                isCorrect = true;
                            } else {
                                System.err.println("Зараплата должна быть больше 0");
                            }
                        } else {
                            System.err.println("Вы непраильно ввели зарплату");
                        }
                        break;
                }
            } else {
                isCorrect = true;
                System.err.println("Такого элемента нет");
            }
        }
    }

    /**
     * Обновляет поле объекта из коллекции, посредством чтения из скрипта
     *
     * @param id      признак, по-которому выбирается элемент
     * @param element поле, которое хотим обновить
     * @param info    информация из скрипта
     */
    public void update(int id, String element, List<String> info) {
        Worker findWorker = null;
        LocalDateTime birthday = null;
        float height = 0;
        String passportID = null;
        Integer x = null;
        Double y = null;
        String type = "x";
        String typeOfPerson = "birthday";
        for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
            if (entry.getValue().getId() == id) {
                findWorker = entry.getValue();
            }
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
                System.err.println("Вы ввели непраильный position");
                System.err.println("DIRECTOR,\n" +
                        "LABORER,\n" +
                        "HUMAN_RESOURCES,\n" +
                        "HEAD_OF_DEPARTMENT,\n" +
                        "MANAGER_OF_CLEANING");
            } else {
                findWorker.setPosition(position);
                printInfoAboutOperation(element);
                setupCreationDate(findWorker);
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
                System.err.println("status. Нужно было выбрать значение из списка");
                System.err.println("HIRED,\n" +
                        "RECOMMENDED_FOR_PROMOTION,\n" +
                        "REGULAR,\n" +
                        "PROBATION");
            } else {
                findWorker.setStatus(status);
            }
            printInfoAboutOperation(element);
            setupCreationDate(findWorker);
        } else if (element.equals("coordinates")) {
            switch (type) {
                case "x":
                    x = Utils.integerConverter(info.get(0));
                    if (x != null && x > -716) {
                        type = "y";
                    } else {
                        System.err.println("Значение поля должно быть больше -716");
                    }
                    break;
                case "y":
                    y = Utils.doubleConverter(info.get(1));
                    if (y != null && y <= 943) {
                        findWorker.setupCoordinates(x, y);
                        printInfoAboutOperation(element);
                        setupCreationDate(findWorker);
                        type = "y";
                    } else {
                        System.err.println("Значение поля должно быть меньше, либо = 943");
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
                        e.printStackTrace();
                        System.err.println("Проверьте введенную дату рождения.\nMMMM d, yyyy hh:mm AM/PM");
                    }
                    break;
                case "height":
                    height = Utils.floatConverter(info.get(1));
                    if (height > 0) {
                        typeOfPerson = "passportID";
                    } else {
                        System.err.println("Также рост не может быть равен 0.");
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
                        printInfoAboutOperation(element);
                        setupCreationDate(findWorker);
                    } else {
                        System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
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
                        printInfoAboutOperation(element);
                        setupCreationDate(findWorker);
                    } else {
                        System.err.println("Имя не может быть пустой строкой, повторите попытку.");
                    }
                    break;
                case "salary":
                    Integer salary = null;
                    salary = Utils.integerConverter(info.get(0));
                    if (salary != null) {
                        if (salary > 0) {
                            findWorker.setSalary(salary);
                            printInfoAboutOperation(element);
                            setupCreationDate(findWorker);
                        } else {
                            System.err.println("Зараплата должна быть больше 0");
                        }
                    } else {
                        System.err.println("Вы неправильно ввели зарплату");
                    }
                    break;
            }
        } else {
            System.err.println("Такого элемента нет. Поэтому обновить данные не получится");
        }
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
    public void removeKey(int key) {
        int s1 = checkMapSize(workerList);
        workerList.remove(key);
        if (s1 < checkMapSize(workerList)) {
            System.out.println("Элемент с ключом " + key + " успешно удалён.");
        }
    }

    /**
     * Сохранет коллекцию по пути, который введет пользователь
     */
    public void save() {
        System.out.println("Введите путь к файлу для записи коллекции:");
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(Utils.scanner().nextLine()));
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                String str = "Key: " + entry.getKey() + ". Value: " + entry.getValue();
                buff.write(str + "\n");
            }
            buff.close();
            System.out.println("Сохранение данных прошло успешно.");
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден.");
        } catch (IOException e) {
            System.err.println("Ошибка доступа к файлу.");
            e.printStackTrace();
        }
    }

    /**
     * Сохранет коллекцию по пути, который получен из скрипта
     *
     * @param info путь
     */
    public void save(List<String> info) {
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(info.get(0)));
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                String str = "Key: " + entry.getKey() + ". Value: " + entry.getValue();
                buff.write(str + "\n");
            }
            buff.close();
            System.out.println("Сохранение данных прошло успешно.");
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден.");
        } catch (IOException e) {
            System.err.println("Ошибка доступа к файлу.");
            e.printStackTrace();
        }
    }

    /**
     * Удаляет объекты Worker коллекции, зарплата которых превышает введеную
     *
     * @param salary введеная пользователем зарплата
     */
    public void removeGreater(String salary) {
        List<Integer> list = new ArrayList<>();
        if (!checkMapOnEmpty()) {
            int beginSize = checkMapSize(workerList);
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                if (entry.getValue().compareSalaryValues(Utils.integerConverter(salary)) == 1) {
                    list.add(entry.getKey());
                }
            }
            for (Integer i : list) {
                workerList.remove(i);
            }
            int calc = (beginSize - checkMapSize(workerList));
            System.out.println("Из коллекции удален(о) " + calc + " элементов.");
        } else {
            System.out.println("Элемент не с чем сравнивать. Коллекция пуста.");
        }
    }

    /**
     * Меняет зраплату рабочего по ключу, если она больше введенной
     *
     * @param key   ключ для коллеции
     * @param value введенная зарплата
     */
    public void replaceIfGreater(int key, int value) {
        if (workerList.get(key).compareSalaryValues(Utils.integerConverter(String.valueOf(value))) == -1) {
            workerList.get(key).setSalary(value);
            System.out.println("Зарплата рабочего изменена.");
        } else {
            System.out.println("Зарплата рабочего не может быть уменьшена.");
        }
    }

    /**
     * Удаляет элекмнты коллекции, ключ которых больше
     *
     * @param key введенный ключ
     */
    public void removeGreaterKey(int key) {
        List<Integer> list = new ArrayList<>();
        if (!checkMapOnEmpty()) {
            int beginSize = checkMapSize(workerList);
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                if (entry.getKey() > key) {
                    list.add(entry.getKey());
                }
            }
            for (Integer i : list) {
                workerList.remove(i);
            }
            int calc = (beginSize - checkMapSize(workerList));
            System.out.println("Из коллекции удален(о) " + calc + " элементов.");
        } else {
            System.out.println("Элемент не с чем сравнивать. Коллекция пуста.");
        }
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
    public void maxByCoordinates() {
        Double buff = 0.0;
        int findKey = 0;
        if (!checkMapOnEmpty()) {
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                Double sum = (entry.getValue().getCoordinates().getX() + entry.getValue().getCoordinates().getY());
                if (buff < sum) {
                    buff = sum;
                    findKey = entry.getKey();
                }
            }
            System.out.println("Key: " + findKey + ". Value: " + workerList.get(findKey));
        } else {
            System.out.println("Элемент не с чем сравнивать. Коллекция пуста.");
        }
    }

    /**
     * Выводит элементы коллекции в порядке убывания ключа
     */
    public void printDescending() {
        ArrayList<Integer> list = new ArrayList<>();
        if (!checkMapOnEmpty()) {
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                list.add(entry.getKey());
            }
            Collections.sort(list, Collections.reverseOrder());
            for (Integer i : list) {
                System.out.println(workerList.get(i));
            }
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Выводит поля position в порядке убывания ключей коллекции
     */
    public void printFieldDescendingPosition() {
        ArrayList<Integer> list = new ArrayList<>();
        if (!checkMapOnEmpty()) {
            for (Map.Entry<Integer, Worker> entry : workerList.entrySet()) {
                list.add(entry.getKey());
            }
            Collections.sort(list, Collections.reverseOrder());
            for (Integer i : list) {
                System.out.println(i + ": " + workerList.get(i).getPosition());
            }
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

}
