package com.example.schubydooo.lineagebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Process;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Scott", "Starting shit");
        try {
            turnOnLED();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void turnOnLED() throws IOException {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        while(true) {
            try {
                p = r.exec("cat /sys/class/gpio/gpio21/value");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            line = b.readLine();

            if (line.equals("0"))
            {
                Process p2 = Runtime.getRuntime().exec("su -c 'echo 1 > /sys/class/gpio/gpio5/value'");
                Log.i("scotttt", "line is 0");
                try {
                    p2.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BufferedReader c = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                line = c.readLine();
                Log.i("aoeu", "--: " + line);
            }
            else if (line.equals("1"))
            {
                p = r.exec("su -c 'echo 0 > /sys/class/gpio/gpio5/value'");
            }

            b.close();
        }

    }

}

// /sys/class/gpio/gpioXX

// echo XX > 935