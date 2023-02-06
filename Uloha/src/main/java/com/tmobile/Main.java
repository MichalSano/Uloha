package com.tmobile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        String phoneLog = readPhoneLogFromFile("phone_log.csv");
        Calculator calculator = new Calculator();
        BigDecimal bill = calculator.calculate(phoneLog);
        System.out.println("Telephone bill: " + bill +" kč");
    }

    private static String readPhoneLogFromFile(String fileName) {
        StringBuilder phoneLog = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                phoneLog.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneLog.toString();
    }
}