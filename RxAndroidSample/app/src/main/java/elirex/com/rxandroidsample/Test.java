package elirex.com.rxandroidsample;

import android.view.textservice.TextServicesManager;

import rx.Observer;
import rx.Subscriber;

/**
 * @author Sheng-Yuan Wang (2015/10/29).
 */
public class Test {

    public Test() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        };
    }

}
