package com.example.prmgclient.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 123 on 2016/12/23.
 * 文件工具
 */

public class FileUtil {
    /**
     * 是否有SD卡
     * @return
     */
    public static boolean hasSDCard(){
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 创建目录
     * @param filePath 目录路径
     * @return
     */

    public static boolean createDirectory(String filePath){
        if (null == filePath) {
            return false;
        }

        File file = new File(filePath);

        if (file.exists()){
            return true;
        }

        return file.mkdirs();

    }
    public static boolean deleteDirectorysFile(String filePath){
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if(!file.exists()) return false;
        if (file.isDirectory()){
            File[] files=file.listFiles();
            for(File f:files){
                deleteDirectorysFile(f.getAbsolutePath());
            }
            file.delete();
        }
        else if(file.isFile()) file.delete();
        return true;
    }
    /**
     * 从输入流保存文件
     * @param file 文件
     * @param is 输入流
     * @return 成功 失败
     */
    public static boolean saveFileFromInputStream(File file, InputStream is) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] tmp = new byte[1024 * 5];
            int len = 0;
            while ((len = is.read(tmp)) >= 0) {

                fos.write(tmp, 0, len);

            }
            fos.flush();

            System.out.println(file.getAbsolutePath()+file.exists());
            tmp = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return false;
    }
}
