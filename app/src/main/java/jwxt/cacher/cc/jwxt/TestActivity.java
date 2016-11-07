package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.transition.ActionBarTransition;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    public void onTestButtonClick(View view) {
        popupWindow = new PopupWindow(context);
        View popupView = View.inflate(context, R.layout.dlg_week_choose, null);
        final WeekGridViewAdapter adapter = new WeekGridViewAdapter(context);
        final List<View> viewList = adapter.getViewList();
        final Button buttonOdd = (Button) popupView.findViewById(R.id.dlg_week_choose_odd);
        final Button buttonEven = (Button) popupView.findViewById(R.id.dlg_week_choose_Even);
        final Button buttonFull = (Button) popupView.findViewById(R.id.dlg_week_choose_Full);
        for(int i=0;i<viewList.size();i++){
            final TextView textView = (TextView) viewList.get(i).findViewWithTag("text");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setSelected(!textView.isSelected());
                    switch (adapter.getSelectedStatus()){
                        case 1:
                            buttonOdd.setSelected(true);
                            break;
                        case 2:
                            buttonEven.setSelected(true);
                            break;
                        case 3:
                            buttonFull.setSelected(true);
                            break;
                        case 4:
                            buttonOdd.setSelected(false);
                            buttonEven.setSelected(false);
                            buttonFull.setSelected(false);
                            break;
                    }
                }
            });
        }

        buttonOdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOdd.setSelected(!buttonOdd.isSelected());
                if (buttonEven.isSelected()) {
                    buttonEven.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    buttonFull.setSelected(false);
                }
                if (buttonOdd.isSelected()) {
                    for (int i = 1; i <= 25; i++) {
                        View view = viewList.get(i - 1);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        if (i % 2 == 1) {
                            textView.setSelected(true);
                        } else {
                            textView.setSelected(false);
                        }
                    }
                } else {
                    for (int i = 1; i <= 25; i++) {
                        if (i % 2 == 1) {
                            View view = viewList.get(i - 1);
                            TextView textView = (TextView) view.findViewWithTag("text");
                            textView.setSelected(false);
                        }
                    }
                }

            }
        });

        buttonEven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEven.setSelected(!buttonEven.isSelected());
                if (buttonOdd.isSelected()) {
                    buttonOdd.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    buttonFull.setSelected(false);
                }
                if (buttonEven.isSelected()) {
                    for (int i = 1; i <= 25; i++) {
                        View view = viewList.get(i - 1);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        if (i % 2 == 0) {

                            textView.setSelected(true);
                        } else {
                            textView.setSelected(false);
                        }
                    }
                } else {
                    for (int i = 1; i <= 25; i++) {
                        if (i % 2 == 0) {
                            View view = viewList.get(i - 1);
                            TextView textView = (TextView) view.findViewWithTag("text");
                            textView.setSelected(false);
                        }
                    }
                }
            }
        });
        buttonFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFull.setSelected(!buttonFull.isSelected());
                if (buttonOdd.isSelected()) {
                    buttonOdd.setSelected(false);
                }
                if (buttonEven.isSelected()) {
                    buttonEven.setSelected(false);
                }
                if (buttonFull.isSelected()) {
                    for (int i = 0; i < 25; i++) {
                        View view = viewList.get(i);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        textView.setSelected(true);
                    }
                } else {
                    for (int i = 0; i < 25; i++) {
                        View view = viewList.get(i);
                        TextView textView = (TextView) view.findViewWithTag("text");
                        textView.setSelected(false);
                    }
                }
            }
        });
        final GridView gridView = (GridView) popupView.findViewById(R.id.gridView_week);
        gridView.setAdapter(adapter);
        /*修复少1像素Bug*/
        gridView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = gridView.getMeasuredHeight() * 5 + 1;
        gridView.setLayoutParams(params);
        /********/
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.classroom_search_white));
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
}
