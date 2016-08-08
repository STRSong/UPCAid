package jwxt.cacher.cc.jwxt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xhaiben on 2016/8/8.
 */
public class ScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        ListView listView=(ListView)this.findViewById(R.id.listView_Score);
        List<HashMap<String,String>> data=new ArrayList<>();
        HashMap<String,String> item=new HashMap<>();
        item.put("kksj","2015-2016-2");
        item.put("kcmc","大学英语视听说(4-4)");
        item.put("zcj","77");
        item.put("xf","1");
        data.add(item);

        SimpleAdapter adapter=new SimpleAdapter(this,MainActivity.data,R.layout.score_item,new String[]{
                "kksj","kcmc","zcj","xf"},new int[]{R.id.kksj,R.id.kcmc,R.id.zjc,R.id.xf});

        listView.setAdapter(adapter);
    }
}
