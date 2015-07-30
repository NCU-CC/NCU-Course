package tw.edu.ncu.cc.course.data.v1;


import java.util.Map;

public class Course {

    private String no;
    private String classNo;
    private String name;
    private String memo;
    private String language;
    private String[] teachers;
    private String[] classRooms;
    private Map< String, Integer[] > times;
    private boolean isMasterDoctor;
    private boolean isClosed;
    private boolean isFirstRun;
    private boolean isPreSelect;
    private boolean isSelected;
    private String type;
    private String fullHalf;
    private String passwordCard;
    private int serialNo;
    private int maxStudents;
    private int credit;

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo( int serialNo ) {
        this.serialNo = serialNo;
    }

    public String getNo() {
        return no;
    }

    public void setNo( String no ) {
        this.no = no;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo( String classNo ) {
        this.classNo = classNo;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo( String memo ) {
        this.memo = memo;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage( String language ) {
        this.language = language;
    }

    public String[] getTeachers() {
        return teachers;
    }

    public void setTeachers( String[] teachers ) {
        this.teachers = teachers;
    }

    public String[] getClassRooms() {
        return classRooms;
    }

    public void setClassRooms( String[] classRooms ) {
        this.classRooms = classRooms;
    }

    public Map< String, Integer[] >  getTimes() {
        return times;
    }

    public void setTimes( Map< String, Integer[] >  times ) {
        this.times = times;
    }

    public boolean isMasterDoctor() {
        return isMasterDoctor;
    }

    public void setIsMasterDoctor( boolean isMasterDoctor ) {
        this.isMasterDoctor = isMasterDoctor;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setIsClosed( boolean isClosed ) {
        this.isClosed = isClosed;
    }

    public boolean isFirstRun() {
        return isFirstRun;
    }

    public void setIsFirstRun( boolean isFirstRun ) {
        this.isFirstRun = isFirstRun;
    }

    public boolean isPreSelect() {
        return isPreSelect;
    }

    public void setIsPreSelect( boolean isPreSelect ) {
        this.isPreSelect = isPreSelect;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getFullHalf() {
        return fullHalf;
    }

    public void setFullHalf( String fullHalf ) {
        this.fullHalf = fullHalf;
    }

    public String getPasswordCard() {
        return passwordCard;
    }

    public void setPasswordCard( String passwordCard ) {
        this.passwordCard = passwordCard;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents( int maxStudents ) {
        this.maxStudents = maxStudents;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit( int credit ) {
        this.credit = credit;
    }

}
