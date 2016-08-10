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

/**
 * Created by xhaiben on 2016/8/8.
 */
public class ScoreActivity extends AppCompatActivity {
    private Context context = null;
    private ImageView mag_icon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        context = this;

        SearchView scoreSearchView = (SearchView) findViewById(R.id.search_score_1);
        scoreSearchView.setSubmitButtonEnabled(true);
        scoreSearchView.setQueryHint("开课时间");
        scoreSearchView.setSubmitButtonEnabled(true);

        String[] columnNames = {"_id", "text"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] array = {"2014-2015-1", "2014-2015-2", "2014-2015-3"};
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
        scoreSearchView.setSuggestionsAdapter(simpleCursorAdapter);

        try {
            Field[] fields=scoreSearchView.getClass().getDeclaredFields();
            System.out.println(fields.length);
            for(Field f:fields){
                System.out.println(f);
            }

            Field mSearchButton = scoreSearchView.getClass().getDeclaredField("mSearchButton");
            mSearchButton.setAccessible(true);
            ImageView searchButton = (ImageView) mSearchButton.get(scoreSearchView);

            Field mSubmitButton = scoreSearchView.getClass().getDeclaredField("mSubmitButton");
            mSubmitButton.setAccessible(true);
            ImageView submit = (ImageView) mSubmitButton.get(scoreSearchView);
            submit.setImageDrawable(searchButton.getDrawable());

            //暴力反射+上转型 设置auto
            Field mSearchEditFrame = scoreSearchView.getClass().getDeclaredField("mSearchEditFrame");
            mSearchEditFrame.setAccessible(true);
            LinearLayout linearLayout = (LinearLayout) mSearchEditFrame.get(scoreSearchView);
            LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(1);
            AutoCompleteTextView a = (AutoCompleteTextView) linearLayout1.getChildAt(0);
            a.setThreshold(0);
            a.setTextColor(Color.WHITE);

            mag_icon=(ImageView)linearLayout.getChildAt(0);
            mag_icon.setImageDrawable(null);
            linearLayout.removeViewAt(0);
//            mag_icon.setImageResource(R.drawable.search_16px);


//            fMethod[] methods=searchView.getClass().getDeclaredMethods();
//            for(Method m:methods){
//                System.out.println(m);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score);
//        toolbar.inflateMenu(R.menu.score_menu);
//
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.search_score:
//                        SearchView searchView = (SearchView) item.getActionView();
//                        searchView.setQueryHint("开课时间");
//                        searchView.setSubmitButtonEnabled(true);
//
//                        try {
//                            //Java暴力反射
//                            Field mSubmitButton = searchView.getClass().getDeclaredField("mGoButton");
//                            mSubmitButton.setAccessible(true);
//                            ImageView submit = (ImageView) mSubmitButton.get(searchView);
//                            submit.setImageResource(R.drawable.search_16px);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        final SearchView.SearchAutoComplete searchAutoComplete=(SearchView.SearchAutoComplete)
//                                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//                        searchAutoComplete.setTextColor(Color.WHITE);
//                        //设置输入一个字母就开始匹配
//                        searchAutoComplete.setThreshold(1);
//
//                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
//                                context,R.layout.search_item
//                        );
//                        adapter.add("2014-2015-1");
//                        adapter.add("2014-2015-2");
//                        adapter.add("2014-2015-3");
//                        searchAutoComplete.setAdapter(adapter);
//
//                        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                String searchString=(String)parent.getItemAtPosition(position);
//                                searchAutoComplete.setText(searchString);
//                                searchAutoComplete.setSelection((searchAutoComplete.getText().toString().length()));
//                            }
//                        });
//                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//                            @Override
//                            public boolean onQueryTextChange(String newText) {
//                                Toast.makeText(context,newText,Toast.LENGTH_SHORT).show();
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onQueryTextSubmit(String query) {
//                                Toast.makeText(context,query,Toast.LENGTH_SHORT).show();
//                                return false;
//                            }
//                        });
//                        break;
//                }
//                return false;
//            }
//        });


        ListView listView = (ListView) this.findViewById(R.id.listView_Score);

//        SimpleAdapter adapter=new SimpleAdapter(this,MainActivity.data,R.layout.score_item,new String[]{
//                "kksj","kcmc","zcj","xf"},new int[]{R.id.kksj,R.id.kcmc,R.id.zjc,R.id.xf});
//        listView.setAdapter(adapter);

        //Button button=(Button)this.findViewById(R.id.Btn_win);
    }
//    public void onBtnWinCLick(View view){
//        View contentView= LayoutInflater.from(context).inflate(R.layout.time_choice_popupwindow,null);
//        PopupWindow popupWindow=new PopupWindow(
//                contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,true);
//        popupWindow.setTouchable(true);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        popupWindow.showAsDropDown(view);
//    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
