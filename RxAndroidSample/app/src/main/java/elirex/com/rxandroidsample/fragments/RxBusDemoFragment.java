package elirex.com.rxandroidsample.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import elirex.com.rxandroidsample.R;

/**
 * @author Sheng-Yuan Wang (2015/10/13).
 */
public class RxBusDemoFragment extends Fragment {

    private static final String LOG_TAG = RxBusDemoFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rxbus_demo,
                container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.framelayout_rxbus_1, new RxBusDemoTopFragment())
                .replace(R.id.framelayout_rxbus_2, new RxBusDemoBottomFragment())
                .commit();
    }

    public static class TapEvent {}

}
