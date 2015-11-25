package elirex.com.rxandroidsample.rx;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Sheng-Yuan Wang (2015/11/25).
 */
public class RxUtils {

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if(subscription != null) subscription.unsubscribe();
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(
            CompositeSubscription subscription) {
        if(subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }
        return subscription;
    }

}
