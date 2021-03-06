package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

@SuppressWarnings("PointlessBooleanExpression")
public class WatekMiniatury extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean zajety;
    public volatile long odswiezminiatury;
    public volatile long odswiezylemminiatury;
    public volatile boolean przerwij;

    public WatekMiniatury() {
        zakoncz = false;
        odswiezminiatury = 0;
        odswiezylemminiatury = 0;
        zajety = false;
    }

    public static void przerwijMiniatury() {
        AppService.watekminiatury.przerwij = true;
    }

    private int pobierzOrient(it.sephiroth.android.library.exif2.ExifInterface exiflib) {
        int orient = 0;
        if(exiflib.getTag(it.sephiroth.android.library.exif2.ExifInterface.TAG_ORIENTATION) != null) {
            orient = exiflib.getTag(it.sephiroth.android.library.exif2.ExifInterface.TAG_ORIENTATION).getValueAsInt(0);
        }
        return orient;
    }

    private Bitmap wczytajMiniatureIOdwroc(int orient, byte[] imageData) {
        if(orient != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(it.sephiroth.android.library.exif2.ExifInterface.getRotationForOrientationValue((short) orient));
            Bitmap thumbtmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            return Bitmap.createBitmap(thumbtmp, 0, 0, thumbtmp.getWidth(), thumbtmp.getHeight(), matrix, false);
        } else {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
    }

    private Bitmap stworzBitmapeDoWyswietlenia() {
        Bitmap thumb = Bitmap.createBitmap(MainActivity.rozdzielczosc.x / 10, MainActivity.rozdzielczosc.x / 10, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas(thumb);
        cc.drawColor(Color.TRANSPARENT);
        return thumb;
    }

    private void naniesMiniatureNaBitmape(Bitmap miniaturazexif, Canvas cc) {
        if(miniaturazexif.getWidth() >= miniaturazexif.getHeight()) {
            int nw = MainActivity.rozdzielczosc.x / 10 - 10;
            int nh = (miniaturazexif.getHeight() * nw) / miniaturazexif.getWidth();
            int start = 5 + nw - nh;
            cc.drawBitmap(miniaturazexif, null, new Rect(5, start, MainActivity.rozdzielczosc.x / 10 - 5, start + nh), null);
        } else {
            int nh = MainActivity.rozdzielczosc.x / 10 - 10;
            int nw = (miniaturazexif.getWidth() * nh) / miniaturazexif.getHeight();
            int start = (5 + nh - nw) / 2;
            cc.drawBitmap(miniaturazexif, null, new Rect(start, 5, start + nw, MainActivity.rozdzielczosc.x / 10 - 5), null);
        }
    }

    private void rysujBladMiniatury(ImageView miniatura) {
        Bitmap bitmapaminiatury = Bitmap.createBitmap(MainActivity.rozdzielczosc.x / 10, MainActivity.rozdzielczosc.x / 10, Bitmap.Config.ARGB_8888);
        Canvas canvasminiatury = new Canvas(bitmapaminiatury);
        canvasminiatury.drawBitmap(Bitmapy.jpgbitmapbrakminiatury, null, new Rect(0, 0, MainActivity.rozdzielczosc.x / 10, MainActivity.rozdzielczosc.x / 10), null);
        MainActivity.ustawBitmapeWImageView(miniatura, bitmapaminiatury);
    }

    private void ustawMiniature(ImageView miniatura) {
        try {
            ExifInterface exifjava = new ExifInterface(miniatura.getTag().toString());
            it.sephiroth.android.library.exif2.ExifInterface exiflib = new it.sephiroth.android.library.exif2.ExifInterface();
            exiflib.readExif(miniatura.getTag().toString(), it.sephiroth.android.library.exif2.ExifInterface.Options.OPTION_ALL);
            byte[] imageData = exifjava.getThumbnail();
            if (imageData != null) {
                int orient = pobierzOrient(exiflib);
                Bitmap miniaturazexif = wczytajMiniatureIOdwroc(orient, imageData);
                Bitmap thumb = stworzBitmapeDoWyswietlenia();
                Canvas cc = new Canvas(thumb);
                naniesMiniatureNaBitmape(miniaturazexif, cc);
                if(miniaturazexif != null) {
                    MainActivity.ustawBitmapeWImageView(miniatura, thumb);
                } else {
                    rysujBladMiniatury(miniatura);
                }
            } else {
                rysujBladMiniatury(miniatura);
            }
        } catch (IOException e) {
            rysujBladMiniatury(miniatura);
            e.printStackTrace();
        }
    }

    private void zaktualizujMiniatury() {
        try {
            int iloscrzedow = Widoki.layoutscrollviewminiatury.getChildCount();
            for (int i = 0; i < iloscrzedow; i++) {
                LinearLayout rzadminiatury = (LinearLayout) Widoki.layoutscrollviewminiatury.getChildAt(i);
                int iloscminiatur = rzadminiatury.getChildCount();
                for (int j = 0; j < iloscminiatur; j++) {
                    LinearLayout miniaturalayout = (LinearLayout) rzadminiatury.getChildAt(j);
                    ImageView miniatura = (ImageView) miniaturalayout.getChildAt(0);
                    if (((String) (miniatura.getTag())).toLowerCase().endsWith(".jpg") == true) {
                        if (przerwij == false) {
                            ustawMiniature(miniatura);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(50);
            if(odswiezminiatury > odswiezylemminiatury) {
                Rozne.czekaj(100);
                odswiezylemminiatury = odswiezminiatury;
                zajety = true;
                przerwij = false;
                MainActivity.widocznoscPostepuOpcje(View.VISIBLE, Color.GREEN);
                zaktualizujMiniatury();
                MainActivity.widocznoscPostepuOpcje(View.INVISIBLE, Color.GREEN);
                zajety = false;
            }
        }
    }
}
