package com.example.noteconverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TesseractOCR {

    private final TessBaseAPI mTess;

    public TesseractOCR(Context context, String language){

        mTess = new TessBaseAPI();
        String datapath = "/tesseract/tessdata/";
        try {
            Log.d("filesdir", "filesdir: " + context.getFilesDir());
            datapath = context.getFilesDir() + datapath; mTess.init(datapath, language);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getOCRResult (Bitmap bitmap){
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy(){
        if (mTess != null) mTess.end();
    }

}
