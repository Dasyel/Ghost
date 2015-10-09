package ghost.mprog.nl.ghost;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class WinActivity extends ActionBarActivity {
    public static final String ACTIVITY_NAME = "win";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentActivity(ACTIVITY_NAME);
        String winnerName = spw.getWinnerName();
        String loserName = spw.getLoserName();

        DatabaseHandler db = new DatabaseHandler(this);
        Player winner = db.getPlayer(winnerName);
        Player loser = db.getPlayer(loserName);

        if (winner == null || loser == null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            return;
        }

        TextView winnerField = (TextView) findViewById(R.id.winnerName);
        TextView loserField = (TextView) findViewById(R.id.loserName);
        winnerField.setText("Winner: " + winnerName + " " + winner.getWins()
                + "/" + winner.getPlays());
        loserField.setText("Loser: " + loserName + " " + loser.getWins()
                + "/" + loser.getPlays());
        winnerField.setTextColor(Color.GREEN);
        loserField.setTextColor(Color.RED);

        ListView hsListView = (ListView) findViewById(R.id.hsListView);
        ArrayList<String> highscores = db.getHighscores();
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, highscores);
        hsListView.setAdapter(arrayAdapter);
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
                this.newGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        this.newGame();
    }

    private void newGame(){
        Intent intent = new Intent(this, ResetActivity.class);
        intent.putExtra("previous_activity", ACTIVITY_NAME);
        startActivity(intent);
    }
}
