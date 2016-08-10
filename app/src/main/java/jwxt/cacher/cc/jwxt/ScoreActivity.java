package jwxt.cacher.cc.jwxt;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.xml.datatype.Duration;

/**
 * Created by xhaiben on 2016/8/8.
 */
public class ScoreActivity extends AppCompatActivity {
    private Context context = null;
    private ImageView mag_icon = null;
    private SearchView searchView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        
        searchView = (SearchView) findViewById(R.id.search_score_1);

        setSearchViewProperties();


        ListView listView = (ListView) this.findViewById(R.id.listView_Score);

//        SimpleAdapter adapter=new SimpleAdapter(this,MainActivity.data,R.layout.score_item,new String[]{
//                "kksj","kcmc","zcj","xf"},new int[]{R.id.kksj,R.id.kcmc,R.id.zjc,R.id.xf});
//        listView.setAdapter(adapter);

        //Button button=(Button)this.findViewById(R.id.Btn_win);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener getMenuItemClickListener(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.whole_score:

                        break;
                }
                return false;
            }
        };
    }
    private void setSearchViewProperties(){
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("开课时间");
        searchView.setSubmitButtonEnabled(true);

        String[] columnNames = {"_id", "text"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] array = getResources().getStringArray(R.array.kksjChoice);
        String[] temp = new String[2];
        int id = 0;
        for (String item : array) {
            temp[0] = Integer.toString(id++);
            temp[1] = item;
            cursor.addRow(temp);
        }
        String[] from = {"text"};
        int[] to = {R.id.search_textView};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.search_item, cursor, from, to, 0);
        searchView.setSuggestionsAdapter(simpleCursorAdapter);
        try {
            Field mSearchButton = searchView.getClass().getDeclaredField("mSearchButton");
            mSearchButton.setAccessible(true);
            ImageView searchButton = (ImageView) mSearchButton.get(searchView);

            Field mSubmitButton = searchView.getClass().getDeclaredField("mSubmitButton");
            mSubmitButton.setAccessible(true);
            ImageView submit = (ImageView) mSubmitButton.get(searchView);
            submit.setImageDrawable(searchButton.getDrawable());

            //暴力反射+上转型 设置auto
            Field mSearchEditFrame = searchView.getClass().getDeclaredField("mSearchEditFrame");
            mSearchEditFrame.setAccessible(true);
            LinearLayout linearLayout = (LinearLayout) mSearchEditFrame.get(searchView);
            LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(1);
            AutoCompleteTextView a = (AutoCompleteTextView) linearLayout1.getChildAt(0);
            a.setThreshold(0);
            a.setTextColor(Color.WHITE);

            mag_icon = (ImageView) linearLayout.getChildAt(0);
            mag_icon.setImageDrawable(null);
            linearLayout.removeViewAt(0);

//            Field[] fields = searchView.getClass().getDeclaredFields();
//            System.out.println(fields.length);
//            for (Field f : fields) {
//                System.out.println(f);
//            }
//            fMethod[] methods=searchView.getClass().getDeclaredMethods();
//            for(Method m:methods){
//                System.out.println(m);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
