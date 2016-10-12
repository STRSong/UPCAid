package jwxt.cacher.cc.jwxt;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jwxt.cacher.cc.jwxt.info.BookInfo;

/**
 * Created by xhaiben on 2016/10/11.
 */

public class LibListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BookInfo> bookList;
    private Bitmap bitmap;
    private SZSDConnection szsdConnection;
    private Handler renewHandler;
    private AlertDialog alertDialog;
    private String checkCode = null;

    public LibListAdapter(final Context context, ArrayList<BookInfo> bookList, Bitmap bitmap, SZSDConnection szsdConnection) {
        this.context = context;
        this.bitmap = bitmap;
        this.bookList = bookList;
        this.szsdConnection = szsdConnection;
        renewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.library_item, null);
        TextView textViewBookName = (TextView) linearLayout.getChildAt(0);
        TextView textViewAuthorName = (TextView) linearLayout.getChildAt(1);
        TextView textViewreturnDate = (TextView) linearLayout.getChildAt(2);
        final Button renewButton = (Button) linearLayout.getChildAt(3);
        textViewBookName.setText(this.bookList.get(position).getBookName());
        textViewAuthorName.setText(this.bookList.get(position).getAuthorName());
        textViewreturnDate.setText(this.bookList.get(position).getReturnDate());
        renewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View renewBookView = LayoutInflater.from(context).inflate(R.layout.renew_book_popupwindow, null);
                ImageView imageView = (ImageView) renewBookView.findViewById(R.id.image_captcha);
                imageView.setImageBitmap(bitmap);
                final EditText editText = (EditText) renewBookView.findViewById(R.id.edit_captcha);
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_NUMBER);
                if(checkCode!=null){
                    editText.setText(checkCode);
                    editText.setSelection(checkCode.length());
                }
                Button renewBtn = (Button) renewBookView.findViewById(R.id.btn_renewBook);
                renewBtn.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            v.setBackgroundResource(R.drawable.ic_week_set_button_press);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.setBackgroundResource(R.drawable.ic_week_set_button);
                        }
                        return false;
                    }
                });
                renewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkCode==null){
                            checkCode=editText.getText().toString();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String result = szsdConnection.renewBook(bookList.get(position).getBarCode(),
                                        bookList.get(position).getCheckNum(), editText.getText().toString());
                                Message msg = renewHandler.obtainMessage();
                                msg.obj = result;
                                renewHandler.sendMessage(msg);
                            }
                        }).start();
                        if (alertDialog.isShowing()) {
                            alertDialog.cancel();
                        }
                    }
                });
                alertDialog=new AlertDialog.Builder(context).create();
                alertDialog.show();
                alertDialog.getWindow().setContentView(renewBookView);
                //显示输入法
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });
        return linearLayout;
    }
}
