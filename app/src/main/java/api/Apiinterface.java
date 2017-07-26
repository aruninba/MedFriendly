package api;

import java.util.ArrayList;

import model.MedicineResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by arun_i on 18-Jul-17.
 */

public interface Apiinterface {

    @GET("medicines/brands/{name}")
    Call<ArrayList<MedicineResponse>> getMedicine(@Header("Authorization") String accessToken, @Path("name") String medincine);
}
