package trzcina.pokaz2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static android.view.Gravity.*;

public class Widoki {

    public static LinearLayout opcjelayout;
    public static RelativeLayout activitylayout;
    public static Button buttonzapisz;
    public static Button buttonanuluj;
    public static Button buttonobudzte;
    public static Button buttonmontujte;
    public static CheckBox checkboxpokazslidow;
    public static CheckBox checkboxlosuj;
    public static RadioButton radiobuttonczas2s;
    public static RadioButton radiobuttonczas3s;
    public static RadioButton radiobuttonczas5s;
    public static RadioButton radiobuttonczas7s;
    public static RadioButton radiobuttonczas10s;
    public static EditText edittextfolder;
    public static LinearLayout layoutscrollviewminiatury;
    public static ProgressBar progressbaropcje;

    public static void znajdzWidoki() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.activity.getApplicationContext());
        opcjelayout = (LinearLayout)inflater.inflate(R.layout.opcje, null);
        opcjelayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        activitylayout = (RelativeLayout) MainActivity.activity.findViewById(R.id.activity_main);
        buttonzapisz = (Button) opcjelayout.findViewById(R.id.buttonzapisz);
        buttonanuluj = (Button) opcjelayout.findViewById(R.id.buttonanuluj);
        buttonobudzte = (Button) opcjelayout.findViewById(R.id.buttonobudzte);
        buttonmontujte = (Button) opcjelayout.findViewById(R.id.buttonmontujte);
        checkboxlosuj = (CheckBox) opcjelayout.findViewById(R.id.checkboxlosuj);
        checkboxpokazslidow = (CheckBox) opcjelayout.findViewById(R.id.checkboxpokazslidow);
        radiobuttonczas2s = (RadioButton) opcjelayout.findViewById(R.id.radiobutton2s);
        radiobuttonczas3s = (RadioButton) opcjelayout.findViewById(R.id.radiobutton3s);
        radiobuttonczas5s = (RadioButton) opcjelayout.findViewById(R.id.radiobutton5s);
        radiobuttonczas7s = (RadioButton) opcjelayout.findViewById(R.id.radiobutton7s);
        radiobuttonczas10s = (RadioButton) opcjelayout.findViewById(R.id.radiobutton10s);
        edittextfolder = (EditText) opcjelayout.findViewById(R.id.edittextfolder);
        layoutscrollviewminiatury = (LinearLayout) opcjelayout.findViewById(R.id.layoutscrollviewminiatury);
        progressbaropcje = (ProgressBar) opcjelayout.findViewById(R.id.progressbaropcje);
    }

    private static void zapiszClick() {
        if(AppService.service.wateklistujpliki.zajety == true) {
            MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
        } else {
            MainActivity.trybopcji = false;
            OpcjeProgramu.zapiszOpcje();
            AppService.service.watekwczytaj.przeladuj = true;
            AppService.service.watekrysuj.odswiez = true;
            activitylayout.removeView(opcjelayout);
        }
    }

    private static void anulujClick() {
        if(AppService.service.wateklistujpliki.zajety == true) {
            MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
        } else {
            MainActivity.trybopcji = false;
            activitylayout.removeView(opcjelayout);
        }
    }

    private static void obudzteClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WakeOnLan.wyslijWOL(WakeOnLan.TEMAC);
            }
        }).start();
    }

    private static void montujteClick() {
        boolean hostzyje = Proces.dostepnoscIP(WakeOnLan.TEIP);
        if(hostzyje == false) {
            MainActivity.wyswietlToast("Host " + WakeOnLan.TEIP + " nie odpowiada!");
            AppService.service.wateklistujpliki.montowaniete = null;
        } else {
            Long milisekundy = System.currentTimeMillis();
            String milisek = String.valueOf(milisekundy);
            boolean wynikmontowania = Proces.montuj(milisekundy);
            if(wynikmontowania == true) {
                AppService.service.wateklistujpliki.montowaniete = "/sdcard/" + milisek + "/" + milisek + "/" + milisek + "/";
                MainActivity.wyswietlToast("Zamontowano " + WakeOnLan.TEIP);
            } else {
                AppService.service.wateklistujpliki.montowaniete = null;
                MainActivity.wyswietlToast("Błąd podczas montowania " + WakeOnLan.TEIP);
            }
        }
    }

    public static void przypiszAkcjeDoWidokow() {
        buttonzapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zapiszClick();
            }
        });
        buttonanuluj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anulujClick();
            }
        });
        buttonobudzte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obudzteClick();
            }
        });
        buttonmontujte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                montujteClick();
            }
        });
    }

    private static void wypelnijOpcjePokazSlidow() {
        if (OpcjeProgramu.pokazslidow == 0) {
            checkboxpokazslidow.setChecked(false);
        } else {
            checkboxpokazslidow.setChecked(true);
        }
    }

    private static void wypelnijOpcjeLosuj() {
        if (OpcjeProgramu.losuj == 0) {
            checkboxlosuj.setChecked(false);
        } else {
            checkboxlosuj.setChecked(true);
        }
    }

    private static void wypelnijOpcjeCzas() {
        if(OpcjeProgramu.czaszdjecia == 2) {
            radiobuttonczas2s.setChecked(true);
        }
        if(OpcjeProgramu.czaszdjecia == 3) {
            radiobuttonczas3s.setChecked(true);
        }
        if(OpcjeProgramu.czaszdjecia == 5) {
            radiobuttonczas5s.setChecked(true);
        }
        if(OpcjeProgramu.czaszdjecia == 7) {
            radiobuttonczas7s.setChecked(true);
        }
        if(OpcjeProgramu.czaszdjecia == 10) {
            radiobuttonczas10s.setChecked(true);
        }
    }

    private static void wypelnijOpcjeFolder() {
        edittextfolder.setText(OpcjeProgramu.folder);
    }

    public static void wypelnijOpcje() {
        wypelnijOpcjePokazSlidow();
        wypelnijOpcjeLosuj();
        wypelnijOpcjeCzas();
        wypelnijOpcjeFolder();
    }

}
