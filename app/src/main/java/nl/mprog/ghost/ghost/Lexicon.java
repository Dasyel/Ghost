package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.content.Context;

import java.util.HashSet;
import java.util.Iterator;

class Lexicon {
    private HashSet<String> filteredWordList;

    Lexicon(Context context, int language, String first_letter){
        DatabaseHandler db = new DatabaseHandler(context);
        this.filteredWordList = db.getWords(language, first_letter);
    }

    public HashSet<String> filter(String filterString){
        for (Iterator<String> i = this.filteredWordList.iterator(); i.hasNext();) {
            String el = i.next();
            if (el != null && !el.startsWith(filterString)) {
                i.remove();
            }
        }
        return this.filteredWordList;
    }

    public int count(){
        return this.filteredWordList.size();
    }

    public boolean contains(String word){
        return this.filteredWordList.contains(word);
    }
}
