package com.skjin.dev.recommend.asset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kjjung on 16. 8. 25..
 */
public class Asset {

    private String path;
    private Date date;
    private Location location;
    private ExifInterface exif;

    public Asset(String path, ExifInterface exif) {
        this.path = path;
        this.exif = exif;

        // parse date
        String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        if ( null != date ) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            try {
                this.date = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // parse location
        String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if ( null != latitude && null != longitude ) {
            GeoDegree degree = new GeoDegree(exif);
            if ( degree.isValid() ) {
                Location location = new Location("AssetLocationProvider");
                location.setLatitude(degree.latitude);
                location.setLongitude(degree.longitude);
                this.location = location;
            }
        }

    }

    public String filePath() {
        return this.path;
    }

    public Date date() {
        return this.date;
    }

    public Location location() {
        return this.location;
    }

    public class GeoDegree {
        private boolean valid = false;
        Double latitude, longitude;

        GeoDegree(ExifInterface exif) {
            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null)
                    && (attrLATITUDE_REF != null)
                    && (attrLONGITUDE != null)
                    && (attrLONGITUDE_REF != null)) {
                valid = true;

                if (attrLATITUDE_REF.equals("N")) {
                    latitude = convertToDegree(attrLATITUDE);
                } else {
                    latitude = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    longitude = convertToDegree(attrLONGITUDE);
                } else {
                    longitude = 0 - convertToDegree(attrLONGITUDE);
                }

            }
        }

        private Double convertToDegree(String stringDMS) {
            Float result = null;
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0 / S1;

            result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

            return new Double(result);


        }



        public boolean isValid() {
            return valid;
        }

    }
}
