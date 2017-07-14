package trzcina.pokaz2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static MainActivity activity;
    public static Point rozdzielczosc;
    public static MainSurface surface;

    public static int poziominfo;
    public static int powiekszenie;
    public static int xprzesun;
    public static int yprzesun;
    public static String zacznijodpliku;
    public static boolean jestemwfolderach;
    public static int ktoryplik;
    public static boolean trybopcji;
    public static String folderroboczy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zainicjujZmienne();
        ustawEkran();
        ustawSurface();
        wczytajOpcje();
        ustawWidoki();
        wysartujService();
    }

    private void zainicjujZmienne() {
        activity = this;
        poziominfo = 0;
        powiekszenie = 0;
        xprzesun = 0;
        yprzesun = 0;
        zacznijodpliku = null;
        jestemwfolderach = false;
        trybopcji = false;
        ktoryplik = 0;
    }

    private void ustawEkran() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        Display display = getWindowManager().getDefaultDisplay();
        rozdzielczosc = new Point();
        display.getSize(rozdzielczosc);
        if (rozdzielczosc.y == 1008) {
            rozdzielczosc.y = 1080;
        }
        setContentView(R.layout.activity_main);
    }

    private void ustawSurface() {
        surface = new MainSurface(this);
        RelativeLayout layout = (RelativeLayout) (findViewById(R.id.activity_main));
        layout.addView(surface, 0);
    }

    private void wczytajOpcje() {
        OpcjeProgramu.wczytajOpcje();
        folderroboczy = OpcjeProgramu.folder;
    }

    private void ustawWidoki() {
        Widoki.znajdzWidoki();
        Widoki.przypiszAkcjeDoWidokow();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int key = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            if (Arrays.asList(Kody.ENTERY).contains(key)) {
                if (!trybopcji) {
                    Widoki.activitylayout.addView(Widoki.opcjelayout);
                    Widoki.wypelnijOpcje();
                    AppService.service.wateklistujpliki.focusnawstecz = false;
                    AppService.service.wateklistujpliki.odswiezfoldery = true;
                    trybopcji = true;
                    return true;
                }
            }
            if (Arrays.asList(Kody.LEWO).contains(key)) {
                if((MainActivity.powiekszenie == 0) && (trybopcji == false)){
                    int iloscplikow = AppService.service.watekwczytaj.iloscplikow;
                    if (iloscplikow > 0) {
                        ktoryplik = ktoryplik - 1;
                        if (ktoryplik < 0) {
                            ktoryplik = iloscplikow - 1;
                        }
                    }
                    AppService.service.watekrysuj.odswiez = true;
                    return true;
                }
            }
            if (Arrays.asList(Kody.PRAWO).contains(key)) {
                if((MainActivity.powiekszenie == 0) && (trybopcji == false)){
                    int iloscplikow = AppService.service.watekwczytaj.iloscplikow;
                    if (iloscplikow > 0) {
                        ktoryplik = ktoryplik + 1;
                        if (ktoryplik >= iloscplikow) {
                            ktoryplik = 0;
                        }
                    }
                    AppService.service.watekrysuj.odswiez = true;
                    return true;
                }
            }
            if (Arrays.asList(Kody.STO).contains(key)) {
                MainActivity.powiekszenie = 100;
                AppService.service.watekrysuj.odswiez = true;
            }
            if (Arrays.asList(Kody.ORG).contains(key)) {
                MainActivity.powiekszenie = 0;
                AppService.service.watekrysuj.odswiez = true;
            }
            if (Arrays.asList(Kody.INFO).contains(key)) {
                MainActivity.poziominfo = (MainActivity.poziominfo + 1) % 5;
                AppService.service.watekrysuj.odswiez = true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (trybopcji) {
            if(jestemwfolderach) {
                if (AppService.service.wateklistujpliki.zajety == true) {
                    MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
                } else {
                    String wstecz;
                    if (new File(MainActivity.folderroboczy).getParentFile() != null) {
                        wstecz = new File(MainActivity.folderroboczy).getParentFile().getAbsolutePath() + "/";
                    } else {
                        wstecz = "/";
                    }
                    folderroboczy = wstecz;
                    AppService.service.wateklistujpliki.odswiezfoldery = true;
                }
            } else {
                if (AppService.service.wateklistujpliki.zajety == true) {
                    MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
                } else {
                    Widoki.activitylayout.removeView(Widoki.opcjelayout);
                    trybopcji = false;
                }
            }
        } else {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Wyjscie z aplikacji?").setMessage("Czy na pewno chcesz zakonczyc?").setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("Nie", null).show();
        }
    }

    private void wysartujService() {
        startService(new Intent(this, AppService.class));
    }

    public static void widocznoscPostepuOpcje(final int widocznosc, final int kolor) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Widoki.progressbaropcje.setVisibility(widocznosc);
                Widoki.progressbaropcje.getIndeterminateDrawable().setColorFilter(kolor, android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        });
    }

    public static void dodajViewDoView(final LinearLayout rodzic, final View dziecko) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rodzic.addView(dziecko);
            }
        });
    }

    public static void wyczyscScroolView(final LinearLayout layout) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
            }
        });
    }

    public static void ustawBitmapeWImageView(final ImageView imageview, final Bitmap bitmap) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageview.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AppService.class));
    }

    public static void wyswietlToast(final String tresc) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.activity.getApplicationContext(), tresc, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
