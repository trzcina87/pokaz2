package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import it.sephiroth.android.library.exif2.ExifInterface;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class PlikJPG {

    public String exif;
    public int orient;
    public Bitmap bitmapa;
    public String sciezka;
    public Integer ilosckrokowx;
    public Integer ilosckrokowy;
    public Integer krokx;
    public Integer kroky;
    public Bitmap bitmapaslajd;

    public PlikJPG() {
        sciezka = null;
        bitmapa = null;
        bitmapaslajd = null;
        orient = 0;
        exif = null;
        ilosckrokowx = null;
        ilosckrokowy = null;
        krokx = null;
        kroky = null;

    }

    public void wyczysc() {
        if(bitmapa != null) {
            bitmapa = null;
        }
        if(bitmapaslajd != null) {
            bitmapaslajd = null;
        }
    }

    public void uzupelnijOrient() {
        ExifInterface exifint = new ExifInterface();
        try {
            if(sciezka.startsWith("/")) {
                exifint.readExif(sciezka, ExifInterface.Options.OPTION_ALL);
            } else {
                exifint.readExif(new SmbFileInputStream(new SmbFile(sciezka, AppService.sambaauth)), ExifInterface.Options.OPTION_ALL);
            }
            if(exifint.getTag(ExifInterface.TAG_ORIENTATION) != null) {
                int orientzefix = exifint.getTag(ExifInterface.TAG_ORIENTATION).getValueAsInt(0);
                orient = ExifInterface.getRotationForOrientationValue((short) orientzefix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uzupelnijExif() {
        if(exif == null) {
            ExifInterface exifint = new ExifInterface();
            exif = new File(sciezka).getName();
            exif = exif + " " + bitmapa.getWidth() + "x" + bitmapa.getHeight();
            try {
                if(sciezka.startsWith("/")) {
                    exifint.readExif(sciezka, ExifInterface.Options.OPTION_ALL);
                } else {
                    exifint.readExif(new SmbFileInputStream(new SmbFile(sciezka, AppService.sambaauth)), ExifInterface.Options.OPTION_ALL);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uzupelnijKroki() {
        if(WatekRysuj.czyOdwrocic(orient)) {
            if(bitmapa.getWidth() % 300 == 0) {
                ilosckrokowy = bitmapa.getWidth() / 2 / 300;
            } else {
                ilosckrokowy = bitmapa.getWidth() / 2 / 300 + 1;
            }
            if(bitmapa.getHeight() % 300 == 0) {
                ilosckrokowx = bitmapa.getHeight() / 2 / 300;
            } else {
                ilosckrokowx = bitmapa.getHeight() / 2 / 300 + 1;
            }
        } else {
            if(bitmapa.getWidth() % 300 == 0) {
                ilosckrokowx = bitmapa.getWidth() / 2 / 300;
            } else {
                ilosckrokowx = bitmapa.getWidth() / 2 / 300 + 1;
            }
            if(bitmapa.getHeight() % 300 == 0) {
                ilosckrokowy = bitmapa.getHeight() / 2 / 300;
            } else {
                ilosckrokowy = bitmapa.getHeight() / 2 / 300 + 1;
            }
        }
        ilosckrokowx = 10;
        ilosckrokowy = 10;
    }

    public boolean zaladuj() {
        try {
            uzupelnijOrient();
            PlikLogu.zapiszDoLogu("Otwieram: " + sciezka);
            long start = System.currentTimeMillis();
            if(sciezka.startsWith("/")) {
                bitmapa = BitmapFactory.decodeFile(sciezka);
            } else {
                bitmapa = BitmapFactory.decodeStream(new SmbFileInputStream(new SmbFile(sciezka, AppService.sambaauth)));
            }
            long czas = System.currentTimeMillis() - start;
            PlikLogu.zapiszDoLogu("Wczytalem: " + sciezka + " Czas: " + czas);
            uzupelnijExif();
            uzupelnijKroki();
            return true;
        } catch (OutOfMemoryError error) {
            PlikLogu.zapiszDoLogu("Blad wczytywania: " + sciezka);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
