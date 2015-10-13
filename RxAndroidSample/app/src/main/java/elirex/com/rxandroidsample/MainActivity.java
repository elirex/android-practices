package elirex.com.rxandroidsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import elirex.com.rxandroidsample.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainFragment(),
                            this.toString()).commit();
        }

    }

}
