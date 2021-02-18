package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class MyWorm extends Worm {
    @SerializedName("weapon")
    public Weapon weapon;

    // Added Bananas attribute
    // Attributes are provided from JSON files
    @SerializedName("bananaBombs")
    public Bananas bananas;

    // Added Snowballs attribute
    // Attributes are provided from JSON files
    @SerializedName("snowballs")
    public Snowballs snowballs;
}
