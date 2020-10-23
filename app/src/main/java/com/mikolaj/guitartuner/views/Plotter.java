package com.mikolaj.guitartuner.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.R;

import java.util.ArrayList;
import java.util.List;

public class Plotter extends View {

    private static final String TAG = Tuner.class.getSimpleName();
    private List<Float> amplitudes; // amplitudes for line lengths
    private static final float LINE_WIDTH = 0.005f; // width of visualizer lines
    private static final float LINE_SCALE = 2f; // scales visualizer lines
    private float width = 1f; // width of this View

    private Paint linePaint; // specifies line drawing characteristics

    // drawing tools
    // Circuit param
    private RectF circuitRect;
    private Paint circuitPaint;

    // Surface param
    private RectF surfaceRect;
    private Bitmap surfaceTexture;
    private Paint surfacePaint;

    private Paint backgroundPaint;
    // end drawing tools

    // holds the cached static part
    private Bitmap background;

    public Plotter(Context context) {
        super(context);
        init();
    }

    public Plotter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Plotter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Plotter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
    }

    private void init() {
        initialize();
    }

    private void initialize() {

        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(0x9f004d0f); // set color to green
        linePaint.setStrokeWidth(LINE_WIDTH / 2.f); // set stroke width

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //initialize Circuit params
        circuitRect = new RectF(0f, 0f, 1f, 1f);

        // the linear gradient is a bit skewed for realism
        circuitPaint = new Paint();
        circuitPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        circuitPaint.setShader(new LinearGradient(0.7f, 0.1f, 0.4f, 0.8f,
                Color.rgb(0xf1, 0xf5, 0xf1),
                Color.rgb(0x30, 0x31, 0x31),
                Shader.TileMode.MIRROR));

        //initialize Surface params
        float rimSize = 0.01f;
        surfaceRect = new RectF();
        surfaceRect.set(circuitRect.left + rimSize, circuitRect.top + rimSize,
                circuitRect.right - rimSize, circuitRect.bottom - rimSize);

        surfaceTexture = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.plastic);
        BitmapShader paperShader = new BitmapShader(surfaceTexture,
                Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR);
        Matrix paperMatrix = new Matrix();
        surfacePaint = new Paint();
        surfacePaint.setFilterBitmap(true);
        paperMatrix.setScale(1.0f / surfaceTexture.getWidth(),
                1.0f / surfaceTexture.getHeight());
        paperShader.setLocalMatrix(paperMatrix);
        surfacePaint.setStyle(Paint.Style.FILL);
        surfacePaint.setShader(paperShader);


        backgroundPaint = new Paint();
        backgroundPaint.setFilterBitmap(true);
    }

    private void drawRim(Canvas canvas) {
        // first, draw the metallic body
        canvas.drawRect(circuitRect, circuitPaint);
    }

    private void drawBackground(Canvas canvas) {
        if (background == null) {
            Log.w(TAG, "Background not created");
        } else {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        }
    }

    private void drawFace(Canvas canvas) {
        canvas.drawRect(surfaceRect, surfacePaint);
        // draw the inner rim circle
    }

    private void drawPlot(Canvas canvas) {
        float middle = 0.5f; // get the middle of the View
        float curX = 0f; // start curX at zero
        canvas.drawLine(0f, 0.5f, 1f, 0.5f, linePaint);
        //   canvas.drawCircle(0.1f,0.4f,0.4f,linePaint);
        // for each item in the amplitudes ArrayList
        for (float power : amplitudes) {
            float scaledHeight = power*LINE_SCALE; // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 2.f, curX, middle
                    - scaledHeight / 2.f, linePaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);

        float scaleX = (float) getWidth();
        float scaleY = (float) getHeight();
        canvas.save();     //zmieniamy skale na 0:1 i zapisujemy by moc porócić do stanu przed zapisaem
        canvas.scale(scaleX, scaleY);

//        drawHand(canvas);
        drawPlot(canvas);
        canvas.restore();

        invalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + w + "x" + h);
        int size = (int) (width/LINE_WIDTH);
        amplitudes = new ArrayList<>(size);
        regenerateBackground();
    }

    private void regenerateBackground() {
        // free the old bitmap
        if (background != null) {
            background.recycle();
        }

        background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(background);
        float scaleX = (float) getWidth();
        float scaleY = (float) getHeight();
        backgroundCanvas.save();
        backgroundCanvas.scale(scaleX, scaleY);

        drawRim(backgroundCanvas);
        drawFace(backgroundCanvas);

        backgroundCanvas.restore();
        backgroundCanvas.scale(scaleX, scaleX);
        // drawScale(backgroundCanvas);
    }


}
