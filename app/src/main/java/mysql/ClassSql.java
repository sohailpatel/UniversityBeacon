package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohailpatel on 3/18/18.
 */

public class ClassSql {

    static final public String CLASS_ID = "class_id",CLASS_ID_STUDENT = "class_id_stud", DEPARTMENT_ID = "department_id", CLASS_ID_DETAILS = "class_id_det", LECTURE_COUNT="lecture_count", PREV_DATE="prev_date";

    public HashMap<String,String> getClassId(Connection connection, String beaconName){
        Statement statement;
        ResultSet resultSet;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        int startIndex, endIndex, dayOfWeek;
        double totalLectureTime, startLectureTime, endLectureTime, currentTime, timeDecimalPart;
        HashMap<String,String> jsonString = new HashMap<String,String>();
        //calendar.setTime(now); //commented for testing
        try {
            SimpleDateFormat staticDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");   //remove after testing
            now = staticDateFormat.parse("2018-03-19 13:20");                               //remove after testing

            calendar.setTime(now);
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            statement = connection.createStatement();
            String getPendingTables = "select * from classes where class_room = '" + beaconName + "';";
            resultSet = statement.executeQuery(getPendingTables);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            startIndex = dayOfWeek * 5;
            endIndex = startIndex + 5;
            while (resultSet.next()) {
                String lectureDays = resultSet.getString(4);
                if(lectureDays.charAt(dayOfWeek) == '1') {
                    String lectureTime = resultSet.getString(5).substring(startIndex, endIndex);
                    totalLectureTime = resultSet.getInt(6);
                    startLectureTime = Double.parseDouble(lectureTime.replace(":","."));
                    endLectureTime = startLectureTime + (totalLectureTime / 60);
                    String[] splitEndLectureTime = Double.toString(endLectureTime).split("\\.");
                    timeDecimalPart = Double.parseDouble(splitEndLectureTime[1]);
                    if(splitEndLectureTime[1].length() == 1){
                        timeDecimalPart *= 10;
                    }
                    if(timeDecimalPart >= 60){
                        endLectureTime += 1;
                        endLectureTime -= timeDecimalPart / 100;
                    }
                    SimpleDateFormat hoursMinuteFormat = new SimpleDateFormat("HH:mm");
                    currentTime = Double.parseDouble(hoursMinuteFormat.format(now).replace(":","."));
                    if(currentTime >= startLectureTime && currentTime <= endLectureTime ){
                        jsonString.put(CLASS_ID, resultSet.getString(1));
                        jsonString.put(CLASS_ID_STUDENT, resultSet.getString(1) + "_stud");
                        jsonString.put(CLASS_ID_DETAILS, resultSet.getString(1) + "_det");
                        jsonString.put(DEPARTMENT_ID, resultSet.getString(8));
                        break;
                    }
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        catch (ParseException ex){
            ex.printStackTrace();
        }
        return jsonString;
    }

    public void classStarted(Connection connection, int profID, HashMap<String,String> classDetails){
        Statement statement;
        ResultSet resultSet;
        PreparedStatement preparedStmt;
        HashMap<String,String> lectureCountDeatils;
        Date currentDate, prevLecDate = null;
        SimpleDateFormat staticDateFormat;
        long timeDiff, diffInDays;
        int lectureCount = 0;
        try {
            staticDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            lectureCountDeatils = getTotalLectures(connection, classDetails, profID);
            lectureCount = Integer.parseInt(lectureCountDeatils.get(LECTURE_COUNT));
            prevLecDate = staticDateFormat.parse(lectureCountDeatils.get(PREV_DATE));
            currentDate = new Date();
            staticDateFormat.format(currentDate);
            timeDiff = Math.abs(currentDate.getTime() - prevLecDate.getTime());
            diffInDays = timeDiff / (24 * 60 * 60 * 1000);
            if(diffInDays > 0) {
                java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
                String startAttendance = "update classes set total_lec = ?, prev_lec_date = ? where class_id = ? ;";
                preparedStmt = connection.prepareStatement(startAttendance);
                preparedStmt.setInt(1, lectureCount);
                preparedStmt.setDate(2, sqlDate);
                preparedStmt.setString(3, classDetails.get(CLASS_ID));
                preparedStmt.execute();
            }
            else{
                System.out.println("Attendance already marked");
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        catch (ParseException ex){
            ex.printStackTrace();
        }
    }

    public void markStudentAttendance(Connection connection, int studentID, HashMap<String,String> classDetails){
        Statement statement;
        ResultSet resultSet;
        PreparedStatement preparedStmt;
        HashMap<String,String> studentLecDetails;
        Date currentDate, prevLecDate = null;
        SimpleDateFormat staticDateFormat;
        long timeDiff, diffInDays;
        int currentAttendance = 0;
        try {
            staticDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            studentLecDetails = getStudentLecDetails(connection, classDetails, studentID);
            currentAttendance = Integer.parseInt(studentLecDetails.get(LECTURE_COUNT));
            prevLecDate = staticDateFormat.parse(studentLecDetails.get(PREV_DATE));
            currentDate = new Date();
            staticDateFormat.format(currentDate);
            timeDiff = Math.abs(currentDate.getTime() - prevLecDate.getTime());
            diffInDays = timeDiff / (24 * 60 * 60 * 1000);
            if(diffInDays > 0) {
                java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
                String startAttendance = "update "+ classDetails.get(CLASS_ID_STUDENT) + " set attendance = ?, prev_lec_date = ? where student_id = ? ;";
                preparedStmt = connection.prepareStatement(startAttendance);
                preparedStmt.setInt(1, currentAttendance);
                preparedStmt.setDate(2, sqlDate);
                preparedStmt.setInt(3, studentID);
                preparedStmt.execute();
            }
            else{
                System.out.println("Attendance already marked");
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        catch (ParseException ex){
            ex.printStackTrace();
        }
    }

    public HashMap<String,String> getTotalLectures(Connection connection, HashMap<String,String> classDetails, int profID){
        Statement statement;
        ResultSet resultSet;
        Date prevLecDate = null;
        int lectureCount = 0;
        HashMap<String,String> lectureCountDetails = new HashMap<String, String>();
        try{
            statement = connection.createStatement();
            String currentLectureCount = "select total_lec, prev_lec_date  from classes where class_id = '" + classDetails.get(CLASS_ID) + "' and prof_id = "+ profID +";";
            resultSet = statement.executeQuery(currentLectureCount);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                for(int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++){
                    if(columnIndex == 1) {
                        lectureCount = resultSet.getInt(columnIndex) + 1;
                    }
                    else {
                        prevLecDate = resultSet.getDate(2);
                    }
                }
            }
            lectureCountDetails.put(LECTURE_COUNT, Integer.toString(lectureCount));
            lectureCountDetails.put(PREV_DATE, prevLecDate.toString());
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return lectureCountDetails;
    }

    public HashMap<String,String> getStudentLecDetails(Connection connection, HashMap<String,String> classDetails, int studentID){
        Statement statement;
        ResultSet resultSet;
        Date prevLecDate = null;
        int currentAttendance = 0;
        HashMap<String,String> studentLecDetails = new HashMap<String, String>();
        try{
            statement = connection.createStatement();
            String currentLectureCount = "select attendance, prev_lec_date  from "+ classDetails.get(CLASS_ID_STUDENT) +" where student_id = "+ studentID +";";
            resultSet = statement.executeQuery(currentLectureCount);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                for(int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++){
                    if(columnIndex == 1) {
                        currentAttendance = resultSet.getInt(columnIndex) + 1;
                    }
                    else {
                        prevLecDate = resultSet.getDate(2);
                    }
                }
            }
            studentLecDetails.put(LECTURE_COUNT, Integer.toString(currentAttendance));
            studentLecDetails.put(PREV_DATE, prevLecDate.toString());
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return studentLecDetails;
    }
}

