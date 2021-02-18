package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("x")
    public int x;

    @SerializedName("y")
    public int y;

    // Added Default Constructor
    public Position(){
        x = 99;
        y = 99;
    }

    // Added User-Defined Constructor
    public Position(int newx, int newy){
        x = newx;
        y = newy;
    }
}
