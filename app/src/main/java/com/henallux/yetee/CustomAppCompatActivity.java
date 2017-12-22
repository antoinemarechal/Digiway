package com.henallux.yetee;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public abstract class CustomAppCompatActivity extends AppCompatActivity
{
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
