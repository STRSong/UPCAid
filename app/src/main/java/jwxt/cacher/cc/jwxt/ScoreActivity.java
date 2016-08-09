package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xhaiben on 2016/8/8.
 */
public class ScoreActivity extends AppCompatActivity {
    private Context context=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        context=this;
        ListView listView=(ListView)this.findViewById(R.id.listView_Score);

        SimpleAdapter adapter=new SimpleAdapter(this,MainActivity.data,R.layout.score_item,new String[]{
                "kksj","kcmc","zcj","xf"},new int[]{R.id.kksj,R.id.kcmc,R.id.zjc,R.id.xf});
        listView.setAdapter(adapter);

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
}
