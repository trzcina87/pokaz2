package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

@SuppressWarnings("PointlessBooleanExpression")
public class WatekRysuj extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean odswiez;
    private Paint paintexifwhite;
    private Paint paintexifred;
    private Paint paintexifblack;
    private Paint paintbrakplikow;
    private Paint paintobwodmin;
    private Paint paintwidocznyobszar;
    private Paint paintfilter;

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
        paintobwodmin = new Paint();
        paintobwodmin.setColor(Color.WHITE);
        paintobwodmin.setStrokeWidth(5);
        paintwidocznyobszar = new Paint();
        paintwidocznyobszar.setColor(Color.RED);
        paintwidocznyobszar.setStrokeWidth(5);
        paintwidocznyobszar.setStyle(Paint.Style.STROKE);
        paintfilter = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    public static boolean czyPionowyObraz(int dlugosc, int wysokosc) {
        double ratio = (double)dlugosc / (double)wysokosc;
        if(ratio < MainActivity.ratio) {
            return true;
        } else {
            return false;
        }
    }

    private void naniesInfo(Canvas canvas, int ktoryplik) {
        String exif = AppService.watekwczytaj.pliki[ktoryplik].exif;
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
            canvas.drawRect(0, MainActivity.rozdzielczosc.y - rozmiarytekstu.height() - 1, rozmiarytekstu.width() + 10, MainActivity.rozdzielczosc.y, paintexifblack);
            canvas.drawText(exif, 2, MainActivity.rozdzielczosc.y - 2, paintexifwhite);
        }
    }

    private void zwolnijCanvas(Canvas canvas) {
        if(canvas != null) {
            MainActivity.surface.surfaceholder.unlockCanvasAndPost(canvas);
        }
    }

    public static boolean czyOdwrocic(int kat) {
        if((kat == 90) || (kat == 270)) {
            return true;
        }
        return false;
    }

    private int poprawLewo(int lewo, int wymiar) {
        if(wymiar < MainActivity.rozdzielczosc.x) {
            return (MainActivity.rozdzielczosc.x - wymiar) / 2;
        }
        if(lewo > 0) {
            lewo = 0;
        }
        if(lewo - MainActivity.rozdzielczosc.x < -wymiar) {
            lewo = -wymiar + MainActivity.rozdzielczosc.x;
        }
        return lewo;
    }

    private int poprawGora(int gora, int wymiar) {
        if(wymiar < MainActivity.rozdzielczosc.y) {
            return (MainActivity.rozdzielczosc.y - wymiar) / 2;
        }
        if(gora > 0) {
            gora = 0;
        }
        if(gora - MainActivity.rozdzielczosc.y < -wymiar) {
            gora = -wymiar + MainActivity.rozdzielczosc.y;
        }
        return gora;
    }

    private void obrocMacierz(Macierz matrix, int kat, int dlugosc, int wysokosc) {
        if(kat != 0) {
            matrix.postRotate(kat);
        }
        if(kat == 270) {
            matrix.postTranslate(0, wysokosc);
        }
        if(kat == 90) {
            matrix.postTranslate(dlugosc, 0);
        }
    }

    private Macierz stworzMacierz(int kat, int dlugosc, int wysokosc) {
        Macierz matrix = new Macierz();
        obrocMacierz(matrix, kat, dlugosc, wysokosc);
        int lewo = 0;
        int gora = 0;
        float powiekszenie = MainActivity.powiekszenie / (float)100;
        if(MainActivity.powiekszenie == 0) {
            if(czyPionowyObraz(dlugosc, wysokosc)) {
                powiekszenie = MainActivity.rozdzielczosc.y / (float) wysokosc;
            } else {
                powiekszenie = MainActivity.rozdzielczosc.x / (float) dlugosc;
            }
        }
        if(MainActivity.powiekszenie == 100) {
            lewo = (MainActivity.rozdzielczosc.x - dlugosc) / 2;
            gora = (MainActivity.rozdzielczosc.y - wysokosc) / 2;
            lewo = lewo - MainActivity.xprzesun * ((dlugosc - MainActivity.rozdzielczosc.x) / 2 / 10);
            gora = gora - MainActivity.yprzesun * ((wysokosc - MainActivity.rozdzielczosc.y) / 2 / 10);
            lewo = poprawLewo(lewo, (int) (powiekszenie * dlugosc));
            gora = poprawGora(gora, (int) (powiekszenie * wysokosc));
        } else {
            matrix.postScale(powiekszenie, powiekszenie);
            matrix.powiekszenie = powiekszenie;
            lewo = (int) ((MainActivity.rozdzielczosc.x - dlugosc * powiekszenie) / 2);
            gora = (int) ((MainActivity.rozdzielczosc.y - wysokosc * powiekszenie) / 2);
            lewo = (int) (lewo - MainActivity.xprzesun * ((dlugosc * powiekszenie - MainActivity.rozdzielczosc.x) / 2 / 10));
            gora = (int) (gora - MainActivity.yprzesun * ((wysokosc * powiekszenie - MainActivity.rozdzielczosc.y) / 2 / 10));
            lewo = poprawLewo(lewo, (int) (powiekszenie * dlugosc));
            gora = poprawGora(gora, (int) (powiekszenie * wysokosc));
        }
        matrix.postTranslate(lewo, gora);
        matrix.lewo = lewo;
        matrix.gora = gora;
        return matrix;
    }

    private float znajdzSkaleMiniatury(int dlugosc, int wysokosc) {
        float powiekszenie;
        int max = Math.max(wysokosc, dlugosc);
        powiekszenie = 300 / (float)max;
        return powiekszenie;
    }

    private void rysujObwod(Canvas canvas, int dlugosc, int wysokosc, float powiekszenie) {
        int lewoobwodu = (int) (MainActivity.rozdzielczosc.x - powiekszenie * dlugosc - 20 - 5);
        int goraobwodu = (int) (MainActivity.rozdzielczosc.y - powiekszenie * wysokosc - 20 - 5);
        canvas.drawRect(lewoobwodu, goraobwodu, MainActivity.rozdzielczosc.x - 20 + 5, MainActivity.rozdzielczosc.y - 20 + 5, paintobwodmin);
        String skala = MainActivity.powiekszenie + "%";
        Rect rozmiarytekstu = new Rect();
        paintexifwhite.getTextBounds(skala, 0, skala.length(), rozmiarytekstu);
        canvas.drawRect(lewoobwodu, goraobwodu - rozmiarytekstu.height() - 1, lewoobwodu + rozmiarytekstu.width() + 6, goraobwodu, paintexifwhite);
        canvas.drawText(skala, lewoobwodu, goraobwodu, paintexifblack);
    }

    private void rysujMiniature(Canvas canvas, Bitmap bitmapa, int dlugosc, int wysokosc, float powiekszenie, int kat) {
        Macierz matrixmin = new Macierz();
        obrocMacierz(matrixmin, kat, dlugosc, wysokosc);
        matrixmin.postScale(powiekszenie, powiekszenie);
        matrixmin.postTranslate(MainActivity.rozdzielczosc.x - powiekszenie * dlugosc - 20, MainActivity.rozdzielczosc.y - powiekszenie * wysokosc - 20);
        canvas.drawBitmap(bitmapa, matrixmin, paintfilter);
    }

    private void rysujWidocznyObszar(Canvas canvas, int dlugosc, int wysokosc, float powiekszenie, Macierz matrix) {
        int dlugoscminiatury = (int) (powiekszenie * dlugosc);
        int wysokoscminiatury = (int) (powiekszenie * wysokosc);
        float powiekszenieobrazu = MainActivity.powiekszenie / (float)100;
        int dlugoscobrazu = (int) (powiekszenieobrazu * dlugosc);
        int wysokoscobrazu = (int) (powiekszenieobrazu * wysokosc);
        float stosunekdlugosciobrazu = MainActivity.rozdzielczosc.x / (float)dlugoscobrazu;
        float stosunekwysokosciobrazu = MainActivity.rozdzielczosc.y / (float)wysokoscobrazu;
        int dlugoscobszaru = (int) (stosunekdlugosciobrazu * dlugoscminiatury);
        int wysokoscobszaru = (int) (stosunekwysokosciobrazu * wysokoscminiatury);
        if(dlugoscobszaru > dlugoscminiatury) {
            dlugoscobszaru = dlugoscminiatury;
        }
        if(wysokoscobszaru > wysokoscminiatury) {
            wysokoscobszaru = wysokoscminiatury;
        }
        int lewoobwodu = (int) (MainActivity.rozdzielczosc.x - powiekszenie * dlugosc - 20);
        int goraobwodu = (int) (MainActivity.rozdzielczosc.y - powiekszenie * wysokosc - 20);
        int lewoobrazu = matrix.lewo;
        int goraobrazu = matrix.gora;
        float lewoobrazustosunek = Math.abs(lewoobrazu / ((float)dlugosc * (MainActivity.powiekszenie / (float)100)));
        float goraobrazustosunek = Math.abs(goraobrazu / ((float)wysokosc * (MainActivity.powiekszenie / (float)100)));
        if(lewoobrazu > 0) {
            lewoobrazustosunek = 0;
        }
        if(goraobrazu > 0) {
            goraobrazustosunek = 0;
        }
        lewoobwodu = (int) (lewoobwodu + lewoobrazustosunek * dlugoscminiatury);
        goraobwodu = (int) (goraobwodu + goraobrazustosunek * wysokoscminiatury);
        canvas.drawRect(lewoobwodu, goraobwodu, lewoobwodu + dlugoscobszaru, goraobwodu + wysokoscobszaru, paintwidocznyobszar);
    }

    private void rysujPodglad(Canvas canvas, Bitmap bitmapa, Macierz matrix, int dlugosc, int wysokosc, int kat) {
        float powiekszenie = znajdzSkaleMiniatury(dlugosc, wysokosc);
        rysujObwod(canvas, dlugosc, wysokosc, powiekszenie);
        rysujMiniature(canvas, bitmapa, dlugosc, wysokosc, powiekszenie, kat);
        rysujWidocznyObszar(canvas, dlugosc, wysokosc, powiekszenie, matrix);
    }

    private void uzupelnijBitmapeSlajd(int ktoryplik, int kat, int dlugosc, int wysokosc, Bitmap bitmap) {
        float powiekszenie;
        if(czyPionowyObraz(dlugosc, wysokosc)) {
            powiekszenie = (MainActivity.rozdzielczosc.y + 200) / (float) wysokosc;
        } else {
            powiekszenie = (MainActivity.rozdzielczosc.x + 200) /(float) dlugosc;
        }
        Macierz matrix = new Macierz();
        obrocMacierz(matrix, kat, dlugosc, wysokosc);
        matrix.postScale(powiekszenie, powiekszenie);
        AppService.watekwczytaj.pliki[ktoryplik].bitmapaslajd = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    public static boolean czyAnimowac() {
        if((MainActivity.animacja == true) && (MainActivity.powiekszenie == 0) && (OpcjeProgramu.pokazslidow == 1)) {
            return true;
        } else {
            return false;
        }
    }

    /*private void animujBitmape(final Bitmap bitmapaslajd) {
        int dlugosc = bitmapaslajd.getWidth();
        int wysokosc = bitmapaslajd.getHeight();
        int lewo = (MainActivity.rozdzielczosc.x - dlugosc) / 2;
        int gora = (MainActivity.rozdzielczosc.y - wysokosc) / 2;
        lewo = poprawLewo(lewo, dlugosc) + AppService.watekanimacja.x;
        gora = poprawGora(gora, wysokosc) + AppService.watekanimacja.y;
        Canvas canvas = null;
        try {
            canvas = MainActivity.surface.surfaceholder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bitmapaslajd, lewo, gora, null);
            MainActivity.surface.surfaceholder.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            odswiez = true;
            zwolnijCanvas(canvas);
        }
    }*/

    private void animujBitmape(final Bitmap bitmapaslajd) {
        int dlugosc = bitmapaslajd.getWidth();
        int wysokosc = bitmapaslajd.getHeight();
        int lewo = (MainActivity.rozdzielczosc.x - dlugosc) / 2;
        int gora = (MainActivity.rozdzielczosc.y - wysokosc) / 2;
        lewo = poprawLewo(lewo, dlugosc) + AppService.service.watekanimacja.x;
        gora = poprawGora(gora, wysokosc) + AppService.service.watekanimacja.y;
        MainActivity.uzupelnijParametryAbsoluteLayout(bitmapaslajd.getWidth(), bitmapaslajd.getHeight(), lewo, gora);
    }

    private void rysujObraz(final int ktoryplik) {
        Canvas canvas = null;
        try {
            Bitmap bitmapa = AppService.watekwczytaj.pliki[ktoryplik].bitmapa;
            Bitmap bitmapaslajd = AppService.watekwczytaj.pliki[ktoryplik].bitmapaslajd;
            int kat = AppService.watekwczytaj.pliki[ktoryplik].orient;
            String sciezka = AppService.service.watekwczytaj.pliki[ktoryplik].sciezka;
            if(bitmapa != null) {
                int dlugosc = bitmapa.getWidth();
                int wysokosc = bitmapa.getHeight();
                if(czyOdwrocic(kat)) {
                    dlugosc = bitmapa.getHeight();
                    wysokosc = bitmapa.getWidth();
                }
                if(czyAnimowac()) {
                    if(bitmapaslajd == null) {
                        uzupelnijBitmapeSlajd(ktoryplik, kat, dlugosc, wysokosc, bitmapa);
                        bitmapaslajd = AppService.watekwczytaj.pliki[ktoryplik].bitmapaslajd;
                    }
                    if(! sciezka.equals(Widoki.imageviewanimacja.getTag())) {
                        MainActivity.uzupelnijTagIBitmapeDoAnimacji(sciezka, bitmapaslajd);
                    }
                    animujBitmape(bitmapaslajd);
                } else {
                    Macierz macierz = stworzMacierz(kat, dlugosc, wysokosc);
                    canvas = MainActivity.surface.surfaceholder.lockCanvas();
                    canvas.drawColor(Color.BLACK);
                    if (MainActivity.powiekszenie == 100) {
                        canvas.drawBitmap(bitmapa, macierz, null);
                    } else {
                        canvas.drawBitmap(bitmapa, macierz, paintfilter);
                    }
                    if (MainActivity.powiekszenie != 0) {
                        rysujPodglad(canvas, bitmapa, macierz, dlugosc, wysokosc, kat);
                    }
                    naniesInfo(canvas, ktoryplik);
                    MainActivity.surface.surfaceholder.unlockCanvasAndPost(canvas);
                }
                if(AppService.watekodlicz.ostatniczas > System.currentTimeMillis()) {
                    AppService.watekodlicz.ostatniczas = System.currentTimeMillis();
                }
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
            canvas = MainActivity.surface.surfaceholder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawText("BRAK PLIKÓW", 0, MainActivity.rozdzielczosc.y - 2, paintbrakplikow);
            MainActivity.surface.surfaceholder.unlockCanvasAndPost(canvas);
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
            if(AppService.watekwczytaj.iloscplikow > 0) {
                int ktoryplikrysowac = MainActivity.ktoryplik;
                if(odswiez) {
                    odswiez = false;
                    rysujObraz(ktoryplikrysowac);
                }
            } else {
                rysujBrakPlikow();
            }
        }
    }
}