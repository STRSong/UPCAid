package jwxt.cacher.cc.jwxt;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

/**
 * Created by xhaiben on 2016/8/8.
 */
public class ScoreActivity extends AppCompatActivity {
    private Context context = null;
    private SearchView searchView = null;
    private SZSDConnection connection;
    private ListView listView;
    private Handler handlerListView;
    private Handler handlerProgressbar;
    private Handler handlerDetail;
    private ProgressDialog progressDialog;
    private AutoCompleteTextView mEdit;
    private Thread threadWholeScore;
    private List<HashMap<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        context = this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        if (threadWholeScore != null && !threadWholeScore.isInterrupted()) {
                            threadWholeScore.interrupt();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        this.initHandler();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setOnMenuItemClickListener(getMenuItemClickListener());
        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
//        searchView = (SearchView) findViewById(R.id.search_score_1);
        //setSearchViewProperties();
        listView = (ListView) this.findViewById(R.id.listView_Score);

        connection = SZSDConnection.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_menu, menu);
        this.setSearchViewProperties();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener getMenuItemClickListener() {
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.whole_score:
                        final String kksj = "";
                        threadWholeScore = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg1 = handlerProgressbar.obtainMessage();
                                msg1.arg1 = 1;
                                msg1.obj = progressDialog;
                                handlerProgressbar.sendMessage(msg1);
                                data = connection.getScore(kksj);
                                if (data.size() == 1 && data.get(0).get("评教未完成").equals("")) {
                                    //评教未完成不能查成绩
                                    Message msg = handlerListView.obtainMessage();
                                    msg.arg1 = 1;
                                    handlerListView.sendMessage(msg);
                                } else {
                                    SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.score_item, new String[]{
                                            "kcmc", "kclb", "zcj", "xf"}, new int[]{R.id.kksj, R.id.kcmc, R.id.zjc, R.id.xf});

                                    Message msg = handlerListView.obtainMessage();
                                    msg.obj = adapter;
                                    handlerListView.sendMessage(msg);


                                }
                                msg1 = handlerProgressbar.obtainMessage();
                                msg1.arg1 = 2;
                                msg1.obj = progressDialog;
                                handlerProgressbar.sendMessage(msg1);
                            }
                        });
                        threadWholeScore.start();
                        break;
                }
                return false;
            }
        };
    }

    private View.OnClickListener getNavigationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreActivity.this.finish();
            }
        };
    }

    private void setSearchViewProperties() {
        searchView = (SearchView) findViewById(R.id.search_score_1);
        searchView.setIconified(false);
        searchView.setQueryHint("开课学期");
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        ImageView ico = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView mGoButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_go_btn);
        mGoButton.setImageDrawable(ico.getDrawable());
        ico.setVisibility(View.GONE);
        ico.setImageDrawable(null);

        mEdit = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mEdit.setThreshold(1);
        //默认查询学期
        String initStr = "2016-2017-1";
        mEdit.setText(initStr);
        mEdit.setSelection(initStr.length());
        mEdit.setTextColor(Color.WHITE);

        String[] columnNames = {"_id", "text"};
        final MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] array = getResources().getStringArray(R.array.kksjChoice);
        String[] temp = new String[2];
        int id = 0;
        for (String item : array) {
            temp[0] = Integer.toString(id++);
            temp[1] = item;
            cursor.addRow(temp);
        }
        String[] from = {"text"};
        int[] to = {R.id.search_textView};

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final CursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.search_item, cursor, from, to, 0);
        searchView.setSuggestionsAdapter(simpleCursorAdapter);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor1 = simpleCursorAdapter.getCursor();
                searchView.setQuery(cursor1.getString(1), false);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final String kksj = query;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg1 = handlerProgressbar.obtainMessage();
                        msg1.arg1 = 1;
                        msg1.obj = progressDialog;
                        handlerProgressbar.sendMessage(msg1);
                        data = connection.getScore(kksj);
                        System.out.println(data);
                        if (data.size() == 1 && data.get(0).containsKey("评教未完成")) {
                            //评教未完成不能查成绩
                            Message msg = handlerListView.obtainMessage();
                            msg.arg1 = 1;
                            handlerListView.sendMessage(msg);
                        } else {
                            SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.score_item, new String[]{
                                    "kcmc", "kclb", "zcj", "xf"}, new int[]{R.id.kksj, R.id.kcmc, R.id.zjc, R.id.xf});

                            Message msg = handlerListView.obtainMessage();
                            msg.obj = adapter;
                            handlerListView.sendMessage(msg);


                        }
                        msg1 = handlerProgressbar.obtainMessage();
                        msg1.arg1 = 2;
                        msg1.obj = progressDialog;
                        handlerProgressbar.sendMessage(msg1);
                    }
                }).start();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initHandler() {
        handlerListView = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //评教未完成
                if (msg.arg1 == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("");
                    builder.setMessage("评教未完成，无法获取成绩。");
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                } else {
                    final SimpleAdapter adapter = (SimpleAdapter) msg.obj;
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final String href = data.get(position).get("xx");
                            if (href == null) {
                                Toast.makeText(context, "该课程无详细信息", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject jsonObject = connection.getScoreDetail(href);
                                    if (jsonObject != null) {
                                        Message msg = handlerDetail.obtainMessage();
                                        msg.obj = jsonObject;
                                        handlerDetail.sendMessage(msg);
                                    }
                                }
                            }).start();
                        }
                    });
                    mEdit.setFocusable(false);
                    mEdit.setFocusableInTouchMode(true);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                }

            }
        };
        handlerDetail = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                JSONObject jsonObject = (JSONObject) msg.obj;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("总成绩：" + jsonObject.getString("zcj"));
                View dialogView = View.inflate(context, R.layout.view_score_detail, null);
                TextView pscjText = (TextView) dialogView.findViewById(R.id.tv_score_pscj);
                TextView pscjblText = (TextView) dialogView.findViewById(R.id.tv_score_pscjbl);
                TextView qzcjText = (TextView) dialogView.findViewById(R.id.tv_score_qzcj);
                TextView qzcjblText = (TextView) dialogView.findViewById(R.id.tv_score_qzcjbl);
                TextView qmcjText = (TextView) dialogView.findViewById(R.id.tv_score_qmcj);
                TextView qmcjblText = (TextView) dialogView.findViewById(R.id.tv_score_qmcjbl);
                String pscj = jsonObject.getString("pscj");
                String pscjbl = jsonObject.getString("pscjbl");
                String qzcj = jsonObject.getString("qzcj");
                String qzcjbl = jsonObject.getString("qzcjbl");
                String qmcj = jsonObject.getString("qmcj");
                String qmcjbl = jsonObject.getString("qmcjbl");
                pscjText.setText(pscj.equals("?") ? "无" : pscj);
                pscjblText.setText(pscjbl.equals("?") ? "无" : pscjbl);
                qzcjText.setText(qzcj.equals("?") ? "无" : qzcj);
                qzcjblText.setText(qzcj.equals("?") ? "无" : qzcjbl);
                qmcjText.setText(qmcj.equals("?") ? "无" : qmcj);
                qmcjblText.setText(qmcj.equals("?") ? "无" : qmcjbl);
//                StringBuffer dialogMsg = new StringBuffer();
//                dialogMsg.append("平时成绩：" + jsonObject.getString("pscj") + "\t" +
//                        "平时成绩比例：" + jsonObject.getString("pscjbl") + "\n\n");
//                dialogMsg.append("期中成绩：" + jsonObject.getString("qzcj") + "\t" +
//                        "期中成绩比例：" + jsonObject.getString("qzcjbl") + "\n\n");
//                dialogMsg.append("期末成绩：" + jsonObject.getString("qmcj") + "\t" +
//                        "期末成绩比例：" + jsonObject.getString("qmcjbl") + "\n");
//                builder.setMessage(dialogMsg);
                builder.setView(dialogView);
                builder.create().show();
            }
        };
        handlerProgressbar = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //ProgressBar progressBar=(ProgressBar)msg.obj;
                int command = msg.arg1;
                switch (command) {
                    case 1:
                        ProgressDialog dialog = (ProgressDialog) msg.obj;
                        dialog.show();
                        break;
                    case 2:
                        dialog = (ProgressDialog) msg.obj;
                        dialog.dismiss();
                        break;
                }
            }
        };
    }

}
