package com.fretshot.ihc.endurance;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String,Void, Void>{

    Socket s;
    PrintWriter pw;
    String ip = activity_main.ip;

    @Override
    protected Void doInBackground(String... voids) {

        String message = voids[0];
        try {
            s = new Socket(ip,9999);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            s.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
