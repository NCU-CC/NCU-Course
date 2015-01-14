package tw.edu.ncu.cc.course;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import tw.edu.ncu.cc.course.data.v1.Course;

public class CourseActivity extends ActionBarActivity {

    private final String[] dayTC = {"一", "二", "三", "四", "五", "六", "日"} ;
    private final String[] hourTC = {
            "一","二","三","四","Ｚ","五","六","七","八","九","Ａ","Ｂ","Ｃ","Ｄ","Ｅ","Ｆ"
    };
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        course = ScheduleFragment.course;

        TextView noClassNoText = (TextView) findViewById(R.id.no_class_no);
        TextView classNameText = (TextView) findViewById(R.id.class_name);
        TextView teachersText = (TextView) findViewById(R.id.teachers);
        TextView timeClassroomText = (TextView) findViewById(R.id.time_classroom);
        TextView typeText = (TextView) findViewById(R.id.type);
        TextView creditText = (TextView) findViewById(R.id.credit);
        TextView fullHalfText = (TextView) findViewById(R.id.full_half);
        TextView languageText = (TextView) findViewById(R.id.language);
        TextView passwordCardText = (TextView) findViewById(R.id.password_card);
        TextView maxStudentsText = (TextView) findViewById(R.id.max_students);

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
                timeClassroomText.append("星期" + dayTC[Integer.parseInt(day) - 1] + " " + hourTC[hour - 1] + " / " + course.getClassRooms()[i++]);
            }
        typeText.setText(course.getType());
        creditText.setText(String.valueOf(course.getCredit()));
        fullHalfText.setText(course.getFullHalf());
        languageText.setText(course.getLanguage());
        passwordCardText.setText(course.getPasswordCard());
        maxStudentsText.setText(String.valueOf(course.getMaxStudents()));
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
