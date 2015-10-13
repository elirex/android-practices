package elirex.com.rxandroidsample;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Singleton RxBus
 * @author Sheng-Yuan Wang (2015/10/13).
 */
public class RxBus {

    private static volatile RxBus sInstance;

    private RxBus() {}

    public static synchronized RxBus getInstance() {
        if(sInstance == null) {
            sInstance = new RxBus();
        }
        return sInstance;
    }

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private final Subject<Object, Object> mBus =
            new SerializedSubject<>(PublishSubject.create());

}
