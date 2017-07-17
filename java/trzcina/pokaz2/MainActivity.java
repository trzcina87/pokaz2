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
import android.util.Log;
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
    public static double ratio;
    public static MainSurface surface;

    public static int poziominfo;
    public static int powiekszenie;
    public static int xprzesun;
    public static int yprzesun;
    public static String zacznijodpliku;
    public static int ktoryplik;
    public static boolean trybopcji;
    public static String folderroboczy;

    private volatile long ostatniapauza;

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
        trybopcji = false;
        ktoryplik = 0;
        ostatniapauza = 0;
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
        ratio = (double)rozdzielczosc.x / (double)rozdzielczosc.y;
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

    public void resetujPrzesuj() {
        xprzesun = 0;
        yprzesun = 0;
    }

    private boolean obsluzEnter(int key) {
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
        return false;
    }

    private boolean obsluzLewo(int key) {
        if (Arrays.asList(Kody.LEWO).contains(key)) {
            if(trybopcji == false) {
                if (MainActivity.powiekszenie == 0) {
                    int iloscplikow = AppService.service.watekwczytaj.iloscplikow;
                    if (iloscplikow > 0) {
                        ktoryplik = ktoryplik - 1;
                        if (ktoryplik < 0) {
                            ktoryplik = iloscplikow - 1;
                        }
                        resetujPrzesuj();
                    }
                } else {
                    try {
                        int granica = AppService.service.watekwczytaj.pliki[ktoryplik].ilosckrokowx;
                        xprzesun = xprzesun - 1;
                        if (xprzesun < -granica) {
                            xprzesun = -granica;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppService.service.watekrysuj.odswiez = true;
                return true;
            }
        }
        return false;
    }

    private boolean obsluzPrawo(int key) {
        if (Arrays.asList(Kody.PRAWO).contains(key)) {
            if(trybopcji == false) {
                if (MainActivity.powiekszenie == 0) {
                    int iloscplikow = AppService.service.watekwczytaj.iloscplikow;
                    if (iloscplikow > 0) {
                        ktoryplik = ktoryplik + 1;
                        if (ktoryplik >= iloscplikow) {
                            ktoryplik = 0;
                        }
                        resetujPrzesuj();
                    }
                } else {
                    try {
                        int granica = AppService.service.watekwczytaj.pliki[ktoryplik].ilosckrokowx;
                        xprzesun = xprzesun + 1;
                        if (xprzesun > granica) {
                            xprzesun = granica;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppService.service.watekrysuj.odswiez = true;
                return true;
            }
        }
        return false;
    }

    private boolean obsluzGora(int key) {
        if (Arrays.asList(Kody.GORA).contains(key)) {
            if(trybopcji == false) {
                if(MainActivity.powiekszenie != 0) {
                    try {
                        int granica = AppService.service.watekwczytaj.pliki[ktoryplik].ilosckrokowy;
                        yprzesun = yprzesun - 1;
                        if (yprzesun < -granica) {
                            yprzesun = -granica;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppService.service.watekrysuj.odswiez = true;
                return true;
            }
        }
        return false;
    }

    private boolean obsluzDol(int key) {
        if (Arrays.asList(Kody.DOL).contains(key)) {
            if(trybopcji == false) {
                if(MainActivity.powiekszenie != 0) {
                    try {
                        int granica = AppService.service.watekwczytaj.pliki[ktoryplik].ilosckrokowy;
                        yprzesun = yprzesun + 1;
                        if (yprzesun > granica) {
                            yprzesun = granica;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppService.service.watekrysuj.odswiez = true;
                return true;
            }
        }
        return false;
    }

    private boolean obsluzSto(int key) {
        if (Arrays.asList(Kody.STO).contains(key)) {
            MainActivity.powiekszenie = 100;
            AppService.service.watekrysuj.odswiez = true;
            return true;
        }
        return false;
    }

    private boolean obsluzOrg(int key) {
        if (Arrays.asList(Kody.ORG).contains(key)) {
            MainActivity.powiekszenie = 0;
            AppService.service.watekrysuj.odswiez = true;
            return true;
        }
        return false;
    }

    private boolean obsluzInfo(int key) {
        if (Arrays.asList(Kody.INFO).contains(key)) {
            MainActivity.poziominfo = (MainActivity.poziominfo + 1) % 5;
            AppService.service.watekrysuj.odswiez = true;
            return true;
        }
        return false;
    }

    private boolean obsluzSpacja(int key) {
        if(trybopcji == false) {
            if (Arrays.asList(Kody.SPACJA).contains(key)) {
                if (OpcjeProgramu.pokazslidow == 1) {
                    OpcjeProgramu.pokazslidow = 0;
                    Ustawienia.zapiszUstawienie("pokazslidow", 0);
                    ustawPlayPauza();
                } else {
                    OpcjeProgramu.pokazslidow = 1;
                    Ustawienia.zapiszUstawienie("pokazslidow", 1);
                    ustawPlayPauza();
                }
                return true;
            }
        }
        return false;
    }

    private boolean obsluzPowieksz(int key) {
        if(trybopcji == false) {
            if (Arrays.asList(Kody.POWIEKSZ).contains(key)) {
                if(MainActivity.powiekszenie != 0) {
                    MainActivity.powiekszenie = MainActivity.powiekszenie + 10;
                    AppService.service.watekrysuj.odswiez = true;
                }
                return true;
            }
        }
        return false;
    }


    private boolean obsluzPomniejsz(int key) {
        if(trybopcji == false) {
            if (Arrays.asList(Kody.POMNIEJSZ).contains(key)) {
                if((MainActivity.powiekszenie != 0) && (MainActivity.powiekszenie != 10)) {
                    MainActivity.powiekszenie = MainActivity.powiekszenie - 10;
                    AppService.service.watekrysuj.odswiez = true;
                }
                return true;
            }
        }
        return false;
    }

    private void ustawPlayPauza() {
        final long lokalnaostatniapauza = System.currentTimeMillis();
        ostatniapauza = lokalnaostatniapauza;
        if(OpcjeProgramu.pokazslidow == 1) {
            Widoki.imageviewplaypauza.setImageResource(R.mipmap.play);
        } else {
            Widoki.imageviewplaypauza.setImageResource(R.mipmap.pauza);
        }
        Widoki.imageviewplaypauza.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Rozne.czekaj(2000);
                if(ostatniapauza == lokalnaostatniapauza) {
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Widoki.imageviewplaypauza.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int key = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            if(obsluzEnter(key)) {
                return true;
            };
            if(obsluzLewo(key)) {
                return true;
            }
            if(obsluzPrawo(key)) {
                return true;
            }
            if(obsluzGora(key)) {
                return true;
            }
            if(obsluzDol(key)) {
                return true;
            }
            if(obsluzSto(key)) {
                return true;
            }
            if(obsluzOrg(key)) {
                return true;
            }
            if(obsluzInfo(key)) {
                return true;
            }
            if(obsluzSpacja(key)) {
                return true;
            }
            if(obsluzPowieksz(key)) {
                return true;
            }
            if(obsluzPomniejsz(key)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void wyswietlPotwierdzenieZamkniecia() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Wyjscie z aplikacji?").setMessage("Czy na pewno chcesz zakonczyc?").setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("Nie", null).show();
    }

    @Override
    public void onBackPressed() {
        if (trybopcji) {
            if (AppService.service.wateklistujpliki.zajety == true) {
                MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
            } else {
                if(Widoki.layoutscrollviewminiatury.getFocusedChild() != null) {
                    String wstecz;
                    if (new File(MainActivity.folderroboczy).getParentFile() != null) {
                        wstecz = new File(MainActivity.folderroboczy).getParentFile().getAbsolutePath() + "/";
                    } else {
                        wstecz = "/";
                    }
                    folderroboczy = wstecz;
                    WatekMiniatury.przerwijMiniatury();
                    AppService.service.wateklistujpliki.focusnawstecz = true;
                    AppService.service.wateklistujpliki.odswiezfoldery = true;
                } else {
                    WatekMiniatury.przerwijMiniatury();
                    Widoki.activitylayout.removeView(Widoki.opcjelayout);
                    trybopcji = false;
                }
            }
        } else {
            wyswietlPotwierdzenieZamkniecia();
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

    public static void pokazKlepsydre() {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Widoki.imageviewklepsydra.setVisibility(View.VISIBLE);
                Widoki.imageviewklepsydra.bringToFront();
            }
        });
    }

    public static void ukryjKlepsydre() {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Widoki.imageviewklepsydra.setVisibility(View.INVISIBLE);
            }
        });
    }
}
