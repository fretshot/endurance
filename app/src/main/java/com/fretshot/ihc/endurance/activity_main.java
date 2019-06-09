package com.fretshot.ihc.endurance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;

public class activity_main extends AppCompatActivity {

    TextView data1;
    TextView data2;
    TextView data3;

    WebView streamingVideo;
    Button button_action1;
    Button button_action2;
    Button button_action3;
    Button button_down;
    Button button_up;
    Button button_left;
    Button button_right;

    Socket s;
    ServerSocket ss;

    public static String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        streamingVideo = findViewById(R.id.stream);
        streamingVideo.setVisibility(View.INVISIBLE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ip = sharedPreferences.getString(ajustes.IP_ENDURANCE, null);

        button_action1 = findViewById(R.id.action1);
        button_action2 = findViewById(R.id.action2);
        button_action3 = findViewById(R.id.action3);
        button_down = findViewById(R.id.button_down);
        button_up = findViewById(R.id.button_up);
        button_left = findViewById(R.id.button_left);
        button_right = findViewById(R.id.button_right);
        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);

        button_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("BACK");
            }
        });

        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("GO");
            }
        });

        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("LEFT");
            }
        });

        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("RIGHT");
            }
        });

        button_action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Raspberry.class);
                startActivity(intent);
                if (ss!= null && !ss.isClosed()) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "$$ e: "+e, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button_action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShot(streamingVideo);
            }
        });

        button_action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender messageSender = new MessageSender();
                messageSender.execute("LIGHTS");
            }
        });

        Thread thread = new Thread(new ReceivingData());
        thread.start();
    }


    class ReceivingData implements Runnable{



        InputStreamReader isr;
        BufferedReader bufferedReader;
        String message;

        @Override
        public void run() {
            try{
                ss = new ServerSocket(8888);
                while (true){
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    bufferedReader = new BufferedReader(isr);
                    message = bufferedReader.readLine();

                    final String[] msg = message.split(",");


                    final String Temp = msg[0]+"°C";
                    final String Hum = msg[1]+"%";
                    final String Dist_crude = msg[2];
                    final String Dist = msg[2]+" cms.";

                    final float Dist_float = Float.parseFloat(Dist_crude);


                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {

                            if(Temp.equals("None°C")){
                                //Textview no se actualiza
                            }else{
                                data1.setText(Temp);
                            }

                            if(Hum.equals("None%")){
                                //Textview no se actualiza
                            }else{
                                data2.setText(Hum);
                            }

                            data3.setText(Dist);
                            if (Dist_float < 150.0){
                                data3.setBackgroundColor(Color.GREEN);
                            }
                            if(Dist_float< 60.0){
                                data3.setBackgroundColor(Color.YELLOW);
                            }
                            if(Dist_float< 15.0){
                                data3.setBackgroundColor(Color.RED);
                            }
                            if(Dist_float> 150.0){
                                data3.setBackgroundColor(Color.TRANSPARENT);
                                data3.setText("LIBRE");
                            }


                        }
                    });
                }
            }catch (final Exception e){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Ultima información recibida"+e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.conectar) {
            conectar();
            return true;
        }

        if (id == R.id.ajustes) {
            Intent intent = new Intent(this, ajustes.class);
            startActivity(intent);
            if (ss!= null && !ss.isClosed()) {
                try {
                    ss.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "$$ e: "+e, Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }

        if (id == R.id.acerca_de) {
            Intent intent = new Intent(this, acerca_de.class);
            startActivity(intent);
            if (ss!= null && !ss.isClosed()) {
                try {
                    ss.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "$$ e: "+e, Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }

        if (id == R.id.salir) {
            if (ss!= null && !ss.isClosed()) {
                try {
                    ss.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "$$ e: "+e, Toast.LENGTH_LONG).show();
                }
            }
            finishAndRemoveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void conectar(){

        streamingVideo.setVisibility(View.VISIBLE);
        streamingVideo.clearCache(true);
        streamingVideo.getSettings().setJavaScriptEnabled(true);
        streamingVideo.getSettings().setLoadWithOverviewMode(true);
        streamingVideo.getSettings().setUseWideViewPort(true);
        streamingVideo.setWebViewClient(new WebViewClient());
        streamingVideo.loadUrl("http://"+ip+":8081");

    }

        @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public void screenShot(View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            final File directorio = new File(Environment.getExternalStorageDirectory() + "/Pictures/Endurance/");

            if (!directorio.exists()) {
                File imgDirectory = new File("/sdcard/Pictures/Endurance/");
                imgDirectory.mkdirs();
            }

            String mPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/Endurance/" + now + ".jpg";

            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            final File imageFile = new File(mPath);
            MediaScannerConnection.scanFile(this, new String[] { imageFile.getPath() }, new String[] { "image/jpeg" }, null);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Screenshot guardada en galería", Snackbar.LENGTH_LONG).setAction("Abrir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openScreenshot(imageFile);
                        }
                    });

            snackbar.show();

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                vibrator.vibrate(500);
            }

        } catch (Throwable e) {
            Snackbar.make(findViewById(android.R.id.content), ">>"+e, Snackbar.LENGTH_LONG).show();
            System.out.println(e);
        }

    }

}
