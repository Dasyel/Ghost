package nl.mprog.ghost.ghost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mprog.nl.ghost.db";

    private static final String TABLE_PLAYERS = "Players";

    private static final String TABLE_LANGUAGE_PREFIX = "LANG_";
    private static final String INDEX_LANGUAGE_PREFIX = "INDEX_";

    private static final String KEY_PLAYER_ID = "id";
    private static final String KEY_PLAYER_NAME = "name";
    private static final String KEY_PLAYER_WINS = "wins";
    private static final String KEY_PLAYER_PLAYS = "plays";
    private static final String KEY_WORDS_ID = "id";
    private static final String KEY_WORDS_FIRST_LETTER = "first";
    private static final String KEY_WORDS_WORD = "word";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYERS_TABLE = "CREATE TABLE " + TABLE_PLAYERS + "("
                + KEY_PLAYER_ID + " INTEGER PRIMARY KEY," + KEY_PLAYER_NAME + " VARCHAR UNIQUE,"
                + KEY_PLAYER_WINS + " INTEGER," + KEY_PLAYER_PLAYS + " INTEGER" + ")";
        db.execSQL(CREATE_PLAYERS_TABLE);

        for (int i = 0; i < Language.getCount(); i++) {
            String tableName = TABLE_LANGUAGE_PREFIX + Language.getName(i);
            String indexName = INDEX_LANGUAGE_PREFIX + Language.getName(i);
            String resourcePath = Language.getPath(i);

            String CREATE_TABLE = "CREATE TABLE " + tableName + "("
                    + KEY_WORDS_ID + " INTEGER PRIMARY KEY," + KEY_WORDS_FIRST_LETTER + " VARCHAR,"
                    + KEY_WORDS_WORD + " VARCHAR" + ")";
            String CREATE_INDEX = "CREATE INDEX " + indexName + " ON "
                    + tableName + " (" + KEY_WORDS_FIRST_LETTER + ");";

            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_INDEX);
            this.loadFile(db, tableName, resourcePath);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        for (int i = 0; i < Language.getCount(); i++){
            String tableName = TABLE_LANGUAGE_PREFIX + Language.getName(i);
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
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

        return db.update(TABLE_PLAYERS, values, KEY_PLAYER_ID + " = ?",
                new String[] { String.valueOf(player.getID()) });
    }

    public HashSet<String> getWords(int language, String first_letter){
        if (first_letter.length() != 1){
            return null;
        }

        HashSet<String> words = new HashSet<>();
        String tableName = TABLE_LANGUAGE_PREFIX + Language.getName(language);
        String selectQuery = "SELECT " + KEY_WORDS_WORD + " FROM " + tableName
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
