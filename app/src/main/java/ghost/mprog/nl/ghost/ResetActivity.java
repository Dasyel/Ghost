package ghost.mprog.nl.ghost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;


public class ResetActivity extends ActionBarActivity {
    public static final String ACTIVITY_NAME = "reset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentActivity(ACTIVITY_NAME);
    }

    @Override
    public void onBackPressed() {
        this.goBack();
    }

    public void newGame(View v){
        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.resetCurrentWord();
        spw.resetName1();
        spw.resetName2();

        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void continueGame(View v){
        this.goBack();
    }

    private void goBack(){
        Intent oldIntent = getIntent();
        String previousActivity = oldIntent.getStringExtra("previous_activity");

        Intent intent;
        switch (previousActivity) {
            case GameActivity.ACTIVITY_NAME:
                intent = new Intent(this, GameActivity.class);
                break;
            case WinActivity.ACTIVITY_NAME:
                intent = new Intent(this, WinActivity.class);
                break;
            default:
                intent = new Intent(this, MainMenuActivity.class);
                break;
        }

        startActivity(intent);
    }
}
