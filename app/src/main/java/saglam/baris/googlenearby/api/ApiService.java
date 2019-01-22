package saglam.baris.googlenearby.api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import saglam.baris.googlenearby.model.NearbyModel;

public interface ApiService {

    @GET("json?key=AIzaSyAGxq0fCLaRQ3wwcWBf-LB9c5Yt39D9dpQ")
    Call<NearbyModel> getNearbyPlaces(@Query("location") String latLngStr,
                                      @Query("radius") int radius,
                                      @Query("type") String type);
}
