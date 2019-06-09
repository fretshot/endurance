package com.fretshot.ihc.endurance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Raspberry extends AppCompatActivity {

    Button boton_apagar;
    Button boton_reiniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raspberry);

        boton_apagar = findViewById(R.id.apagar);
        boton_reiniciar = findViewById(R.id.reiniciar);

        boton_apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("APAGAR");
                Toast.makeText(getApplicationContext(), "Apagando...", Toast.LENGTH_LONG).show();
            }
        });

        boton_reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("REINICIAR");
                Toast.makeText(getApplicationContext(), "Reiniciando...", Toast.LENGTH_LONG).show();
            }
        });
    }


}