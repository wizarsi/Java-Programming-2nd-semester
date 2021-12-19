package Dependency;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс вспомогательных методов
 */
public class Utils {
    private static Scanner scn = new Scanner(System.in);

    /**
     * Возврощает объект класса Scanner
     * @return объект Scanner
     */
    public static Scanner scanner() {
        return scn;
    }

    /**
     * Считывает путь к файлу для парсинга
     * @return путь
     */

    public static String scannerForPath() {
        String[] finalUserCommand=null;
        boolean isCorrectPath=false;
        while (!isCorrectPath) {
            try {
                Scanner scanner = new Scanner(System.in);
                String scn = scanner.nextLine();
                finalUserCommand = scn.trim().split(" +", 1);
                if (finalUserCommand[0].length() == 0) throw new FileNotFoundException();
                isCorrectPath = true;
            }catch (FileNotFoundException e) {
                System.err.println("Нельзя вводить пробелы,как путь");
            }catch (NoSuchElementException e){
                System.out.println("Вы вышли из консольного приложения.");
                System.exit(0);
            }
        }
        return finalUserCommand[0];
    }


    public static String scannerForUser() {
        String[] finalUserCommand=null;
        boolean isCorrectField=false;
        while (!isCorrectField) {
                Scanner scanner = new Scanner(System.in);
                String scn = scanner.nextLine();
                finalUserCommand = scn.trim().split(" +");
                if (finalUserCommand[0].length() != 0 && finalUserCommand.length==1){
                    isCorrectField = true;
                }
        }
        return finalUserCommand[0];
    }
    /**
     * Конвертирует Integer в Long
     * @param number Integer
     * @return Long
     */
    public static Long longConverter(Integer number){
        Long converted = null;
            try {
                converted = Long.valueOf(number);
            } catch (NumberFormatException e) {
                System.err.println("При загрузке данных произошла ошибка. Ошибка типов данных.");
            }
        return converted;
    }
    /**
     * Конвертирует String в Float
     * @param number String
     * @return Long
     */
    public static Float floatConverter(String number){
        float converted = 0;
        try {
            converted = Float.valueOf(number);
        }catch (NumberFormatException e){
            System.err.println("При загрузке данных произошла ошибка. Проверьте формат формат ввода, вы должы ввести число c плавающей точкой.");
        }
        return converted;
    }

    public static String encryptThisString(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Конвертирует String в Integer
     * @param number String
     * @return Integer
     */
    public static Integer integerConverter(String number){
        Integer converted=null;
        try {
            converted = Integer.valueOf(number);
        }catch (NumberFormatException e){
            System.err.println("При загрузке данных произошла ошибка. Проверьте формат формат ввода, вы должы ввести целое число.");
        }
        return converted;
    }
    /**
     * Конвертирует String в Double
     * @param number String
     * @return Double
     */
    public static Double doubleConverter(String number){
        Double converted=null;
            try {
                converted = Double.valueOf(number);
            } catch (NumberFormatException e) {
                System.err.println("При загрузке данных произошла ошибка. Проверьте формат формат ввода, вы должы ввести число с плавающей точкой.");
            }
        return converted;
    }

}
