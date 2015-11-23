package tw.edu.ncu.cc.course.client.android;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.Credential;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuman.android.auth.OAuthManager;

import tw.edu.ncu.cc.course.R;
import tw.edu.ncu.cc.course.client.tool.config.CourseConfig;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.College;
import tw.edu.ncu.cc.course.data.v1.Course;
import tw.edu.ncu.cc.course.data.v1.Department;
import tw.edu.ncu.cc.course.data.v1.Target;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NCUCourseClient {

    private OAuthManager oauthManager;
    private RequestQueue queue;
    private String baseURL;
    private String language;
    private String accessToken;
    private String apiToken;

    public NCUCourseClient( CourseConfig config, OAuthManager oauthManager, Context context ) {
        this.baseURL = config.getServerAddress();
        this.language = config.getLanguage();
        this.queue = Volley.newRequestQueue( context );
        this.oauthManager = oauthManager;
        this.apiToken = context.getString(R.string.oauth_api_token);
    }

    public void initAccessToken() {
        try {
            Credential result = oauthManager.authorizeExplicitly( "user", null, null ).getResult();
            if (result.getExpiresInSeconds() <= 60)
                result.refreshToken();
            accessToken = result.getAccessToken();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public void deleteAccessToken() {
        oauthManager.deleteCredential("user", null, null);
    }

    public void getSelectedCourses(ResponseListener<Course[]> responseListener) {
        get(
                "student/selected", responseListener, new TypeToken<Course[]>() {
                }, getAccessTokenHeaders()
        );
    }

    public void getTrackingCourses(ResponseListener<Course[]> responseListener) {
        get(
                "student/tracking", responseListener, new TypeToken<Course[]>(){}, getAccessTokenHeaders()
        );
    }

    public void getRejectedCourses(ResponseListener<Course[]> responseListener) {
        get(
                "student/rejected", responseListener, new TypeToken<Course[]>(){}, getAccessTokenHeaders()
        );
    }

    public void getColleges(ResponseListener<College[]> responseListener) {
        get(
                "colleges", responseListener, new TypeToken<College[]>(){}, getApiTokenHeaders()
        );
    }

    public void getCollegeDepartments(String collegeId, ResponseListener<Department[]> responseListener) {
        get(
                "colleges/" + collegeId + "/departments", responseListener, new TypeToken<Department[]>(){}, getApiTokenHeaders()
        );
    }

    public void getDepartmentTargets(String departId, ResponseListener<Target[]> responseListener) {
        get(
                "departments/" + departId + "/targets", responseListener, new TypeToken<Target[]>(){}, getApiTokenHeaders()
        );
    }

    public void getDepartmentCourses(String departId, ResponseListener<Course[]> responseListener) {
        get(
                "departments/" + departId + "/courses", responseListener, new TypeToken<Course[]>(){}, getApiTokenHeaders()
        );
    }

    public void getTargetCourses(String targetId, ResponseListener<Course[]> responseListener) {
        get(
                "targets/" + targetId + "/courses", responseListener, new TypeToken<Course[]>(){}, getApiTokenHeaders()
        );
    }

    private < T > void get( String path, final ResponseListener< T > responseListener, final TypeToken typeToken, final Map<String, String> headers ) {
        queue.add(new StringRequest(Request.Method.GET, baseURL + path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        T data = new Gson().fromJson(response, typeToken.getType());
                        responseListener.onResponse(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.onError(error);
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        });
    }

    private Map<String, String> getAccessTokenHeaders() {
        Map< String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Accept-Language", language);
        return headers;
    }

    private Map<String, String> getApiTokenHeaders() {
        Map< String, String > headers = new HashMap<>();
        headers.put("X-NCU-API-TOKEN", apiToken);
        headers.put( "Accept-Language", language);
        return headers;
    }

}