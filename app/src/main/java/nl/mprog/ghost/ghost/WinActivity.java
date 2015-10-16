package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class WinActivity extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "win";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        SharedPreferencesWrapper spWrapper = new SharedPreferencesWrapper(this);
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        TextView winnerField = (TextView) findViewById(R.id.winnerName);
        TextView loserField = (TextView) findViewById(R.id.loserName);
        ListView highscoreListView = (ListView) findViewById(R.id.highscoreListView);

        spWrapper.setCurrentActivity(ACTIVITY_NAME);
        String winnerName = spWrapper.getWinnerName();
        String loserName = spWrapper.getLoserName();

        Player winner = dbHandler.getPlayer(winnerName);
        Player loser = dbHandler.getPlayer(loserName);

        if (winner == null || loser == null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            return;
        }

        winnerField.setText("Winner: " + winnerName + " " + winner.getWins()
                + "/" + winner.getPlays());
        loserField.setText("Loser: " + loserName + " " + loser.getWins()
                + "/" + loser.getPlays());
        winnerField.setTextColor(Color.GREEN);
        loserField.setTextColor(Color.RED);

        ArrayList<String> highscores = dbHandler.getHighscores();
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, highscores);
        highscoreListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.new_game_option:
                this.goToResetActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        this.goToResetActivity();
    }

    private void goToResetActivity(){
        Intent intent = new Intent(this, ResetActivity.class);
        intent.putExtra("previous_activity", ACTIVITY_NAME);
        startActivity(intent);
    }
}
