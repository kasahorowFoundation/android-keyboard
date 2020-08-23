package com.anysoftkeyboard.wordusageupload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anysoftkeyboard.dictionaries.DictionaryAddOnAndBuilder;
import com.anysoftkeyboard.nextword.BuildConfig;
import com.anysoftkeyboard.rx.RxSchedulers;
import com.menny.android.anysoftkeyboard.AnyApplication;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public final class KasahorowWordsUploaderHelper {

    private static final String TAG = "KasaWordsUploaderHelper";
    private KasahorowWordsUploaderHelper() {

    }

    public static Disposable getAddons(Context context) {
        return createDictionaryAddOn(context)
                .subscribeOn(RxSchedulers.background())
                .flatMap(addon -> ObservableKasahorowWordsUpload.create(context, addon.getLanguage()))
                .observeOn(RxSchedulers.mainThread())
                .subscribe(status -> {
                    if (BuildConfig.DEBUG) Log.i(TAG, "upload finished with response code "+status);
                }, throwable -> Log.e(TAG, "upload failed", throwable));
    }

    private static Observable<DictionaryAddOnAndBuilder> createDictionaryAddOn(Context context) {
        return Observable.fromIterable(AnyApplication.getExternalDictionaryFactory(context).getAllAddOns())
                .filter(addOn -> !TextUtils.isEmpty(addOn.getLanguage()))
                .distinct(DictionaryAddOnAndBuilder::getLanguage)
                .map(addOn -> addOn);
    }
}
