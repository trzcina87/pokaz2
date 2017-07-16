package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static android.view.Gravity.CENTER;

public class WatekListujPliki extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean zajety;
    public volatile boolean odswiezfoldery;
    public volatile boolean focusnawstecz;
    public volatile String montowaniete;

    public WatekListujPliki() {
        zakoncz = false;
        zajety = false;
        odswiezfoldery = false;
        focusnawstecz = false;
        montowaniete = null;
    }

    private static void wyczyscScroolView() {
        MainActivity.wyczyscScroolView(Widoki.layoutscrollviewminiatury);
    }

    public static void sortujPliki(File[] plikiobecne) {
        Arrays.sort(plikiobecne, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if(file.isDirectory() && t1.isDirectory()) {
                    return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
                if(file.isFile() && t1.isFile()) {
                    return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
                if(file.isDirectory() && t1.isFile()) {
                    return -1;
                }
                if(file.isFile() && t1.isDirectory()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private static LinearLayout stworzRzadMiniatur() {
        LinearLayout rzadminiatur = new LinearLayout(MainActivity.activity.getApplicationContext());
        MainActivity.dodajViewDoView(Widoki.layoutscrollviewminiatury, rzadminiatur);
        return rzadminiatur;
    }

    private static LinearLayout stworzLayoutMiniatury() {
        LinearLayout layoutminiatury = new LinearLayout(MainActivity.activity.getApplicationContext());
        layoutminiatury.setOrientation(LinearLayout.VERTICAL);
        return layoutminiatury;
    }

    private ImageButton utworzObrazMiniatury(Bitmap bitmapa, final boolean katalog, String tag) {
        ImageButton obrazminiatury = new ImageButton(MainActivity.activity.getApplicationContext());
        obrazminiatury.setTag(tag);
        obrazminiatury.setBackgroundColor(Color.WHITE);
        obrazminiatury.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    view.setBackgroundColor(Color.BLUE);
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        });
        obrazminiatury.setPadding(0, 0, 0, 0);
        Bitmap bitmapaminiatury = Bitmap.createBitmap(MainActivity.rozdzielczosc.x / 10, MainActivity.rozdzielczosc.x / 10, Bitmap.Config.ARGB_8888);
        Canvas canvasminiatury = new Canvas(bitmapaminiatury);
        canvasminiatury.drawBitmap(bitmapa, null, new Rect(0, 0, MainActivity.rozdzielczosc.x / 10, MainActivity.rozdzielczosc.x / 10), null);
        obrazminiatury.setImageBitmap(bitmapaminiatury);
        obrazminiatury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppService.service.wateklistujpliki.zajety) {
                    MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
                } else {
                    if (katalog) {
                        if (((String) (view.getTag())).equals("SERWERTE")) {
                            if (montowaniete == null) {
                                MainActivity.wyswietlToast("Udział nie zamontowany!");
                            } else {
                                WatekMiniatury.przerwijMiniatury();
                                MainActivity.folderroboczy = montowaniete;
                                AppService.service.wateklistujpliki.focusnawstecz = true;
                                AppService.service.wateklistujpliki.odswiezfoldery = true;
                            }
                        } else {
                            WatekMiniatury.przerwijMiniatury();
                            MainActivity.folderroboczy = (String) view.getTag();
                            AppService.service.wateklistujpliki.focusnawstecz = true;
                            AppService.service.wateklistujpliki.odswiezfoldery = true;
                        }
                    } else {
                        WatekMiniatury.przerwijMiniatury();
                        MainActivity.trybopcji = false;
                        OpcjeProgramu.zapiszOpcje();
                        AppService.service.watekwczytaj.przeladuj = true;
                        AppService.service.watekrysuj.odswiez = true;
                        Widoki.activitylayout.removeView(Widoki.opcjelayout);
                    }
                }
            }
        });
        return obrazminiatury;
    }

    private static TextView utworzPodpisMiniatury(String podpis) {
        TextView podpisminiatury = new TextView(MainActivity.activity.getApplicationContext());
        podpisminiatury.setText(podpis);
        podpisminiatury.setGravity(CENTER);
        podpisminiatury.setTextColor(Color.BLACK);
        podpisminiatury.setTextSize(10);
        podpisminiatury.setPadding(5, 0, 5, 0);
        podpisminiatury.setGravity(CENTER);
        podpisminiatury.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        podpisminiatury.setMaxWidth(MainActivity.rozdzielczosc.x / 10);
        podpisminiatury.setPadding(0, 0, 0, 0);
        return podpisminiatury;
    }

    private LinearLayout utworzMiniature(String podpis, Bitmap bitmapa, boolean katalog, String tag) {
        LinearLayout layoutminiatury = stworzLayoutMiniatury();
        ImageButton obrazminiatury = utworzObrazMiniatury(bitmapa, katalog, tag);
        layoutminiatury.addView(obrazminiatury);
        TextView podpisminiatury = utworzPodpisMiniatury(podpis);
        layoutminiatury.addView(podpisminiatury);
        return layoutminiatury;
    }

    public void wyswietlZawartoscFolderu() {
        wyczyscScroolView();
        if(! new File(OpcjeProgramu.folder).isDirectory()) {
            OpcjeProgramu.folder = "/";
            Ustawienia.zapiszUstawienie("folder", "/");
            MainActivity.folderroboczy = "/";
        }
        File katalogobecny = new File(MainActivity.folderroboczy);
        final File[] plikiobecne = katalogobecny.listFiles();
        int iloscminiatur = 0;
        LinearLayout rzadminiatur = stworzRzadMiniatur();
        String wstecz;
        if(new File(MainActivity.folderroboczy).getParentFile() != null) {
            wstecz = new File(MainActivity.folderroboczy).getParentFile().getAbsolutePath() + "/";
        } else {
            wstecz = "/";
        }
        LinearLayout miniaturawstecz = utworzMiniature("..", Bitmapy.folderbitmap, true, wstecz);
        if(focusnawstecz) {
            miniaturawstecz.requestFocus();
        }
        MainActivity.dodajViewDoView(rzadminiatur, miniaturawstecz);
        iloscminiatur = iloscminiatur + 1;
        LinearLayout miniaturasieciowa = utworzMiniature("TE", Bitmapy.foldersieciowybitmap, true, "SERWERTE");
        MainActivity.dodajViewDoView(rzadminiatur, miniaturasieciowa);
        iloscminiatur = iloscminiatur + 1;
        LinearLayout miniaturaroot = utworzMiniature("/", Bitmapy.folderroot, true, "/");
        MainActivity.dodajViewDoView(rzadminiatur, miniaturaroot);
        iloscminiatur = iloscminiatur + 1;
        if(plikiobecne != null) {
            sortujPliki(plikiobecne);
            for (int i = 0; i < plikiobecne.length; i++) {
                if (plikiobecne[i].isDirectory()) {
                    if (iloscminiatur % 10 == 0) {
                        rzadminiatur = stworzRzadMiniatur();
                    }
                    LinearLayout miniaturafolderu = utworzMiniature(plikiobecne[i].getName(), Bitmapy.folderbitmap, true, plikiobecne[i].getAbsolutePath() + "/");
                    MainActivity.dodajViewDoView(rzadminiatur, miniaturafolderu);
                    iloscminiatur = iloscminiatur + 1;
                }
                if(plikiobecne[i].isFile() && plikiobecne[i].getName().toLowerCase().endsWith(".jpg")) {
                    if (iloscminiatur % 10 == 0) {
                        rzadminiatur = stworzRzadMiniatur();
                    }
                    LinearLayout miniaturajpg = utworzMiniature(plikiobecne[i].getName(), Bitmapy.jpgbitmap, false, plikiobecne[i].getAbsolutePath());
                    MainActivity.dodajViewDoView(rzadminiatur, miniaturajpg);
                    iloscminiatur = iloscminiatur + 1;
                }
            }
        }
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(odswiezfoldery == true) {
                zajety = true;
                MainActivity.widocznoscPostepuOpcje(View.VISIBLE, Color.RED);
                wyswietlZawartoscFolderu();
                MainActivity.widocznoscPostepuOpcje(View.INVISIBLE, Color.RED);
                AppService.service.watekminiatury.odswiezminiatury = System.currentTimeMillis();
                odswiezfoldery = false;
                zajety = false;
            }
        }
    }

}
