package com.zsl.download.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.orhanobut.logger.Logger;
import com.zsl.download.downloadtask.DownloadTask;
import com.zsl.download.entity.Fileinfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载的Service
 * Created by zsl on 15/5/20.
 */
public class DownloadService extends Service {

    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/Download/";


    //开始下载
    public static final String ACTION_START = "ACTION_START";
    //停止下载
    public static final String ACTION_STOP = "ACTION_STOP";
    //更新进度
    public static final String ACTION_UPDATE = "ACTION_UPDATE";

    //消息表示
    public static final int MSG_INIT = 0;

    private DownloadTask downloadTask;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {//开始下载
            Fileinfo fileinfo = (Fileinfo) intent.getSerializableExtra("fileInfo");
            Logger.e(ACTION_START + "" + fileinfo);

            new InitThread(fileinfo).start();

        } else if (ACTION_STOP.equals(action)) {//停止下载
            Fileinfo fileinfo = (Fileinfo) intent.getSerializableExtra("fileInfo");
            Logger.e(ACTION_STOP + "" + fileinfo);

            if (downloadTask != null) {

                downloadTask.isPuse = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT: {
                    Fileinfo fileinfo = (Fileinfo) msg.obj;
                    Logger.e("MSG_INIT" + fileinfo.getLength());
                    //启动下载任务
                    downloadTask = new DownloadTask(DownloadService.this, fileinfo);
                    downloadTask.download();
                    break;
                }
            }
        }
    };


    /**
     * 下载线程
     */
    class InitThread extends Thread {
        private Fileinfo fileinfo;

        public InitThread(Fileinfo fileinfo) {
            this.fileinfo = fileinfo;
        }

        @Override
        public void run() {

            //下载
            HttpURLConnection urlConnection = null;
            RandomAccessFile raf = null;
            Logger.e(fileinfo.getUrl());

            try {
                URL url = new URL(fileinfo.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                int length = -1;
                if (urlConnection.getResponseCode() == 200) {
                    length = urlConnection.getContentLength();
                }

                if (length < 1) {
                    return;
                }

                //判断是否存在文件夹
                File dir = new File(DOWNLOAD_PATH);
                if (dir.exists()) {
                    dir.mkdir();
                }
                //创建文件
                File file = new File(dir, fileinfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);

                fileinfo.setLength(length);
                handler.obtainMessage(MSG_INIT, fileinfo).sendToTarget();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
