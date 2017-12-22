package com.henallux.yetee;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.henallux.model.Event;
import com.henallux.yetee.common.NetworkUtil;

public class FindYourActivity extends CustomAppCompatActivity implements OnMapReadyCallback
{
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_your);

        Intent intent = getIntent();

        currentEvent = (Event) intent.getSerializableExtra(HomeActivity.EXTRA_EVENT_ID);

        NetworkUtil netUtil = new NetworkUtil(FindYourActivity.this);

        if(netUtil.isAppConnectedToNetwork())
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_fragment);
            mapFragment.getMapAsync(this);
        }
        else
            netUtil.buildNetworkConnectionRequiredDialog(getString(R.string.text_network_connection_required_map)).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

    }
}
