package com.twopibd.dactarbari.ambulance.listeners;

public class PicUploadListener {

    public  static  myPicUploadListener myPicUploadListener;
    public  static  interface myPicUploadListener {
        String onPicUploadSucced(String link);
        String onPicUploadFailed(String errorMessage);

    }

    public static void setMyPicUploadListener(PicUploadListener.myPicUploadListener listener) {
        PicUploadListener.myPicUploadListener = listener;
    }
}
