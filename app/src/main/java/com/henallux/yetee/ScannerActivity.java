package com.henallux.yetee;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.henallux.controller.ApplicationController;
import com.henallux.exception.AlreadyExistingException;
import com.henallux.exception.DataAccessException;
import com.henallux.model.User;
import com.henallux.yetee.common.NetworkUtil;
import com.henallux.yetee.enumeration.TaskResult;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScannerActivity extends CustomAppCompatActivity implements ZXingScannerView.ResultHandler
{
    public static final String FRIENDSHIP_HEADER = "friend";
    public static final String QR_CODES_SEPARATING_CHARACTER = "\0";

    private static final int REQUEST_CAMERA = 1;

    private ZXingScannerView scannerView;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        currentUser = (User) intent.getSerializableExtra(HomeActivity.EXTRA_USER_ID);

        startScanner();
    }

    private boolean isPermissionGranted()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private void startScanner()
    {
        if(isPermissionGranted())
        {
            if(scannerView == null)
            {
                List<BarcodeFormat> formats = new ArrayList<>();
                formats.add(BarcodeFormat.QR_CODE);

                scannerView = new ZXingScannerView(this);
                scannerView.setFormats(formats);

                setContentView(scannerView);
            }

            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
        else
        {
            requestPermission();
        }
    }

    private void stopScanner()
    {
        scannerView.stopCamera();
    }

    private void buildErrorDialog(AlertDialog.Builder builder, String message)
    {
        builder.setTitle(getString(R.string.text_error));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.action_ok), new DefaultOkButtonListener());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if(requestCode == REQUEST_CAMERA)
        {
            if (grantResults.length > 0)
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), R.string.info_camera_permission_granted, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void handleResult(Result rawResult)
    {
        final String result = rawResult.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] textData = result.split(QR_CODES_SEPARATING_CHARACTER);

        switch(textData[0])
        {
            case FRIENDSHIP_HEADER :
                if(textData.length == 3)
                {
                    try
                    {
                        final int friendId = Integer.parseInt(textData[1]);
                        final String friendName = textData[2];

                        builder.setTitle(getString(R.string.text_add_friend_title));
                        builder.setMessage((getString(R.string.text_add_friend)).replace(getString(R.string.global_replace_character), friendName));

                        builder.setPositiveButton(getString(R.string.action_yes), new DefaultOkButtonListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                NetworkUtil netUtil = new NetworkUtil(ScannerActivity.this);

                                if(netUtil.isAppConnectedToNetwork())
                                {
                                    FriendRequestTask friendRequestTask = new FriendRequestTask(friendId, friendName);
                                    friendRequestTask.execute((Void) null);

                                    super.onClick(dialogInterface, i);
                                }
                                else
                                    netUtil.buildNetworkConnectionRequiredDefaultDialog().show();
                            }
                        });

                        builder.setNeutralButton(getString(R.string.action_no), new DefaultOkButtonListener());
                    }
                    catch(NumberFormatException e)
                    {
                        buildErrorDialog(builder, getString(R.string.error_malformed_qr_code));
                    }
                }
                else
                    buildErrorDialog(builder, getString(R.string.error_malformed_qr_code));
                break;

            default :
                buildErrorDialog(builder, getString(R.string.error_unknown_qr_code_format));
                break;
        }

        builder.create().show();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        startScanner();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        stopScanner();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        stopScanner();
    }

    private class DefaultOkButtonListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
            scannerView.resumeCameraPreview(ScannerActivity.this);
        }
    }

    private class FriendRequestTask extends AsyncTask<Void, Void, TaskResult>
    {
        private final long friendId;
        private final String friendName;

        private FriendRequestTask(long friendId, String friendName)
        {
            this.friendId = friendId;
            this.friendName = friendName;
        }

        @Override
        protected TaskResult doInBackground(Void... params)
        {
            ApplicationController controller = new ApplicationController();

            try
            {
                controller.addFriendRequest(currentUser.getId(), friendId);

                return TaskResult.NO_ERROR;
            }
            catch (AlreadyExistingException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ScannerActivity.this, getString(R.string.error_friend_already_requested), Toast.LENGTH_LONG).show();
                    }
                });

                return TaskResult.ALREADY_EXISTING_RECORD;
            }
            catch (final DataAccessException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ScannerActivity.this, getString(e.getMessageId()), Toast.LENGTH_LONG).show();
                    }
                });

                return TaskResult.GENERIC_ERROR;
            }
        }

        @Override
        protected void onPostExecute(final TaskResult success)
        {
            if(success.equals(TaskResult.NO_ERROR))
            {
                String replaceCharacter = getString(R.string.global_replace_character);

                Toast.makeText(ScannerActivity.this, getString(R.string.info_friend_request_confirmed).replace(replaceCharacter, friendName), Toast.LENGTH_LONG).show();
            }
        }
    }
}
