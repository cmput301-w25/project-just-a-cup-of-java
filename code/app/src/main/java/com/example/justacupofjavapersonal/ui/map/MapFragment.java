package com.example.justacupofjavapersonal.ui.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;
import com.example.justacupofjavapersonal.class_resources.mood.SocialSituation;
import com.example.justacupofjavapersonal.databinding.FragmentMapBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.example.justacupofjavapersonal.ui.feed.FeedFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;


public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private ArrayList<Mood> moods;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                setupMap();
            }
        });

        return root;
    }

    private void setupMap() {

        moods = new ArrayList<>();
        // will automatically populate when database works
        Mood m1 = new Mood(EmotionalState.SADNESS, new Date(System.currentTimeMillis() - 47363234L));
        Mood m2 = new Mood(EmotionalState.FEAR, new Date(System.currentTimeMillis() - 56152438L));
        Mood m3 = new Mood(EmotionalState.SHAME, new Date(System.currentTimeMillis() - 20252171L));
        Mood m4 = new Mood(EmotionalState.HAPPINESS, new Date(System.currentTimeMillis() - 300525030L));
        m1.setLocation(new Location("") {{
            setLatitude(53.543598);
            setLongitude(-113.525827);
        }});
        m2.setLocation(new Location("") {{
            setLatitude(53.530982);
            setLongitude(-113.451663);
        }});
        m3.setLocation(new Location("") {{
            setLatitude(53.499826);
            setLongitude(-113.531201);
        }});
        m4.setLocation(new Location("") {{
            setLatitude(53.492633);
            setLongitude(-113.482834);
        }});

        moods.add(m1);
        moods.add(m2);
        moods.add(m3);
        moods.add(m4);

        if (googleMap != null) {
            googleMap.setMapStyle(
                new com.google.android.gms.maps.model.MapStyleOptions(
                    "["
                        + "{\"featureType\":\"all\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"off\"}]},"
                        + "{\"featureType\":\"landscape\",\"elementType\":\"all\",\"stylers\":[{\"color\":\"#2c2c2c\"}]},"
                        + "{\"featureType\":\"poi\",\"elementType\":\"all\",\"stylers\":[{\"visibility\":\"off\"}]},"
                        + "{\"featureType\":\"road\",\"elementType\":\"all\",\"stylers\":[{\"color\":\"#3d3d3d\"}]},"
                        + "{\"featureType\":\"transit\",\"elementType\":\"all\",\"stylers\":[{\"visibility\":\"off\"}]},"
                        + "{\"featureType\":\"water\",\"elementType\":\"all\",\"stylers\":[{\"color\":\"#1f1f1f\"}]}"
                    + "]"
                )
            );
            populateMap();
        }
    }

    private void populateMap() {
        if (moods != null && !moods.isEmpty()) {
            double totalLat = 0;
            double totalLng = 0;

            for (Mood mood : moods) {
                Location moodLocation = mood.getLocation();
                if (moodLocation == null) continue;
                double lat = moodLocation.getLatitude();
                double lng = moodLocation.getLongitude();
                LatLng location = new LatLng(lat, lng);
                Objects.requireNonNull(
                    googleMap.addMarker(
                        new MarkerOptions()
                            .position(location)
                            .title(mood.getState().toString())
                            .snippet("Click for more details")
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    createCustomMarker(
                                        getColorFromState(mood.getState()),
                                        null // we still can't get user from mood
                                    )
                                )
                            )
                    )
                );
                googleMap.setOnInfoWindowClickListener(marker -> {
                    for (Mood __mood : moods) {
                        Location __moodLocation = __mood.getLocation();
                        if (__moodLocation != null && marker.getPosition().equals(new LatLng(__moodLocation.getLatitude(), __moodLocation.getLongitude()))) {

                            Log.d("MapFragment", "mood clicked: " + __mood);
                            // todo: go to mood detail page
                        }
                    }
                });
                totalLat += mood.getLocation().getLatitude();
                totalLng += mood.getLocation().getLongitude();
            }

            double avgLat = totalLat / moods.size();
            double avgLng = totalLng / moods.size();
            LatLng center = new LatLng(avgLat, avgLng);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12));
        }
    }

    private Bitmap createCustomMarker(float hue, User user) {
        int markerSize = 130;
        Bitmap bitmap = Bitmap.createBitmap(markerSize, markerSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint lCircle = new Paint();
        Paint sCircle = new Paint();
        Paint oCircle = new Paint();
        oCircle.setStyle(Paint.Style.FILL);
        lCircle.setStyle(Paint.Style.FILL);
        sCircle.setStyle(Paint.Style.FILL);
        oCircle.setColor(android.graphics.Color.HSVToColor(new float[]{hue, .6f, .6f}));
        lCircle.setColor(android.graphics.Color.HSVToColor(new float[]{hue, 1, 1}));
        sCircle.setColor(android.graphics.Color.HSVToColor(new float[]{1, 0, 1}));
        canvas.drawCircle(markerSize / 2.f, markerSize / 2.f, markerSize / 2.f, oCircle);
        canvas.drawCircle(markerSize / 2.f, markerSize / 2.f, markerSize / 2.f * 0.9f, lCircle);
        canvas.drawCircle(markerSize / 2.f, markerSize / 2.f, markerSize / 2.f * 0.85f, sCircle);

        Drawable drawable = getResources().getDrawable(R.drawable.baseline_account_circle_24, null);
        if (drawable != null) {
            int iconSize = (int) (markerSize * 0.9f);
            int left = (int) (markerSize * 0.05f);
            int top = (int) (markerSize * 0.05f);
            
            drawable.setBounds(left, top, left + iconSize, top + iconSize);
            drawable.draw(canvas);
        }

        return bitmap;
    }

    private float getColorFromState(EmotionalState state) {
        // bitmapdescriptor only accepts hue, so we have to convert from hex string
        float[] hsv = new float[3];
        int color = android.graphics.Color.parseColor(state.getColor());
        android.graphics.Color.colorToHSV(color, hsv);
        return hsv[0];
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        binding.mapFollowingButton.setOnClickListener(v ->
                navController.navigate(R.id.navigation_feed)
        );

        binding.mapRecentButton.setOnClickListener(v ->
                navController.navigate(R.id.navigation_feed)
        );

    }
}