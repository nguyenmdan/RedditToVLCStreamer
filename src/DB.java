import javax.swing.*;
import java.sql.*;

/**
 * Created by danielnguyen on 10/19/14.
 */
public class DB implements DBMS
{
    Connection dbConn;
    Statement dbStatement;
    ResultSet dbResultSet;


    /**
     * Adds selected record with desired field to specified field
     *
     * @param strTable
     * @param strKeyName
     * @param strKeyContents
     */
    @Override
    public void addRecord(String strTable, String strKeyName, String strKeyContents)
    {
        String strSQL;
        try
        {
            // check to see if the record exists
            dbStatement = dbConn.createStatement();
            strSQL = "SELECT * FROM " + strTable + " WHERE " + strKeyName + "='" + strKeyContents + "'";
            dbResultSet = dbStatement.executeQuery(strSQL);
            if (!moreRecords())
            {
                // the record does not exist, therefore add it to the database
                strSQL = "INSERT INTO " + strTable + " (" + strKeyName + ") VALUES ('" + strKeyContents + "')";
                dbStatement.executeUpdate(strSQL);
            }
            else
            {
                generateErrorDialog("Error adding record to database", "Database Error");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            generateErrorDialog("Error adding record to database", "Database Error");
        }
    }

    /**
     * Closes current DB connection
     */
    @Override
    public void close()
    {
        try
        {
            dbConn.close();
        } catch (SQLException e)
        {
            generateErrorDialog("Error closing database", "Database Error");
        }
    }

    /**
     * Deletes all records from SQL Table
     *
     * @param strTable
     */
    @Override
    public void deleteAll(String strTable)
    {
        String sqlString = "DELETE * FROM " + strTable;

        try
        {
            dbStatement.executeUpdate(sqlString);
        } catch (SQLException e)
        {
            generateErrorDialog("Error deleting records", "Database Error");
        }
    }

    /**
     * Gets records from specified fields in table
     *
     * @param strFieldName
     * @return
     */
    @Override
    public String getField(String strFieldName)
    {
        String strRet = "Error retrieving records";
        try
        {
            strRet = dbResultSet.getString(strFieldName);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return strRet;
    }

    /**
     * Checks if there are more records in DB
     *
     * @return
     */
    @Override
    public boolean moreRecords()
    {
        boolean blnRet;
        try
        {
            blnRet = dbResultSet.next();
        } catch (Exception e)
        {
            blnRet = false;
        }

        return blnRet;
    }

    /**
     * Opens a connection to DB
     *
     * @param strDataSourceName
     */
    @Override
    public void openConnection(String strDataSourceName)
    {
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://data/" + strDataSourceName.toUpperCase() + ".mdb;newdatabaseversion=V2010");
            dbConn = DriverManager.getConnection("jdbc:derby:" + strDataSourceName);
            if (dbConn == null)
            {

            }
            else
            {
                dbStatement = dbConn.createStatement();
            }
        } catch (Exception e)
        {
            generateErrorDialog("Error opening database connection", "Database Error");
            e.printStackTrace();
        }
    }

    /**
     * Executes a SQL query
     *
     * @param strSQL
     */
    @Override
    public void query(String strSQL)
    {
        try
        {
            dbStatement = dbConn.createStatement();
            dbResultSet = dbStatement.executeQuery(strSQL);
        } catch (Exception ex)
        {
            generateErrorDialog("Error executing query", "Database Error");
            ex.printStackTrace();
        }
    }

    /**
     * Sets field in DB table to specified contents
     *
     * @param strTable
     * @param strKeyName
     * @param strKeyContents
     * @param strFieldName
     * @param strFieldContents
     * @return
     */
    @Override
    public void setField(String strTable, String strKeyName, String strKeyContents, String strFieldName, String strFieldContents)
    {
        try
        {
            dbStatement = dbConn.createStatement();
            String strSQL = "UPDATE " + strTable + " SET " + strFieldName + "='" + strFieldContents + "' " +
                    " WHERE " + strKeyName + "='" + strKeyContents + "' ";
            dbStatement.executeUpdate(strSQL);
        } catch (SQLException e)
        {
            generateErrorDialog("Error setting record", "Database Error");
        }
    }

    private void generateErrorDialog(String message, String dialogName)
    {
        JOptionPane.showMessageDialog(null, message, dialogName, JOptionPane.ERROR_MESSAGE);
    }
}
