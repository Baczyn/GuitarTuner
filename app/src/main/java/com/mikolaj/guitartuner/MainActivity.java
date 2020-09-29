package com.mikolaj.guitartuner;


        import androidx.appcompat.app.AppCompatActivity;


        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import com.mikolaj.guitartuner.views.Tuner;

public class MainActivity extends AppCompatActivity {
    private Tuner tuner;
    private DetectThread detectThread;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tuner = findViewById(R.id.tuner);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((detectThread == null) || (!detectThread.isAlive())) {
                    button.setText("Stop");
                    detectThread = new DetectThread(tuner);
                    detectThread.startDetection();

                }
                else {
                    detectThread.stopDetection();
                    button.setText("Start");
                }

            }
        });

    }
}