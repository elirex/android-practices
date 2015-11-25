package elirex.com.rxandroidsample.retrofit;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author Sheng-Yuan Wang (2015/11/24).
 */
public interface GithubApi {


    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> contributor(@Path("owner") String owner,
                                              @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/contributors")
    List<Contributor> getContributors(@Path("owner") String owner,
                                      @Path("repo") String repo);

    @GET("/users/{user}")
    Observable<User> user(@Path("user") String user);

    @GET("/user/{user}")
    User getUser(@Path("user") String user);

}
