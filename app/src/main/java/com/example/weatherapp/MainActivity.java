package com.example.weatherapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static TextView txtCity;
    private static TextView txtDegrees;
    private static TextView txtWind;
    private static TextView txtMaxMin;
    private static TextView txtWeather;
    private static TextView txtHumidity;
    private static ImageView imgWeather;
    private static ImageView imageView3;
    private static ImageView imageView4;
    private static ImageView imageView2;
    private static ImageView imageView5;

    private LocationHelper locationHelper;

    private static int upColor;
    private static int downColor;
    private static int img;

    private  static boolean isNight;

    GetWeather gw;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCity = findViewById(R.id.txtCity);
        txtDegrees = findViewById(R.id.txtDegrees);
        txtWind = findViewById(R.id.txtWind);
        txtMaxMin = findViewById(R.id.txtMaxMin);
        txtWeather = findViewById(R.id.txtWeather);
        txtHumidity = findViewById(R.id.txtHumidity);
        imgWeather = findViewById(R.id.imgWeather);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView2 = findViewById(R.id.imageView2);
        imageView5 = findViewById(R.id.imageView5);

        locationHelper = new LocationHelper(this);

        gw = new GetWeather();

        requestLocation();

        animateView(txtCity, 1);
        animateView(txtDegrees, 1);
        animateView(txtWind, 1);
        animateView(txtMaxMin, 1);
        animateView(txtWeather, 1);
        animateView(txtHumidity, 1);
        animateView(imgWeather, 1);
        animateView(imageView3, 0.8f);
        animateView(imageView4, 0.8f);
        animateView(imageView2, 0.8f);
        animateView(imageView5, 0.8f);
    }

    private void animateView(View view, float alpha) {
        view.setAlpha(0f);
        view.setTranslationY(50f);

        view.animate()
                .alpha(alpha)
                .translationY(0f)
                .setDuration(1000)
                .start();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationHelper.getLocation(new LocationHelper.OnLocationReceivedListener() {
                @Override
                public void onLocationReceived(float latitude, float longitude) {
                    String cityName = locationHelper.getCityName(latitude, longitude);
                    gw.getWeatherDetails(getApplicationContext());

                    txtCity.setText(cityName);
                }

                @Override
                public void onPermissionDenied() {
                    txtCity.setText("Ошибка");
                }

                @Override
                public void onProviderDisabled() {
                    txtCity.setText("Ошибка");
                }
            });
        }
    }

    public static void setTxtWeather(String weather)
    {txtWeather.setText(weather);}

    public static void setTxtDegrees(String degrees)
    {txtDegrees.setText(degrees + "°");}

    public static void setTxtMaxMin(String min, String max)
    {txtMaxMin.setText(max + "°/" + min + "°");}

    public static void setTxtHumidity(String humidity)
    {txtHumidity.setText(humidity + "\n%");}

    public static void setTxtWind(String wind)
    {txtWind.setText(wind + "\nкм/ч");}

    public static void setIsNight(boolean status)
    {isNight = status;}

    public static void updateBackground(String weather) {
        int upColor, downColor, imageResource;

        if (!isNight) {
            switch (weather) {
                case "Облачно":
                    upColor = R.color.cloudUp;
                    downColor = R.color.cloudDown;
                    imageResource = R.drawable.cloud;
                    break;
                case "Дождь":
                    upColor = R.color.rainUp;
                    downColor = R.color.rainDown;
                    imageResource = R.drawable.rain;
                    break;
                case "Снег":
                    upColor = R.color.snowUp;
                    downColor = R.color.snowDown;
                    imageResource = R.drawable.snow;
                    break;
                case "Шторм":
                    upColor = R.color.stormUp;
                    downColor = R.color.stormDown;
                    imageResource = R.drawable.thunderstorm;
                    break;
                case "Туман":
                    upColor = R.color.fogUp;
                    downColor = R.color.fogDown;
                    imageResource = R.drawable.fog;
                    break;
                default:
                    upColor = R.color.sunnyUp;
                    downColor = R.color.sunnyDown;
                    imageResource = R.drawable.sunny;
                    break;
            }
        }
        else
        {
            upColor = R.color.nightUp;
            downColor = R.color.nightDown;
            imageResource = R.drawable.night;
        }

        int colorTop = txtCity.getContext().getResources().getColor(upColor);
        int colorBottom = txtCity.getContext().getResources().getColor(downColor);

        GradientDrawable gradientDrawable = new GradientDrawable();

        View rootView = ((Activity) txtCity.getContext()).findViewById(R.id.main);
        rootView.setBackground(gradientDrawable);

        ValueAnimator colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration(1000);
        gradientDrawable.setColors(new int[]{colorTop, colorBottom});
        rootView.setBackground(gradientDrawable);

        colorAnimator.start();
        imgWeather.setImageResource(imageResource);
    }
}