package ghost.mprog.nl.ghost;

public class Language {
    static final String DUTCH_FULL = "Dutch";
    static final String ENGLISH_FULL = "English";

    static final String DUTCH = "NL";
    static final String ENGLISH = "EN";

    static String getShort(String fullname){
        switch (fullname){
            case DUTCH_FULL: return DUTCH;
            case ENGLISH_FULL: return ENGLISH;
            default: return ENGLISH;
        }
    }

    static String getFull(String shortname){
        switch (shortname){
            case DUTCH: return DUTCH_FULL;
            case ENGLISH: return ENGLISH_FULL;
            default: return ENGLISH_FULL;
        }
    }
}
