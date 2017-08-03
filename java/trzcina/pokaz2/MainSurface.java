package trzcina.pokaz2;

import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class MainSurface extends SurfaceView {
    public SurfaceHolder surfaceholder;

    public MainSurface(final MainActivity activity) {
        super(activity);
        surfaceholder = getHolder();
        surfaceholder.addCallback(new SurfaceHolder.Callback(){

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