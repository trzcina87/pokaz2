package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import it.sephiroth.android.library.exif2.ExifInterface;

public class PlikJPG {

    public String exif;
    public int orient;
    public Bitmap bitmapa;
    public String sciezka;

    public PlikJPG() {
        sciezka = null;
        bitmapa = null;
        orient = 0;
        exif = null;
    }

    public void wyczysc() {
        if(bitmapa != null) {
            bitmapa.recycle();
            bitmapa = null;
        }
    }

    public void uzupelnijOrient() {
        ExifInterface exifint = new ExifInterface();
        try {
            exifint.readExif(sciezka, ExifInterface.Options.OPTION_ALL);
        } catch (IOException e) {
        }
        if(exifint.getTag(ExifInterface.TAG_ORIENTATION) != null) {
            int orientzefix = exifint.getTag(ExifInterface.TAG_ORIENTATION).getValueAsInt(0);
            orient = ExifInterface.getRotationForOrientationValue((short) orientzefix);
        }
    }

    public void uzupelnijExif() {
        if(exif == null) {
            ExifInterface exifint = new ExifInterface();
            exif = new File(sciezka).getName();
            exif = exif + " " + bitmapa.getWidth() + "x" + bitmapa.getHeight();
            try {
                exifint.readExif(sciezka, ExifInterface.Options.OPTION_ALL);
            } catch (IOException e) {
            }
            if (exifint.getTag(ExifInterface.TAG_MODEL) != null) {
                exif = exif + " " + exifint.getTag(ExifInterface.TAG_MODEL).getValueAsString();
            }
            if (exifint.getTag(ExifInterface.TAG_LENS_MODEL) != null) {
                exif = exif + " (" + exifint.getTag(ExifInterface.TAG_LENS_MODEL).getValueAsString() + ")";
            }
            if (exifint.getTag(ExifInterface.TAG_FOCAL_LENGTH) != null) {
                exif = exif + " " + exifint.getTag(ExifInterface.TAG_FOCAL_LENGTH).getValueAsRational(0).toDouble() + "mm";
            }
            if (exifint.getTag(ExifInterface.TAG_ISO_SPEED_RATINGS) != null) {
                exif = exif + " ISO" + exifint.getTag(ExifInterface.TAG_ISO_SPEED_RATINGS).getValueAsInt(0);
            }
            if (exifint.getTag(ExifInterface.TAG_F_NUMBER) != null) {
                exif = exif + " F/" + exifint.getTag(ExifInterface.TAG_F_NUMBER).getValueAsRational(0).toDouble();
            }
            if (exifint.getTag(ExifInterface.TAG_EXPOSURE_TIME) != null) {
                exif = exif + " " + exifint.getTag(ExifInterface.TAG_EXPOSURE_TIME).getValueAsRational(0).toString() + "s";
            }
            if (exifint.getTag(ExifInterface.TAG_DATE_TIME_ORIGINAL) != null) {
                exif = exif + " (" + exifint.getTag(ExifInterface.TAG_DATE_TIME_ORIGINAL).getValueAsString() + ")";
            }
        }
    }

    public boolean zaladuj() {
        try {
            Log.e("PLIKJPG", "Probuje ladowac: " + sciezka);
            bitmapa = BitmapFactory.decodeFile(sciezka);
            uzupelnijExif();
            uzupelnijOrient();
            Log.e("PLIKJPG", "Zaladowalem: " + sciezka);
            return true;
        } catch (OutOfMemoryError error) {
            return false;
        }
    }
}
