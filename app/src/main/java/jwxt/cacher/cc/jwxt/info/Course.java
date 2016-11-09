package jwxt.cacher.cc.jwxt.info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xhaiben on 2016/8/20.
 */
public class Course implements Serializable {
    private String courseName;
    private String classRoom;
    private String teacherName;
    private int day; //周日到周六 0-6
    private int beginLesson; //开始节数
    private int endLesson; //结束节数
    private int beginWeek; //开始周数
    private int endWeek; //结束周数
    private int courseType = 1; //1单周，2双周,3有特殊周
    private Set<Integer> expected;

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

    private boolean isMulti;

    public Course() {
        this((String) null);
    }

    public Course(String courseName) {
        this.courseName = courseName;
        this.expected = new HashSet();
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassRoom() {
        return this.classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getBeginLesson() {
        return this.beginLesson;
    }

    public void setBeginLesson(int beginLesson) {
        this.beginLesson = beginLesson;
    }

    public int getEndLesson() {
        return this.endLesson;
    }

    public void setEndLesson(int endLesson) {
        this.endLesson = endLesson;
    }

    public int getBeginWeek() {
        return this.beginWeek;
    }

    public void setBeginWeek(int beginWeek) {
        this.beginWeek = beginWeek;
    }

    public int getEndWeek() {
        return this.endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getCourseType() {
        return this.courseType;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
    }

    public Set<Integer> getExpected() {
        return expected;
    }

    public void setExpected(Set<Integer> expected) {
        this.expected = expected;
    }

    public void addWeek(int week) {
        this.expected.add(Integer.valueOf(week));
    }

    public boolean isThisWeek(int week) {
        return this.expected.contains(Integer.valueOf(week));
    }

    public String toString() {
        return this.courseName + "," + this.classRoom + "," + this.day + "," + this.teacherName + "," + this.beginLesson + "," + this.endLesson + "," + this.expected + "," + this.courseType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (day != course.day) return false;
        if (beginLesson != course.beginLesson) return false;
        if (endLesson != course.endLesson) return false;
        if (beginWeek != course.beginWeek) return false;
        if (endWeek != course.endWeek) return false;
        if (courseType != course.courseType) return false;
        if (isMulti != course.isMulti) return false;
        if (!courseName.equals(course.courseName)) return false;
        if (classRoom != null ? !classRoom.equals(course.classRoom) : course.classRoom != null)
            return false;
        if (teacherName != null ? !teacherName.equals(course.teacherName) : course.teacherName != null)
            return false;
        return expected.equals(course.expected);

    }

    @Override
    public int hashCode() {
        int result = courseName.hashCode();
        result = 31 * result + (classRoom != null ? classRoom.hashCode() : 0);
        result = 31 * result + (teacherName != null ? teacherName.hashCode() : 0);
        result = 31 * result + day;
        result = 31 * result + beginLesson;
        result = 31 * result + endLesson;
        result = 31 * result + beginWeek;
        result = 31 * result + endWeek;
        result = 31 * result + courseType;
        result = 31 * result + expected.hashCode();
        result = 31 * result + (isMulti ? 1 : 0);
        return result;
    }
}
