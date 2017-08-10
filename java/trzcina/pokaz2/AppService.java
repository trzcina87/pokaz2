package trzcina.pokaz2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class AppService extends Service {

    public volatile static AppService service;
    public volatile static WatekListujPliki wateklistujpliki;
    public volatile static WatekMiniatury watekminiatury;
    public volatile static WatekWczytaj watekwczytaj;
    public volatile static WatekRysuj watekrysuj;
    public volatile static WatekOdlicz watekodlicz;
    public volatile static WatekAnimacja watekanimacja;

    public AppService() {
        watkiNaNull();
        service = this;
        PlikLogu.otworzLog();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void wystartujWatki() {
        if(wateklistujpliki == null) {
            wateklistujpliki = new WatekListujPliki();
            watekminiatury = new WatekMiniatury();
            watekwczytaj = new WatekWczytaj();
            watekrysuj = new WatekRysuj();
            watekodlicz = new WatekOdlicz();
            watekanimacja = new WatekAnimacja();
            wateklistujpliki.start();
            watekminiatury.start();
            watekwczytaj.start();
            watekrysuj.start();
            watekodlicz.start();
            watekanimacja.start();
            watekwczytaj.przeladuj = true;
            watekrysuj.odswiez = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        wystartujWatki();
        return START_NOT_STICKY;
    }

    private void czekajNaZakonczenie(Thread watek) {
        watek.interrupt();
        while(watek.isAlive()) {
            try {
                watek.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void zakonczWatekListujPliki() {
        wateklistujpliki.zakoncz = true;
        czekajNaZakonczenie(wateklistujpliki);
    }

    private void zakonczWatekMiniatury() {
        watekminiatury.zakoncz = true;
        watekminiatury.przerwij = true;
        czekajNaZakonczenie(watekminiatury);
    }

    private void zakonczWatekWczytaj() {
        watekwczytaj.zakoncz = true;
        czekajNaZakonczenie(watekwczytaj);
    }

    private void zakonczWatekRysuj() {
        watekrysuj.zakoncz = true;
        czekajNaZakonczenie(watekrysuj);
    }

    private void zakonczWatekOdlicz() {
        watekodlicz.zakoncz = true;
        czekajNaZakonczenie(watekodlicz);
    }

    private void zakonczWatekAnimacja() {
        watekanimacja.zakoncz = true;
        czekajNaZakonczenie(watekanimacja);
    }

    private void watkiNaNull() {
        watekanimacja = null;
        watekodlicz = null;
        watekrysuj = null;
        watekwczytaj = null;
        watekminiatury = null;
        wateklistujpliki = null;
    }

    private void odmontujKatalog() {
        if(wateklistujpliki.montowaniete != null) {
            Proces.odmontuj(wateklistujpliki.montowaniete);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zakonczWatekListujPliki();
        zakonczWatekMiniatury();
        zakonczWatekRysuj();
        zakonczWatekWczytaj();
        zakonczWatekOdlicz();
        zakonczWatekAnimacja();
        odmontujKatalog();
        PlikLogu.zamknijLog();
        watkiNaNull();
        Toast.makeText(MainActivity.activity.getApplicationContext(), "Zakonczono Pokaz2!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int level) {
    }

}
