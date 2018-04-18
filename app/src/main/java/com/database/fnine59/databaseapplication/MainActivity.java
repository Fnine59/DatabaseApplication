package com.database.fnine59.databaseapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText nameString, ageString, heightString, inputIdString;
    Button addData, showAll, clearShow, deleteAll, deleteById,queryById,updateById;
    ListView showList;
    String querySql = "select * from stu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * 用于初始化
     */
    private void init(){
        showList = (ListView) findViewById(R.id.showList);
        nameString = (EditText) findViewById(R.id.name_input);
        ageString = (EditText) findViewById(R.id.age_input);
        heightString = (EditText) findViewById(R.id.height_input);
        inputIdString = (EditText) findViewById(R.id.inputID);
    }

    /**
     * 用于每次点击某些按钮清空上方文本框的输入
     */
    private void clearInput(){
        nameString.setText("");
        ageString.setText("");
        heightString.setText("");
    }

    /**
     * 监听添加数据按钮的点击事件
     */
    public void addData(View v){
        //首先检测用户输入是否为空
        if(!whetherDataIsNull()){//如果为空
            Toast.makeText(MainActivity.this, "请输入完整的数据！", Toast.LENGTH_LONG).show();
        }else{//如果不为空正常执行插入操作，因为一定是单条操作所以均不使用事务处理
            SQLiteDatabase sqLiteDatabase = null;
            try{
                Object[] values = openWritableDatabase();
                sqLiteDatabase = (SQLiteDatabase) values[0];
                ContentValues contentValues = (ContentValues) values[1];
                sqLiteDatabase.insert("stu", null, contentValues);
                updateShowList();
            }catch (Exception e){
                Log.e(MainActivity.class.toString(), e.toString());
            }finally {
                closeDatabase(sqLiteDatabase);
                clearInput();//清空用户输入
            }
        }
    }

    /**
     * 监听全部显示按钮的点击事件
     */
    public void showAll (View v){
        //其实就相当于更新一下列表内容
        updateShowList();
    }

    /**
     * 监听清除显示按钮的点击事件
     */
    public void clearShowAll (View v){
        ArrayList arrayList = new ArrayList();//新建一个空的arrayList传入
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.activity_listview, new String[]{"id","name","age","height"}, new int[]{R.id.id, R.id.name, R.id.age, R.id.height});
        showList.setAdapter(simpleAdapter);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //监听列表的点击事件
            }
        });
    }

    /**
     * 监听全部删除按钮的点击事件
     */
    public void deleteAll(View v){
        SQLiteDatabase sqLiteDatabase = null;
        try{
            Object[] values = openWritableDatabase();
            sqLiteDatabase = (SQLiteDatabase) values[0];
            ContentValues contentValues = (ContentValues) values[1];
            sqLiteDatabase.delete("stu","",new String[]{});//条件为空，永远为真，也就是删除全部数据
            updateShowList();
        }catch (Exception e){
            Log.e(MainActivity.class.toString(), e.toString());
        }finally {
            closeDatabase(sqLiteDatabase);
            clearInput();//清空用户输入
        }
    }

    /**
     * 监听Id删除按钮的点击事件
     */
    public void deleteById (View v){
        if(inputIdString.getText().toString() == null || (inputIdString.getText().toString()).equals("")){//如果根本没输入id，提示用户输入
            Toast.makeText(MainActivity.this, "请先输入您要指定的ID！", Toast.LENGTH_LONG).show();
        }else {//如果输入了，正常执行删除
            SQLiteDatabase sqLiteDatabase = null;
            try{
                Object[] values = openWritableDatabase();
                sqLiteDatabase = (SQLiteDatabase) values[0];
                ContentValues contentValues = (ContentValues) values[1];
                sqLiteDatabase.delete("stu","id=?",new String[]{inputIdString.getText().toString()});//条件为空，永远为真，也就是删除全部数据
                updateShowList();
            }catch (Exception e){
                Log.e(MainActivity.class.toString(), e.toString());
            }finally {
                closeDatabase(sqLiteDatabase);
            }
        }
    }

    /**
     * 监听Id查询按钮的点击事件
     * 其实和更新列表的方法是有共同点的，因为只用这一次，就不单独列函数了
     */
    public void queryById(View v){
        if(inputIdString.getText().toString() == null || (inputIdString.getText().toString()).equals("")){//如果根本没输入id，提示用户输入
            Toast.makeText(MainActivity.this, "请先输入您要指定的ID！", Toast.LENGTH_LONG).show();
        }else {//如果输入了，正常执行查询
            ArrayList arrayList = null;
            SQLiteDatabase sqLiteDatabase = null;
            try{
                arrayList = new ArrayList();
                sqLiteDatabase = openReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(querySql+" where id=?", new String[]{inputIdString.getText().toString()});
                while(cursor.moveToNext()){
                    HashMap hm = new HashMap();
                    hm.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                    hm.put("name",cursor.getString(cursor.getColumnIndex("name")));
                    hm.put("age",cursor.getInt(cursor.getColumnIndex("age")));
                    hm.put("height",cursor.getFloat(cursor.getColumnIndex("height")));
                    arrayList.add(hm);
                }
                cursor.close();
            }catch (Exception e){
                Log.e(MainActivity.class.toString(), e.toString());
            }finally {
                closeDatabase(sqLiteDatabase);
            }
            if(arrayList.size() == 0){
                Toast.makeText(MainActivity.this, "没有匹配结果！", Toast.LENGTH_LONG).show();
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.activity_listview, new String[]{"id","name","age","height"}, new int[]{R.id.id, R.id.name, R.id.age, R.id.height});
            showList.setAdapter(simpleAdapter);
            showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //监听列表的点击事件
                }
            });
        }
    }

    /**
     * 监听ID更新按钮的点击事件
     */
    public void updateById(View v){
        if(inputIdString.getText().toString() == null || (inputIdString.getText().toString()).equals("")){//如果根本没输入id，提示用户输入
            Toast.makeText(MainActivity.this, "请先输入您要更新内容的ID！", Toast.LENGTH_LONG).show();
        }else {//如果输入了，首先检测是否输入更新内容，然后执行更新
            if(!whetherDataIsNull()){
                Toast.makeText(MainActivity.this, "请输入完整的数据！", Toast.LENGTH_LONG).show();
            }else{
                SQLiteDatabase sqLiteDatabase = null;
                try{
                    Object[] values = openWritableDatabase();
                    sqLiteDatabase = (SQLiteDatabase) values[0];
                    ContentValues contentValues = (ContentValues) values[1];
                    contentValues.put("name", nameString.getText().toString());
                    contentValues.put("age", ageString.getText().toString());
                    contentValues.put("height", heightString.getText().toString());
                    sqLiteDatabase.update("stu", contentValues, "id=?", new String[]{inputIdString.getText().toString()});
                    updateShowList();
                }catch (Exception e){
                    Log.e(MainActivity.class.toString(), e.toString());
                }finally {
                    closeDatabase(sqLiteDatabase);
                    clearInput();//清空上方的输入
                }
            }
        }
    }

    /**
     * 用于初始化需要读写操作的数据库
     */
    private Object[] openWritableDatabase(){
        MyDbHelper myDbHelper = new MyDbHelper(this);
        SQLiteDatabase sqLiteDatabase = myDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",nameString.getText().toString());
        contentValues.put("age",ageString.getText().toString());
        contentValues.put("height", heightString.getText().toString());
        return new Object[]{sqLiteDatabase, contentValues};
    }

    /**
     * 用于初始化具有读权限的数据库
     */
    private SQLiteDatabase openReadableDatabase(){
        MyDbHelper myDbHelper = new MyDbHelper(this);
        SQLiteDatabase sqLiteDatabase = myDbHelper.getReadableDatabase();
        return sqLiteDatabase;
    }

    /**
     * 用于关闭数据库
     */
    private void closeDatabase(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.close();
    }

    /**
     * 用于监测上面的输入框中是否存在输入内容为空的情况，防止程序访问了空值导致的异常中止
     * 考虑成数据库中的三列都不可为空，所以这个函数不需要动态传参了，每次都检测三个输入框即可
     */
    private boolean whetherDataIsNull () {
        if(nameString.getText().toString() == null || (nameString.getText().toString()).equals("")  ){
            return false;
        }
        if(ageString.getText().toString() == null || (ageString.getText().toString()).equals("")){
            return false;
        }
        if(heightString.getText().toString() == null || (heightString.getText().toString()).equals("")){
            return false;
        }
        return true;
    }

    /**
     * 用于刷新列表的显示
     * 因为每次点击每一个按钮都要刷新下面的列表，因此抽象成一个函数方便代码复用
     */
    private void updateShowList(){
        ArrayList arrayList = null;
        SQLiteDatabase sqLiteDatabase = null;
        try{
            arrayList = new ArrayList();
            sqLiteDatabase = openReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(querySql+";", new String[]{});
            while(cursor.moveToNext()){
                HashMap hm = new HashMap();
                hm.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                hm.put("name",cursor.getString(cursor.getColumnIndex("name")));
                hm.put("age",cursor.getInt(cursor.getColumnIndex("age")));
                hm.put("height",cursor.getFloat(cursor.getColumnIndex("height")));
                arrayList.add(hm);
            }
            cursor.close();
        }catch (Exception e){
            Log.e(MainActivity.class.toString(), e.toString());
        }finally {
            closeDatabase(sqLiteDatabase);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.activity_listview, new String[]{"id","name","age","height"}, new int[]{R.id.id, R.id.name, R.id.age, R.id.height});
        showList.setAdapter(simpleAdapter);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //监听列表的点击事件
            }
        });
    }
}
