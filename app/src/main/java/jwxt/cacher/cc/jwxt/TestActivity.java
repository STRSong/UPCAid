package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.transition.ActionBarTransition;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import jwxt.cacher.cc.jwxt.views.WeekGridViewAdapter;


/**
 * Created by xhaiben on 2016/10/26.
 */

public class TestActivity extends AppCompatActivity {
    private Context context;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;

    }
    public void onTestButtonClick(View view){
        popupWindow=new PopupWindow(context);
        View popupView=View.inflate(context,R.layout.dlg_week_choose,null);

        popupWindow.setContentView(popupView);

        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        GridView gridView=(GridView) popupView.findViewById(R.id.gridView_week);
        WeekGridViewAdapter adapter=new WeekGridViewAdapter(context);
        gridView.setAdapter(adapter);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));

        popupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);
    }

}
