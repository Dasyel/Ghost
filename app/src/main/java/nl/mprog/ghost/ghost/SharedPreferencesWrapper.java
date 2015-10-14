package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesWrapper {
    private static final String PREFERENCES_NAME = "mprog.nl.ghost.preferences";
    private static final String DB_VERSION = "dbVersion";
    private static final String LANGUAGE = "language";
    private static final String NAME_1 = "name1";
    private static final String NAME_2 = "name2";
    private static final String NAME_WINNER = "winner";
    private static final String NAME_LOSER = "loser";
    private static final String NAME_1_PICKER = "name1Picker";
    private static final String NAME_2_PICKER = "name2Picker";
    private static final String CURRENT_WORD = "currentWord";
    private static final String CURRENT_ACTIVITY = "currentActivity";
    private static final String CURRENT_TURN = "turn";

    SharedPreferences sp;

    SharedPreferencesWrapper(Context context){
        this.sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public int getDbVersion(){
        return sp.getInt(DB_VERSION, -1);
    }

    public int getLanguage(){
        return sp.getInt(LANGUAGE, 0);
    }

    public String getName1(){
        return sp.getString(NAME_1, "");
    }

    public String getName2(){
        return sp.getString(NAME_2, "");
    }

    public String getWinnerName(){
        return sp.getString(NAME_WINNER, "");
    }

    public String getLoserName(){
        return sp.getString(NAME_LOSER, "");
    }

    public String getCurrentWord(){
        return sp.getString(CURRENT_WORD, "");
    }

    public String getCurrentActivity(){
        return sp.getString(CURRENT_ACTIVITY, "");
    }

    public String getName1Picker(){
        return sp.getString(NAME_1_PICKER, "");
    }

    public String getName2Picker(){
        return sp.getString(NAME_2_PICKER, "");
    }

    public int getCurrentTurn(){
        return sp.getInt(CURRENT_TURN, Game.NO_PLAYER);
    }

    public void setDbVersion(int dbVersion){
        this.setInt(DB_VERSION, dbVersion);
    }

    public void setLanguage(int language){
        this.setInt(LANGUAGE, language);
    }

    public void setName1(String name1){
        this.setString(NAME_1, name1);
    }

    public void setName2(String name2){
        this.setString(NAME_2, name2);
    }

    public void setWinnerName(String winnerName){
        this.setString(NAME_WINNER, winnerName);
    }

    public void setLoserName(String loserName){
        this.setString(NAME_LOSER, loserName);
    }

    public void setCurrentWord(String currentWord){
        this.setString(CURRENT_WORD, currentWord);
    }

    public void setCurrentActivity(String currentActivity){
        this.setString(CURRENT_ACTIVITY, currentActivity);
    }

    public void setName1Picker(String name1Picker){
        this.setString(NAME_1_PICKER, name1Picker);
    }

    public void setName2Picker(String name2Picker){
        this.setString(NAME_2_PICKER, name2Picker);
    }

    public void setCurrentTurn(int turn){
        this.setInt(CURRENT_TURN, turn);
    }

    public void resetName1(){
        this.remove(NAME_1);
    }

    public void resetName2(){
        this.remove(NAME_2);
    }

    public void resetName1Picker(){
        this.remove(NAME_1_PICKER);
    }

    public void resetName2Picker(){
        this.remove(NAME_2_PICKER);
    }

    public void resetCurrentWord(){
        this.remove(CURRENT_WORD);
    }

    private void setString(String key, String value){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(key, value);
        spEditor.apply();
    }

    private void setInt(String key, int value){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(key, value);
        spEditor.apply();
    }

    private void remove(String key){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.remove(key);
        spEditor.apply();
    }
}
