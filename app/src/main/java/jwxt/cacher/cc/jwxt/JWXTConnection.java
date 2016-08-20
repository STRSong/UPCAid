package jwxt.cacher.cc.jwxt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by xhaiben on 2016/8/15.
 */
public class JWXTConnection implements Serializable {
    private String cookie;

    public JWXTConnection(){

    }
    public Bitmap getRandomCode(){
        try{
            URL url=new URL("http://120.27.117.34:4549/JWXTServlet/you?command=getRandomCode");
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            cookie=httpURLConnection.getHeaderField("Set-Cookie");
            System.out.println(cookie);

            InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            httpURLConnection.disconnect();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    //登陆失败返回错误信息，登陆成功返回空
    public String connect(String account,String passwd,String randomcode){
        try {
            String logURL="http://120.27.117.34:4549/JWXTServlet/you?command=logToJwxt"
                    +"&username=" +account
                    + "&password=" +passwd
                    +"&randomcode="+randomcode;
            URL url=new URL(logURL);
            System.out.println(url);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Cookie",cookie);
            httpURLConnection.connect();

            InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            Scanner scanner=new Scanner(reader);
            StringBuilder stringBuilder=new StringBuilder();
            while (scanner.hasNextLine()){
                stringBuilder.append(scanner.nextLine()+"\n");
            }
            reader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            JSONObject json= JSONObject.fromObject(stringBuilder.toString());
            String result=json.getString("result");
            if(!result.equals("登录成功")){
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public List<HashMap<String,String>> getScore(String kksj){

        //成绩查询
        try{
            String scoreUrl="http://120.27.117.34:4549/JWXTServlet/you?command=getScore"
                    +"&kksj="+kksj;
            URL url=new URL(scoreUrl);
            System.out.println(scoreUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Cookie",cookie);
            httpURLConnection.connect();
            InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            Scanner scanner=new Scanner(reader);
            StringBuilder stringBuilder=new StringBuilder();
            while (scanner.hasNextLine()){
                stringBuilder.append(scanner.nextLine()+"\n");
            }
            JSONArray jsonArray=JSONArray.fromObject(stringBuilder.toString());
            List<HashMap<String,String>> data=new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                HashMap<String,String> item=new HashMap<>();
                item.put("kksj",jsonObject.getString("kksj"));
                item.put("kcmc",jsonObject.getString("kcmc"));
                item.put("zcj",jsonObject.getString("zcj"));
                item.put("xf",jsonObject.getString("xf"));
                data.add(item);
            }
            return data;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private void initHandler(){

    }
}
