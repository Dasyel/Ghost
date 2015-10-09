package ghost.mprog.nl.ghost;

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

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.db = new DatabaseHandler(this);
        ArrayList<String> playerNames = db.getPlayerNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, playerNames);
        final AutoCompleteTextView name1Picker = (AutoCompleteTextView) findViewById(R.id.name1Picker);
        AutoCompleteTextView name2Picker = (AutoCompleteTextView) findViewById(R.id.name2Picker);
        name1Picker.setAdapter(adapter);
        name2Picker.setAdapter(adapter);

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView namePicker = (AutoCompleteTextView) v;
                if(hasFocus)
                {
                    namePicker.showDropDown();
                } else {
                    namePicker.dismissDropDown();
                }
            }
        };
        name1Picker.setOnFocusChangeListener(focusChangeListener);
        name2Picker.setOnFocusChangeListener(focusChangeListener);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setCurrentActivity(ACTIVITY_NAME);
        name1Picker.setText(spw.getName1Picker());
        name2Picker.setText(spw.getName2Picker());
        Spinner languagePicker = (Spinner) findViewById(R.id.languagePicker);
        switch (spw.getLanguage()){
            case Language.DUTCH:
                languagePicker.setSelection(1);
                break;
            default:
                languagePicker.setSelection(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        AutoCompleteTextView name1Picker = (AutoCompleteTextView) findViewById(R.id.name1Picker);
        AutoCompleteTextView name2Picker = (AutoCompleteTextView) findViewById(R.id.name2Picker);
        String name1 = name1Picker.getText().toString();
        String name2 = name2Picker.getText().toString();

        Spinner languagePicker = (Spinner) findViewById(R.id.languagePicker);
        String language = languagePicker.getSelectedItem().toString();
        language = Language.getShort(language);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setName1Picker(name1);
        spw.setName2Picker(name2);
        spw.setLanguage(language);
    }

    @Override
    public void onBackPressed() {
        //do nothing, otherwise this may direct to DispatcherActivity
    }

    public void startGame(View v){
        AutoCompleteTextView name1Picker = (AutoCompleteTextView) findViewById(R.id.name1Picker);
        AutoCompleteTextView name2Picker = (AutoCompleteTextView) findViewById(R.id.name2Picker);
        String name1 = name1Picker.getText().toString().toLowerCase().replaceAll("\\s", "");
        String name2 = name2Picker.getText().toString().toLowerCase().replaceAll("\\s", "");

        if (name1.equals(name2)){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_same_names, Toast.LENGTH_SHORT).show();
            return;
        } else if (name1.length() == 0 || name2.length() == 0){
            Toast.makeText(getApplicationContext(),
                    R.string.toast_no_name, Toast.LENGTH_SHORT).show();
            return;
        }

        db.addPlayer(new Player(name1));
        db.addPlayer(new Player(name2));

        Spinner languagePicker = (Spinner) findViewById(R.id.languagePicker);
        String language = languagePicker.getSelectedItem().toString();
        language = Language.getShort(language);

        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);
        spw.setName1(name1);
        spw.setName2(name2);
        spw.setLanguage(language);

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
