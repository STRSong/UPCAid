package jwxt.cacher.cc.jwxt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
            URL imgurl=new URL("http://jwxt.upc.edu.cn/jwxt/verifycode.servlet");
            HttpURLConnection connection=(HttpURLConnection) imgurl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            try{
                connection.connect();
                this.cookie=connection.getHeaderField("Set-Cookie");
                this.cookie=this.cookie.substring(0,this.cookie.indexOf(";"));
                System.out.println(this.cookie);
                InputStream inputStream=new BufferedInputStream(connection.getInputStream());
                return BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String connect(String account,String passwd,String randomcode){
        try{
            URL url=new URL("http://jwxt.upc.edu.cn/jwxt/Logon.do?method=logon");
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            try{
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Cookie", this.cookie);

                DataOutputStream outputStream=new DataOutputStream(connection.getOutputStream());
                String content = "USERNAME=" + URLEncoder.encode(account, "UTF-8")
                        + "&PASSWORD=" + URLEncoder.encode(passwd, "UTF-8")
                        + "&RANDOMCODE=" + URLEncoder.encode(randomcode, "UTF-8");
                outputStream.writeBytes(content);
                outputStream.flush();
                outputStream.close();
                System.out.println(content);
                BufferedInputStream inputStream=new BufferedInputStream(connection.getInputStream());
                byte[] b=new byte[120];
                int count=inputStream.read(b);
                if(count>118){
                    BufferedReader bufferedReader=new
                            BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    StringBuilder resultBuilder=new StringBuilder();
                    Scanner scan=new Scanner(bufferedReader);
                    while(scan.hasNextLine()){
                        resultBuilder.append(scan.nextLine()+"\n");
                    }
                    Document doc1= Jsoup.parse(resultBuilder.toString());
                    Element element=doc1.select("tr").get(3);
                    element=element.select("td").get(1);
//                    String error=element.text();
                    bufferedReader.close();
                    inputStream.close();
                    System.out.println(element.text());
                    return element.text();
                }
            }catch (Exception e){
                e.printStackTrace();
                return "Exception";
            }finally {
                connection.disconnect();
            }
            URL url1=new URL("http://jwxt.upc.edu.cn/jwxt/Logon.do?method=logonBySSO");
            connection=(HttpURLConnection)url1.openConnection();
            try{
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("Cookie",cookie);
                connection.connect();
                InputStream inputStream=new BufferedInputStream(connection.getInputStream());
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
                return "Exception";
            }finally {
                connection.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
            return "Exception";
        }
        return null;
    }
    public List<HashMap<String,String>> getScore(String kksj){
        //成绩查询
        try{
            URL searchScore=new URL("http://jwxt.upc.edu.cn/jwxt/xszqcjglAction.do?method=queryxscj");
            HttpURLConnection
            httpURLConnection=(HttpURLConnection)searchScore.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Cookie",cookie);

            DataOutputStream outputStream=new DataOutputStream(httpURLConnection.getOutputStream());
            String content2="kksj="+URLEncoder.encode(kksj,"UTF-8")
                    +"&xsfs="+URLEncoder.encode("qbcj","UTF-8")
                    +"&PageNum="+URLEncoder.encode("1","UTF-8");
            outputStream.writeBytes(content2);
            outputStream.flush();
            outputStream.close();

            InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader in=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            Scanner scann=new Scanner(in);
            StringBuilder stringBuilder=new StringBuilder();
            while(scann.hasNextLine()){
                stringBuilder.append(scann.nextLine()+"\n");
            }
            String result=stringBuilder.toString();
            System.out.println(result.length());
            Document doc= Jsoup.parse(result);
            List<HashMap<String,String>> data=new ArrayList<>();
            Integer i=1;
            for(i=1;i<=10;i++){
                HashMap<String,String> item=new HashMap<>();
                Element elem;
                if((elem=doc.getElementById(i.toString()))!=null){
                    Element temp;
                    temp=elem.select("td").get(3);
                    item.put("kksj",temp.text());
                    temp=elem.select("td").get(4);
                    item.put("kcmc",temp.text());
                    temp=elem.select("td").get(5);
                    item.put("zcj",temp.text());
                    temp=elem.select("td").get(10);
                    item.put("xf",temp.text());
                }else{
                    break;
                }
                data.add(item);
            }
            return data;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
