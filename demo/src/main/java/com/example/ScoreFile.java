package com.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScoreFile {
    public double totalScore;
    public static Map<String, Integer> thresholdCounts = new HashMap<>();
    public static Set<Integer> primeDigit_list;

    public ScoreFile() {
        this.totalScore = 0.0;
        this.thresholdCounts = new HashMap<>();
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void addThreshold(String threshold, Integer count) {
        thresholdCounts.merge(threshold, count, Integer::sum);
    }

    private static ScoreFile calculate(String randomNumber, String userGuess) {
        ScoreFile result = new ScoreFile();
        double score = 0.0;
        primeDigit_list = Set.of(2, 3, 5, 7);
        int len = Math.min(randomNumber.length(), userGuess.length());
        boolean[] matchedExact = new boolean[len];
        boolean[] matchedExists = new boolean[randomNumber.length()];
        Set<Character> existsAwarded = new HashSet<>();
        Set<Character> repeatAwarded = new HashSet<>();
        if (!SubLevel.correct) {
            if (randomNumber.equals(userGuess)) {
                result.totalScore = 1.0 * LevelFile.getPlayerLevel();
                result.addThreshold("Correct", 1);
                SubLevel.correct = true;
            } else {
                // Count digit frequencies in random number
                Map<Character, Integer> randomFreq = new HashMap<>();
                for (char c : randomNumber.toCharArray()) {
                    randomFreq.put(c, randomFreq.getOrDefault(c, 0) + 1);
                }
                // Count digit frequencies in user guess
                Map<Character, Integer> guessFreq = new HashMap<>();
                for (char c : userGuess.toCharArray()) {
                    guessFreq.put(c, guessFreq.getOrDefault(c, 0) + 1);
                }
                // Prime check
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i] || matchedExists[i])
                        continue;
                    int userDigit = Character.getNumericValue(userGuess.charAt(i));
                    int randomDigit = Character.getNumericValue(randomNumber.charAt(i));
                    if (isPrime(userDigit) == isPrime(randomDigit)) {
                        score += 0.01;
                        result.addThreshold("Prime_Exists", 1);
                    }
                }
                // Repeating digit match
                for (char c : guessFreq.keySet()) {
                    if (randomFreq.getOrDefault(c, 0) > 1) {
                        if (!repeatAwarded.contains(c)) {
                            score += 0.05;
                            repeatAwarded.add(c);
                            result.addThreshold("Duplicate_Exists", 1);
                        }
                    }
                }
                // Odd/Even match in same position
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i] || matchedExists[i])
                        continue;
                    int userDigit = Character.getNumericValue(userGuess.charAt(i));
                    int randomDigit = Character.getNumericValue(randomNumber.charAt(i));
                    if ((userDigit % 2) == (randomDigit % 2)) {
                        score += 0.10;
                        result.addThreshold("Property_Exists", 1);
                    }
                }
                // Check for presence matches (+0.25)
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i])
                        continue;
                    char uc = userGuess.charAt(i);
                    if (randomNumber.indexOf(uc) != -1 && !existsAwarded.contains(uc)) {
                        score += 0.25;
                        matchedExists[i] = true;
                        existsAwarded.add(uc);
                        result.addThreshold("Exists", 1);
                    }
                }
                // Check for exact matches
                for (int i = 0; i < len; i++) {
                    if (userGuess.charAt(i) == randomNumber.charAt(i)) {
                        score += 0.50;
                        matchedExact[i] = true;
                        matchedExists[i] = true;
                        existsAwarded.add(userGuess.charAt(i));
                        result.addThreshold("Exists_Exact", 1);
                    }
                }
            }
        }
        if (!SubLevel.correct && score <= 0) {
            GameFile.wrongPoints++;
            result.addThreshold("None", 1);
        } 
        if (SubLevel.correct) {
            score = 0.0;
            result.addThreshold("Correct_Exacts", 1);
        }
        result.totalScore = Math.min(score, 1.00);
        return result;
    }

    public static Boolean isPrime(Integer digit) {
        return primeDigit_list.contains(digit);
    }
}
