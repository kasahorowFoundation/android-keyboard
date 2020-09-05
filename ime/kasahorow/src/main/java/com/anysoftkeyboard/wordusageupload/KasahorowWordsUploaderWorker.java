package com.anysoftkeyboard.wordusageupload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import com.anysoftkeyboard.dictionaries.DictionaryAddOnAndBuilder;
import com.anysoftkeyboard.nextword.BuildConfig;
import com.menny.android.anysoftkeyboard.AnyApplication;

import io.reactivex.Observable;
import io.reactivex.Single;

public class KasahorowWordsUploaderWorker extends RxWorker {

    private static final String TAG = "KasaWordsUploaderWorker";

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public KasahorowWordsUploaderWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        return createDictionaryAddOn(getApplicationContext())
                .flatMap(addon -> ObservableKasahorowWordsUpload.create(getApplicationContext(), addon.getLanguage()))
                .toList()
                .doOnSuccess( status -> {
                    if (BuildConfig.DEBUG) Log.i(TAG, "upload finished with response code "+status.get(0));
                })
                .map(statues -> Result.success())
                .onErrorReturn(throwable -> {
                    Log.e(TAG, "upload failed", throwable);
                    return Result.failure();
                });
    }

    private Observable<DictionaryAddOnAndBuilder> createDictionaryAddOn(Context context) {
        return Observable.fromIterable(AnyApplication.getExternalDictionaryFactory(context).getAllAddOns())
                .filter(addOn -> !TextUtils.isEmpty(addOn.getLanguage()))
                .distinct(DictionaryAddOnAndBuilder::getLanguage)
                .map(addOn -> addOn);
    }
}
