package jwxt.cacher.cc.jwxt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    WebView webView;
    EditText edit_Username;
    EditText edit_PassWd;
    EditText edit_randomCode;

    HttpURLConnection connection;
    ExecutorService threadPool;
    String cookie;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            webView.loadData((String)msg.obj,"text/html; charset=UTF-8",null);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.randomImg);
        button=(Button)findViewById(R.id.btn_login);
        edit_Username=(EditText)findViewById(R.id.edit_username);
        edit_PassWd=(EditText)findViewById(R.id.edit_passwd);
        edit_randomCode=(EditText)findViewById(R.id.edit_randomcode);
        threadPool= Executors.newSingleThreadExecutor();

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    URL imgurl=new URL("http://jwxt.upc.edu.cn/jwxt/verifycode.servlet");
                    connection=(HttpURLConnection) imgurl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setDoInput(true);
                    try{
                        connection.connect();
                        cookie=connection.getHeaderField("Set-Cookie");
                        cookie=cookie.substring(0,cookie.indexOf(";"));

                        System.out.println(cookie);

                        InputStream inputStream=new BufferedInputStream(connection.getInputStream());
                        Bitmap bitmap=(Bitmap) BitmapFactory.decodeStream(inputStream);
                        Message msg=handler.obtainMessage();
                        msg.obj=bitmap;
                        handler.sendMessage(msg);
                    }catch (Exception e){
                        throw e;
                    }finally {
                        connection.disconnect();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        webView=(WebView)findViewById(R.id.webView);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }
    public void onRandomImgClick(View view){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    URL imgurl=new URL("http://jwxt.upc.edu.cn/jwxt/verifycode.servlet");
                    connection=(HttpURLConnection) imgurl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setDoInput(true);
                    try{
                        connection.connect();
                        cookie=connection.getHeaderField("Set-Cookie");
                        cookie=cookie.substring(0,cookie.indexOf(";"));

                        System.out.println(cookie);

                        InputStream inputStream=new BufferedInputStream(connection.getInputStream());
                        Bitmap bitmap=(Bitmap) BitmapFactory.decodeStream(inputStream);
                        Message msg=handler.obtainMessage();
                        msg.obj=bitmap;
                        handler.sendMessage(msg);
                    }catch (Exception e){
                        throw e;
                    }finally {
                        connection.disconnect();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void onBtn_loginClick(View view){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    //第一次登录
                    URL url=new URL("http://jwxt.upc.edu.cn/jwxt/Logon.do?method=logon");
                    HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Cookie", cookie);

                    DataOutputStream outstream1 = new DataOutputStream(httpURLConnection.getOutputStream());
                    String content = "USERNAME=" + URLEncoder.encode("1403010814", "UTF-8")
                            + "&PASSWORD=" + URLEncoder.encode("zxc009zxc", "UTF-8")
                            + "&RANDOMCODE=" + URLEncoder.encode(edit_randomCode.getText().toString(), "UTF-8");
                    outstream1.writeBytes(content);
                    outstream1.flush();
                    outstream1.close();
                    System.out.println(content);
                    InputStream instream1 = new BufferedInputStream(httpURLConnection.getInputStream());
                    byte[] b1=new byte[1024];
                    int len1=instream1.read(b1);
                    Log.w("logon",new String(b1,0,7));
                    instream1.close();
                    httpURLConnection.disconnect();

                    //第二次登录
                    URL url2=new URL("http://jwxt.upc.edu.cn/jwxt/Logon.do?method=logonBySSO");
                    httpURLConnection=(HttpURLConnection)url2.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("Cookie",cookie);

                    httpURLConnection.connect();
                    InputStream instream2=new BufferedInputStream(httpURLConnection.getInputStream());
                    byte[] b2=new byte[1024];
                    int len2=instream2.read(b2);
                    Log.w("logonBySSO",new String(b2,2,3));
                    instream2.close();
                    httpURLConnection.disconnect();

                    //成绩查询
                    URL searchScore=new URL("http://jwxt.upc.edu.cn/jwxt/xszqcjglAction.do?method=queryxscj");
                    httpURLConnection=(HttpURLConnection)searchScore.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Cookie",cookie);

                    DataOutputStream outputStream=new DataOutputStream(httpURLConnection.getOutputStream());
                    String content2="kksj="+URLEncoder.encode("2015-2016-2","UTF-8")
                            +"&xsfs="+URLEncoder.encode("qbcj","UTF-8")
                            +"&PageNum="+URLEncoder.encode("1","UTF-8");
                    outputStream.writeBytes(content2);
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                    StringBuilder stringBuilder=new StringBuilder();
                    byte[] b=new byte[1024];
                    int len=inputStream.read(b);
                    System.out.println(len);
                    while(len!=-1){
                        stringBuilder.append(new String(b,0,len,"UTF-8"));
                        len=inputStream.read(b);
                    }
                    String result=stringBuilder.toString();
                    System.out.println(result.length());
                    Document doc= Jsoup.parse(result);
                    Integer id=1;
                    StringBuilder s=new StringBuilder();
                    for(id=1;id<=10;id++){
                        Element elem=doc.getElementById(id.toString());
                        s.append(elem.toString());
                    }
                    Message msg=handler1.obtainMessage();
                    msg.obj=s.toString();
                    handler1.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void login(){

    }
}
