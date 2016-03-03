package elirexcom.dragdropsample;

import android.app.Application;
import android.content.Context;

/**
 * Created by nickwang on 2016/3/3.
 */
public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

}
