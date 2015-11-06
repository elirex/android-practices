package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import elirex.com.rxandroidsample.LogAdapter;
import elirex.com.rxandroidsample.R;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * @author Sheng-Yuan Wang (2015/11/5).
 */
public class BufferDemoFragment extends Fragment {

    private static final String LOG_TAG = BufferDemoFragment.class.getSimpleName();

    private View mRootView;
    private Button mTapButton;
    private ListView mLogListView;
    private List<String> mLogList;
    private Subscription mSubscription;
    private LogAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_buffer_demo,container,
                false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUIComponents();
        mSubscription = getBufferedSubscriptoin();
    }

    private void setupUIComponents() {
        mTapButton = (Button) mRootView.findViewById(R.id.button_tap);
        mLogListView = (ListView) mRootView.findViewById(R.id.list_log);
        setupLogger();
        mLogListView.setAdapter(mAdapter);
    }

    private void setupLogger() {
        mLogList = new ArrayList<String>();
        mAdapter = new LogAdapter(getActivity(), mLogList);
    }

    private Subscription getBufferedSubscriptoin() {
        return RxView.clickEvents(mTapButton)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent viewClickEvent) {
                        log("Got a tap");
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                               @Override
                               public void onCompleted() {
                                    Log.d(LOG_TAG, "onCompleted");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e(LOG_TAG, "onError", e);
                                   log("Dang error! check your logs");
                               }

                               @Override
                               public void onNext(List<Integer> integers) {
                                    if(integers.size() > 0) {
                                        log(integers.size() + " taps");
                                    } else {
                                        Log.d(LOG_TAG, "No taps received");
                                    }
                               }
                           }
                );
    }

    private void log(String message) {
        if(isCurrentlyOnMainThread()) {
            mLogList.add(0, message + " (Main Thread)");
        } else {
            mLogList.add(0, message + " (Not Main Thread");
        }
        mAdapter.notifyDataSetChanged();
    }

    private boolean isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }
}
