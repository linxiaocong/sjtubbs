package io.github.linxiaocong.sjtubbs.models;

public class Image {
    private String mUrl;
    private String mFileName;
    private String mAbsolutePath;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        mAbsolutePath = absolutePath;
    }

}
