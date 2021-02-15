package za.co.entelect.challenge.enums;

import com.google.gson.annotations.SerializedName;

public enum CellType {
    // Added More Cell Types
    // Types are provided from JSON files
    @SerializedName("DEEP_SPACE")
    DEEP_SPACE,
    @SerializedName("DIRT")
    DIRT,
    @SerializedName("AIR")
    AIR,
    @SerializedName("LAVA")
    LAVA;
}
