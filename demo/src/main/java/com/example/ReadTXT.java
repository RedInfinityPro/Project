package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadTXT {
    public ArrayList<String> getTextFromFile(String filename, String returnText) {
        ArrayList<String> items = new ArrayList<>();
        try (BufferedReader buffer = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (returnText.equals("get_name")) {
                        items.addAll(getNames(line));
                    } else if (returnText.equals("get_description")) {
                        items.addAll(getDescription(line));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return items;
    }

    private ArrayList<String> getNames(String line) {
        ArrayList<String> namesList = new ArrayList<>();
        String[] parts = line.split(":");
        if (parts.length > 0) {
            namesList.add(parts[0].trim());
        }
        return namesList;
    }

    private ArrayList<String> getDescription(String line) {
        ArrayList<String> descriptionList = new ArrayList<>();
        String[] parts = line.split(":");
        if (parts.length > 1) {
            descriptionList.add(parts[1].trim());
        }
        return descriptionList;
    }
}