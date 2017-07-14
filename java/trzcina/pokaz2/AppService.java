package trzcina.pokaz2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class AppService extends Service {

    public static AppService service;

    WatekListujPliki wateklistujpliki;
    WatekMiniatury watekminiatury;
    WatekWczytaj watekwczytaj;
    WatekRysuj watekrysuj;

    public AppService() {
        wateklistujpliki = null;
        watekminiatury = null;
        watekwczytaj = null;
        watekrysuj = null;
        service = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void wystartujWatki() {
        if(wateklistujpliki == null) {
            wateklistujpliki = new WatekListujPliki();
            wateklistujpliki.start();
        }
        if(watekminiatury == null) {
            watekminiatury = new WatekMiniatury();
            watekminiatury.start();
        }
        if(watekwczytaj == null) {
            watekwczytaj = new WatekWczytaj();
            watekwczytaj.start();
            watekwczytaj.przeladuj = true;
        }
        if(watekrysuj == null) {
            watekrysuj = new WatekRysuj();
            watekrysuj.start();
            watekrysuj.odswiez = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        wystartujWatki();
        return START_NOT_STICKY;
    }

    private void zakonczWatekListujPliki() {
        wateklistujpliki.zakoncz = true;
        wateklistujpliki.interrupt();
        while(wateklistujpliki.isAlive()) {
            try {
                wateklistujpliki.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private void zakonczWatekMiniatury() {
        watekminiatury.zakoncz = true;
        watekminiatury.interrupt();
        while(watekminiatury.isAlive()) {
            try {
                watekminiatury.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private void zakonczWatekWczytaj() {
        watekwczytaj.zakoncz = true;
        watekwczytaj.interrupt();
        while(watekwczytaj.isAlive()) {
            try {
                watekwczytaj.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private void zakonczWatekRysuj() {
        watekrysuj.zakoncz = true;
        watekrysuj.interrupt();
        while(watekrysuj.isAlive()) {
            try {
                watekrysuj.join();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zakonczWatekListujPliki();
        zakonczWatekMiniatury();
        zakonczWatekWczytaj();
        zakonczWatekRysuj();
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
