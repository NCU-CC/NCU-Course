package tw.edu.ncu.cc.course;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import tw.edu.ncu.cc.course.client.android.NCUCourseClient;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.Course;


public class ScheduleFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private NCUCourseClient ncuCourseClient;
    private List<List<List<Course>>> courses;
    public static Course course;

    public static ScheduleFragment newInstance(int sectionNumber) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        courses = new ArrayList<>(7);
        for (int i = 0; i != 7; ++i) {
            courses.add(new ArrayList<List<Course>>(16));
            List<List<Course>> dayCourses = courses.get(i);
            for (int j = 0; j != 16; ++j)
                dayCourses.add(new ArrayList<Course>());
        }

        ncuCourseClient = MainActivity.ncuCourseClient;
        ncuCourseClient.getSelectedCourse(new ResponseListener<Course[]>() {
            @Override
            public void onResponse(Course[] responses) {
                for (Course course : responses) {
                    Map<String, Integer[]> times = course.getTimes();
                    for (String day : times.keySet())
                        for (Integer hour : times.get(day))
                            courses.get(Integer.parseInt(day) - 1).get(hour - 1).add(course);
                }
                if (mViewPager != null)
                    mViewPager.setAdapter(mSectionsPagerAdapter);
            }

            @Override
            public void onError(Throwable throwable) {

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

        mViewPager.setAdapter(mSectionsPagerAdapter);

        return mViewPager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
                    "一","二","三","四","Ｚ","五","六","七","八","九","Ａ","Ｂ","Ｃ","Ｄ","Ｅ","Ｆ"
            };
            final List<List<Course>> dayCourses = courses.get(args.getInt(ARG_SECTION_NUMBER));
            for (int i = 0; i != 16; ++i) {
                List<Course> hourCourses = dayCourses.get(i);
                for (Course course : hourCourses)
                    daySchedule[i] += " " + course.getName();
            }
            listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, daySchedule));
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
    }

}
