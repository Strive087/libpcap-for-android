package com.example.zdl;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.io.DataOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        createDir(path+"/armpcap");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run("cd /data/local");
                CommandResult result = Shell.SU.run("./armpcap 3 ");
                if (result.isSuccessful()) {
                    System.out.println(result.getStdout());
                    // Example output on a rooted device:
                    // uid=0(root) gid=0(root) groups=0(root) context=u:r:init:s0
                }
            }
        }).start();
        if(upgradeRootPermission(getPackageCodePath()) == true){
            Toast.makeText(this,"成功获取root权限",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"获取root权限失败",Toast.LENGTH_SHORT).show();
        }

    }


    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("chmod 777 " +pkgCodePath + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }


    public int createDir (String dirPath) {

        File dir = new File(dirPath);
        //文件夹是否已经存在
        if (dir.exists()) {
            return -1;
        }
        if (!dirPath.endsWith(File.separator)) {//不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
        }
        //创建文件夹
        if (dir.mkdirs()) {
            return 1;
        }
        return 0;
    }


}

