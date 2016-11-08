package hust.example.bluetooth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import hust.example.bluetooth.R;

public class StartActivity extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        listView=(ListView)findViewById(R.id.listView);
        SimpleAdapter adapter=new SimpleAdapter(this,getData(),R.layout.start_item,new String[]{"image","text"},new int[]{R.id.image,R.id.text});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    Intent intent=new Intent();
                    intent.setClass(StartActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    private List<HashMap<String,Object>> getData(){
        List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
        HashMap map1=new HashMap<String,Object>();
        map1.put("image",R.drawable.wifi);
        map1.put("text","wifi控制");
        list.add(map1);

        HashMap map2=new HashMap<String,Object>();
        map2.put("image",R.drawable.bluetooth);
        map2.put("text","蓝牙控制");
        list.add(map2);
        return list;
    }
}
