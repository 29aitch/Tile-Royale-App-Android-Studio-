package com.example.practical_assignment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> scores;

    public LeaderboardAdapter(Context context, List<String> scores) {
        super(context, R.layout.leaderboard_item, scores);
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.leaderboard_item, parent, false);
        }

        TextView scoreView = rowView.findViewById(R.id.leaderboardItem);
        ImageView trophyIcon = rowView.findViewById(R.id.trophyIcon);

        // Set the score text
        scoreView.setText(scores.get(position));

        // Handle trophy visibility and color for top 3
        if (position < 3 && !scores.get(position).equals("No scores yet!")) {
            trophyIcon.setVisibility(View.VISIBLE);

            // Set trophy color based on position
            switch (position) {
                case 0: // Gold
                    trophyIcon.setColorFilter(0xFFFFD700, PorterDuff.Mode.SRC_IN);
                    break;
                case 1: // Silver
                    trophyIcon.setColorFilter(0xFFC0C0C0, PorterDuff.Mode.SRC_IN);
                    break;
                case 2: // Bronze
                    trophyIcon.setColorFilter(0xFFCD7F32, PorterDuff.Mode.SRC_IN);
                    break;
            }
        } else {
            trophyIcon.setVisibility(View.GONE);
        }

        return rowView;
    }
}