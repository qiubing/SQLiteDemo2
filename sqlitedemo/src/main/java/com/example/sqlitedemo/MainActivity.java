package com.example.sqlitedemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    private static final String TAG = "SQLiteDemo";
    private SQLiteDBManager mDBManager;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBManager = new SQLiteDBManager(this);
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDB();
    }

    public void add(View view){
        Log.e(TAG,"add()...");
        ArrayList<Person> persons = new ArrayList<>();
        Person person1 = new Person("zhangsan",18,"a programmer");
        Person person2 = new Person("lisi",20,"a teacher");
        Person person3 = new Person("wangwu",25,"a football player");
        Person person4 = new Person("bing",27,"a nice person");
        Person person5 = new Person("winne",24,"a beautiful girl");

        persons.add(person1);
        persons.add(person2);
        persons.add(person3);
        persons.add(person4);
        persons.add(person5);

        mDBManager.addPersonList(persons);
    }

    public void update(View view){
        Log.e(TAG,"update()...");
        Person person = new Person();
        person.name = "lisi";
        person.age = 30;
        mDBManager.updatePersonAge(person);
    }

    public void delete(View view){
        Log.e(TAG,"delete()...");
        Person person = new Person();
        person.age = 30;
        mDBManager.deleteOldPerson(person);
    }

    public void query(View view){
        Log.e(TAG,"query()...");
        List<Person> persons = mDBManager.queryAllPersons();
        ArrayList<Map<String,String>> list = new ArrayList<Map<String, String>>();
        for (Person person : persons){
            HashMap<String,String> map = new HashMap<>();
            map.put("name",person.name);
            map.put("info",person.age + " years old, " + person.info);
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,
                new String[]{"name","info"},new int[]{android.R.id.text1,android.R.id.text2});
        mListView.setAdapter(adapter);
    }
}
