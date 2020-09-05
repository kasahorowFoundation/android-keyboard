package com.anysoftkeyboard.wordusageupload;

import android.content.Context;
import com.anysoftkeyboard.nextword.NextWordsStorage;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public final class ObservableKasahorowWordsUpload implements ObservableOnSubscribe<Integer> {

    private final Context mContext;
    private final String mLocale;

    private ObservableKasahorowWordsUpload(Context context, String locale) {
        this.mContext = context;
        this.mLocale = locale;
    }

    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) {
        final NextWordsStorage storage = new NextWordsStorage(mContext, mLocale);
        KasahorowWordsUploader kasahorowWordsUploader = new KasahorowWordsUploader(storage);
        int code = 0;
        try {
            code = kasahorowWordsUploader.upload(mLocale);
        } catch (Exception e) {
            if (!emitter.isDisposed()) {
                emitter.onError(e);
            }
        }
        if (!emitter.isDisposed()) {
            emitter.onNext(code);
            emitter.onComplete();
        }
    }

    public static Observable<Integer> create(Context context, String locale) {
        return Observable.create(new ObservableKasahorowWordsUpload(context, locale));
    }
}
