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
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private ArrayList<Mood> moods;
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<User> followedUsers = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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

    private void loadLocationMoods() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseDB dbHelper = new FirebaseDB();
        dbHelper.getFollowing(currentUser.getUid(), new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                List<String> followedUids = new ArrayList<>();
                for (User user : userList) {
                    followedUids.add(user.getUid());
                }

                db.collection("moods")
                        .whereIn("uid", followedUids)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            moods.clear();

                            for (DocumentSnapshot doc : snapshot) {
                                Mood mood = doc.toObject(Mood.class);
                                if (mood != null && mood.getLocation() != null) {
                                    moods.add(mood);
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("FeedFragment", "Error fetching moods", e));
            }
            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Log.e("FeedFragment", "Failed to fetch followed users", e);
            }
        });
    }

    private void setupMap() {

        moods = new ArrayList<>();

        loadLocationMoods();


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