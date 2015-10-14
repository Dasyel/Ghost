package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.content.Context;

public class Game {
    public static final int REASON_NO_WORDS = 1;
    public static final int REASON_MADE_WORD = 2;
    public static final int REASON_WORDS_AVAILABLE = 3;
    public static final int REASON_ONE_WORD_AVAILABLE = 4;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int NO_PLAYER = 0;

    private static final int WORD_THRESHOLD = 3;

    private Lexicon lexicon;
    private int turn;
    private int winner;
    private String subWord;
    private int language;
    private Context context;
    private int endReason;

    Game(Context context, int language, String subWord, int turn){
        this.subWord = subWord;
        this.context = context;
        this.language = language;

        if (turn == NO_PLAYER){
            this.turn = PLAYER_1;
        } else {
            this.turn = turn;
        }

        if (subWord.length() > 0){
            String first_letter = subWord.substring(0, 1);
            this.lexicon = new Lexicon(context, language, first_letter);
        }
    }

    public boolean guess(String letter){
        this.subWord = this.subWord.concat(letter);

        if (this.lexicon == null){
            this.lexicon = new Lexicon(this.context, this.language, this.subWord.substring(0, 1));
        }

        this.lexicon.filter(this.subWord);
        return true;
    }

    public int getTurn(){
        return this.turn;
    }

    public void end(){
        if (this.lexicon == null){
            this.endReason = REASON_WORDS_AVAILABLE;
            this.winner = this.otherPlayer();
        } else if (this.lexicon.contains(this.subWord) && this.subWord.length() > WORD_THRESHOLD){
            this.endReason = REASON_MADE_WORD;
            this.winner = this.turn;
        } else if (this.lexicon.count() == 0){
            this.endReason = REASON_NO_WORDS;
            this.winner = this.turn;
        } else if (this.lexicon.count() == 1){
            this.endReason = REASON_ONE_WORD_AVAILABLE;
            this.winner = this.otherPlayer();
        } else {
            this.endReason = REASON_WORDS_AVAILABLE;
            this.winner = this.otherPlayer();
        }
    }

    public int getEndReason(){
        return this.endReason;
    }

    public int getWinner(){
        return this.winner;
    }

    public void endTurn(){
        this.turn = this.otherPlayer();
    }

    public String getSubWord(){
        return this.subWord;
    }

    private int otherPlayer(){
        return (PLAYER_1 + PLAYER_2) - this.turn;
    }
}
