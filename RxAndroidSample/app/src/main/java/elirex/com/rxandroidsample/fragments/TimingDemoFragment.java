package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import elirex.com.rxandroidsample.LogAdapter;
import elirex.com.rxandroidsample.R;
import elirex.com.rxandroidsample.ThreadLogAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;


/**
 * @author Sheng-Yuan Wang (2015/11/16).
 */
public class TimingDemoFragment extends Fragment {

    private static final String LOG_TAG = TimingDemoFragment.class.getSimpleName();

    private View mRootView;
    private Button mRunSingleTaskAfter2sButton, mRunTaskIntervalOf1sDelay1s,
            mRunTaskIntervalOf1sImmediately, mRunTask5TimesIntervalOf3s,
            mCleanLogButton;

    private ThreadLogAdapter mLogAdapter;
    private ListView mLogListView;
    private List<String> mLogs;

    private Subscription mSubscription1 = null, mSubscription2 = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.framgmet_timing_demo, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
        setupUIComponents();
    }

    private void setupUIComponents() {
        mRunSingleTaskAfter2sButton = (Button) mRootView
                .findViewById(R.id.button_1);
        mRunTaskIntervalOf1sDelay1s = (Button) mRootView
                .findViewById(R.id.button_2);
        mRunTaskIntervalOf1sImmediately = (Button) mRootView
                .findViewById(R.id.button_3);
        mRunTask5TimesIntervalOf3s = (Button) mRootView
                .findViewById(R.id.button_4);
        mCleanLogButton = (Button) mRootView.findViewById(R.id.button_clean);
        mLogListView = (ListView) mRootView.findViewById(R.id.list_thread_log);
        mLogs = new ArrayList<String>();
        mLogAdapter = new ThreadLogAdapter(getActivity(), R.layout.row_item_log,
                R.id.item_log, mLogs);
        mLogListView.setAdapter(mLogAdapter);

        mRunSingleTaskAfter2sButton.setOnClickListener(onClickButton1Listener);
        mRunTaskIntervalOf1sDelay1s.setOnClickListener(onClickButton2Listener);
        mRunTaskIntervalOf1sImmediately.setOnClickListener(onClickButton3Listener);
        mRunTask5TimesIntervalOf3s.setOnClickListener(onClickButton4Listener);
        mCleanLogButton.setOnClickListener(onClickButtonCleanListener);
    }

    private Button.OnClickListener onClickButton1Listener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateThreadLogs(String.format("A1 [%s] --- Button click",
                            getCurrentTimestamp()));
                    Observable.timer(2, TimeUnit.SECONDS)
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {
                                    updateThreadLogs(String
                                            .format("A1 [%s] XXX COMPLETE",
                                                    getCurrentTimestamp()));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(LOG_TAG, "Something went wrong in TimingDemoFragment example", e);
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    Log.d(LOG_TAG, "Log:" + aLong);
                                    updateThreadLogs(String.format("A1 [%s] NEXT",
                                            getCurrentTimestamp()));
                                }
                            });
                }
            };

    private Button.OnClickListener onClickButton2Listener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSubscription1 != null && mSubscription1.isUnsubscribed()) {
                        mSubscription1.unsubscribe();
                        updateThreadLogs(String.format("B2 [%s] XXX Button KILLED",
                                 getCurrentTimestamp()));
                        return;
                    }

                    updateThreadLogs(String.format("B2 [%s] --- Button click",
                            getCurrentTimestamp()));
                    mSubscription1 = Observable.interval(1, TimeUnit.SECONDS)
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {
                                   updateThreadLogs(String.format("B2 [%s] XXXX COMPLETE"
                                           , getCurrentTimestamp()));
                                }

                                @Override
                                public void onError(Throwable e) {
                                   Log.e(LOG_TAG, "Something went wrong in TimingDemoFragment example", e);
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    updateThreadLogs(String.format("B2 [%s] NEXT",
                                            getCurrentTimestamp()));
                                }

                            });
                }
            };

    private Button.OnClickListener onClickButton3Listener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSubscription2 != null && mSubscription2.isUnsubscribed()) {
                        mSubscription2.unsubscribe();
                        updateThreadLogs(String.format("C3 [%s] XXXX Button KILLED",
                                getCurrentTimestamp()));
                        return;
                    }

                    updateThreadLogs(String.format("C3 [%s] --- Button click",
                            getCurrentTimestamp()));

                    mSubscription2 = Observable
                            .interval(1, TimeUnit.SECONDS)
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {
                                    updateThreadLogs(String.format("C3 [%s] XXXX COMPLETE",
                                            getCurrentTimestamp()));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(LOG_TAG, "Something went wrong in TimingDemoFragment example", e);
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    updateThreadLogs(String.format("C3 [%s] NEXT",
                                            getCurrentTimestamp()));
                                }
                            });
                }
            };

    private Button.OnClickListener onClickButton4Listener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateThreadLogs(String.format("D4 [%s] --- Button click",
                            getCurrentTimestamp()));

                    Observable.interval(3, TimeUnit.SECONDS).take(5)
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onCompleted() {
                                   updateThreadLogs(String.format("D4 [%s] XXX COMPLETE",
                                           getCurrentTimestamp()));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(LOG_TAG, "Something went wrong in TimingDemoFragment example", e);
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    updateThreadLogs(String.format("D4 [%s] NEXT",
                                            getCurrentTimestamp()));
                                }
                            });
                }
            };

    private Button.OnClickListener onClickButtonCleanListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogs.clear();
                    mLogAdapter.notifyDataSetChanged();
                }
            };

    private void updateThreadLogs(final String log) {
        final String message = String.format(log + " [Is in MainThread: %b]",
                Looper.getMainLooper() == Looper.myLooper());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLogs.add(0, message);
                mLogAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getCurrentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("h:m:s:S a").format(calendar.getTimeInMillis());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription1.unsubscribe();
        mSubscription2.unsubscribe();
    }
}
