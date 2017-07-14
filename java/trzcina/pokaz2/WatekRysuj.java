package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class WatekRysuj extends Thread {

    public volatile boolean zakoncz;
    public int ktorynarysowany;
    public volatile boolean odswiez;
    private Paint paintexifwhite;
    private Paint paintexifred;
    private Paint paintexifblack;

    public WatekRysuj() {
        zakoncz = false;
        ktorynarysowany = -1;
        odswiez = false;
        inicjujPainty();
    }

    private void inicjujPainty() {
        paintexifwhite = new Paint();
        paintexifwhite.setColor(Color.WHITE);
        paintexifwhite.setTextSize(30);
        paintexifwhite.setAntiAlias(true);
        paintexifred = new Paint();
        paintexifred.setColor(Color.RED);
        paintexifred.setTextSize(30);
        paintexifred.setAntiAlias(true);
        paintexifblack = new Paint();
        paintexifblack.setColor(Color.BLACK);
        paintexifblack.setTextSize(30);
        paintexifblack.setAntiAlias(true);
    }

    private int obliczDlugoscProporcjonalnie(int dlugosc, int wysokosc, int docelowawysokosc) {
        double ratio = (double)dlugosc / (double)wysokosc;
        return (int) (ratio * (float)docelowawysokosc);
    }

    private Rect caloscBitmapyRect(Bitmap bitmapa) {
        return new Rect(0, 0, bitmapa.getWidth(), bitmapa.getHeight());
    }

    private Rect wysrodkujRect(int dlugosc, int wysokosc) {
        Rect rect = new Rect();
        rect.left = MainActivity.rozdzielczosc.x / 2 - dlugosc / 2;
        rect.top = MainActivity.rozdzielczosc.y / 2 - wysokosc / 2;
        rect.right = rect.left + dlugosc;
        rect.bottom = rect.top + wysokosc;
        return rect;
    }

    private void naniesInfo(Canvas canvas, int ktoryplik) {
        String exif = AppService.service.watekwczytaj.pliki[ktoryplik].exif;
        if (MainActivity.poziominfo == 1) {
            canvas.drawText(exif, 2, MainActivity.rozdzielczosc.y - 2, paintexifwhite);
        }
        if (MainActivity.poziominfo == 2) {
            canvas.drawText(exif, 2, MainActivity.rozdzielczosc.y - 2, paintexifred);
        }
        if (MainActivity.poziominfo == 3) {
            canvas.drawText(exif, 2, MainActivity.rozdzielczosc.y - 2, paintexifblack);
        }
        if (MainActivity.poziominfo == 4) {
            Rect rozmiarytekstu = new Rect();
            paintexifwhite.getTextBounds(exif, 0, exif.length(), rozmiarytekstu);
            canvas.drawRect(0, MainActivity.rozdzielczosc.y - rozmiarytekstu.height(), rozmiarytekstu.width() + 5, MainActivity.rozdzielczosc.y, paintexifblack);
            canvas.drawText(exif, 2, MainActivity.rozdzielczosc.y - 2, paintexifwhite);
        }
    }

    private void zwolnijCanvas(Canvas canvas) {
        if(canvas != null) {
            MainActivity.surface.surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void rysujObraz100(int ktoryplik) {
        Canvas canvas = null;
        try {
            Bitmap bitmapa = AppService.service.watekwczytaj.pliki[ktoryplik].bitmapa;
            if(bitmapa != null) {
                canvas = MainActivity.surface.surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(bitmapa, caloscBitmapyRect(bitmapa), wysrodkujRect(bitmapa.getWidth(), bitmapa.getHeight()), null);
                naniesInfo(canvas, ktoryplik);
                MainActivity.surface.surfaceHolder.unlockCanvasAndPost(canvas);
                ktorynarysowany = ktoryplik;
            }
        } catch (Exception e) {
            e.printStackTrace();
            odswiez = true;
            zwolnijCanvas(canvas);
        }
    }

    private void rysujObrazPelnyEkran(int ktoryplik) {
        Canvas canvas = null;
        try {
            Bitmap bitmapa = AppService.service.watekwczytaj.pliki[ktoryplik].bitmapa;
            if(bitmapa != null) {
                int dlugosc = bitmapa.getWidth();
                int wysokosc = bitmapa.getHeight();
                int docelowawysokosc = MainActivity.rozdzielczosc.y;
                int docelowadlugosc = obliczDlugoscProporcjonalnie(dlugosc, wysokosc, docelowawysokosc);
                canvas = MainActivity.surface.surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(bitmapa, caloscBitmapyRect(bitmapa), wysrodkujRect(docelowadlugosc, docelowawysokosc), null);
                naniesInfo(canvas, ktoryplik);
                MainActivity.surface.surfaceHolder.unlockCanvasAndPost(canvas);
                ktorynarysowany = ktoryplik;
            }
        } catch (Exception e) {
            e.printStackTrace();
            odswiez = true;
            zwolnijCanvas(canvas);
        }
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(AppService.service.watekwczytaj.iloscplikow > 0) {
                int ktoryplikrysowac = MainActivity.ktoryplik;
                if((ktorynarysowany != ktoryplikrysowac) || (odswiez)) {
                    odswiez = false;
                    ktorynarysowany = -1;
                    if(MainActivity.powiekszenie == 0) {
                        rysujObrazPelnyEkran(ktoryplikrysowac);
                    }
                    if(MainActivity.powiekszenie == 100) {
                        rysujObraz100(ktoryplikrysowac);
                    }
                }
            }
        }
    }
}
