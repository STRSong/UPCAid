package jwxt.cacher.cc.jwxt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by xhaiben on 2016/9/20.
 */

public class ClassRoomActivity extends AppCompatActivity {
    private TextView textViewNJ;
    private TextView textViewNT;
    private TextView textViewXH;
    private TextView textViewXL;
    private TextView textViewDH;
    private TextView textViewDL;
    private Toolbar toolbar;

    private SZSDConnection szsdConnection;
    private HashMap<String,String> classRoomMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        toolbar=(Toolbar)findViewById(R.id.toolbar_classroom);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        //toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewNJ=(TextView)findViewById(R.id.tv_classroom_nj);
        textViewNT=(TextView)findViewById(R.id.tv_classroom_nt);
        textViewXH=(TextView)findViewById(R.id.tv_classroom_xh);
        textViewXL=(TextView)findViewById(R.id.tv_classroom_xl);
        textViewDH=(TextView)findViewById(R.id.tv_classroom_dh);
        textViewDL=(TextView)findViewById(R.id.tv_classroom_dl);
        szsdConnection=(SZSDConnection)getIntent().getSerializableExtra("connection");
        classRoomMap=(HashMap<String,String>)getIntent().getSerializableExtra("classRoomMap");
        System.out.println(classRoomMap);
        textViewNJ.setText(classRoomMap.get("NJ"));
        textViewNT.setText(classRoomMap.get("NT"));
        textViewXH.setText(classRoomMap.get("XH"));
        textViewXL.setText(classRoomMap.get("XL"));
        textViewDH.setText(classRoomMap.get("DH"));
        textViewDL.setText(classRoomMap.get("DL"));
    }
    private View.OnClickListener getNavigationOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassRoomActivity.this.finish();
            }
        };
    }
}
