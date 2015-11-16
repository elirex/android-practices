package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import elirex.com.rxandroidsample.R;
import elirex.com.rxandroidsample.ThreadLogAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Sheng-Yuan Wang (2015/11/2).
 */
public class ConcurrencySchedulersDemoFragment extends Fragment {

    private static final String LOG_TAG = ConcurrencySchedulersDemoFragment.class.getSimpleName();

    private View mRootView;

    private Button mStartOperation;
    private ProgressBar mProgressBar;
    private ListView mLogListView;
    private ThreadLogAdapter mLogAdapter;
    private List<String> mLogs;
    private Subscription mSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.framgmet_concurrency_schedulers_demo,
                container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
        setupUIComponents();
        setupLogger();
    }

    private void setupUIComponents() {
        mStartOperation = (Button) mRootView.findViewById(R.id.button_start_operation);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_operation_running);
        mLogListView = (ListView) mRootView.findViewById(R.id.list_thread_log);

        mStartOperation.setOnClickListener(onClickStartOperationListener);
    }

    // UI Listener
    private Button.OnClickListener onClickStartOperationListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    updateLog("Button Clicked");
                    mSubscription = getObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(getObserver());
                }
            };

    private Observable<Boolean> getObservable() {
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                updateLog("Within Observable");
                runningLongOperation();
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getObserver() {
        return new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                updateLog("On Complete");
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "Error in RxJava Demo concurrency", e);
                updateLog(String.format("Boo! Error %s", e.getMessage()));
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                updateLog(String.format("onNext with return value \"%b\"",
                        aBoolean));
            }
        };
    }

    private void runningLongOperation() {
        updateLog("Performing long operation");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Operaton was interrupted");
        }
    }

    private void updateLog(String log) {
        if(isCurrentlyOnMainThread()) {
            mLogs.add(0, log + " (In the main thread)");
            mLogAdapter.notifyDataSetChanged();
        } else {
            mLogs.add(0, log + " (Not in the main therad)");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mLogAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void setupLogger() {
        mLogs = new ArrayList<String>();
        mLogAdapter = new ThreadLogAdapter(getActivity(), R.layout.row_item_log,
                R.id.item_log, mLogs);
        mLogListView.setAdapter(mLogAdapter);
    }

    private boolean isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
