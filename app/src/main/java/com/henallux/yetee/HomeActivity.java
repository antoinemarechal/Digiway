package com.henallux.yetee;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.model.Event;
import com.henallux.model.EventCategory;
import com.henallux.model.PointOfInterest;
import com.henallux.model.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String EXTRA_USER_ID = "currentUser";
    public static final String EXTRA_EVENT_ID = "selectedEvent";

    private DrawerLayout navigationDrawerLayout;
    private ListView navigationDrawer;

    private ActionBarDrawerToggle navigationDrawerToggle;

    private RelativeLayout eventButton;
    private RelativeLayout depositButton;
    private RelativeLayout scannerButton;
    private RelativeLayout findYourButton;
    private RelativeLayout mapButton;

    private User currentUser;
    private Event selectedEvent;

    private final int eventListRequestCode = 42;
    private final int balanceRequestCode = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Intent intent = getIntent();

        navigationDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        navigationDrawer = (ListView) findViewById(R.id.home_navigation_drawer);

        String[] menuOptionsLabels = getResources().getStringArray(R.array.drawer_menu_options_array);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        navigationDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, menuOptionsLabels));
        navigationDrawer.setOnItemClickListener(new DrawerItemClickListener());

        navigationDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navigationDrawerLayout.addDrawerListener(navigationDrawerToggle);

        eventButton = (RelativeLayout) findViewById(R.id.home_event_button);
        eventButton.setOnClickListener(this);
        depositButton = (RelativeLayout) findViewById(R.id.home_deposit_button);
        depositButton.setOnClickListener(this);
        scannerButton = (RelativeLayout) findViewById(R.id.home_scanner_button);
        scannerButton.setOnClickListener(this);
        findYourButton = (RelativeLayout) findViewById(R.id.home_find_your_button);
        findYourButton.setOnClickListener(this);
        mapButton = (RelativeLayout) findViewById(R.id.home_map_button);
        mapButton.setOnClickListener(this);

        if(savedInstanceState == null)
        {
            currentUser = (User) intent.getSerializableExtra(EXTRA_USER_ID);

            selectedEvent = (Event) intent.getSerializableExtra(EXTRA_EVENT_ID);
        }
        else
        {
            currentUser = (User) savedInstanceState.getSerializable(EXTRA_USER_ID);

            selectedEvent = (Event) savedInstanceState.getSerializable(EXTRA_EVENT_ID);
        }

        updateSelectedEventUI();
        updateUserBalanceUI();
    }

    private void updateSelectedEventUI()
    {
        if(selectedEvent != null)
        {
            ((TextView) findViewById(R.id.home_selected_event_title)).setText(selectedEvent.getName());
            ((TextView) findViewById(R.id.home_selected_event_description)).setText(selectedEvent.getDescription());
        }
        else
            ((TextView) findViewById(R.id.home_selected_event_title)).setText(getString(R.string.text_no_event_selected));
    }

    private void updateUserBalanceUI()
    {
        if(currentUser != null)
            ((TextView) findViewById(R.id.home_your_balance)).setText(NumberFormat.getCurrencyInstance().format(currentUser.getBalance()));
    }

    private void selectItem(int position)
    {
        Intent intent;

        switch(position)
        {
            case 0 :
                switchToEventListActivity();
                break;

            case 1 :
                switchToMapActivity();
                break;

            case 2 :
                switchToFindYourActivity();
                break;

            case 3 :
                switchToBalanceActivity();
                break;

            case 4 :
                intent = new Intent(this, SocialActivity.class);

                intent.putExtra(EXTRA_USER_ID, currentUser);

                startActivity(intent);
                break;

            case 5 :
                intent = new Intent(this, SettingsActivity.class);

                startActivity(intent);
                break;
        }

        navigationDrawer.setItemChecked(position, false);
        navigationDrawerLayout.closeDrawer(navigationDrawer);
    }

    private void switchToBalanceActivity()
    {
        Intent intent = new Intent(this, BalanceActivity.class);

        intent.putExtra(EXTRA_USER_ID, currentUser);

        startActivityForResult(intent, balanceRequestCode);
    }

    private void switchToFindYourActivity()
    {
        if(selectedEvent == null)
        {
            Toast.makeText(HomeActivity.this, R.string.error_no_event_selected, Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(HomeActivity.this, FindYourActivity.class);

            intent.putExtra(EXTRA_EVENT_ID, selectedEvent);

            startActivity(intent);
        }
    }

    private void switchToMapActivity()
    {
        if(selectedEvent == null)
        {
            Toast.makeText(HomeActivity.this, R.string.error_no_event_selected, Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(this, EventMapActivity.class);

            intent.putExtra(EXTRA_EVENT_ID, selectedEvent);

            startActivity(intent);
        }
    }

    private void switchToEventListActivity()
    {
        Intent intent = new Intent(this, EventListActivity.class);

        startActivityForResult(intent, eventListRequestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(EXTRA_USER_ID, currentUser);
        savedInstanceState.putSerializable(EXTRA_EVENT_ID, selectedEvent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == eventListRequestCode)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                selectedEvent = (Event) data.getSerializableExtra(EXTRA_EVENT_ID);

                updateSelectedEventUI();
            }
        }
        else if(requestCode == balanceRequestCode && data != null)
        {
            currentUser = (User) data.getSerializableExtra(EXTRA_USER_ID);

            updateUserBalanceUI();
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == eventButton)
        {
            if(selectedEvent == null)
                switchToEventListActivity();
        }
        else if(view == depositButton)
        {
            switchToBalanceActivity();
        }
        else if(view == scannerButton)
        {
            Intent intent = new Intent(this, ScannerActivity.class);

            intent.putExtra(EXTRA_USER_ID, currentUser);

            startActivity(intent);
        }
        else if(view == findYourButton)
        {
            switchToFindYourActivity();
        }
        else if(view == mapButton)
        {
            switchToMapActivity();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return navigationDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        navigationDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }
}
