package tw.edu.ncu.cc.course;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tw.edu.ncu.cc.course.client.android.NCUCourseClient;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.Course;


public class ScheduleFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private NCUCourseClient ncuCourseClient;
    private List<List<List<Course>>> courses;
    private ProgressDialog progressDialog;
    private int leftCount = 2;
    private static final String selectedColor = "#003d79";
    private static final String trackingColor = "#FF8070";
    public static Course course;

    public static ScheduleFragment newInstance(Context context) {
        ScheduleFragment fragment = new ScheduleFragment();
        ProgressDialog progressDialog = new ProgressDialog(context);
        fragment.setProgressDialog(progressDialog);
        progressDialog.setMessage(context.getString(R.string.loading_schedule));
        progressDialog.show();
        return fragment;
    }

    public ScheduleFragment() {
        setHasOptionsMenu(true);
        ncuCourseClient = MainActivity.ncuCourseClient;
        getCourses();
    }

    private void getCourses() {
        courses = new ArrayList<>(7);
        for (int i = 0; i != 7; ++i) {
            courses.add(new ArrayList<List<Course>>(16));
            List<List<Course>> dayCourses = courses.get(i);
            for (int j = 0; j != 16; ++j)
                dayCourses.add(new ArrayList<Course>());
        }

        ncuCourseClient.getSelectedCourse(new ResponseListener<Course[]>() {
            @Override
            public void onResponse(Course[] responses) {
                for (Course course : responses) {
                    Map<String, Integer[]> times = course.getTimes();
                    course.setIsSelected(true);
                    for (String day : times.keySet())
                        for (Integer hour : times.get(day)) {
                            courses.get(Integer.parseInt(day) - 1).get(hour - 1).add(course);
                        }
                }
                showSchedule();
            }

            @Override
            public void onError(Throwable throwable) {
                fail();
            }
        });


        ncuCourseClient.getTrackingCourse(new ResponseListener<Course[]>() {
            @Override
            public void onResponse(Course[] responses) {
                for (Course course : responses) {
                    Map<String, Integer[]> times = course.getTimes();
                    course.setIsSelected(false);
                    for (String day : times.keySet())
                        for (Integer hour : times.get(day))
                            courses.get(Integer.parseInt(day) - 1).get(hour - 1).add(course);
                }
                if (mViewPager != null)
                    mViewPager.setAdapter(mSectionsPagerAdapter);
            }

            @Override
            public void onError(Throwable throwable) {
                fail();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) inflater.inflate(R.layout.fragment_schedule, container, false);

        showSchedule();

        return mViewPager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            progressDialog.show();
            leftCount = 1;
            getCourses();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSchedule() {
        leftCount--;
        if (leftCount == 0) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            progressDialog.dismiss();
        }
    }

    private void fail() {
        if (leftCount <= 0)
            return;
        progressDialog.dismiss();
        leftCount = -1;
        Toast.makeText(getActivity(), R.string.loading_failed , Toast.LENGTH_SHORT).show();
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, courses);
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_monday);
                case 1:
                    return getString(R.string.title_tuesday);
                case 2:
                    return getString(R.string.title_wednesday);
                case 3:
                    return getString(R.string.title_thursday);
                case 4:
                    return getString(R.string.title_friday);
                case 5:
                    return getString(R.string.title_saturday);
                case 6:
                    return getString(R.string.title_sunday);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Bundle args;
        private List<List<List<Course>>> courses;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, List<List<List<Course>>> courses) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setCourses(courses);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public void setCourses(List<List<List<Course>>> courses) {
            this.courses = courses;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            args = getArguments();
            View rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.list_view);
            String[] daySchedule = new String[]{
                    "１","２","３","４","Ｚ","５","６","７","８","９","Ａ","Ｂ","Ｃ","Ｄ","Ｅ","Ｆ"
            };
            final List<List<Course>> dayCourses = courses.get(args.getInt(ARG_SECTION_NUMBER));
            for (int i = 0; i != 16; ++i) {
                List<Course> hourCourses = dayCourses.get(i);
                for (Course course : hourCourses)
                    daySchedule[i] += "&nbsp;<font color=\"" + ( course.isSelected() ? selectedColor : trackingColor ) + "\">" + course.getName() + "</font>";
            }
            listView.setAdapter(new ListAdapter(getActivity(), daySchedule));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<Course> hourCourses = dayCourses.get(position);
                    if (hourCourses.isEmpty())
                        return;
                    if (hourCourses.size() == 1)
                        openCourse(hourCourses.get(0));
                    else
                        openCourses(hourCourses);
                }
            });
            return rootView;
        }

        public void openCourse(Course course) {
            ScheduleFragment.course = course;
            startActivity(new Intent(getActivity(), CourseActivity.class));
        }

        public void openCourses(final List<Course> courses) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            List<String> courseNames = new ArrayList<>();
            for (Course course : courses)
                courseNames.add(course.getName());
            builder.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, courseNames), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ScheduleFragment.course = courses.get(which);
                    startActivity(new Intent(getActivity(), CourseActivity.class));
                }
            });
            builder.show();
        }

        private static class ListAdapter extends BaseAdapter {

            private Context context;
            private String[] items;

            public ListAdapter(Context context, String[] items) {
                this.context = context;
                this.items = items;
            }

            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.course_list_item, parent, false);
                }
                ((TextView) convertView).setText(Html.fromHtml(items[position]));
                return convertView;
            }
        }
    }

}
