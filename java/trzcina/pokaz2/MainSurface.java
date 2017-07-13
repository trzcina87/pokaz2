package trzcina.pokaz2;

import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class MainSurface extends SurfaceView {
    public SurfaceHolder surfaceHolder;

    public MainSurface(final MainActivity activity) {
        super(activity);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback(){

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }
}