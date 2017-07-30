package com.example.rekhahindwar.applinksaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.rekhahindwar.applinksaver.R.drawable.myrect;

public class MainActivity extends AppCompatActivity {


    LinearLayout wall;
    EditText content;
    ImageButton post;
    ScrollView scroll;
    int selectedViews = 0;
    Drawable d;
    TextView textView;
    String baseurl = null;
    String title = null;
    String description = null;
    byte[] imgsrc = null;
    String info = null;
    public static final String PREFS_NAME = "LinkSaverApp";
    public static final String FAVORITES = "Favorite";
    static Activity activity;
    ArrayList<String> list;
    TextView tv;
    private ActionMode mActionMode;
    List<String> itemList = new ArrayList<String>();
    List<Integer> keys = new ArrayList<Integer>();
    SharedPreference sp = new SharedPreference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall);
        initialize();

        post.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String cntnt = content.getText().toString();
                wall.addView(createNewTextView(cntnt));
                content.setText("");
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                activity = getActivity();
                new SharedPreference().addTextView(activity, cntnt);
            }
        });
        tv = new TextView(this);
        setActivity(this);
        activity = getActivity();
        list = sp.createTextViewList(activity);

        if(list != null){
            int count = 0;
            while(count < list.size()){
                String contnt = list.get(count);
                //System.out.println("Content : " + contnt);
                wall.addView(createNewTextView(contnt));
                count++;
            }
        }
            Bundle gotBasket = getIntent().getExtras();
            baseurl = gotBasket.getString("url");
            title = gotBasket.getString("title");
            description = gotBasket.getString("des");
            if(baseurl != null && title != null)
            //imgsrc = gotBasket.getByteArray("img");
                info = baseurl + "\n" + "Title : " + title;
            if (description != null)
                info = info + "\nDescription : " + description;
            if(info != null)
                content.setText(info);
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView = new TextView(this);
        int id = View.generateViewId();
        textView.setId(id);
        textView.setTextColor(Color.BLACK);
        textView.setClickable(true);
        lparams.gravity = Gravity.RIGHT;
        lparams.setMargins(200,10,10,0);
        textView.setLayoutParams(lparams);
        textView.setTextSize(15);

        textView.setBackgroundColor(Color.WHITE);
        textView.setText(text);
        Linkify.addLinks(textView, Linkify.WEB_URLS);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(Color.WHITE);
        gd.setStroke(2, Color.BLACK);
        gd.setCornerRadius(20.0f);
        textView.setPadding(10,10,10,10);
        textView.setGravity(Gravity.LEFT);
        textView.setBackground(gd);


        textView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                //boolean selected = !v.isSelected();
                selectedViews++;
                TextView tv = (TextView) findViewById(v.getId());
                boolean selected = !tv.isSelected();
                tv.setSelected(selected);
                tv.setBackgroundColor(selected ? Color.parseColor("#808080"): Color.WHITE);
                if(selected) {
                    mActionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
                    mActionMode.setTitle(selectedViews + " selected");
                    itemList.add(tv.getText().toString());
                }
                return true;
            }
        });
        textView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(selectedViews > 0){
                    TextView tv = (TextView) findViewById(v.getId());
                    boolean selected = !tv.isSelected();
                    tv.setSelected(selected);
                    tv.setBackgroundColor(selected ? Color.parseColor("#808080"): Color.WHITE);
                    if(selected){
                        selectedViews++;
                        mActionMode.setTitle(selectedViews+" selected");
                        itemList.add(tv.getText().toString());
                    }else{
                        selectedViews--;
                        if(selectedViews == 0) {
                            mActionMode.finish();
                            Intent intent = getIntent();
                            finish();
                            getActivity().finish();
                            startActivity(intent);
                        }
                        else
                            mActionMode.setTitle(selectedViews+ " selected");
                        itemList.remove(tv.getText().toString());
                    }
                }
            }
        });

        //new FetchImageAsyncTask().execute();
        return textView;
    }

    private void initialize() {
        wall = (LinearLayout) findViewById(R.id.wall);
        content = (EditText) findViewById(R.id.content);
        post = (ImageButton) findViewById(R.id.post);
        scroll = (ScrollView) findViewById(R.id.scroll);
    }

    public Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity mContext) {
        activity = mContext;
    }
    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()){
                case R.id.item_delete :
                    sp.deleteItems(getActivity(), itemList);
                    Intent intent = getIntent();
                    finish();
                    getActivity().finish();
                    startActivity(intent);
                    return true;

            }
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
