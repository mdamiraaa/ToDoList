package com.example.sqlitetodolist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText text;
    private Button addBtn, readBtn, clearBtn;
    private DBHelper dbHelper;
    private static final String TAG = "MainActivity";

    private SharedPreferences preferences;
    SQLiteDatabase database;

    ArrayList<MyAdapter> listData = new ArrayList<MyAdapter>();
    MyCustomAdapter myCustomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text= findViewById(R.id.editText);
        addBtn = findViewById(R.id.addBtn);
        clearBtn = findViewById(R.id.clearBtn);
        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        preferences = getPreferences(MODE_PRIVATE);
        database =  dbHelper.getWritableDatabase();

        readData();

    }

    @Override
    public void onClick(View v) {

        String content = text.getText().toString();
        ContentValues contentValues = new ContentValues();
        database =  dbHelper.getWritableDatabase();
        switch (v.getId()){

            case R.id.addBtn:
                contentValues.put("task",content);
                long row = database.insert("TodoList",null,contentValues);
                Log.e(TAG,"works insert " + row);
                readData();
                break;
            case R.id.clearBtn:
                int rowCount = database.delete("TodoList", null, null);
                Log.e(TAG, "deleted: "+rowCount);
                readData();
                break;
        }

        dbHelper.close();
    }
    void readData(){
        listData.clear();
        Cursor cursor = database.query("TodoList", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            Log.e(TAG," read workks too22");
            int idIndex = cursor.getColumnIndex("id");
            int taskIndex = cursor.getColumnIndex("task");

            do {
                listData.add(new MyAdapter(cursor.getString(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("task"))));
                Log.e(TAG, "onClick: "+cursor.getInt(idIndex)+cursor.getString(taskIndex));
            }while (cursor.moveToNext());
        }

        myCustomAdapter = new MyCustomAdapter(listData);
        ListView lv = findViewById(R.id.listView);
        lv.setAdapter(myCustomAdapter);

        cursor.close();

    }

    private class MyCustomAdapter extends BaseAdapter{
        public ArrayList<MyAdapter>  listData;

        public MyCustomAdapter (ArrayList<MyAdapter>  listData){
            this.listData=listData;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView = layoutInflater.inflate(R.layout.task_layout,null);

            final MyAdapter s =listData.get(position);

            TextView id = myView.findViewById(R.id.tvId);
            id.setText(s.id);

            TextView task = myView.findViewById(R.id.tvTask);
            task.setText(s.task);

            Button dltBtn = myView.findViewById(R.id.dltBtn);
            dltBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database = dbHelper.getWritableDatabase();
                    Log.e(TAG,"onclick works");
                    String [] SelectionArgs={s.id};
                    database.delete("TodoList","id=?",SelectionArgs);
                    readData();
                }
            });
            return myView;
        }
    }
}
