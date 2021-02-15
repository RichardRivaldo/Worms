package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class MyPlayer {
    @SerializedName("id")
    public int id;

    @SerializedName("score")
    public int score;

    @SerializedName("health")
    public int health;

    @SerializedName("worms")
    public MyWorm[] worms;

    // Added Remaining Select Command Count for My Worms
    // Attribute is provided from JSON files
    @SerializedName("remainingWormSelections")
    public int remainingWormSelections;
}
