package ghost.mprog.nl.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DUTCH_RESOURCE_PATH = "res/raw/dutch.txt";
    private static final String ENGLISH_RESOURCE_PATH = "res/raw/english.txt";

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "mprog.nl.ghost.db";

    private static final String TABLE_PLAYERS = "Players";
    private static final String TABLE_ENGLISH = "EN";
    private static final String TABLE_DUTCH = "NL";

    private static final String KEY_PLAYER_ID = "id";
    private static final String KEY_PLAYER_NAME = "name";
    private static final String KEY_PLAYER_WINS = "wins";
    private static final String KEY_PLAYER_PLAYS = "plays";
    private static final String KEY_WORDS_ID = "id";
    private static final String KEY_WORDS_FIRST_LETTER = "first";
    private static final String KEY_WORDS_WORD = "word";

    private static final String INDEX_DUTCH_FIRST_LETTER = "dutch_first_letter";
    private static final String INDEX_ENGLISH_FIRST_LETTER = "english_first_letter";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYERS_TABLE = "CREATE TABLE " + TABLE_PLAYERS + "("
                + KEY_PLAYER_ID + " INTEGER PRIMARY KEY," + KEY_PLAYER_NAME + " VARCHAR UNIQUE,"
                + KEY_PLAYER_WINS + " INTEGER," + KEY_PLAYER_PLAYS + " INTEGER" + ")";
        String CREATE_ENGLISH_TABLE = "CREATE TABLE " + TABLE_ENGLISH + "("
                + KEY_WORDS_ID + " INTEGER PRIMARY KEY," + KEY_WORDS_FIRST_LETTER + " VARCHAR,"
                + KEY_WORDS_WORD + " VARCHAR" + ")";
        String CREATE_DUTCH_TABLE = "CREATE TABLE " + TABLE_DUTCH + "("
                + KEY_WORDS_ID + " INTEGER PRIMARY KEY," + KEY_WORDS_FIRST_LETTER + " VARCHAR,"
                + KEY_WORDS_WORD + " VARCHAR" + ")";
        String CREATE_DUTCH_INDEX = "CREATE INDEX " + INDEX_DUTCH_FIRST_LETTER + " ON "
                + TABLE_DUTCH + " (" + KEY_WORDS_FIRST_LETTER +");";
        String CREATE_ENGLISH_INDEX = "CREATE INDEX " + INDEX_ENGLISH_FIRST_LETTER + " ON "
                + TABLE_ENGLISH + " (" + KEY_WORDS_FIRST_LETTER +");";

        db.execSQL(CREATE_PLAYERS_TABLE);
        db.execSQL(CREATE_ENGLISH_TABLE);
        db.execSQL(CREATE_DUTCH_TABLE);
        db.execSQL(CREATE_DUTCH_INDEX);
        db.execSQL(CREATE_ENGLISH_INDEX);

        this.loadFile(db, TABLE_DUTCH, DUTCH_RESOURCE_PATH);
        this.loadFile(db, TABLE_ENGLISH, ENGLISH_RESOURCE_PATH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGLISH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DUTCH);
        onCreate(db);
    }

    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, player.getName());
        values.put(KEY_PLAYER_WINS, player.getWins());
        values.put(KEY_PLAYER_PLAYS, player.getPlays());

        db.insertWithOnConflict(TABLE_PLAYERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public static int getDatabaseVersion(){
        return DATABASE_VERSION;
    }

    public Player getPlayer(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYERS, new String[]{KEY_PLAYER_ID,
                        KEY_PLAYER_NAME, KEY_PLAYER_WINS, KEY_PLAYER_PLAYS}, KEY_PLAYER_NAME + "=?",
                new String[]{name}, null, null, null, null);

        assert cursor != null;
        if (cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        Player player =  new Player(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)));
        cursor.close();
        db.close();
        return player;
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> playerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Player player = new Player(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)));

                playerList.add(player);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playerList;
    }

    public ArrayList<String> getPlayerNames() {
        ArrayList<String> nameList = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PLAYER_NAME + " FROM " + TABLE_PLAYERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                nameList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return nameList;
    }

    public ArrayList<String> getHighscores() {
        ArrayList<String> hsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " ORDER BY "
                + KEY_PLAYER_WINS + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String hsString;
        if (cursor.moveToFirst()) {
            do {
                hsString = cursor.getString(1) + " " + cursor.getString(2) + "/"
                        + cursor.getString(3);
                hsList.add(hsString);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hsList;
    }

    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, player.getName());
        values.put(KEY_PLAYER_WINS, player.getWins());
        values.put(KEY_PLAYER_PLAYS, player.getPlays());

        // updating row
        return db.update(TABLE_PLAYERS, values, KEY_PLAYER_ID + " = ?",
                new String[] { String.valueOf(player.getID()) });
    }

    public void deletePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS, KEY_PLAYER_ID + " = ?",
                new String[]{String.valueOf(player.getID())});
        db.close();
    }

    public int getPlayersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PLAYERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public HashSet<String> getWords(String language, String first_letter){
        if (!language.equals(TABLE_DUTCH) && !language.equals(TABLE_ENGLISH)){
            return null;
        } else if (first_letter.length() == 0){
            return null;
        }

        HashSet<String> words = new HashSet<>();
        String selectQuery = "SELECT " + KEY_WORDS_WORD + " FROM " + language
                + " WHERE " + KEY_WORDS_FIRST_LETTER + " = '" + first_letter + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                words.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return words;
    }

    private void loadFile(SQLiteDatabase db, String table, String sourcePath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourcePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;

        db.beginTransaction();
        String sql = "Insert into " + table + " (" + KEY_WORDS_WORD + ", "
                + KEY_WORDS_FIRST_LETTER + ") values(?,?)";
        SQLiteStatement insert = db.compileStatement(sql);
        try {
            while (( line = buffReader.readLine()) != null) {
                insert.bindString(1, line.toLowerCase());
                insert.bindString(2, line.substring(0, 1));
                insert.execute();
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.d("DatabaseHandler", "Problems loading file: " + sourcePath);
        } finally {
            db.endTransaction();
        }
    }

}
