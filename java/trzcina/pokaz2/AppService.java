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

    public AppService() {
        wateklistujpliki = null;
        watekminiatury = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        zakonczWatekListujPliki();
        zakonczWatekMiniatury();
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
