package com.skjin.dev.moment.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class ThumbnailTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        String imagePath = (String)objects[0];
        int targetWidth = (Integer)objects[1];

        // Get the dimensions of the bitmap
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), targetWidth, targetWidth);

        return bitmap;
    }

}
