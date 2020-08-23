package com.anysoftkeyboard.wordusageupload;

import android.util.Log;

import com.anysoftkeyboard.nextword.BuildConfig;
import com.anysoftkeyboard.nextword.NextWordsContainer;
import com.anysoftkeyboard.nextword.NextWordsStorage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KasahorowWordsUploader {

    private static String PATH = ".kasahorow.org/metrics/android";
    private static String TAG = "KasaWordsUploader";
    private Request.Builder requestBuilder;
    private NextWordsStorage mNextWordsStorage;

    public KasahorowWordsUploader(NextWordsStorage nextWordsStorage) {
        this.mNextWordsStorage = nextWordsStorage;
        this.requestBuilder = new Request.Builder().addHeader("Content-Type", "application/text");
    }

    public int upload(String locale) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isEmpty = true;
        for (NextWordsContainer container : mNextWordsStorage.loadStoredNextWords()) {
            isEmpty = false;
            if (BuildConfig.DEBUG) Log.d(TAG, "Loaded for "+locale +" " + container);
            KasahorowData kasahorowData = new KasahorowData(container.word);
            stringBuilder.append(kasahorowData).append("\n");
        }
        int code = 0;
        if(isEmpty) return code;
        String url = "https://"+locale+PATH;
        if (BuildConfig.DEBUG) Log.d(TAG, "upload url " + url+ "data: "+stringBuilder.toString());
        RequestBody body = new FormBody.Builder().add(locale,stringBuilder.toString()).build();
        Request request = requestBuilder.url(url).post(body).build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (BuildConfig.DEBUG) Log.d(TAG, "response code for " + response.code() );
            code = response.code();
        } catch (IOException e) {
            Log.e(TAG,"HTTP upload request failed",e);
        }
        return code;
    }

    @SuppressWarnings("JdkObsolete")
    static class KasahorowData {
        private String word;
        private String date;

        KasahorowData(String word, Date date) {
            this.word = word;
            this.date = parseDate(date);
        }

        KasahorowData(String word) {
            this(word, new Date());
        }

        @Override
        public String toString() {
            return word+"\t"+date;
        }

        private String parseDate(Date date) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
        }
    }
}
