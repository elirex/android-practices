package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import elirex.com.rxandroidsample.R;
import elirex.com.rxandroidsample.RxBus;
import elirex.com.rxandroidsample.rx.RxTapEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Sheng-Yuan Wang (2015/10/13).
 */
public class RxBusDemoBottomFragment extends Fragment {

    private View mRootView;
    private RxBus mRxBus;
    private CompositeSubscription mSubscriptions;
    private TextView mTapCountTextView, mTapStatusTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_rxbus_demo_bottom,
                container, false);
        return mRootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRxBus = RxBus.getInstance();

        mTapCountTextView = (TextView) mRootView.findViewById(
                R.id.textview_tap_count);
        mTapStatusTextView = (TextView) mRootView.findViewById(
                R.id.textview_tap_status);
    }


    @Override
    public void onStart() {
        super.onStart();
        mSubscriptions = new CompositeSubscription();
        ConnectableObservable<Object> tapEventEmitter =
                mRxBus.toObserverable().publish();

        mSubscriptions.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof RxTapEvent) {
                    showTapStatus();
                }
            }
        }));

        mSubscriptions.add(tapEventEmitter.publish(new Func1<Observable<Object>,
                        Observable<List<Object>>>() {
                    @Override
                    public Observable<List<Object>> call(Observable<Object> stream) {
                        return stream.buffer(stream.debounce(1, TimeUnit.SECONDS));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Object>>() {
                    @Override
                    public void call(List<Object> objects) {
                        showTapCount(objects.size());
                    }
                }));

        mSubscriptions.add(tapEventEmitter.connect());

    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscriptions.clear();
    }

    private void showTapStatus() {
        mTapStatusTextView.setVisibility(View.VISIBLE);
        mTapStatusTextView.setAlpha(1f);
        ViewCompat.animate(mTapStatusTextView).alpha(-1f).setDuration(400);
    }

    private void showTapCount(int count) {
        mTapCountTextView.setText(String.valueOf(count));
        mTapCountTextView.setVisibility(View.VISIBLE);
        mTapCountTextView.setScaleX(1f);
        mTapCountTextView.setScaleY(1f);
        ViewCompat.animate(mTapCountTextView)
                .scaleXBy(-1f)
                .scaleYBy(-1f)
                .setDuration(800)
                .setStartDelay(100);
    }

}
