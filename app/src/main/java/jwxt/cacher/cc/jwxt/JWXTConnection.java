package jwxt.cacher.cc.jwxt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            try{
                DataOutputStream outputStream1=new DataOutputStream(httpURLConnection.getOutputStream());
                String content2="kksj="+URLEncoder.encode(kksj,"UTF-8")
                        +"&xsfs="+URLEncoder.encode("qbcj","UTF-8")
                        +"&PageNum="+URLEncoder.encode("1","UTF-8");
                outputStream1.writeBytes(content2);
                outputStream1.flush();
                outputStream1.close();

                InputStream inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader in=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                StringBuffer stringBuffer=new StringBuffer();
                Scanner scann=new Scanner(in);
                while(scann.hasNextLine()){
                    stringBuffer.append(scann.nextLine()+"\n");
                }
                String result=stringBuffer.toString();
                System.out.println(result.length());
                //result=result.substring(0,result.length()-1);
                //System.out.println(result);
                httpURLConnection.disconnect();

                Document doc=Jsoup.parse(result);
                Element e=doc.getElementById("PageNavigation");
                e=e.select("font").first();

                Integer itemNum=new Integer(e.text());
                System.out.println(itemNum);
                List<HashMap<String,String>> data=new ArrayList<HashMap<String,String>>();

                Integer num;
                Integer pageNum=2;
                if(itemNum>=10){
                    do{
                        if(itemNum>=10){
                            num=10;
                        }else{
                            num=itemNum;
                        }
                        for(Integer i=1;i<=num;i++){
                            HashMap<String,String> item=new HashMap<String,String>();
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
                        httpURLConnection=(HttpURLConnection)searchScore.openConnection();
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Cookie",cookie);

                        outputStream1=new DataOutputStream(httpURLConnection.getOutputStream());
                        content2="kksj="+URLEncoder.encode(kksj,"UTF-8")
                                +"&xsfs="+URLEncoder.encode("qbcj","UTF-8")
                                +"&PageNum="+URLEncoder.encode(pageNum.toString(),"UTF-8");
                        outputStream1.writeBytes(content2);
                        outputStream1.flush();
                        outputStream1.close();

                        inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                        in=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                        stringBuffer=new StringBuffer();
                        scann=new Scanner(in);
                        while(scann.hasNextLine()){
                            stringBuffer.append(scann.nextLine()+"\n");
                        }
                        result=stringBuffer.toString();
                        System.out.println(result.length());
                        httpURLConnection.disconnect();
                        //result=result.substring(0,result.length()-1);
                        //System.out.println(result);
                        doc=Jsoup.parse(result);
                        itemNum-=num;
                        pageNum+=1;
                    }while(itemNum>0);

                }else{
                    num=itemNum;
                    for(Integer i=1;i<=num;i++){
                        HashMap<String,String> item=new HashMap<String,String>();
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
                }
//                System.out.println(data.toString());
//                progressBar.setVisibility(View.GONE);
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                httpURLConnection.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    private void initHandler(){

    }
    public List<Course> getCourseInfo(String xq,String zc) {
        List<Course> courseList = new ArrayList<>();
        try {
            String courseUrl="http://jwxt.upc.edu.cn/jwxt/tkglAction.do?method=goListKbByXs&istsxx=no"
                    +"&xnxqh=" +xq
                    +"&zc="+zc
                    +"&xs0101id=";
            URL
            url1 = new URL(courseUrl);
            HttpURLConnection
            httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Cookie", cookie);
            httpURLConnection.connect();
            InputStream
            instream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            Scanner
            scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                //fw.write(scanner.nextLine()+"\n");
                stringBuilder.append(scanner.nextLine() + "\n");
            }

            Document doc = Jsoup.parse(stringBuilder.toString());

            reader.close();
            instream.close();
            httpURLConnection.disconnect();
            Elements elements = doc.getAllElements();
            Element element = elements.select("form").first();

            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 7; j++) {
                    String id = String.valueOf(i) + "-" + String.valueOf(j) + "-" + "1";
                    String id2 = String.valueOf(i) + "-" + String.valueOf(j) + "-" + "2";
                    Element temp = element.getElementById(id);
                    if (temp.text().length() < 3) {

                        continue;
                    } else {
                        temp = element.getElementById(id2);
                    }
                    String info = temp.text().substring(1);
                    String[] strings = info.split("\\s+");
                    for (int k = 0; k < strings.length / 6; k++) {
                        Course item = new Course();
                        item.setCourseName(strings[k * 6 + 0]);
                        item.setTeacherName(strings[k * 6 + 2]);
                        item.setClassRoom(strings[k * 6 + 4]);
                        item.setBeginLesson(i * 2 - 1);
                        item.setEndLesson(i * 2);
                        if (j == 7) {
                            item.setDay(0);
                        } else {
                            item.setDay(j);
                        }
                        String lessonInfo = strings[k * 6 + 3];
                        String week = lessonInfo.substring(0, lessonInfo.indexOf("周"));
                        if (week.contains(",") && week.contains("-")) {
                            String[] weeks1 = week.split("[,]");
                            for (String str : weeks1) {
                                String[] weeks = str.split("[-]");
                                for (int x = Integer.parseInt(weeks[0]); x <= Integer.parseInt(weeks[1]); x++) {
                                    item.addWeek(x);
                                }
                            }
                            item.setCourseType(3);
                        } else if (week.contains(",")) {
                            String[] weeks = week.split("[,]");
                            for(String str:weeks){
                                item.addWeek(Integer.parseInt(str));
                            }
                            item.setCourseType(2);
                        } else if(week.contains("-")){
                            String[] weeks = week.split("[-]");
                            for(int x=Integer.parseInt(weeks[0]);x<=Integer.parseInt(weeks[1]);x++){
                                item.addWeek(x);
                            }
                            item.setCourseType(1);
                        }else{
                            item.addWeek(Integer.parseInt(week));
                            item.setCourseType(3);
                        }
                        courseList.add(item);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return courseList;
    }
}
