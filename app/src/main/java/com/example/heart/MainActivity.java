package com.example.heart;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity {
    static { System.loadLibrary("heart"); }
    static native void nInit();
    static native void nResize(int w, int h);
    static native void nDraw();
    static native void nTouch(float x, float y);
    static native void nMode(int m);

    private GLSurfaceView view;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(new GLSurfaceView.Renderer() {
            public void onSurfaceCreated(GL10 g, EGLConfig c) { nInit(); }
            public void onSurfaceChanged(GL10 g, int w, int h) { nResize(w, h); }
            public void onDrawFrame(GL10 g) { nDraw(); }
        });
        view.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
                final float x = e.getX(), y = e.getY();
                view.queueEvent(() -> nTouch(x, y));
            }
            return true;
        });
        FrameLayout root = new FrameLayout(this);
        root.addView(view);
        final TextView text = new TextView(this);
        text.setText("Спасибо что была рядом все это время...");
        text.setTextColor(Color.WHITE); text.setTextSize(22); text.setGravity(Gravity.CENTER);
        text.setPadding(60, 0, 60, 140); text.setAlpha(0f);
        root.addView(text, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));
        final Button btn = new Button(this);
        btn.setText("Открыть"); btn.setTextColor(Color.WHITE); btn.setAllCaps(false);
        btn.setBackgroundColor(Color.parseColor("#2563eb"));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        lp.bottomMargin = 140;
        root.addView(btn, lp);
        btn.setOnClickListener(v -> {
            view.queueEvent(() -> nMode(1));
            btn.setVisibility(View.GONE);
            text.animate().alpha(1f).setDuration(1500).start();
        });
        setContentView(root);
    }
    @Override protected void onPause()  { super.onPause();  view.onPause(); }
    @Override protected void onResume() { super.onResume(); view.onResume(); }
}
