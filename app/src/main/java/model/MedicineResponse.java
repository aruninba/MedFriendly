package model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by arun_i on 18-Jul-17.
 */

public class MedicineResponse {
    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
