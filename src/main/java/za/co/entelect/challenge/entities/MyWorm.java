package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class MyWorm extends Worm {
    @SerializedName("weapon")
    public Weapon weapon;

    @SerializedName("bananaBombs")
    public Bananas bananas;

    @SerializedName("snowballs")
    public Snowballs snowballs;
}
