package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

public class Language {
    //Use the same order here as in res/values/arrays.xml!
    private static final String[][] languages = {
            {"English", "res/raw/english.txt"},
            {"Dutch", "res/raw/dutch.txt"}
    };

    static int getIndex(String fullName){
        for (int i = 0; i < languages.length; i++){
            if (fullName.equals(languages[i][0])){
                return i;
            }
        }
        return 0;
    }

    static String getName(int index){
        return languages[index][0];
    }

    static String getPath(int index){
        return languages[index][1];
    }

    static int getCount(){
        return languages.length;
    }
}
