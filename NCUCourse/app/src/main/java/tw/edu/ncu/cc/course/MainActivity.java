package tw.edu.ncu.cc.course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.wuman.android.auth.OAuthManager;

import tw.edu.ncu.cc.course.client.android.NCUCourseClient;
import tw.edu.ncu.cc.course.client.tool.config.CourseConfig;
import tw.edu.ncu.cc.oauth.client.android.AndroidOauthBuilder;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private CookieManager cookieManager;

    private boolean auth = false;

    public static NCUCourseClient ncuCourseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //check network status
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle(R.string.network_unreachable);
            builder.setMessage(R.string.open_network_message);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton(R.string.network_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    finish();
                }
            });
            builder.show();
        }
        else {
            CourseConfig courseConfig = new CourseConfig("https://api.cc.ncu.edu.tw/course/v1/", getString(R.string.language));
            AndroidOauthBuilder oauthBuilder = AndroidOauthBuilder.initContext(this)
                    .clientID(getString(R.string.oauth_id))
                    .clientSecret(getString(R.string.oauth_secret))
                    .callback(getString(R.string.callback))
                    .scope("course.schedule.read")
                    .fragmentManager(getSupportFragmentManager());
            OAuthManager oAuthManager = oauthBuilder.build();
            ncuCourseClient = new NCUCourseClient(courseConfig, oAuthManager, this);

            new AuthTask().execute();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (!auth)
            return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance(this))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_my_course);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ncuCourseClient.deleteAccessToken();
            auth = false;
            new AuthTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AuthTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            cookieManager.setCookie("portal.ncu.edu.tw", "JSESSIONID=");
            try {
                ncuCourseClient.initAccessToken();
            } catch (Exception e) {
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            auth = true;
            mNavigationDrawerFragment.selectItem(0);
        }
    }

}
