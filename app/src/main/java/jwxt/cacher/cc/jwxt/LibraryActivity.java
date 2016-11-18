package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ArrowKeyMovementMethod;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import jwxt.cacher.cc.jwxt.info.BookInfo;
import jwxt.cacher.cc.jwxt.info.Course;

/**
 * Created by xhaiben on 2016/10/11.
 */

public class LibraryActivity extends AppCompatActivity {
    private Context context;
    private ListView listView;
    private Bitmap captcha;
    private ArrayList<BookInfo> bookInfoArrayList;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        context = this;

        toolbar=(Toolbar)findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        //toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.listView_lib);
        bookInfoArrayList = (ArrayList<BookInfo>) getIntent().getSerializableExtra("bookInfoArrayList");
        captcha=(Bitmap)getIntent().getParcelableExtra("captcha");
        LibListAdapter adapter = new LibListAdapter(context, bookInfoArrayList, captcha);
        listView.setAdapter(adapter);
    }
    private View.OnClickListener getNavigationOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryActivity.this.finish();
            }
        };
    }
}
