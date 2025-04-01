package com.example.justacupofjavapersonal.ui.map;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class NearbyMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDB db = new FirebaseDB();
        db.getFollowedUserMoodsGrouped(user.getUid(), new FirebaseDB.OnUserMoodsGroupedListener() {
            @Override
            public void onUserMoodsGrouped(Map<String, List<Mood>> moodMap, Map<String, User> userMap) {
                for (String uid : moodMap.keySet()) {
                    List<Mood> moods = moodMap.get(uid);
                    User moodOwner = userMap.get(uid);

                    for (Mood mood : moods) {
                        if (mood.getLocation() != null) {
                            LatLng pos = new LatLng(
                                    mood.getLocation().getLatitude(),
                                    mood.getLocation().getLongitude()
                            );

                            map.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(moodOwner.getName())
                                    .snippet(mood.getEmotion()));
                        }
                    }
                }

                // Optional: Move camera to your location or first mood
                if (!moodMap.isEmpty()) {
                    for (List<Mood> moods : moodMap.values()) {
                        for (Mood mood : moods) {
                            if (mood.getLocation() != null) {
                                LatLng pos = new LatLng(mood.getLocation().getLatitude(), mood.getLocation().getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onUserMoodsGroupedFailed(Exception e) {
                Log.e("NearbyMap", "Failed to load moods with location", e);
            }
        });
    }
}
