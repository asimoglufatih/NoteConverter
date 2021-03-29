package com.example.noteconverter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    ImageView ivCamera;
    ImageView ivPlus;
    ImageView resim;
    String language = "eng.traineddata";
    Context context = this;

    private String mCurrentPhotoPath;

    ProgressDialog mProgressDialog;
    TesseractOCR mTessOCR = new TesseractOCR(MainActivity.this, language);


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("onActivityResult", "resultCode" + " " + resultCode + " " + requestCode);
            Bundle bundle = data.getExtras();
            Bitmap bm = (Bitmap)bundle.get("data");
            resim.setImageBitmap(bm);
            doOCR(bm);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("bilgi", "OnCreate calisti.");

        tanimla();
        mainTakePhoto();

    }

    public void tanimla(){

        ivCamera = findViewById(R.id.ivCamera);
        ivPlus = findViewById(R.id.ivPlus);
        TextView tvMain = findViewById(R.id.tvMain);
        resim = findViewById(R.id.resim);
        tvResult = findViewById(R.id.tvResult);
    }

    public void takePhoto(){
        try {
            Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(photoIntent, 1);
        }catch (Exception e){
            Log.d("kamera", "kamera baslatilamadi.");
        }
    }

    public void mainTakePhoto(){
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void doOCR(final Bitmap bitmap){
        if (mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(this, "Processing", "Doing OCR..." , true);
        }else{
            mProgressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String srcText = mTessOCR.getOCRResult(bitmap);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (srcText != null && !srcText.equals("")){
                            tvResult.setText(srcText);
                            Log.d("istvwork", "başardık abi");
                        }
                        mTessOCR.onDestroy();
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}