package jwxt.cacher.cc.jwxt.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhaiben on 2016/10/26.
 */

public class CourseAdapter extends RecyclingPagerAdapter {
    private final List<View> mList;
    private final Context mContext;

    public CourseAdapter(Context context) {
        mList = new ArrayList<>();
        mContext = context;
    }

    public void addAll(List<View> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
