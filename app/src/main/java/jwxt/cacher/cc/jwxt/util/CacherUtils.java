package jwxt.cacher.cc.jwxt.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.SortedMap;

/**
 * Created by xhaiben on 2016/12/27.
 */

public class CacherUtils {
    /**
     * @param httpURLConnection 需要解析的http连接
     * @return 返回http的返回正文 String
     */
    public static String getHttpString(HttpURLConnection httpURLConnection) {
        try {
            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Scanner scanner = new Scanner(reader);
            StringBuffer buffer = new StringBuffer();
            while (scanner.hasNext()) {
                buffer.append(scanner.next());
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getExternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (type.isEmpty()) {
                appCacheDir = context.getExternalCacheDir();
            } else {
                appCacheDir = context.getExternalFilesDir(type);
            }
            if (appCacheDir == null) {
                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + type);
            }
            if (appCacheDir == null) {
                System.out.println("获取目录失败");
            } else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                    System.out.println("创建文件夹失败 E");
                }
            }
        } else {
            System.out.println("无SD卡");
        }
        return appCacheDir;
    }

    public static File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (type.isEmpty()) {
            appCacheDir = context.getCacheDir();
        } else {
            appCacheDir = new File(context.getFilesDir(), type);
        }
        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            System.out.println("创建文件夹失败 I");
        }
        return appCacheDir;
    }
}
