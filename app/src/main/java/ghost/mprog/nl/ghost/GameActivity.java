package ghost.mprog.nl.ghost;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends ActionBarActivity {
    public static final String ACTIVITY_NAME = "game";

    private Game game;
    private String name1;
    private String name2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentActivity(ACTIVITY_NAME);
        spw.resetName1Picker();
        spw.resetName2Picker();
        String language = spw.getLanguage();
        String currentWord = spw.getCurrentWord();
        boolean turn = spw.getCurrentTurn();
        this.game = new Game(this, language, currentWord, turn);
        this.name1 = spw.getName1();
        this.name2 = spw.getName2();

        DatabaseHandler db = new DatabaseHandler(this);
        if (db.getPlayer(this.name1) == null || db.getPlayer(this.name2) == null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            return;
        }

        TextView name1Field = (TextView) findViewById(R.id.name1Field);
        TextView name2Field = (TextView) findViewById(R.id.name2Field);
        TextView wordField = (TextView) findViewById(R.id.wordField);

        name1Field.setText(name1);
        name2Field.setText(name2);
        if (this.game.turn()){
            name1Field.setTextColor(Color.BLUE);
            name2Field.setTextColor(Color.LTGRAY);
        } else {
            name1Field.setTextColor(Color.LTGRAY);
            name2Field.setTextColor(Color.BLUE);
        }
        wordField.setText(currentWord);
    }

    @Override
    public void onPause() {
        super.onPause();

        TextView wordField = (TextView) findViewById(R.id.wordField);
        String currentWord = wordField.getText().toString();

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentWord(currentWord);
        spw.setCurrentTurn(this.game.turn());
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

    public void guess(View v){
        EditText guessField = (EditText) findViewById(R.id.guessField);
        TextView wordField = (TextView) findViewById(R.id.wordField);
        TextView name1Field = (TextView) findViewById(R.id.name1Field);
        TextView name2Field = (TextView) findViewById(R.id.name2Field);
        String guessString = guessField.getText().toString().toLowerCase();

        if (guessString.length() != 1){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_no_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (!Character.isLetter(guessString.charAt(0))){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_invalid_input, Toast.LENGTH_SHORT).show();
            return;
        }

        this.game.guess(guessString);
        if (this.game.ended()){
            String winnerName;
            String loserName;
            if (this.game.winner()){
                winnerName = this.name1;
                loserName = this.name2;
            } else {
                winnerName = this.name2;
                loserName = this.name1;
            }
            if (this.game.getEndReason() == Game.REASON_MADE_WORD){
                Toast.makeText(getApplicationContext(),
                        R.string.toast_created_word, Toast.LENGTH_LONG).show();
            } else if (this.game.getEndReason() == Game.REASON_NO_WORDS){
                Toast.makeText(getApplicationContext(),
                        R.string.toast_no_words_left, Toast.LENGTH_LONG).show();
            }
            goToWinActivity(winnerName, loserName);
        }
        wordField.setText(this.game.getSubWord());
        guessField.setText("");
        this.game.endTurn();
        if (this.game.turn()){
            name1Field.setTextColor(Color.BLUE);
            name2Field.setTextColor(Color.LTGRAY);
        } else {
            name1Field.setTextColor(Color.LTGRAY);
            name2Field.setTextColor(Color.BLUE);
        }
    }

    private void goToWinActivity(String winnerName, String loserName){
        DatabaseHandler db = new DatabaseHandler(this);
        Player winner = db.getPlayer(winnerName);
        Player loser = db.getPlayer(loserName);
        winner.addWin();
        winner.addPlays();
        loser.addPlays();
        db.updatePlayer(loser);
        db.updatePlayer(winner);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setWinnerName(winnerName);
        spw.setLoserName(loserName);

        Intent intent = new Intent(this, WinActivity.class);
        startActivity(intent);
    }

    private void newGame(){
        Intent intent = new Intent(this, ResetActivity.class);
        intent.putExtra("previous_activity", ACTIVITY_NAME);
        startActivity(intent);
    }
}
