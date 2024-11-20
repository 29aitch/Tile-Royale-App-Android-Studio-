// MainActivity.java
package com.example.practical_assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private int score = 0;
    private int currentLevel = 1;
    private CountDownTimer timer;
    private CountDownTimer startCountdown;
    private List<ImageButton> gameViews;
    private ImageButton currentHighlightedView;
    private Set<ImageButton> pressedTiles;
    private GridLayout gridLayout;
    private TextView scoreText;
    private TextView levelText;
    private TextView timerText;
    private Random random;
    private boolean gameInProgress = false;
    private Button endButton;

    // Array of drawable resource IDs for tile images
    private final int[] tileImages = {
            R.drawable.tile1,
            R.drawable.tile2,
            R.drawable.tile3,
            R.drawable.tile4,
            R.drawable.tile5
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        gameViews = new ArrayList<>();
        pressedTiles = new HashSet<>();
        random = new Random();

        // Find views
        gridLayout = findViewById(R.id.gridLayout);
        scoreText = findViewById(R.id.scoreText);
        levelText = findViewById(R.id.levelText);
        timerText = findViewById(R.id.timerText);
        endButton = findViewById(R.id.endButton);

        // Set click listeners
        endButton.setOnClickListener(v -> {
            if (gameInProgress) {
                endGame();
            }
        });

        // Initialize UI
        resetGame();

        // Start 3-second countdown when activity starts
        startCountdown = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Starting in: " + (millisUntilFinished / 1000 + 1));
            }

            @Override
            public void onFinish() {
                startGame();
            }
        }.start();
    }

    private void resetGame() {
        gameInProgress = false;
        score = 0;
        currentLevel = 1;
        if (timer != null) {
            timer.cancel();
        }
        gridLayout.removeAllViews();
        gameViews.clear();
        pressedTiles.clear();
        updateUI();
        endButton.setEnabled(false);
        timerText.setText("Starting in: 3");
    }

    private void startGame() {
        gameInProgress = true;
        score = 0;
        currentLevel = 1;
        endButton.setEnabled(true);
        updateUI();
        startLevel();
    }

    private void startLevel() {
        // Clear previous views
        gridLayout.removeAllViews();
        gameViews.clear();
        pressedTiles.clear();

        // Set up grid based on level
        int viewCount;
        int gridSize;

        switch (currentLevel) {
            case 1:
                viewCount = 4;  // 2x2
                gridSize = 2;
                break;
            case 2:
                viewCount = 9;  // 3x3
                gridSize = 3;
                break;
            case 3:
                viewCount = 16; // 4x4
                gridSize = 4;
                break;
            case 4:
                viewCount = 25; // 5x5
                gridSize = 5;
                break;
            default:
                viewCount = 4;
                gridSize = 2;
        }

        gridLayout.setRowCount(gridSize);
        gridLayout.setColumnCount(gridSize);

        // Create views
        for (int i = 0; i < viewCount; i++) {
            ImageButton view = new ImageButton(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(i % gridSize, 1f);
            params.rowSpec = GridLayout.spec(i / gridSize, 1f);
            params.setMargins(8, 8, 8, 8);
            view.setLayoutParams(params);

            // Set a random tile image
            int randomImageIndex = random.nextInt(tileImages.length);
            view.setImageResource(tileImages[randomImageIndex]);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            view.setOnClickListener(v -> {
                if (gameInProgress) {
                    if (v == currentHighlightedView) {
                        score++;
                        scoreText.setText("Score: " + score);
                        highlightRandomView();
                    } else {
                        // Wrong tile clicked
                        score = Math.max(0, score - 1); // Prevent negative scores
                        scoreText.setText("Score: " + score);
                        Toast.makeText(MainActivity.this, "Wrong tile! -1 point", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            gameViews.add(view);
            gridLayout.addView(view);
        }

        // Start timer
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (currentLevel < 4) {
                    currentLevel++;
                    startLevel();
                } else {
                    endGame();
                }
            }
        }.start();

        highlightRandomView();
    }

    private void highlightRandomView() {
        if (currentHighlightedView != null) {
            currentHighlightedView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        }

        // Allow re-highlighting of tiles
        List<ImageButton> availableTiles = new ArrayList<>(gameViews);

        if (availableTiles.isEmpty()) {
            if (currentLevel < 4) {
                currentLevel++;
                startLevel();
            } else {
                endGame();
            }
            return;
        }

        int randomIndex = random.nextInt(availableTiles.size());
        currentHighlightedView = availableTiles.get(randomIndex);
        currentHighlightedView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
    }

    private void endGame() {
        if (!gameInProgress) return;

        gameInProgress = false;
        if (timer != null) {
            timer.cancel();
        }
        checkHighScore();
    }

    private void checkHighScore() {
        SharedPreferences sharedPrefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        List<ScoreEntry> scoresList = new ArrayList<>();

        // Load existing scores
        for (int i = 0; i < 25; i++) {
            String name = sharedPrefs.getString("name_" + i, null);
            int savedScore = sharedPrefs.getInt("score_" + i, -1);
            if (name != null && savedScore != -1) {
                scoresList.add(new ScoreEntry(name, savedScore));
            }
        }

        // Check if current score makes it to top 25
        if (scoresList.size() < 25 || score > (scoresList.isEmpty() ? -1 : scoresList.get(scoresList.size() - 1).score)) {
            showNameInputDialog();
        } else {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showNameInputDialog() {
        EditText input = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You made it to the top 25! Enter your name:")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("OK", null) // Set to null initially
                .create();

        dialog.show();

        // Set the button click listener after showing the dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "Please enter your name", Toast.LENGTH_SHORT).show();
            } else {
                saveScore(name);
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
    }

    private void saveScore(String name) {
        SharedPreferences sharedPrefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        List<ScoreEntry> scoresList = new ArrayList<>();

        // Load existing scores
        for (int i = 0; i < 25; i++) {
            String existingName = sharedPrefs.getString("name_" + i, null);
            int existingScore = sharedPrefs.getInt("score_" + i, -1);
            if (existingName != null && existingScore != -1) {
                scoresList.add(new ScoreEntry(existingName, existingScore));
            }
        }

        // Add new score
        scoresList.add(new ScoreEntry(name, score));

        // Sort and keep top 25
        Collections.sort(scoresList, (s1, s2) -> s2.score - s1.score);
        if (scoresList.size() > 25) {
            scoresList.remove(scoresList.size() - 1);
        }

        // Save back to SharedPreferences
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        for (int i = 0; i < scoresList.size(); i++) {
            ScoreEntry entry = scoresList.get(i);
            editor.putString("name_" + i, entry.name);
            editor.putInt("score_" + i, entry.score);
        }
        editor.apply();
    }

    private void updateUI() {
        scoreText.setText("Score: " + score);
        levelText.setText("Level: " + currentLevel);
    }

    // Helper class for storing score entries
    private static class ScoreEntry {
        String name;
        int score;

        ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}