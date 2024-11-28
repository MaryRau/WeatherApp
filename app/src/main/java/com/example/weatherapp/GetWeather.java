package com.example.weatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GetWeather {

    private static final String key = "0f9ffbfafd48715e72f3170cf6fd233d";
    private static final String url = "https://api.openweathermap.org/data/2.5/weather";
    private static String tempUrl;
    private String weather = "";

    public String getWeather()
    {return weather;}

    public static void setTempUrl(double lat, double lon)
    {
        DecimalFormat lf = new DecimalFormat("#.##");
        tempUrl = url + "?lat=" + lf.format(lat) + "&lon=" + lf.format(lon) + "&appid=" + key + "&units=metric";
    }

    public void getWeatherDetails(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responce", response);
                        DecimalFormat df = new DecimalFormat("#.#");
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArrayWeather = jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            JSONObject jsonObjectTemp = jsonResponse.getJSONObject("main");
                            int temp = (int)jsonObjectTemp.getDouble("temp");
                            int min = (int)jsonObjectTemp.getDouble("temp_min");
                            int max = (int)jsonObjectTemp.getDouble("temp_max");
                            int humidity = jsonObjectTemp.getInt("humidity");
                            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                            String speed = df.format(jsonObjectWind.getDouble("speed") * 3.6);
                            String icon = jsonObjectWeather.getString("icon");

                            if (jsonObjectWeather.getInt("id") == 800) {
                                weather = "Ясно";
                            }

                            else {
                                switch (String.valueOf(jsonObjectWeather.getInt("id")).charAt(0)) {
                                    case '8':
                                        weather = "Облачно";
                                        break;
                                    case '5':
                                        weather = "Дождь";
                                        break;
                                    case '3':
                                        weather = "Изморось";
                                        break;
                                    case '6':
                                        weather = "Снег";
                                        break;
                                    case '2':
                                        weather = "Шторм";
                                        break;
                                    default:
                                        weather = "Туман";
                                        break;
                                }
                            }

                            MainActivity.setIsNight(icon.charAt(2) == 'n');
                            MainActivity.updateBackground(weather);
                            MainActivity.setTxtWeather(weather);
                            MainActivity.setTxtDegrees(String.valueOf(temp));
                            MainActivity.setTxtMaxMin(String.valueOf(min), String.valueOf(max));
                            MainActivity.setTxtHumidity(String.valueOf(humidity));
                            MainActivity.setTxtWind(speed);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                    }
                });

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}