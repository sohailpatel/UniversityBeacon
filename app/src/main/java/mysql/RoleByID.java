package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by sohailpatel on 3/21/18.
 */

public class RoleByID {

    public boolean isProfessor(Connection connection, String emailId, HashMap<String,String> classDetails){
        try {
            int profId = getProfId(connection, emailId);
            if(profId != 0) {
                Statement statement;
                ResultSet resultSet;
                statement = connection.createStatement();
                String findRole = "select * from classes where class_id = '" + classDetails.get(ClassSql.CLASS_ID) +"' and prof_id = " + profId + ";";
                resultSet = statement.executeQuery(findRole);
                if(resultSet.next())
                    return true;
                else
                    return false;
            }
            else{
                return false;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean isStudent(Connection connection, String emailId, HashMap<String,String> classDetails){
        try {
            int studentId = getStudentId(connection, emailId);
            if(studentId != 0) {
                Statement statement;
                ResultSet resultSet;
                statement = connection.createStatement();
                String findRole = "select * from " + classDetails.get(ClassSql.CLASS_ID_STUDENT) + " where student_id = " + studentId + ";";
                resultSet = statement.executeQuery(findRole);
                if(resultSet.next())
                    return true;
                else
                    return false;
            }
            else{
                return false;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public int getProfId(Connection connection, String emailId){
        try {
            Statement statement;
            ResultSet resultSet;
            int profId = 0;
            statement = connection.createStatement();;
            String findId = "select * from professors where email_id = '" + emailId +"';";
            resultSet = statement.executeQuery(findId);
            if(resultSet.next()) {
                profId = resultSet.getInt(1);
            }
            return profId;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return 0;
        }
    }

    public int getStudentId(Connection connection, String emailId){
        try {
            Statement statement;
            ResultSet resultSet;
            int studentId = 0;
            statement = connection.createStatement();;
            String findId = "select * from students where email_id = '" + emailId +"';";
            resultSet = statement.executeQuery(findId);
            if(resultSet.next()) {
                studentId = resultSet.getInt(1);
            }
            return studentId;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return 0;
        }
    }
}
