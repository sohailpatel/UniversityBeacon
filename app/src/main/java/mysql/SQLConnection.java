package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Created by sohailpatel on 3/13/18.
 */

public class SQLConnection {

    public void getConnection(String beaconName){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/university_beacon?" + "user=root&password=root");
            System.out.println(connection);
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (InstantiationException ex) {
            ex.printStackTrace();
        }
        catch (IllegalAccessException ex){
            ex.printStackTrace();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
