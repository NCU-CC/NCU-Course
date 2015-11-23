package tw.edu.ncu.cc.course;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tw.edu.ncu.cc.course.client.android.NCUCourseClient;
import tw.edu.ncu.cc.course.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.course.data.v1.Course;

/**
 * A fragment representing a list of Items.
 */
public class CoursesFragment extends ListFragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_ID = "id";

    private int type;
    private String id;

    private NCUCourseClient ncuCourseClient;
    private Course[] courses;

    public static CoursesFragment newInstance(int type, String id) {
        CoursesFragment fragment = new CoursesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CoursesFragment() {
        ncuCourseClient = MainActivity.ncuCourseClient;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            id = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.no_course));
        getCourses();
    }

    private void getCourses() {
        ResponseListener<Course[]> responseListener = new ResponseListener<Course[]>() {
            @Override
            public void onResponse(Course[] responses) {
                showCourses(responses);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        };
        switch (type) {
            case 0:
                ncuCourseClient.getDepartmentCourses(id, responseListener);
                break;
            case 1:
                ncuCourseClient.getTargetCourses(id, responseListener);
                break;
            case 2:
                ncuCourseClient.getRejectedCourses(responseListener);
                break;
        }
    }

    private void showCourses(Course[] courses) {
        this.courses = courses;
        String[] courseNames = new String[courses.length];
        for (int i = 0; i != courses.length; ++i)
            courseNames[i] = courses[i].getName();
        setListAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, courseNames));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        CourseActivity.course = courses[position];
        startActivity(new Intent(getActivity(), CourseActivity.class));
    }

}
