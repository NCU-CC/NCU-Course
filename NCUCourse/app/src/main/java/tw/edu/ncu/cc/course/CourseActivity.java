package tw.edu.ncu.cc.course;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import tw.edu.ncu.cc.course.data.v1.Course;

public class CourseActivity extends ActionBarActivity {

    private String[] dayFW ;
    private final String[] hourFW = {
            "１","２","３","４","Ｚ","５","６","７","８","９","Ａ","Ｂ","Ｃ","Ｄ","Ｅ","Ｆ"
    };
    private Course course;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dayFW = new String[]{getString(R.string.title_monday), getString(R.string.title_tuesday), getString(R.string.title_wednesday), getString(R.string.title_thursday), getString(R.string.title_friday), getString(R.string.title_saturday), getString(R.string.title_sunday)};
        setContentView(R.layout.activity_course);
        course = ScheduleFragment.course;

        TextView noClassNoText = (TextView) findViewById(R.id.code_class);
        TextView classNameText = (TextView) findViewById(R.id.course_name);
        TextView teachersText = (TextView) findViewById(R.id.teachers);
        TextView timeClassroomText = (TextView) findViewById(R.id.time_classroom);
        TextView typeText = (TextView) findViewById(R.id.type);
        TextView creditText = (TextView) findViewById(R.id.credit);
        TextView fullHalfText = (TextView) findViewById(R.id.full_half);
        TextView languageText = (TextView) findViewById(R.id.language);
        TextView passwordCardText = (TextView) findViewById(R.id.password_card);
        TextView maxStudentsText = (TextView) findViewById(R.id.max_students);
        TextView memoText = (TextView) findViewById(R.id.memo);

        noClassNoText.setText(course.getNo() + " - " + course.getClassNo());
        classNameText.setText(course.getName());
        for (String teacher : course.getTeachers())
            teachersText.append(teacher + " ");
        int i = 0;
        boolean endLineFlag = false;
        for (String day : course.getTimes().keySet())
            for (Integer hour : course.getTimes().get(day)) {
                if (endLineFlag)
                    timeClassroomText.append("\n");
                else
                    endLineFlag = true;
                timeClassroomText.append(dayFW[Integer.parseInt(day) - 1] + " " + hourFW[hour - 1] + " / " + course.getClassRooms()[i++]);
            }
        typeText.setText(course.getType());
        creditText.setText(String.valueOf(course.getCredit()));
        fullHalfText.setText(course.getFullHalf());
        languageText.setText(course.getLanguage());
        passwordCardText.setText(course.getPasswordCard());
        maxStudentsText.setText(String.valueOf(course.getMaxStudents()));
        memoText.setText(course.getMemo());

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float sensitvity = 200;
                if (Math.abs(e1.getY() - e2.getY()) * 2 < (e2.getX() - e1.getX()) && (e2.getX() - e1.getX()) > sensitvity)
                    finish();
                return true;
            }
        });

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
