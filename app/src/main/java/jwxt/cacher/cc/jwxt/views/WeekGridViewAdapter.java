package jwxt.cacher.cc.jwxt.views;

import android.content.Context;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import jwxt.cacher.cc.jwxt.R;

/**
 * Created by xhaiben on 2016/10/31.
 */

public class WeekGridViewAdapter extends BaseAdapter {
    private List<View> viewList;
    private Context context;

    public WeekGridViewAdapter(Context context) {
        this.context = context;
        viewList = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            View view = View.inflate(context, R.layout.view_week_grid_view, null);
            if (i % 5 == 1) {
                view.setBackgroundResource(R.drawable.shape_week_choose2);
            }
            if (i == 21) {
                view.setBackgroundResource(R.drawable.shape_week_choose1);
            }
            if (i > 21) {
                view.setBackgroundResource(R.drawable.shape_week_choose4);
            }
            final TextView textView = (TextView) view.findViewWithTag("text");
            textView.setText(String.valueOf(i));

            viewList.add(view);
        }
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.viewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return viewList.get(position);
        } else {
            return convertView;
        }
    }

    public List<View> getViewList() {
        return this.viewList;
    }

    private int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }
    public void setTextViewSelected(int position,boolean b){
        TextView textView=(TextView) viewList.get(position).findViewWithTag("text");
        textView.setSelected(b);
    }
    private boolean isOdd() {
        boolean flag = true;
        for (int i = 1; i <= 25; i++) {
            TextView textView = (TextView) viewList.get(i-1).findViewWithTag("text");
            if (i % 2 == 1 && textView.isSelected() == false) {
                flag = false;
                break;
            }
            if (i % 2 == 0 && textView.isSelected() == true) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    private boolean isEven(){
        boolean flag = true;
        for (int i = 1; i <= 25; i++) {
            TextView textView = (TextView) viewList.get(i-1).findViewWithTag("text");
            if (i % 2 == 0 && textView.isSelected() == false) {
                flag = false;
                break;
            }
            if (i % 2 == 1 && textView.isSelected() == true) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    private boolean isFull(){
        boolean flag=true;
        for(int i=1;i<=25;i++){
            TextView textView = (TextView) viewList.get(i-1).findViewWithTag("text");
            if(textView.isSelected()==false){
                flag=false;
                break;
            }
        }
        return flag;
    }
    public int getSelectedStatus(){
        /*1 单周 2双周 3全选 4其他*/
        if(isOdd()){
            return 1;
        }else if(isEven()){
            return 2;
        }else if(isFull()){
            return 3;
        }else{
            return 4;
        }
    }
}
