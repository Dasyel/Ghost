package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

public class Player {
    private int id;
    private String name;
    private int wins;
    private int plays;

    Player(String name){
        this(name, 0, 0);
    }

    Player(String name, int wins, int plays){
        this.name = name;
        this.wins = wins;
        this.plays = plays;
    }

    Player(int id, String name, int wins, int plays){
        this.id = id;
        this.name = name;
        this.wins = wins;
        this.plays = plays;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getWins(){
        return this.wins;
    }

    public int getPlays(){
        return this.plays;
    }

    public void addWin(){
        this.wins++;
    }

    public void addPlays(){
        this.plays++;
    }
}
