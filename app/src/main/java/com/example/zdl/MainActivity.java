package com.example.zdl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.jaredrummler.android.shell.ShellNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.jaredrummler.android.shell.Shell.SU.run;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/armpcap/";

    TextView info;

    EditText index;

    EditText port;

    EditText protocol;

    EditText num;

    EditText filter;

    CheckBox isFilter;

    Button start;

    Button stop;

    Shell.Console console;

    boolean Fisrt = true;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case 1:
                    Bundle bundle = message.getData();
                    String info_str = "";
                    List<String> liststr = bundle.getStringArrayList("data");
                    for (String str : liststr){
                        info_str+=str+"\n";
                    }
                    info.setText(info_str);
                    if(Fisrt){
                        Fisrt = false;
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        moveFile();
        verifyStoragePermissions(this);
        createDir();
        if (Fisrt){
            sniff();
        }


    }

    private void moveFile(){
        Resources myResources = getResources();
        InputStream myFile = myResources.openRawResource(R.raw.armpcap);
        File file = new File(path+"armpcap");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = myFile.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            myFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Shell.Console.Builder builder = new Shell.Console.Builder();
            Shell.Console console1 = builder.useSU().build();
            console1.run("cd /sdcard/armpcap/");
            console1.run("mv armpcap /data/local/");
            console1.run("cd /data/local/");
            console1.run("chmod 777 armpcap");
            console1.close();
        } catch (ShellNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void sniff(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filter_str = "";
                String port_str = "";
                String protocol_str = "";
                String index_str = "";
                String num_str = "";
                if (!Fisrt){
                    if (isFilter.isChecked()){
                        num_str = "-1";
                        index_str = index.getText().toString();
                        if(!num.getText().toString().equals("")){
                            num_str = num.getText().toString();
                        }
                        filter_str = "\""+filter.getText().toString()+"\"";
                    } else {
                        num_str = "-1";
                        index_str = index.getText().toString();
                        if(!num.getText().toString().equals("")){
                            num_str = num.getText().toString();
                        }
                        protocol_str = protocol.getText().toString();
                        port_str = port.getText().toString();
                        if (port_str.equals("")){
                            filter_str = "\""+protocol_str+"\"";
                        }else{
                            filter_str = "\""+protocol_str+" port "+port_str+"\"";
                        }
                    }
                }
                if (Shell.SU.available()){
                    try {
                        console = Shell.SU.getConsole();
                        console.run("cd /data/local");
                        CommandResult result = console.run("./armpcap "+index_str+" "+filter_str+" "+num_str);
                        Message message = new Message();
                        message.what = 1;
                        Bundle data = new Bundle();
                        ArrayList<String> liststr = new ArrayList<>();
                        for (String str : result.stdout){
                            liststr.add(str);
                        }
                        if (!result.isSuccessful())
                            liststr.add("未指定抓包数量，强行中断抓包！！！！");
                        data.putStringArrayList("data", liststr);
                        message.setData(data);
                        handler.sendMessage(message);

                        if (Fisrt){
                            console.close();
                        }
                    } catch (ShellNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the u
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }



    public void createDir () {

        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

    }


    private void init(){
        info = findViewById(R.id.info);
        index = findViewById(R.id.index);
        port = findViewById(R.id.port);
        protocol = findViewById(R.id.protocol);
        num = findViewById(R.id.num);
        filter = findViewById(R.id.filter);
        isFilter = findViewById(R.id.is_filter);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"开始抓包",Toast.LENGTH_SHORT).show();
                sniff();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Shell.Console.Builder builder = new Shell.Console.Builder();
                    Shell.Console console1 = builder.useSU().build();
                    console1.run("pkill armpcap");
                    console1.close();
                } catch (ShellNotFoundException e) {
                    e.printStackTrace();
                }
                if(!console.isClosed()){
                    console.close();
                    Toast.makeText(MainActivity.this,"已停止抓包",Toast.LENGTH_SHORT).show();
                }
            }
        });
        isFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    filter.setEnabled(true);
                    port.setText("");
                    protocol.setText("");
                    port.setEnabled(false);
                    protocol.setEnabled(false);
                } else {
                    filter.setText("");
                    filter.setEnabled(false);
                    port.setEnabled(true);
                    protocol.setEnabled(true);
                }
            }
        });
    }

}

