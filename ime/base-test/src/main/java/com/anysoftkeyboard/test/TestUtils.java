package com.anysoftkeyboard.test;

import android.os.Build;
import androidx.core.util.Pair;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
  public static final int LATEST_STABLE_API_LEVEL = Build.VERSION_CODES.VANILLA_ICE_CREAM;
  // This is the latest version that does not fail with
  // "IllegalStateException: The Window Context should have been attached to a DisplayArea"
  public static final int LATEST_WINDOW_SUPPORTING_API_LEVEL = Build.VERSION_CODES.S;

  public static <T> List<T> convertToList(Iterable<T> iterable) {
    ArrayList<T> list = new ArrayList<>();
    for (T t : iterable) {
      list.add(t);
    }

    return list;
  }

  public static <K, V, O> Map<K, V> convertToMap(
      Iterable<O> iterable, Function<O, Pair<K, V>> parser) {
    Map<K, V> map = new HashMap<>();
    Observable.fromIterable(iterable)
        .map(parser)
        .blockingSubscribe(pair -> map.put(pair.first, pair.second));

    return map;
  }
}
