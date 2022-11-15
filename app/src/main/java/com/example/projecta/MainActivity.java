package com.example.projecta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button button, button2, button3;
    TextView latitude, longitude, country, city, address;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn);
        button2 = findViewById(R.id.btn2);
        button3 = findViewById(R.id.btn3);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                    t1.setLanguage(new Locale("id", "ID"));
            }
        });

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text =  address.getText().toString();
                t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {
            getLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

        @SuppressLint("MissingPermission")
        private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );
                        //Set latitude on TextView
                        latitude.setText("Latitude: " + addresses.get(0).getLatitude());
                        //Set longitude on TextView
                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
                        //Set country on TextView
                        country.setText("Country: " + addresses.get(0).getCountryName());
                        //Set city on TextView
                        city.setText("City: " + addresses.get(0).getLocality());
                        //Set address on TextView
                        address.setText("Address: " + addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    @SuppressLint("MissingPermission")
//    private void getLocation() {
//        LocationManager locationManager = (LocationManager) getSystemService(
//                Context.LOCATION_SERVICE
//        );
//        //Check condition
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            //When location service is enabled
//            //Get last location
//            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    //Initialize location
//                    Location location = task.getResult();
//                    //Check condition
//                    if (location != null) {
//                        //When location result is not null
//                        //Set values
//                        latitude.setText(String.valueOf(location.getLatitude()));
//                        longitude.setText(String.valueOf(location.getLongitude()));
//                    } else {
//                        //When location result is null
//                        //Initialize location request
//                        LocationRequest locationRequest = new LocationRequest()
//                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                                .setInterval(10000)
//                                .setFastestInterval(1000)
//                                .setNumUpdates(1);
//                        //Initialize location call back
//                        LocationCallback locationCallback = new LocationCallback() {
//                            @Override
//                            public void onLocationResult(@NonNull LocationResult locationResult) {
//                                //Initialize location
//                                Location location1 = locationResult.getLastLocation();
//                                //Set values
//                                latitude.setText("Latitude: " + location1.getLatitude());
//                                longitude.setText("Longitude: " + location1.getLongitude());
//
//                            }
//                        };
//                        //Request location updates
//                        fusedLocationProviderClient.requestLocationUpdates(locationRequest
//                                , locationCallback, Looper.myLooper());
//                    }
//                }
//            });
//        } else {
//            //When location service is not enabled
//            //Open location setting
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        }
//    }
}