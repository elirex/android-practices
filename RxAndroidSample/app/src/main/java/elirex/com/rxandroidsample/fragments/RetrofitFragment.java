package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.AndroidCharacter;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import elirex.com.rxandroidsample.LogAdapter;
import elirex.com.rxandroidsample.R;
import elirex.com.rxandroidsample.retrofit.Contributor;
import elirex.com.rxandroidsample.retrofit.GithubApi;
import elirex.com.rxandroidsample.retrofit.User;
import elirex.com.rxandroidsample.rx.RxUtils;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Sheng-Yuan Wang (2015/11/24).
 */
public class RetrofitFragment extends Fragment {

    private static final String LOG_TAG = RetrofitFragment.class.getSimpleName();

    private GithubApi mApi;
    private LogAdapter mAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private List<String> mResponses;
    private ListView mResultList;
    private Button mRetrofitContributors, mRetrofitContributorsWithUserInfo;
    private EditText mUsername, mRepo;

    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = createGithubApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_retrofit, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
        setupUIComponents();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(mSubscriptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(mSubscriptions);
    }

    private void setupUIComponents() {
        mRetrofitContributors = (Button) mRootView
                .findViewById(R.id.btn_demo_retrofit_contributors);
        mRetrofitContributorsWithUserInfo = (Button) mRootView
                .findViewById(R.id.btn_demo_retrofit_contributors_with_user_info);
        mUsername = (EditText) mRootView
                .findViewById(R.id.demo_retrofit_contributors_username);
        mRepo = (EditText) mRootView
                .findViewById(R.id.demo_retrofit_contributors_repository);
        mResultList = (ListView) mRootView.findViewById(R.id.log_list);
        mResponses = new ArrayList<String>();
        mAdapter = new LogAdapter(getActivity(), mResponses);
        mAdapter.addAll(mResponses);
        mResultList.setAdapter(mAdapter);

        mRetrofitContributors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListContributorsClicked();
            }
        });

        mRetrofitContributorsWithUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListContributorsWithFullUserInfoClicked();
            }
        });

    }

    private void onListContributorsWithFullUserInfoClicked() {
        mSubscriptions.add(mApi.contributor(mUsername.getText().toString(),
                mRepo.getText().toString())
                .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
                    @Override
                    public Observable<Contributor> call(List<Contributor> contributors) {
                        return Observable.from(contributors);
                    }
                })
                .flatMap(new Func1<Contributor, Observable<Pair<User, Contributor>>>() {
                    @Override
                    public Observable<Pair<User, Contributor>> call(Contributor contributor) {
                        Observable<User> userObservable = mApi.user(contributor.login)
                                .filter(new Func1<User, Boolean>() {
                                    @Override
                                    public Boolean call(User user) {
                                        return user.name.length() > 0 && user.email.length() > 0;
                                    }
                                });
                        return Observable.zip(userObservable,
                                Observable.just(contributor), new Func2<User, Contributor, Pair<User, Contributor>>() {
                                    @Override
                                    public Pair<User, Contributor> call(User user, Contributor contributor) {
                                        return new Pair<>(user, contributor);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<User, Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(LOG_TAG, "Retrofit call 2 completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "Error while getting the list of contributors along with full names", e);
                    }

                    @Override
                    public void onNext(Pair<User, Contributor> userContributorPair) {
                        User user = userContributorPair.first;
                        Contributor contributor = userContributorPair.second;

                        mResponses.add(String.format("%s(%s) has made %d contributions to %s",
                                user.name,
                                user.email,
                                contributor.contributions,
                                mRepo.getText().toString()));
                        mAdapter.notifyDataSetChanged();
                    }
                })

        );
    }

    private void onListContributorsClicked() {
        mSubscriptions.add(mApi.contributor(mUsername.getText().toString(),
                mRepo.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(LOG_TAG, "Retrofit call 1 completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "Woops we got an error while getting the list of contributors");
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        for(Contributor c : contributors) {
                            mResponses.add(String.format(
                                  "%s has made %d contributions to %s",
                                  c.login, c.contributions,
                                  mRepo.getText().toString()));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
        );
    }


    private GithubApi createGithubApi() {
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint("https://api.github.com/");
        final String githubToken = "";
        if(githubToken.length()  > 0) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", String.format("token %s", githubToken));
                }
            });
        }
        return builder.build().create(GithubApi.class);

    }

}
