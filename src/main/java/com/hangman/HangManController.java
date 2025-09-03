package com.hangman;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HangManController {

    @FXML
    private Label guessingWord, hint;
    @FXML
    private Circle ManHead;
    @FXML
    private CubicCurve ManBody, ManLeftHand, ManRightHand, ManLeftLeg, ManRightLeg, HangingRope;
    @FXML
    private Button a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;

    private Node[] hangManParts;
    private int nextHangManPart = 0;
    private String solution;
    Map<Character, List<Integer>> charMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Process the guessing session
        initGame();
        System.out.println(charMap);
    }


    private void handleButton(Button button) {
        char character = button.getText().toLowerCase().charAt(0);

        button.setVisible(false);   // hide it
        button.setDisable(true);    // prevent click

        // So the logic handle here
        if (charMap.containsKey(character)) {
            String current = guessingWord.getText();
            char[] chars =  current.toCharArray();

            List<Integer> positions = charMap.get(character);

            // set the characters in the positions of the gussingWord label
            for(int pos:positions) {
                chars[pos*2] = character;
            }

            guessingWord.setText(new String(chars));

            // check if the word is fully guessed
            if (isWordComplete(chars)) {
                endGame(true);
            }
        } else {
            if (nextHangManPart+1 < hangManParts.length) {
                hangManParts[nextHangManPart].setVisible(true);
                nextHangManPart++;
            } else {
                HangingRope.setVisible(true);
                endGame(false);
            }
        }
    }

    private List<String> readFile(String filePath) throws Exception {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File Created: " + file.getAbsolutePath());
            }
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("file not found: " +  filePath);
        }

        return lines;
    }

    private int getLineCountFromFile(List<String> lines) {
        return lines.size();
    }

    private void initGame() {
        // Reset state
        charMap.clear();  // clear old word positions
        nextHangManPart = 0; // reset hangman parts index
        solution = ""; // reset solution

        // List of buttons
        List<Button> buttons = Arrays.asList(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z);

        // Add event listener to all buttons
        for (Button button : buttons) {
            button.setOnAction(e -> handleButton(button));
        }

        // File locations
        String hangManWordsFile = "files/HangManWords.txt";
        String resultsFile = "files/Results.txt";

        // File data
        List<String> hangManWords = new ArrayList<>(), results = new ArrayList<>();
        try{
            hangManWords = readFile(hangManWordsFile);
            results = readFile(resultsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hide the man and rope
        hangManParts = new Node[]{
                ManHead, ManBody, ManLeftHand, ManRightHand, ManLeftLeg, ManRightLeg, HangingRope
        };

        for(Node part :  hangManParts) {
            part.setVisible(false);
        }

        // Number of options to guess
        int numOfOptionsToGuess = ThreadLocalRandom.current().nextInt(0, getLineCountFromFile(hangManWords)-1);

//        String[] getQuestionLine = hangManWords.get(120).split(", ");
        String[] getQuestionLine = hangManWords.get(numOfOptionsToGuess).split(", ");

        String gaps = "";

        solution = getQuestionLine[0];
        hint.setText(String.join(", ", Arrays.copyOfRange(getQuestionLine, 1, getQuestionLine.length)));

        for(int i = 0; i< solution.length(); i++) {
            gaps += "_ ";
        }
        guessingWord.setText(gaps);

        // Get the characters from the word to guess and their positions too
        for (int i = 0; i < solution.length(); i++) {
            char c = solution.charAt(i);
            charMap.computeIfAbsent(c, k -> new ArrayList<>()).add(i);
        }
    }

    private boolean isWordComplete(char[] chars) {
        for (char c : chars) {
            if (c == '_') return false;
        }
        return true;
    }

    private void endGame(boolean playerWon) {
        // List of buttons
        List<Button> buttons = Arrays.asList(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z);
        // disable all buttons
        for (Button btn : buttons) {
            btn.setDisable(true);
        }

        if (playerWon) {
            System.out.println("Player Won");
            hint.setText("Player Won");
        } else {
            System.out.println("GameOver, the word was: " + solution);
            hint.setText("Game Over, the word was: " + solution);
        }

        // wait 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> {

            // Process the guessing session
            initGame();

            for (Button btn : buttons) {
                btn.setDisable(false);
                btn.setVisible(true);
            }
        });
        pause.play();
    }
}