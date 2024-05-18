package ru.mirea.galnykin.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

class Point {
    String name;
    String description;
    String address;
    GeoPoint location;

    public Point(String name, String description, String address, GeoPoint location) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getLocation() {
        return location;
    }
}


public class MapsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FacilitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private MapView mapView;
    List<Point> points = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue("ru.mirea.galnykin.mireaproject");
        points.add(new Point("Дом", "Мой дом", "г. Ревда", new GeoPoint(56.800084, 59.908718)));
        points.add(new Point("Университет", "Университет", "ул. Стромынка, 20, Москва", new GeoPoint(55.79425, 37.70154)));
        points.add(new Point("Красная площадь", "Красная площадь", "Красная площадь, Москва", new GeoPoint(55.753544, 37.621202)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(10);
        GeoPoint startPoint = new GeoPoint(55.751244, 37.618423);
        mapController.setCenter(startPoint);
        addMarkersToMap();
        FloatingActionButton fabMap = view.findViewById(R.id.fabmap);
        fabMap.setOnClickListener(v -> toggleMarkersVisibility());

        return view;
    }

    private void addMarkersToMap() {
        for (Point point : points) {
            addMarker(point.getLocation(), point.getName(), point.getDescription(), point.getAddress());
        }
    }


    private void addMarker(GeoPoint point, String title, String description, String address) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title + "\n" + description + "\n" + address);
        mapView.getOverlays().add(marker);
    }

    private boolean markersVisible = true;

    private void toggleMarkersVisibility() {
        if (markersVisible) {
            hideMarkers();
        } else {
            showMarkers();
        }
        mapView.invalidate();
        markersVisible = !markersVisible;
    }

    private void hideMarkers() {
        mapView.getOverlays().clear();
    }

    private void showMarkers() {
        addMarkersToMap();
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
}