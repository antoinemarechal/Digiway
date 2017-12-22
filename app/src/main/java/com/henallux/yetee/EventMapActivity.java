package com.henallux.yetee;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.henallux.model.Event;
import com.henallux.model.PointOfInterest;
import com.henallux.yetee.common.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventMapActivity extends CustomAppCompatActivity implements OnMapReadyCallback
{
   private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_map);

        Intent intent = getIntent();

        if(savedInstanceState == null)
            currentEvent = (Event) intent.getSerializableExtra(HomeActivity.EXTRA_EVENT_ID);
        else
            currentEvent = (Event) savedInstanceState.getSerializable(HomeActivity.EXTRA_EVENT_ID);

        NetworkUtil netUtil = new NetworkUtil(EventMapActivity.this);

        if(netUtil.isAppConnectedToNetwork())
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_fragment);
            mapFragment.getMapAsync(this);
        }
        else
            netUtil.buildNetworkConnectionRequiredDialog(getString(R.string.text_network_connection_required_map)).show();
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        LayoutInflater inflater = LayoutInflater.from(EventMapActivity.this);

        ArrayList<Float> sortedColors = new ArrayList<>();
        sortedColors.add(BitmapDescriptorFactory.HUE_BLUE);
        sortedColors.add(BitmapDescriptorFactory.HUE_AZURE);
        sortedColors.add(BitmapDescriptorFactory.HUE_VIOLET);
        sortedColors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        sortedColors.add(BitmapDescriptorFactory.HUE_CYAN);
        sortedColors.add(BitmapDescriptorFactory.HUE_ORANGE);
        sortedColors.add(BitmapDescriptorFactory.HUE_YELLOW);
        sortedColors.add(BitmapDescriptorFactory.HUE_GREEN);
        sortedColors.add(BitmapDescriptorFactory.HUE_ROSE);
        sortedColors.add(BitmapDescriptorFactory.HUE_RED);

        boolean geocoderSuccess = false;

        if(Geocoder.isPresent())
        {
            try
            {
                String location = currentEvent.getZipCode() + " " + currentEvent.getCity();
                Geocoder gc = new Geocoder(this, Locale.FRENCH);

                List<Address> possibleAddresses = gc.getFromLocationName(location, 1);

                if(possibleAddresses.size() > 0)
                {
                    LatLng cityCoordinates = new LatLng(
                            possibleAddresses.get(0).getLatitude(),
                            possibleAddresses.get(0).getLongitude()
                    );

                    map.addMarker(new MarkerOptions()
                            .position(cityCoordinates)
                            .title(currentEvent.getCity())
                            .snippet(getString(R.string.text_event_city)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCoordinates, 15));

                    geocoderSuccess = true;
                }
            }
            catch (IOException e)
            {
                Toast.makeText(EventMapActivity.this, getString(R.string.error_automatic_location_failed), Toast.LENGTH_LONG).show();
            }
        }

        PointOfInterest lastPointOfInterest = null;
        int lastCounterStep = -1;
        int counter = 0;

        for(PointOfInterest poi : currentEvent.getPointsOfInterest())
        {
            if(lastPointOfInterest != null && !lastPointOfInterest.getCategory().equals(poi.getCategory()))
                counter++;

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                    .title(poi.getLabel())
                    .snippet(poi.getCategory() == null ? getString(R.string.text_misc) : poi.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(sortedColors.get(counter % sortedColors.size()))));

            if(counter > lastCounterStep)
            {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    ViewGroup parent = (ViewGroup) findViewById(R.id.event_map_legend);

                    View view = inflater.inflate(R.layout.legend_item, parent, false);

                    TextView legendItemNameView = view.findViewById(R.id.map_legend_item_name);
                    legendItemNameView.setText(poi.getCategory());

                    ImageView legendItemColorSquare = view.findViewById(R.id.map_legend_item_color_square);

                    Drawable background = legendItemColorSquare.getDrawable();

                    int color = Color.HSVToColor(new float[] {sortedColors.get(counter % sortedColors.size()), 1.0f, 1.0f});

                    GradientDrawable gradientDrawable = (GradientDrawable) background;
                    gradientDrawable.setColor(color);

                    parent.addView(view);
                }

                lastCounterStep++;
            }

            lastPointOfInterest = poi;
        }

        if(!geocoderSuccess && lastPointOfInterest != null)
        {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    lastPointOfInterest.getLatitude(),
                    lastPointOfInterest.getLongitude()
            ), 15));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(HomeActivity.EXTRA_EVENT_ID, currentEvent);
    }
}

