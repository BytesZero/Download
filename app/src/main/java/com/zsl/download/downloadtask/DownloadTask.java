package com.zsl.download.downloadtask;

import android.content.Context;
import android.content.Intent;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;
import com.zsl.download.database.MyDataBase;
import com.zsl.download.entity.Fileinfo;
import com.zsl.download.entity.ThreadInfo;
import com.zsl.download.services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by zsl on 15/5/20.
 */
public class DownloadTask {
    private Context context;
    private Fileinfo fileinfo;

    private int finished = 0;

    public   boolean isPuse=false;

    public DownloadTask(Context context, Fileinfo fileinfo) {
        this.context = context;
        this.fileinfo = fileinfo;
    }
    public void download(){
        //读取数据库的线程信息
        QueryBuilder qb=new QueryBuilder(ThreadInfo.class).where(ThreadInfo.COL_URL+ "=?", new String[]{fileinfo.getUrl()});
        ArrayList<ThreadInfo> threadInfos=MyDataBase.init(context).getDb().query(qb);

        Logger.e(threadInfos.toString());

        ThreadInfo threadInfo=null;
        if (threadInfos.size()==0){
            //初始化线程信息
            threadInfo=new ThreadInfo(0,fileinfo.getUrl(),0,fileinfo.getLength(),0);
        }else{
            threadInfo=threadInfos.get(0);
        }
        //创建子线程开始下载
        new DownloadThread(threadInfo).start();

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
            Logger.e(threadInfo.toString());
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input=null;

            try {
                URL url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");

                //设置下载位置
                int start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());

                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, fileinfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                finished += threadInfo.getFinished();

                //开始下载
                if (conn.getResponseCode() == 206) {
                    //读取数据
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024*4];
                    int len = -1;
                    long time=System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer,0,len);
                        //把进度发送广播给Actvity
                        finished+=len;
                        if (System.currentTimeMillis()-time>500) {
                            intent.putExtra("finished", finished * 100 / fileinfo.getLength());
                            context.sendBroadcast(intent);
                        }
                        if (isPuse){
                            threadInfo.setFinished(finished);
                            MyDataBase.init(context).getDb().save(threadInfo);
                            return;
                        }
                    }
                    MyDataBase.init(context).getDb().delete(threadInfo);
                }

                //在暂停时，保存下载进度


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    //各种关闭
                    conn.disconnect();
                    raf.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }
    }
}
