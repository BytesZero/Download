package com.zsl.download.entity;

import java.io.Serializable;

/**
 * FileInof
 * Created by zsl on 15/5/20.
 */
public class Fileinfo implements Serializable{
    private int id;
    private String fileName,url;
    private int length,filished;

    public Fileinfo(int id, String fileName, String url, int length, int filished) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.length = length;
        this.filished = filished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFilished() {
        return filished;
    }

    public void setFilished(int filished) {
        this.filished = filished;
    }

    @Override
    public String toString() {
        return "Fileinfo{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", filished=" + filished +
                '}';
    }
}
