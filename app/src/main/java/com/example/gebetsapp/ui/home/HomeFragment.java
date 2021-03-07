package com.example.gebetsapp.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gebetsapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView user_location, datetime;
    private TextView[] time;
    private double latitude = 0, longitude = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        user_location = root.findViewById(R.id.user_location);
        datetime = root.findViewById(R.id.date);
        time = new TextView[6];
        time[0] = root.findViewById(R.id.TextViewTime);
        time[1] = root.findViewById(R.id.TextViewTime2);
        time[2] = root.findViewById(R.id.TextViewTime3);
        time[3] = root.findViewById(R.id.TextViewTime4);
        time[4] = root.findViewById(R.id.TextViewTime5);
        time[5] = root.findViewById(R.id.TextViewTime6);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fetchLocation()) {
                    fetchTimes();
                }
            }
        });

        return root;
    }

    private void fetchTimes(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String month = currentDate.substring(3,5);
        String year = currentDate.substring(6);
        datetime.setText(currentDate + " " + currentTime);
        //Koordinaten Essen Altendorf
        latitude = 51.458184;
        longitude = 6.998448;
        //month = 4, year = 2017;
        String url = "https://api.aladhan.com/v1/calendar?latitude="+ latitude +"&longitude="+ longitude +
                "&method=2&month="+ month +"&year="+ year +""+"/";
        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int day = response.indexOf(currentDate);
                        int time0 = response.lastIndexOf("Fajr",day) + 7;
                        int time1 = response.lastIndexOf("Sunrise",day) + 10;
                        int time2 = response.lastIndexOf("Dhuhr",day) + 8;
                        int time3 = response.lastIndexOf("Asr",day) + 6;
                        int time4 = response.lastIndexOf("Maghrib",day) + 10;
                        int time5 = response.lastIndexOf("Isha",day) + 7;

                        time[0].setText(response.substring(time0,time0 + 5));

                        time[1].setText(response.substring(time1,time1 + 5));

                        time[2].setText(response.substring(time2,time2 + 5));

                        time[3].setText(response.substring(time3,time3 + 5));

                        time[4].setText(response.substring(time4,time4 + 5));

                        time[5].setText(response.substring(time5,time5 + 5));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                time[0].setText("That didn't work!");
                System.out.println(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean fetchLocation(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale("Manifest.permission.ACCESS_COARSE_LOCATION")){
                new AlertDialog.Builder(getContext())
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                System.out.println("Got Location");
                                user_location.setText("Latitude = "+latitude + "\nLongitude = " + longitude);
                            }
                        }
                    });
            return latitude + longitude != 0;
        }
        return false;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{

            }
        }
    }


}