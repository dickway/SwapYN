package com.zzkj.structure.util.device.dv;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Utils {
    public static String runtimeExec(String command) {
        try {
            if (TextUtils.isEmpty(command)) {
                return null;
            }
            Process resultExecution = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(resultExecution.getInputStream()));
            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
            br.close();
            int resultStatust = resultExecution.waitFor();
            if (resultExecution != null) {
                resultExecution.destroy();
            }
            return sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String File2String(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            //将file文件内容转成字符串
            BufferedReader bf = new BufferedReader(isr);

            String content = "";
            StringBuilder sb = new StringBuilder();
            while (content != null) {
                content = bf.readLine();
                if (content == null) {
                    break;
                } else {
                    content = content + "\n";
                }
                sb.append(content);
            }
            bf.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
