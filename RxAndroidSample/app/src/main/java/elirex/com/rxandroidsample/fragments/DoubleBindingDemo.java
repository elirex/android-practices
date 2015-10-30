package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import elirex.com.rxandroidsample.R;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * @author Sheng-Yuan Wang (2015/10/30).
 */
public class DoubleBindingDemo extends Fragment {

    private View mRootView;
    private EditText mNumber1EditText, mNumber2EditText;
    private TextView mResultTextView;

    private Subscription mSubscriptoin;
    private PublishSubject<Double> mResultSubject;


    public DoubleBindingDemo() {
        mResultSubject = PublishSubject.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_dobule_binding_demo,
                container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUICompontents();
        mSubscriptoin = mResultSubject.asObservable().subscribe(
                new Action1<Double>() {
                    @Override
                    public void call(Double aDouble) {
                        mResultTextView.setText(String.valueOf(aDouble));
                    }
                });
        onNumberChanged();
        mNumber2EditText.requestFocus();
    }

    private void setUICompontents() {
        mNumber1EditText = (EditText) mRootView.findViewById(R.id.number_1);
        mNumber2EditText = (EditText) mRootView.findViewById(R.id.number_2);
        mResultTextView = (TextView) mRootView.findViewById(R.id.result);
        mNumber1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onNumberChanged();
            }
        });
        mNumber2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onNumberChanged();
            }
        });
    }

    private void onNumberChanged() {
        double x = 0;
        double y = 0;
        if(!mNumber1EditText.getText().toString().equals("")) {
            x = Double.parseDouble(mNumber1EditText.getText().toString());
        }
        if(!mNumber2EditText.getText().toString().equals("")) {
            y = Double.parseDouble(mNumber2EditText.getText().toString());
        }
        mResultSubject.onNext(x + y);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSubscriptoin != null) {
            mSubscriptoin.unsubscribe();
        }
    }
}
