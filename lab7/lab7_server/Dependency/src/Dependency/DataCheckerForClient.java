package Dependency;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DataCheckerForClient implements Serializable {



    /**
     * Проверка поля name для checkWorker
     *
     * @param name проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkName(String name) {
        boolean isCorrect = false;
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
     * @param x проверяемый объект
     * @return true-правильный,false-неправильный
     */
    public boolean checkX(Integer x) {
            boolean isCorrect = false;
            if (x != null && x > -716) {
                    isCorrect = true;

            }else{
                System.err.println("Значение поля должно быть больше -716");
            }
        return isCorrect;
    }

    public boolean checkY(Double y) {
        boolean isCorrect = false;
            if (y != null && y <= 943) {
                isCorrect = true;
            }else {
                System.err.println("Максимальное значение поля: 943");
            }
        return isCorrect;
    }

    /**
     * Проверка поля salary для checkWorker
     *
     * @param salary проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkSalary(Integer salary) {
        boolean isCorrect = false;
        if (salary != null) {
            if (salary > 0) {
                isCorrect = true;
            }
        } else {
            System.err.println("Зарплата введена неверно");
        }
        return isCorrect;
    }

    /**
     * Проверка поля position для checkWorker
     *
     * @param position проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkPosition(Position position) {
        boolean isCorrect = false;
        if (position != null) {
            isCorrect = true;
        }
        return isCorrect;
    }

    /**
     * Проверка поля status для checkWorker
     *
     * @param status проверяемый объект
     * @return true-правильное,false-неправильное
     */
    public boolean checkStatus(Status status) {
        boolean isCorrect = false;
        if (status != null) {
            isCorrect = true;
        }else{
            System.err.println("Нужно было выбрать значение из списка");
        }
        return isCorrect;
    }

    /**
     * Проверка объекта статического класса Person для checkWorker
     *
     * @param birthdayIn проверяемая строка
     * @return true-правильный,false-неправильный
     */
    public boolean checkBirthday(String birthdayIn) {
        boolean isCorrect = false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy hh':'mm a", Locale.US);
            LocalDateTime birthday = LocalDateTime.parse(birthdayIn, formatter);
            if (birthday != null){
                isCorrect = true;
            }
        } catch (DateTimeParseException e) {
            System.err.println("Проверьте введенную дату рождения.\nОна должна соответствовать формату: MMMM d, yyyy hh:mm AM/PM");
        }
        return isCorrect;
    }
    public boolean checkHeight(float height) {
        boolean isCorrect = false;
                if (height > 0) {
                        isCorrect = true;
                }else {
                    System.err.println("Также рост не может быть равен 0.");
                }
        return isCorrect;
    }


}
