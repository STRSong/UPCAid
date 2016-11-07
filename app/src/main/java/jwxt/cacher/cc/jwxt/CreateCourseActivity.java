package jwxt.cacher.cc.jwxt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import jwxt.cacher.cc.jwxt.util.UIUtils;
import jwxt.cacher.cc.jwxt.views.CreateCourseTimeAdapter;

/**
 * Created by xhaiben on 2016/11/3.
 */

public class CreateCourseActivity extends AppCompatActivity {
    private ListView listView;
    private Context context;
    private TextView textViewAddTime;
    private CreateCourseTimeAdapter adapter;
    private Activity activity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        context = this;
        activity=this;
        listView = (ListView) findViewById(R.id.create_course_listview);
        textViewAddTime = (TextView) findViewById(R.id.add_course_add_section);
        adapter = new CreateCourseTimeAdapter(context,activity,listView);
        listView.setAdapter(adapter);
        UIUtils.setListViewHeightBasedOnItems(listView);
    }

    public void on_create_course_add_time_click(View view) {
        adapter.addOtherTime();
        adapter.notifyDataSetChanged();
        UIUtils.setListViewHeightBasedOnItems(listView);
    }
}
