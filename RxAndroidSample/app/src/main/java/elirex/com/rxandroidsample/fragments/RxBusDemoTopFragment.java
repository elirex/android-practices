package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import elirex.com.rxandroidsample.R;
import elirex.com.rxandroidsample.RxBus;
import elirex.com.rxandroidsample.rx.RxTapEvent;
import rx.functions.Action1;

/**
 * @author Sheng-Yuan Wang (2015/10/13).
 */
public class RxBusDemoTopFragment extends Fragment {


    private RxBus mRxBus;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_rxbus_demo_top,
                container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRxBus = RxBus.getInstance();
        Button tapButton = (Button) mRootView.findViewById(R.id.button_tap);
        RxView.clicks(tapButton).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if(mRxBus.hasObservers()) {
                    mRxBus.send(new RxTapEvent());
                }
            }
        });

    }

}
