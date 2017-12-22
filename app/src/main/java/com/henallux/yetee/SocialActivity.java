package com.henallux.yetee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.henallux.model.User;

public class SocialActivity extends CustomAppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_social);

        Intent intent = getIntent();

        User currentUser = (User) intent.getSerializableExtra(HomeActivity.EXTRA_USER_ID);

        QRCodeWriter writer = new QRCodeWriter();

        try
        {
            BitMatrix bitMatrix = writer.encode(ScannerActivity.FRIENDSHIP_HEADER
                    + ScannerActivity.QR_CODES_SEPARATING_CHARACTER
                    + currentUser.getId()
                    + ScannerActivity.QR_CODES_SEPARATING_CHARACTER
                    + currentUser.getFirstName()
                    + " " + currentUser.getLastName(),
                    BarcodeFormat.QR_CODE, 512, 512);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ((ImageView) findViewById(R.id.social_qr_code)).setImageBitmap(bmp);
        }
        catch (WriterException e)
        {
            Toast.makeText(this, getString(R.string.error_qr_code_generation), Toast.LENGTH_LONG).show();
        }
    }
}
