package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ResetActivity extends Activity {
    public static final String ACTIVITY_NAME = "reset";

    private SharedPreferencesWrapper spw;

    private String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        Button continueButton = (Button) findViewById(R.id.continueButton);

        this.spw = new SharedPreferencesWrapper(this);
        this.spw.setCurrentActivity(ACTIVITY_NAME);

        Intent intent = getIntent();
        this.previousActivity = intent.getStringExtra("previous_activity");

        if (this.previousActivity.equals(WinActivity.ACTIVITY_NAME)){
            messageTextView.setText(R.string.reset_prompt_highscores);
            continueButton.setText(R.string.back_to_highscores);
        }
    }

    @Override
    public void onBackPressed() {
        this.goBack();
    }

    public void newGame(View v){
        this.spw.resetCurrentWord();
        this.spw.resetName1();
        this.spw.resetName2();

        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void continueGame(View v){
        this.goBack();
    }

    private void goBack(){
        Intent intent;
        switch (this.previousActivity) {
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
