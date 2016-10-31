package jwxt.cacher.cc.jwxt.views;

import android.content.Context;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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
        this.context=context;
        viewList=new ArrayList<>();
        for(int i=1;i<=25;i++){
            View view=View.inflate(context, R.layout.view_week_grid_view,null);
            if(i%5==1){
                view.setBackgroundResource(R.drawable.shape_week_choose2);
            }
            if(i==21){
                view.setBackgroundResource(R.drawable.shape_week_choose);
            }
            if(i>21){
                view.setBackgroundResource(R.drawable.shape_week_choose4);
            }
            TextView textView=(TextView) view.findViewWithTag("text");
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            return viewList.get(position);
        }else{
            return convertView;
        }
    }
    private int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }
}
