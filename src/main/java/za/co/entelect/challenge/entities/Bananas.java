package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Bananas {
    @SerializedName("damage")
    public int damage;

    @SerializedName("range")
    public int range;

    @SerializedName("count")
    public int count;

    @SerializedName("damageRadius")
    public int damageRadius;
}
