package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "game";

    private SharedPreferencesWrapper spWrapper;
    private DatabaseHandler dbHandler;

    private Game game;
    private String name1;
    private String name2;

    private TextView name1Field;
    private TextView name2Field;
    private TextView wordField;
    private EditText guessField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.spWrapper = new SharedPreferencesWrapper(this);
        this.spWrapper.setCurrentActivity(ACTIVITY_NAME);
        this.spWrapper.resetName1Picker(); //resets filled names in MainMenuActivity
        this.spWrapper.resetName2Picker();
        int language = this.spWrapper.getLanguage();
        String currentWord = this.spWrapper.getCurrentWord();
        int turn = this.spWrapper.getCurrentTurn();
        this.game = new Game(this, language, currentWord, turn);
        this.name1 = this.spWrapper.getName1();
        this.name2 = this.spWrapper.getName2();

        this.dbHandler = new DatabaseHandler(this);
        if (this.dbHandler.getPlayer(this.name1) == null || this.dbHandler.getPlayer(this.name2) == null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            return;
        }

        this.name1Field = (TextView) findViewById(R.id.name1Field);
        this.name2Field = (TextView) findViewById(R.id.name2Field);
        this.wordField = (TextView) findViewById(R.id.wordField);
        this.guessField = (EditText) findViewById(R.id.guessField);

        name1Field.setText(name1);
        name2Field.setText(name2);
        this.setNameColors(this.game.getTurn());
        wordField.setText(currentWord);
    }

    @Override
    public void onPause() {
        super.onPause();

        String currentWord = this.wordField.getText().toString();

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentWord(currentWord);
        spw.setCurrentTurn(this.game.getTurn());
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

    public void guess(View v){
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

        this.wordField.setText(this.game.getSubWord());
        this.guessField.setText("");
        this.game.endTurn();
        this.setNameColors(this.game.getTurn());
    }

    public void checkWinner(View v){
        String winnerName;
        String loserName;

        this.game.end();
        if (this.game.getWinner() == Game.PLAYER_1){
            winnerName = this.name1;
            loserName = this.name2;
        } else {
            winnerName = this.name2;
            loserName = this.name1;
        }

        this.showReasonToast(this.game.getEndReason());
        this.goToWinActivity(winnerName, loserName);
    }

    private void goToWinActivity(String winnerName, String loserName){
        Player winner = this.dbHandler.getPlayer(winnerName);
        Player loser = this.dbHandler.getPlayer(loserName);

        winner.addWin();
        winner.addPlays();
        loser.addPlays();

        this.dbHandler.updatePlayer(loser);
        this.dbHandler.updatePlayer(winner);

        this.spWrapper.setWinnerName(winnerName);
        this.spWrapper.setLoserName(loserName);

        Intent intent = new Intent(this, WinActivity.class);
        startActivity(intent);
    }

    private void goToResetActivity(){
        Intent intent = new Intent(this, ResetActivity.class);
        intent.putExtra("previous_activity", ACTIVITY_NAME);
        startActivity(intent);
    }

    private void setNameColors(int turn){
        switch (turn) {
            case Game.PLAYER_1:
                this.name1Field.setTextColor(Color.BLUE);
                this.name2Field.setTextColor(Color.LTGRAY);
                break;
            case Game.PLAYER_2:
                this.name1Field.setTextColor(Color.LTGRAY);
                this.name2Field.setTextColor(Color.BLUE);
        }
    }

    private void showReasonToast(int reason){
        int message;
        switch (reason){
            case Game.REASON_MADE_WORD:
                message = R.string.toast_created_word;
                break;
            case Game.REASON_NO_WORDS:
                message = R.string.toast_no_words_left;
                break;
            case Game.REASON_WORDS_AVAILABLE:
                message = R.string.toast_words_available;
                break;
            case Game.REASON_ONE_WORD_AVAILABLE:
                message = R.string.toast_one_word_available;
                break;
            default:
                message = R.string.toast_default;
        }
        Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_LONG).show();
    }
}
