/**
 * @Project WeatherApp
 * @Class WeatherService
 * *
 * @author Shivangam_Soni
 * @since 27 Jan 2020 : 4:14 PM
 */
package Dev.Shivi.WeatherApp.Controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    private OkHttpClient client;
    private Response response;
    private String cityName, unit;

    public JSONObject getWeather(){
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q="+getCityName()+"&units="+getUnit()+"&appid=API_KEY")
                .build();
        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) { e.printStackTrace(); }

        return null;
    }

    public JSONArray getWeatherArray() throws JSONException {
        return getWeather().getJSONArray("weather");
    }

    public JSONObject getMainObject() throws JSONException {
        return getWeather().getJSONObject("main");
    }

    public JSONObject getWindObject() throws JSONException {
        return getWeather().getJSONObject("wind");
    }

    public JSONObject getSunObject() throws JSONException {
        return getWeather().getJSONObject("sys");
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
