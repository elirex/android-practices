package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import elirex.com.rxandroidsample.R;
import rx.functions.Action1;

/**
 * @author Sheng-Yuan Wang (2015/10/13).
 */
public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initUIComponents(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(false);
    }

    private void initUIComponents(View rootView) {
        Button rxBusDemoButton = (Button) rootView.findViewById(
                R.id.button_rxbus_demo);

        Button doubleBindingDemoButton = (Button) rootView.findViewById(
                R.id.button_double_binding_demo);

        Button concurrencySchedulers = (Button) rootView
                .findViewById(R.id.button_concurrency_schedulers_demo);

        RxView.clicks(rxBusDemoButton).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onClick(new RxBusDemoFragment(), getString(R.string.actionbar_rxbus));
            }
        });

        RxView.clicks(doubleBindingDemoButton).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onClick(new DoubleBindingDemo(), getString(R.string.actionbar_double_binding));
            }
        });

        RxView.clicks(concurrencySchedulers).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onClick(new ConcurrencySchedulersDemoFragment(),
                        getString(R.string.actionbar_concurrency_schedulers));
            }
        });

    }

    private void onClick(@NonNull Fragment fragment, String title) {
        getActivity().getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment, title)
                .addToBackStack(title).commit();
    }

}
