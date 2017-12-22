package com.henallux.yetee.common;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.henallux.yetee.R;

public class NetworkUtil
{
    private Context context;

    public NetworkUtil(Context context)
    {
        this.context = context;
    }

    public boolean isAppConnectedToNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public AlertDialog buildNetworkConnectionRequiredDefaultDialog()
    {
        return buildNetworkConnectionRequiredDialog(context.getString(R.string.text_network_connection_required_default));
    }

    public AlertDialog buildNetworkConnectionRequiredDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.text_error));
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.action_ok), null);

        return builder.create();
    }
}
