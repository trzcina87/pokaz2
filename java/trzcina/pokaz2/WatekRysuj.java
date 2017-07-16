package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class WatekRysuj extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean odswiez;
    private Paint paintexifwhite;
    private Paint paintexifred;
    private Paint paintexifblack;
    private Paint paintbrakplikow;

    public WatekRysuj() {
        zakoncz = false;
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
        paintbrakplikow = new Paint();
        paintbrakplikow.setColor(Color.WHITE);
        paintbrakplikow.setTextSize(60);
        paintbrakplikow.setAntiAlias(true);
    }

    private int obliczDlugoscProporcjonalnie(int dlugosc, int wysokosc, int docelowawysokosc) {
        double ratio = (double)dlugosc / (double)wysokosc;
        return (int) (ratio * (float)docelowawysokosc);
    }

    private boolean czyPionowyObraz(int dlugosc, int wysokosc) {
        double ratio = (double)dlugosc / (double)wysokosc;
        if(ratio < MainActivity.ratio) {
            return true;
        } else {
            return false;
        }
    }

    private Rect wysrodkujRect(int dlugosc, int wysokosc) {
        Rect rect = new Rect();
        int roznicawpoziomie = dlugosc - MainActivity.rozdzielczosc.x;
        int roznicawpionie = wysokosc - MainActivity.rozdzielczosc.y;
        int dx = 0;
        int dy = 0;
        if(roznicawpoziomie > 0) {
            dx = (int)(((double)roznicawpoziomie / (double)40) * (double)MainActivity.xprzesun);
        }
        if(roznicawpionie > 0) {
            dy = (int) (((double)roznicawpionie / (double)40) * (double)MainActivity.yprzesun);
        }
        rect.left = MainActivity.rozdzielczosc.x / 2 - dlugosc / 2 - dx;
        rect.top = MainActivity.rozdzielczosc.y / 2 - wysokosc / 2 - dy;
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

    public static boolean czyOdwrocic(int kat) {
        if((kat == 90) || (kat == 270)) {
            return true;
        }
        return false;
    }

    private Matrix stworzMacierz(int kat, int dlugosc, int wysokosc) {
        Matrix matrix = new Matrix();
        if(kat != 0) {
            matrix.postRotate(kat);
        }
        if(kat == 270) {
            matrix.postTranslate(0, wysokosc);
        }
        if(kat == 90) {
            matrix.postTranslate(dlugosc, 0);
        }
        int lewo = 0;
        int gora = 0;
        if(czyPionowyObraz(dlugosc, wysokosc)) {
            if(MainActivity.powiekszenie == 0) {
                matrix.postScale(MainActivity.rozdzielczosc.y / (float) wysokosc, MainActivity.rozdzielczosc.y / (float) wysokosc);
                int docelowadlugosc = obliczDlugoscProporcjonalnie(dlugosc, wysokosc, MainActivity.rozdzielczosc.y);
                lewo = (MainActivity.rozdzielczosc.x - docelowadlugosc) / 2;
            }
            if(MainActivity.powiekszenie == 100) {
                lewo = (MainActivity.rozdzielczosc.x - dlugosc) / 2;
                gora = (MainActivity.rozdzielczosc.y - wysokosc) / 2;
                lewo = lewo - MainActivity.xprzesun * 300;
                gora = gora - MainActivity.yprzesun * 300;
            }
        } else {
            if(MainActivity.powiekszenie == 0) {
                matrix.postScale(MainActivity.rozdzielczosc.x / (float) dlugosc, MainActivity.rozdzielczosc.x / (float) dlugosc);
                int docelowawysokosc = obliczDlugoscProporcjonalnie(wysokosc, dlugosc, MainActivity.rozdzielczosc.x);
                gora = (MainActivity.rozdzielczosc.y - docelowawysokosc) / 2;
            }
            if(MainActivity.powiekszenie == 100) {
                lewo = (MainActivity.rozdzielczosc.x - dlugosc) / 2;
                gora = (MainActivity.rozdzielczosc.y - wysokosc) / 2;
                lewo = lewo - MainActivity.xprzesun * 300;
                gora = gora - MainActivity.yprzesun * 300;
            }
        }
        matrix.postTranslate(lewo, gora);
        return matrix;
    }

    private void rysujObraz(int ktoryplik) {
        Canvas canvas = null;
        try {
            Bitmap bitmapa = AppService.service.watekwczytaj.pliki[ktoryplik].bitmapa;
            int kat = AppService.service.watekwczytaj.pliki[ktoryplik].orient;
            if(bitmapa != null) {
                int dlugosc = bitmapa.getWidth();
                int wysokosc = bitmapa.getHeight();
                if(czyOdwrocic(kat)) {
                    dlugosc = bitmapa.getHeight();
                    wysokosc = bitmapa.getWidth();
                }
                Matrix macierz = stworzMacierz(kat, dlugosc, wysokosc);
                canvas = MainActivity.surface.surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(bitmapa, macierz, null);
                naniesInfo(canvas, ktoryplik);
                MainActivity.surface.surfaceHolder.unlockCanvasAndPost(canvas);
                MainActivity.ukryjKlepsydre();
            } else {
                MainActivity.pokazKlepsydre();
                odswiez = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            odswiez = true;
            zwolnijCanvas(canvas);
        }
    }

    private void rysujBrakPlikow() {
        Canvas canvas = null;
        try {
            canvas = MainActivity.surface.surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawText("BRAK PLIKÓW", 0, MainActivity.rozdzielczosc.y - 2, paintbrakplikow);
            MainActivity.surface.surfaceHolder.unlockCanvasAndPost(canvas);
            MainActivity.ukryjKlepsydre();
        } catch (Exception e) {
            e.printStackTrace();
            zwolnijCanvas(canvas);
            odswiez = true;
        }
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(AppService.service.watekwczytaj.iloscplikow > 0) {
                int ktoryplikrysowac = MainActivity.ktoryplik;
                if(odswiez) {
                    odswiez = false;
                    if(MainActivity.powiekszenie == 0) {
                        rysujObraz(ktoryplikrysowac);
                    }
                    if(MainActivity.powiekszenie == 100) {
                        rysujObraz(ktoryplikrysowac);
                    }
                    AppService.service.watekodlicz.ostatniczas = System.currentTimeMillis();
                }
            } else {
                rysujBrakPlikow();
            }
        }
    }
}