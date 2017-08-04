package trzcina.pokaz2;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


@SuppressWarnings("PointlessBooleanExpression")
public class Widoki {

    public static LinearLayout opcjelayout;
    public static LinearLayout ustawienialayout;
    public static RelativeLayout activitylayout;
    public static Button buttonzapisz;
    public static Button buttonanuluj;
    public static Button buttonobudzte;
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
    public static ImageView imageviewklepsydra;
    public static ImageView imageviewplaypauza;
    public static TextView textviewczaszdjecia;
    public static EditText smbip;
    public static EditText smbudzial;
    public static EditText smbuzytkownik;
    public static EditText smbhaslo;

    public static void znajdzWidoki() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.activity.getApplicationContext());
        opcjelayout = (LinearLayout)inflater.inflate(R.layout.opcje, null);
        opcjelayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ustawienialayout = (LinearLayout)inflater.inflate(R.layout.ustawienia, null);
        ustawienialayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        activitylayout = (RelativeLayout) MainActivity.activity.findViewById(R.id.activity_main);
        buttonzapisz = (Button) opcjelayout.findViewById(R.id.buttonzapisz);
        buttonanuluj = (Button) opcjelayout.findViewById(R.id.buttonanuluj);
        buttonobudzte = (Button) opcjelayout.findViewById(R.id.buttonobudzte);
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
        imageviewklepsydra = (ImageView) MainActivity.activity.findViewById(R.id.klepsydra);
        imageviewplaypauza = (ImageView) MainActivity.activity.findViewById(R.id.playpauza);
        textviewczaszdjecia = (TextView) MainActivity.activity.findViewById(R.id.czaszdjecia);
        smbip = (EditText)ustawienialayout.findViewById(R.id.edittextipsmb);
        smbudzial = (EditText)ustawienialayout.findViewById(R.id.edittextudzial);
        smbuzytkownik = (EditText)ustawienialayout.findViewById(R.id.edittextuzytkownik);
        smbhaslo = (EditText)ustawienialayout.findViewById(R.id.edittextuhaslo);
    }


    private static void zapiszClick() {
        if(AppService.wateklistujpliki.zajety == true) {
            MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
        } else {
            WatekMiniatury.przerwijMiniatury();
            MainActivity.trybopcji = false;
            OpcjeProgramu.zapiszOpcje();
            AppService.watekwczytaj.przeladuj = true;
            AppService.watekrysuj.odswiez = true;
            activitylayout.removeView(opcjelayout);
            AppService.watekodlicz.ostatniczas = 2 * System.currentTimeMillis();
        }
    }

    private static void anulujClick() {
        if(AppService.wateklistujpliki.zajety == true) {
            MainActivity.wyswietlToast("Zaczekaj na wczytanie plikow!");
        } else {
            WatekMiniatury.przerwijMiniatury();
            MainActivity.trybopcji = false;
            activitylayout.removeView(opcjelayout);
        }
    }

    private static void obudzteClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean wyslano = WakeOnLan.wyslijWOL(WakeOnLan.TEMAC, WakeOnLan.TEBROADCAST);
                if(wyslano) {
                    MainActivity.wyswietlToast("Serwer " + WakeOnLan.TEMAC + " obudzony!");
                } else {
                    MainActivity.wyswietlToast("Blad podczas budzenia serwera " + WakeOnLan.TEMAC + "!");
                }
            }
        }).start();
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

    public static void wypelnijUstawienia() {
        smbip.setText(AppService.smbip);
        smbudzial.setText(AppService.smbudzial);
        smbuzytkownik.setText(AppService.smbuzytkownik);
        smbhaslo.setText(AppService.smbhaslo);
    }

}
