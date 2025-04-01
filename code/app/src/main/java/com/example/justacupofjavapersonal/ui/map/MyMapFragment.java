package com.example.justacupofjavapersonal.ui.map;

import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.LatLngLocation;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.databinding.FragmentMyMapBinding;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyMapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMyMapBinding binding;

    public MyMapFragment() {
    }

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng center = new LatLng(53.5461, -113.4938); // Edmonton fallback
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));

        fetchMyMoods();
    }

    private void fetchMyMoods() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("moods")
                .whereEqualTo("uid", currentUid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Mood mood = doc.toObject(Mood.class);
                        if (mood.getLocation() != null) {
                            addMoodMarker(mood);
                        }
                    }
                });
    }

    private void addMoodMarker(Mood mood) {
        LatLngLocation loc = mood.getLocation();
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

        String emoji = getEmojiForMood(mood.getEmotion());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(emoji + " " + mood.getEmotion())
                .snippet(mood.getDate() + " @ " + mood.getTime());

        googleMap.addMarker(markerOptions);
    }

    private String getEmojiForMood(String mood) {
        if (mood == null) return "🙂";
        switch (mood.toUpperCase()) {
            case "JOY":
                return "😄";
            case "SADNESS":
                return "😢";
            case "FEAR":
                return "😨";
            case "ANGER":
                return "😠";
            case "LOVE":
                return "😍";
            case "SURPRISE":
                return "😲";
            default:
                return "🙂";
        }
    }

    // --- Required lifecycle methods for MapView ---
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
