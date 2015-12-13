package com.yiyangzhu.yyweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.util.Random;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = WeatherActivity.class.getSimpleName();

    private static final int LOCATION_PERMISSION_REQUEST = 37;

    private LocationManager locationManager;
    private Location location;
    private Settings settings;

    private ImageView backgroundImageView;
    private TextView cityTextView;
    private TextView weatherTextView;
    private TextView temperatureTextView;

    private boolean refreshed = false;
;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        backgroundImageView = (ImageView) findViewById(R.id.background_image_imageview);
        cityTextView = (TextView) findViewById(R.id.city_textview);
        weatherTextView = (TextView) findViewById(R.id.weather_textview);
        temperatureTextView = (TextView) findViewById(R.id.temperature_textview);

        // check Internet connection
        new AsyncTask<Void, Void, Void>(){

            boolean connected = false;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Inet4Address.getByName("www.baidu.com");
                    connected = true;
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!connected) {
                    Toast.makeText(WeatherActivity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }

        // get user settings
        settings = new Settings(this);

        // get last data
        SharedPreferences cacheStorage = getSharedPreferences(getPackageName() + ".cache", MODE_PRIVATE);
        CityWeather cacheCityWeather = new CityWeather.Builder()
                .setName(cacheStorage.getString("city", getString(R.string.default_city)))
                .setWeather(cacheStorage.getString("weather", getString(R.string.default_weather)))
                .setTemperature(Double.parseDouble(cacheStorage.getString("temperature", "37.0")))
                .build();
        showData(cacheCityWeather);

        // show the image saved locally, if exists
        File internalDirectory = getFilesDir();
        File backgroundImageFile = new File(internalDirectory, Constants.BACKGROUND_IMAGE_FILENAME);
        if (backgroundImageFile.exists()) {
            Picasso.with(this).load(backgroundImageFile).into(backgroundImageView);
        }

        // initialize RequestQueue
        ProgramRequestQueue.initialize(this);

        // download background image
        if (!settings.getCity().equals(Settings.DEFAULT_LOCATION)) {
            getAndSetBackgroundImage(settings.getCity());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // get settings
        settings = new Settings(this);

        // get location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (settings.getCity().equals(Settings.DEFAULT_LOCATION) && location == null) {
            return;
        }

        getAndSetWeather();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (locationManager == null) {
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                }
                try {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {}
                getAndSetWeather();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh_background_menuitem:
                getAndSetBackgroundImage(cityTextView.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showData(CityWeather data) {
        if (data == null) {
            return;
        }
        cityTextView.setText(data.getName());
        weatherTextView.setText(data.getWeather());
        switch (settings.getMeasurement()) {
            case Settings.MEASUREMENT_CELSIUS:
                temperatureTextView.setText(String.format("%.1f°C", WeatherHelper.k2c(data.getTemperature())));
                break;
            case Settings.MEASUREMENT_FAHRENHEIT:
                temperatureTextView.setText(String.format("%.1f°F", WeatherHelper.k2f(data.getTemperature())));
                break;
        }
    }

    private void getAndSetWeather() {

        Uri uri = null;

        if (settings.getCity().equals(Settings.DEFAULT_LOCATION)) {
            uri = Uri.parse("http://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("lat", Double.toString(location.getLatitude()))
                    .appendQueryParameter("lon", Double.toString(location.getLongitude()))
                    .appendQueryParameter("appid", getString(R.string.OPEN_WEATHER_API))
                    .build();
        } else {
            uri = Uri.parse("http://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("q", settings.getCity())
                    .appendQueryParameter("appid", getString(R.string.OPEN_WEATHER_API))
                    .build();
        }

        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String cityTemp;
                            String weatherTemp;
                            double temperatureTemp;

                            JsonObject root = (JsonObject)
                                    new JsonParser().parse(response.toString());
                            cityTemp = root.get("name").getAsString();
                            weatherTemp = root.get("weather").getAsJsonArray().get(0)
                                    .getAsJsonObject().get("main").getAsString();
                            temperatureTemp = root.get("main").getAsJsonObject().get("temp")
                                    .getAsDouble();

                            CityWeather data = new CityWeather.Builder()
                                    .setName(cityTemp)
                                    .setTemperature(temperatureTemp)
                                    .setWeather(weatherTemp)
                                    .build();

                            SharedPreferences.Editor editor = getSharedPreferences(getPackageName() + ".cache", MODE_PRIVATE).edit();
                            editor.putString("city", cityTemp);
                            editor.putString("weather", weatherTemp);
                            editor.putString("temperature", Double.toString(temperatureTemp));
                            editor.apply();

                            showData(data);

                            if (settings.getCity().equals(Settings.DEFAULT_LOCATION) && !refreshed) {
                                getAndSetBackgroundImage(data.getName());
                                refreshed = true;
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }, null);
        weatherRequest.setTag("weather");

        ProgramRequestQueue.getRequestQueue().add(weatherRequest);

    }

    private void getAndSetBackgroundImage(String city) {
        Uri uri = Uri.parse("https://api.flickr.com/services/rest/?method=flickr.photos.search&privacy_filter=1&content_type=1&has_geo=1&format=json&nojsoncallback=1")
                .buildUpon()
                .appendQueryParameter("api_key", getString(R.string.FLICKR_API))
                .appendQueryParameter("tags", city)
                .build();
        final JsonObjectRequest imageRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        JsonObject root = new JsonParser().parse(response.toString()).getAsJsonObject();
                        JsonArray imageInfoArray = root.get("photos").getAsJsonObject().get("photo")
                                .getAsJsonArray();
                        Random random = new Random();
                        JsonObject imageInfo = imageInfoArray.get(random.nextInt(imageInfoArray.size())).getAsJsonObject();
                        String imageUrl = String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                                imageInfo.get("farm").getAsString(),
                                imageInfo.get("server").getAsString(),
                                imageInfo.get("id").getAsString(),
                                imageInfo.get("secret").getAsString());

                        // get image
                        RequestCreator creator =  Picasso.with(WeatherActivity.this).load(imageUrl);

                        // set it the background image
                        creator.into(backgroundImageView);

                        // save it to internal storage
                        creator.into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                File internalDirectory = getFilesDir();
                                File imageFile = new File(internalDirectory, Constants.BACKGROUND_IMAGE_FILENAME);
                                if (imageFile.exists()) {
                                    imageFile.delete();
                                }
                                try {
                                    imageFile.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(imageFile);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {}
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                }, null);
        ProgramRequestQueue.getRequestQueue().add(imageRequest);
    }

}