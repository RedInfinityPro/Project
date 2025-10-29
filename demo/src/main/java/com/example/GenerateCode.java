package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class GenerateCode {
    String inputFile;
    Scanner input;

    GenerateCode(String inputFile) {
        this.inputFile = inputFile;
    }

    public void setInputFIle(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void buildInputFile() {
        try {
            File fileObject = new File(getInputFile());
            if (fileObject.createNewFile()) {
                System.out.println("File created: " + fileObject.getName());
                writeFile(256, 65536, buildString(12), buildString(12));
            } else {
                System.out.println("File already exists.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        closeInput();
    }

    public String buildString(Integer length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer.");
        } else if (length == null) {
            length = 7;
        }
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();

    }

    public void openInput() {
        try {
            this.input = new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("could not open input file: " + inputFile);
        }
    }

    public ArrayList<String> readInputFile() {
        openInput();
        ArrayList<String> results = new ArrayList<>();
        if (input == null) {
            throw new IllegalStateException("Input file is not open.");
        }
        if (!input.hasNextLine()) {
            writeFile(256, 65536, buildString(12), buildString(12));
        }
        String line = input.nextLine().trim();
        if (line.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = line.split(",");
        for (String part : parts) {
            results.add(part.trim());
        }
        return results;
    }

    private void writeFile(Integer key_length, Integer iteration_count, String secretKey, String salt) {
        try (BufferedWriter writter = new BufferedWriter(new FileWriter(inputFile))) {
            writter.write(String.format("%s,%s,%s,%s", key_length, iteration_count, secretKey, salt));
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void closeInput() {
        if (this.input != null) {
            this.input.close();
        }
    }
}
