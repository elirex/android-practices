package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private void initUIComponents(View rootView) {
        Button rxbuxDemoButton = (Button) rootView.findViewById(
                R.id.button_rxbus_demo);

        RxView.clicks(rxbuxDemoButton).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onClick(new RxBusDemoFragment());
            }
        });

    }

    private void onClick(@NonNull Fragment fragment) {
        final String tag = fragment.getClass().toString();
        getActivity().getFragmentManager().beginTransaction()
                .addToBackStack(tag)
                .replace(android.R.id.content, fragment, tag)
                .commit();
    }

}
