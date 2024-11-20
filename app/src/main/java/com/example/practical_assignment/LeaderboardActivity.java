package com.example.practical_assignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        ListView leaderboardList = findViewById(R.id.leaderboardList);
        Button backButton = findViewById(R.id.backButton);

        // Load scores
        SharedPreferences sharedPrefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        List<String> scoresList = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            String name = sharedPrefs.getString("name_" + i, null);
            int savedScore = sharedPrefs.getInt("score_" + i, -1);
            if (name != null && savedScore != -1) {
                scoresList.add((i + 1) + ". " + name + ": " + savedScore);
            }
        }

        if (scoresList.isEmpty()) {
            scoresList.add("No scores yet!");
        }

        // Use custom adapter
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, scoresList);
        leaderboardList.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
    }
}