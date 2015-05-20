package com.zsl.download.downloadtask;

import android.content.Context;

import com.zsl.download.database.MyDataBase;
import com.zsl.download.entity.Fileinfo;
import com.zsl.download.entity.ThreadInfo;
import com.zsl.download.services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zsl on 15/5/20.
 */
public class DownloadTask {
    private Context context;
    private Fileinfo fileinfo;

    public DownloadTask(Context context, Fileinfo fileinfo) {
        this.context = context;
        this.fileinfo = fileinfo;
    }

    class DownloadThread extends Thread {
        private ThreadInfo threadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            //想数据库插入线程信息
            MyDataBase.init(context).getDb().save(threadInfo);

            HttpURLConnection conn=null;
            RandomAccessFile raf=null;

            try {
                URL url=new URL(threadInfo.getUrl());
                conn= (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");

                //设置下载位置
                int start=threadInfo.getStart()+threadInfo.getFinished();
                conn.setRequestProperty("Range","bytes="+start+"-"+threadInfo.getEnd());

                //设置文件写入位置
                File file=new File(DownloadService.DOWNLOAD_PATH,fileinfo.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);

                //开始下载

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }


        }
    }
}
