import com.example.fitproplus_final.FoodResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NutritionixAPI {

    @Headers(
        "x-app-id: ee02120f",
        "x-app-key: 4bdeac400a13b58c8ed60e6fa7a5ee21",
        "x-remote-user-id: 0"
    )
    @POST("/v2/natural/nutrients")
    fun getFoodInfo(@Body body: Map<String, String>): Call<FoodResponse>
}
