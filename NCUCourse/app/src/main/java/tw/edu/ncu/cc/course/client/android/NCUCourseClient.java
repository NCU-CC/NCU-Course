package tw.edu.ncu.cc.course.client.android;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuman.android.auth.OAuthManager;
import tw.edu.ncu.cc.course.client.tool.config.CourseConfig;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.Course;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NCUCourseClient {

    private OAuthManager oauthManager;
    private RequestQueue queue;
    private String baseURL;
    private String language;
    private String token;

    public NCUCourseClient( CourseConfig config, OAuthManager oauthManager, Context context ) {
        this.baseURL = config.getServerAddress();
        this.language = config.getLanguage();
        this.queue = Volley.newRequestQueue( context );
        this.oauthManager = oauthManager;
    }

    public void initAccessToken() {
        try {
            token = oauthManager.authorizeExplicitly( "user", null, null ).getResult().getAccessToken();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public void deleteAccessToken() {
        oauthManager.deleteCredential( "user", null, null );
    }

    public void getSelectedCourse( ResponseListener< Course[] > responseListener ) {
        get(
                "student/selected", responseListener, new TypeToken< Course[] >(){}
        );
    }

    public void getTrackingCourse( ResponseListener< Course[] > responseListener ) {
        get(
                "student/tracking", responseListener, new TypeToken< Course[] >(){}
        );
    }

    private < T > void get( String path, final ResponseListener< T > responseListener, final TypeToken typeToken ) {
        queue.add( new StringRequest( Request.Method.GET, baseURL + path,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse( String response ) {
                        T data = new Gson().fromJson( response, typeToken.getType() );
                        responseListener.onResponse( data );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse( VolleyError error ) {
                        responseListener.onError( error );
                    }
                }
        ) {
            public Map< String, String > getHeaders() throws AuthFailureError {
                Map< String, String > headers = new HashMap<>();
                headers.put( "Authorization", "Bearer " + token );
                headers.put( "Accept-Language", language);
                return headers;
            }
        } );
    }


}