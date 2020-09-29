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
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mikolaj.guitartuner.R;

public final class Tuner extends View {

    private static final String TAG = Tuner.class.getSimpleName();

    // tuner(scale and hand ) position
    float tunerX = 0;
    float tunerY = 0;

    // drawing tools
    // Circuit param
    private RectF circuitRect;
    private Paint circuitPaint;

    // Surface param
    private RectF surfaceRect;
    private Bitmap surfaceTexture;
    private Paint surfacePaint;

    // Scale params
    private Paint scalePaint;

    //hand params
    private Paint handPaint;
    private Path handPath;
    private Paint handScrewPaint;

    //Note params
    private Paint notePaint;
    private String noteName = "A4";
    private String noteFrequency = "440Hz";

    // hand dynamics -- all are angular expressed in F degrees
    private boolean handInitialized = true;
    private float handPosition = 0;
    private float handTarget = 0;
    private float handVelocity = 0.0f;
    private float handAcceleration = 0.0f;
    private long lastHandMoveTime = -1L;

    private float degree = 0;


    private static final int totalCent = 100;
    private static final float degreesPerCent = 180.0f / totalCent;
    // private


    private Paint backgroundPaint;
    // end drawing tools

    // holds the cached static part
    private Bitmap background;

    public Tuner(Context context) {
        super(context);
        init();
    }

    public Tuner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Tuner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initialize();
    }

    private void initialize() {
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
        float rimSize = 0.02f;
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

        //initialize Scale params
        scalePaint = new Paint();
        scalePaint.setColor(0x9f004d0f);
        scalePaint.setAntiAlias(true);
        //set in drawScale()
        // scalePaint.setStrokeWidth(0.005f);
        // scalePaint.setTextSize(0.045f);
        scalePaint.setTextAlign(Paint.Align.CENTER);
        scalePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        //init Hand params
        handPaint = new Paint();
        handPaint.setAntiAlias(true);
        handPaint.setColor(0xff392f2c);
        handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
        handPaint.setStyle(Paint.Style.FILL);


        tunerY = getScaleY() - 0.35f;
        tunerX = getScaleX()/2f;

        handPath = new Path();
        handPath.moveTo(tunerX, tunerY + 0.2f);
        handPath.lineTo(tunerX - 0.010f, tunerY + 0.2f - 0.007f);
        handPath.lineTo(tunerX - 0.002f, tunerY - 0.32f);
        handPath.lineTo(tunerX + 0.002f, tunerY - 0.32f);
        handPath.lineTo(tunerX + 0.010f, tunerY + 0.2f - 0.007f);
        handPath.lineTo(tunerX, tunerY + 0.2f);
        handPath.addCircle(tunerX, tunerY, 0.025f, Path.Direction.CW);


        handScrewPaint = new Paint();
        handScrewPaint.setAntiAlias(true);
        handScrewPaint.setColor(0xff493f3c);
        handScrewPaint.setStyle(Paint.Style.FILL);

        notePaint = new Paint();
        notePaint.setAntiAlias(true);
        notePaint.setColor(0xff493f3c);
        notePaint.setStyle(Paint.Style.FILL);
        notePaint.setTextAlign(Paint.Align.RIGHT);

        backgroundPaint = new Paint();
        backgroundPaint.setFilterBitmap(true);
    }


    private void drawRim(Canvas canvas) {
        // first, draw the metallic body
        canvas.drawRect(circuitRect, circuitPaint);
    }

    private void drawFace(Canvas canvas) {
        canvas.drawRect(surfaceRect, surfacePaint);
        // draw the inner rim circle
    }

    //In my android version text size must by greater then 1.0 so i have to scale canvas.
    private void drawScale(Canvas canvas) {

        final float magnifier = 100f; //lupa
        final float tempTunerX = tunerX * magnifier;
        final float tempTunerY = tunerY * magnifier;

        canvas.save();
        canvas.scale(1f / magnifier, 1f / magnifier);
        scalePaint.setTextSize(magnifier * 0.04f);
        scalePaint.setStrokeWidth(0.008f * magnifier);

        float radius = 0.4f * magnifier;
        int counter = 0;

        float startX = 0;
        float startY = 0;
        float stopX = 0;
        float stopY = 0;

        float angle;
        float length;

        int minDegrees = -50;
        for (float i = -90; i <= 90 + 1; i += degreesPerCent) {
            angle = (float) Math.toRadians(i); // Need to convert to radians first

            startX = (float) (tempTunerX + radius * Math.sin(angle));
            startY = (float) (tempTunerY - radius * Math.cos(angle));

            length = 0.02f * magnifier;
            if (counter % 10 == 0) {

                length = 0.06f * magnifier;
                canvas.drawText(String.valueOf(minDegrees), startX + i / 20f, startY - i / 540f, scalePaint);
                minDegrees += 10;

            }

            stopX = (float) (tempTunerX + (radius - length) * Math.sin(angle));
            stopY = (float) (tempTunerY - (radius - length) * Math.cos(angle));

            canvas.drawLine(startX, startY, stopX, stopY, scalePaint);

            counter++;
        }

        canvas.drawText("CENT", startX - 4, startY + 4, scalePaint);

        canvas.restore();
    }


    private void drawHand(Canvas canvas) {
        if (handInitialized) {

            canvas.save();
            canvas.rotate(degree, tunerX, tunerY);
            canvas.drawPath(handPath, handPaint);
            canvas.restore();

            canvas.drawCircle(tunerX, tunerY, 0.01f, handScrewPaint);
        }
    }


    private void drawBackground(Canvas canvas) {
        if (background == null) {
            Log.w(TAG, "Background not created");
        } else {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);

        float scale = (float) getWidth();
        canvas.save();     //zmieniamy skale na 0:1 i zapisujemy by moc porócić do stanu przed zapisaem
        canvas.scale(scale, scale);

        drawHand(canvas);
        drawNote(canvas);
        canvas.restore();

        invalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + w + "x" + h);
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
        drawScale(backgroundCanvas);
    }

    public void updateHand(int cent){
        degree = centToDegree(cent);
    }

    private float centToDegree(int cent)
    {
        return degreesPerCent*cent;
    }

    private void drawNote(final Canvas canvas){
        canvas.save();
        final float magnifier = 100f; //lupa
        canvas.scale(1f/magnifier,1f/magnifier);

        notePaint.setTextSize(10f);
       // canvas.drawText(noteName.fo, tunerX*magnifier,tunerY*magnifier/3.5f,notePaint);
        for(int i = 0 ; i<noteName.length() ;i++){
            if(i<(noteName.length()-1))
                canvas.drawText(""+noteName.charAt(i), tunerX*magnifier+i*5f,tunerY*magnifier/3.5f,notePaint);
            else{
                notePaint.setTextSize(5f);
                canvas.drawText(""+noteName.charAt(i), tunerX*magnifier+i*5f,tunerY*magnifier/3.5f+1f,notePaint);
            }
        }

        //DrawFrequency
        canvas.drawText(noteFrequency,1.9f*tunerX*magnifier,tunerY*magnifier/6f,notePaint);

        canvas.restore();
    }

    public void updateNote(String noteName){
        this.noteName = noteName;
    }
    public void updateFrequency(String noteFrequency){
        this.noteFrequency = noteFrequency;
    }

}
