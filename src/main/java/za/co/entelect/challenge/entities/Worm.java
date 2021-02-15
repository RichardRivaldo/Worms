package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Worm {
    @SerializedName("id")
    public int id;

    @SerializedName("health")
    public int health;

    @SerializedName("position")
    public Position position;

    @SerializedName("diggingRange")
    public int diggingRange;

    @SerializedName("movementRange")
    public int movementRange;

    // Added Round Countdown for frozen worms
    // Attribute is provided from JSON files
    @SerializedName("roundsUntilUnfrozen")
    public int notFrozen;
}
