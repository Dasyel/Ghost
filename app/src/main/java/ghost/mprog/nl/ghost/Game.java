package ghost.mprog.nl.ghost;


import android.content.Context;

public class Game {
    public static final int REASON_NO_WORDS = 1;
    public static final int REASON_MADE_WORD = 2;

    private Lexicon lexicon;
    private boolean player1Turn;
    private String subWord;
    private String language;
    private Context context;
    private int endReason;

    Game(Context context, String language, String subWord, boolean turn){
        this.subWord = subWord;
        this.context = context;
        this.language = language;
        this.player1Turn = turn;

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

    public boolean turn(){
        return this.player1Turn;
    }

    public boolean ended(){
        if (this.lexicon == null){
            return false;
        } else if (this.lexicon.contains(this.subWord) && this.subWord.length() > 3){
            this.endReason = REASON_MADE_WORD;
            return true;
        } else if (this.lexicon.count() == 0){
            this.endReason = REASON_NO_WORDS;
            return true;
        }
        return false;
    }

    public int getEndReason(){
        return this.endReason;
    }

    public boolean winner(){
        return !this.turn();
    }

    public void endTurn(){
        this.player1Turn = !this.player1Turn;
    }

    public String getSubWord(){
        return this.subWord;
    }
}
