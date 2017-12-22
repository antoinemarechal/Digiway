package com.henallux.yetee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.controller.ApplicationController;
import com.henallux.exception.DataAccessException;
import com.henallux.model.Event;
import com.henallux.model.EventCategory;
import com.henallux.yetee.common.NetworkUtil;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventListActivity extends CustomAppCompatActivity
{
    private ListView eventsListView;
    private ProgressBar progressBar;

    private ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        eventsListView = (ListView) findViewById(R.id.event_list_list);
        progressBar = (ProgressBar) findViewById(R.id.event_listing_progress);

        showProgress(true);

        NetworkUtil netUtil = new NetworkUtil(EventListActivity.this);

        if(netUtil.isAppConnectedToNetwork())
        {
            new ListingEventsTask().execute();
        }
        else
            netUtil.buildNetworkConnectionRequiredDialog(getString(R.string.text_network_connection_required_event_list)).show();
    }

    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        eventsListView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        eventsListView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                eventsListView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private class ListingEventsTask extends AsyncTask<Void, Void, ArrayList<Event>>
    {
        @Override
        protected ArrayList<Event> doInBackground(Void... params)
        {
            ApplicationController controller = new ApplicationController();

            try
            {
                return controller.getAllEvents();
            }
            catch (final DataAccessException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EventListActivity.this, getString(e.getMessageId()), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<Event> events)
        {
            showProgress(false);

            eventList = events;

            if(events != null && events.size() > 0)
            {
                CustomAdapter adapter = new CustomAdapter(EventListActivity.this, events);
                adapter.notifyDataSetChanged();

                eventsListView.setAdapter(adapter);
                eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position, long id)
                    {
                        Intent result = new Intent();
                        result.putExtra(HomeActivity.EXTRA_EVENT_ID, eventList.get(position));

                        setResult(Activity.RESULT_OK, result);

                        finish();
                    }
                });
            }
            else
            {
                Toast.makeText(EventListActivity.this, getString(R.string.info_no_events_to_come), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled()
        {
            showProgress(false);
        }
    }

    private class CustomAdapter extends ArrayAdapter<Event>
    {
        private Context context;
        private EventCategory lastCategory = null;

        private CustomAdapter(Context context, ArrayList<Event> data)
        {
            super(context, R.layout.event_list_item, data);

            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            Event event = getItem(position);
            View view = convertView;

            if (event != null)
            {
                if (view == null)
                {
                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    if(lastCategory == null || lastCategory.getId() != event.getCategory().getId())
                    {
                        lastCategory = event.getCategory();

                        view = inflater.inflate(R.layout.event_list_category_title, parent, false);

                        TextView categoryTitleView = view.findViewById(R.id.event_list_category_title);
                        categoryTitleView.setText(lastCategory.getName());

                        LinearLayout eventCategoryTitleLayout = view.findViewById(R.id.event_list_category_title_layout);
                        eventCategoryTitleLayout.addView(inflater.inflate(R.layout.event_list_item, parent, false));
                    }
                    else
                        view = inflater.inflate(R.layout.event_list_item, parent, false);
                }

                TextView eventTitle = view.findViewById(R.id.event_list_item_title);
                TextView eventDetails = view.findViewById(R.id.event_list_item_details);

                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.global_date_format));
                String replaceCharacter = getString(R.string.global_replace_character);

                eventTitle.setText(event.getName());

                eventDetails.setText(context.getString(R.string.text_event_details)
                        .replaceFirst(replaceCharacter, event.getCity())
                        .replaceFirst(replaceCharacter, dateFormat.format(event.getDate().getTime()))
                        .replaceFirst(replaceCharacter, currencyFormatter.format(event.getTicketPrice())));
            }

            return view;
        }
    }
}
