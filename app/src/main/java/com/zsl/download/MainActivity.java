package com.zsl.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.litesuits.orm.db.DataBase;
import com.zsl.download.database.MyDataBase;
import com.zsl.download.entity.Fileinfo;
import com.zsl.download.services.DownloadService;


public class MainActivity extends AppCompatActivity {
    private TextView tv_fileName;
    private Button bt_start,bt_stop;
    private ProgressBar pb_plan;

    DataBase db;

    Fileinfo fileinfo;

    String url="http://dlsw.baidu.com/sw-search-sp/soft/2a/25677/QQ_V4.0.2.1427684136.dmg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        //得到数据库对象
        db=MyDataBase.init(getApplicationContext()).getDb();


        initView();
        initData();
        initEvent();


    }

    private void initEvent() {
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo",fileinfo);
                startService(intent);
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileInfo",fileinfo);
                startService(intent);
            }
        });
    }

    private void initData() {
        //创建文件信息
        fileinfo=new Fileinfo(0,"QQ for Mac",url,0,0);
        tv_fileName.setText(fileinfo.getFileName());
        db.save(fileinfo);

    }

    private void initView() {
        tv_fileName= (TextView) findViewById(R.id.list_item_tv_filename);
        bt_start= (Button) findViewById(R.id.list_item_bt_start);
        bt_stop= (Button) findViewById(R.id.list_item_bt_stop);
        pb_plan= (ProgressBar) findViewById(R.id.list_item_pb_plan);
    }
}
