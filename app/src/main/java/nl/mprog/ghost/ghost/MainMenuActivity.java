package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainMenuActivity extends Activity {
    public static final String ACTIVITY_NAME = "main";

    private DatabaseHandler dbHandler;
    SharedPreferencesWrapper spWrapper;

    private AutoCompleteTextView name1Picker;
    private AutoCompleteTextView name2Picker;
    private Spinner languagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.dbHandler = new DatabaseHandler(this);
        this.spWrapper = new SharedPreferencesWrapper(this);

        this.name1Picker = (AutoCompleteTextView) findViewById(R.id.name1Picker);
        this.name2Picker = (AutoCompleteTextView) findViewById(R.id.name2Picker);
        this.languagePicker = (Spinner) findViewById(R.id.languagePicker);

        spWrapper.setCurrentActivity(ACTIVITY_NAME);

        ArrayList<String> playerNames = this.dbHandler.getPlayerNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, playerNames);
        this.name1Picker.setAdapter(adapter);
        this.name2Picker.setAdapter(adapter);

        this.name1Picker.setText(spWrapper.getName1Picker());
        this.name2Picker.setText(spWrapper.getName2Picker());
        this.languagePicker.setSelection(spWrapper.getLanguage());
    }

    @Override
    public void onPause() {
        super.onPause();

        String name1 = this.name1Picker.getText().toString();
        String name2 = this.name2Picker.getText().toString();
        String language = this.languagePicker.getSelectedItem().toString();

        this.spWrapper.setName1Picker(name1);
        this.spWrapper.setName2Picker(name2);
        this.spWrapper.setLanguage(Language.getIndex(language));
    }

    @Override
    public void onBackPressed() {
        //do nothing, otherwise this may direct to DispatcherActivity
    }

    public void startGame(View v){
        String name1 = this.name1Picker.getText().toString().toLowerCase().replaceAll("\\s", "");
        String name2 = this.name2Picker.getText().toString().toLowerCase().replaceAll("\\s", "");
        String language = this.languagePicker.getSelectedItem().toString();

        if (name1.equals(name2)){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_same_names, Toast.LENGTH_SHORT).show();
            return;
        } else if (name1.length() == 0 || name2.length() == 0){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_no_name, Toast.LENGTH_SHORT).show();
            return;
        }

        this.dbHandler.addPlayer(new Player(name1));
        this.dbHandler.addPlayer(new Player(name2));

        this.spWrapper.setName1(name1);
        this.spWrapper.setName2(name2);
        this.spWrapper.setLanguage(Language.getIndex(language));

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
