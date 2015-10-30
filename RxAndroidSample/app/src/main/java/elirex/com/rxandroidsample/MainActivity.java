package elirex.com.rxandroidsample;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import elirex.com.rxandroidsample.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainFragment(),
                            this.toString()).commit();
            getFragmentManager().addOnBackStackChangedListener(this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            if(getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();
        if(count > 0) {
            setTitle(manager.getBackStackEntryAt(count - 1).getName());
        } else {
            setTitle(R.string.app_name);
        }
    }
}
