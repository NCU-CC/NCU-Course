package tw.edu.ncu.cc.course;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.wuman.android.auth.OAuthManager;

import tw.edu.ncu.cc.course.client.android.NCUCourseClient;
import tw.edu.ncu.cc.course.client.tool.config.CourseConfig;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.College;
import tw.edu.ncu.cc.course.data.v1.Department;
import tw.edu.ncu.cc.course.data.v1.Target;
import tw.edu.ncu.cc.course.data.v1.Unit;
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

    private int position = 0;

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
                    .clientID(getString(R.string.NCU_OAuth_Client_ID))
                    .clientSecret(getString(R.string.NCU_OAuth_Client_Secret))
                    .callback(getString(R.string.NCU_OAuth_Call_Back))
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
        this.position = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ScheduleFragment())
                        .commit();
                break;
            case 1:
                chooseCollege();
                break;
            case 2:
                chooseCollege();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CoursesFragment.newInstance(2, null))
                        .commit();
                break;
        }
        onSectionAttached(position);
    }

    private void chooseCollege() {
        ncuCourseClient.getColleges(new ResponseListener<College[]>() {
            @Override
            public void onResponse(final College[] responses) {
                String[] collegeNames = new String[responses.length];
                for (int i = 0; i != responses.length; ++i)
                    collegeNames[i] = responses[i].getName();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(position == 1 ? R.string.title_by_depart : R.string.title_by_taker)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNavigationDrawerFragment.selectItem(0);
                            }
                        })
                        .setItems(collegeNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chooseDepart(responses[which]);
                            }
                        }).show();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void chooseDepart(final College college) {
        ncuCourseClient.getCollegeDepartments(college.getId(), new ResponseListener<Department[]>() {
            @Override
            public void onResponse(final Department[] responses) {
                String[] departNames = new String[responses.length];
                for (int i = 0; i != responses.length; ++i)
                    departNames[i] = responses[i].getName();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(college.getName())
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNavigationDrawerFragment.selectItem(0);
                            }
                        })
                        .setItems(departNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (position == 1)
                                    showCourses(0, responses[which]);
                                else
                                    chooseTarget(responses[which]);
                            }
                        }).show();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void chooseTarget(final Department department) {
        ncuCourseClient.getDepartmentTargets(department.getId(), new ResponseListener<Target[]>() {
            @Override
            public void onResponse(final Target[] responses) {
                String[] targetNames = new String[responses.length];
                for (int i = 0; i != responses.length; ++i)
                    targetNames[i] = responses[i].getName();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(department.getName())
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNavigationDrawerFragment.selectItem(0);
                            }
                        })
                        .setItems(targetNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showCourses(1, responses[which]);
                            }
                        }).show();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public void showCourses(int type , Unit unit) {
        mTitle = unit.getName();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CoursesFragment.newInstance(type, unit.getId()))
                .commit();
        restoreActionBar();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_my_course);
                break;
            case 1:
                mTitle = getString(R.string.title_by_depart);
                break;
            case 2:
                mTitle = getString(R.string.title_by_taker);
                break;
            case 3:
                mTitle = getString(R.string.title_rejected);
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
        switch (id) {
            case R.id.action_logout:
                ncuCourseClient.deleteAccessToken();
                auth = false;
                new AuthTask().execute();
                return true;
            case R.id.action_about:
                showAbout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about)
                .setView(R.layout.about)
                .show();
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
