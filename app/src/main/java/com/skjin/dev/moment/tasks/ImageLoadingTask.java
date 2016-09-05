package com.skjin.dev.moment.tasks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class ImageLoadingTask extends AsyncTask {

    private final WeakReference imageViewReference;
    private String imagePath = "";
    private boolean fitMin;

    public ImageLoadingTask(ImageView imageView, boolean fitMin) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference(imageView);
        this.fitMin = fitMin;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        imagePath = (String)objects[0];
        int targetWidth = (Integer)objects[1];
        int targetHeight = (Integer)objects[2];

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

    // Determine how much to scale down the image
        int scaleFactor = 0;
        if ( this.fitMin ) {
            scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);
        } else {
            scaleFactor = Math.max(photoW / targetWidth, photoH / targetHeight);
        }

    // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Object o) {
        Bitmap bitmap = (Bitmap)o;
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = (ImageView)imageViewReference.get();
            final ImageLoadingTask bitmapWorkerTask = AsyncDrawable.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(500);

                imageView.setAnimation(fadeIn);

            }
        }
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ImageLoadingTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
        }

        public ImageLoadingTask getBitmapWorkerTask() {
            return ((ImageLoadingTask)bitmapWorkerTaskReference.get());
        }

        public static boolean cancelPotentialWork(String imagePath, ImageView imageView) {
            final ImageLoadingTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (bitmapWorkerTask != null) {
                final String path = bitmapWorkerTask.imagePath;
                if (path != imagePath) {
                    // Cancel previous task
                    bitmapWorkerTask.cancel(true);
                } else {
                    // The same work is already in progress
                    return false;
                }
            }
            // No task associated with the ImageView, or an existing task was cancelled
            return true;
        }

        private static ImageLoadingTask getBitmapWorkerTask(ImageView imageView) {
            if (imageView != null) {
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AsyncDrawable) {
                    final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                    return asyncDrawable.getBitmapWorkerTask();
                }
            }
            return null;
        }
    }
}
