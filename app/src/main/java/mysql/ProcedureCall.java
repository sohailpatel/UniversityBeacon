package mysql;

import android.icu.text.SymbolTable;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;

/**
 * Created by sohailpatel on 3/18/18.
 */

public class ProcedureCall {

    private boolean createDeptTables(Connection connection, ArrayList<String> tableNames){
        Statement statement;
        PreparedStatement preparedStatement;
        try {
            statement = connection.createStatement();
            for (String tableName : tableNames) {
                String createDeptTable = "call createDeptTable('" + tableName +"');";
                statement.executeQuery(createDeptTable);
                String deleteEntry = "delete from pending_tables where table_name = ?;";
                PreparedStatement preparedStmt = connection.prepareStatement(deleteEntry);
                preparedStmt.setString(1,  tableName);
                preparedStmt.execute();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean createClassTables(Connection connection, ArrayList<String> tableNames){
        Statement statement;
        PreparedStatement preparedStatement;
        try {
            statement = connection.createStatement();
            for (String tableName : tableNames) {
                String createDeptTable = "call createClassTable('" + tableName +"');";
                statement.executeQuery(createDeptTable);
                String deleteEntry = "delete from pending_tables where table_name = ?;";
                PreparedStatement preparedStmt = connection.prepareStatement(deleteEntry);
                preparedStmt.setString(1,  tableName);
                preparedStmt.execute();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean callStoredProcedure(){
        SQLConnection sqlConnection = new SQLConnection();
        ProcedureCall procedureCall = new ProcedureCall();
        Connection connection;
        Statement statement;
        ResultSet resultSet;
        ArrayList<String> departmentTableName, classTableName;
        try {
            connection = sqlConnection.getConnection();
            statement = connection.createStatement();
            departmentTableName = new ArrayList<String>();
            classTableName = new ArrayList<String>();
            String getPendingTables = "select * from pending_tables;";
            resultSet = statement.executeQuery(getPendingTables);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                int tableType = resultSet.getInt(1);
                int status = resultSet.getInt(2);
                if(tableType == 1 && status == 1) {
                    String tableName = resultSet.getString(3);
                    departmentTableName.add(tableName);
                }
                else if(tableType == 2 && status == 1) {
                    String tableName = resultSet.getString(3);
                    classTableName.add(tableName);
                }
            }
            return procedureCall.createDeptTables(connection, departmentTableName) && procedureCall.createClassTables(connection, classTableName);
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }

    }
}
