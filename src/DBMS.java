/**
 * Created by danielnguyen on 10/19/14.
 */
public interface DBMS
{
    /**
     * Adds selected record with desired field to specified field
     */
    public void addRecord(String strTable, String strKeyName, String strKeyContents);

    /**
     * Closes current DB connection
     */
    public void close();

    /**
     * Deletes all records from SQL Table
     *
     * @param strTable
     */
    public void deleteAll(String strTable);

    /**
     * Gets records from specified fields in table
     *
     * @param strFieldName
     * @return
     */
    public String getField(String strFieldName);

    /**
     * Checks if there are more records in DB
     *
     * @return
     */
    public boolean moreRecords();

    /**
     * Opens a connection to DB
     *
     * @param strDataSourceName
     */
    public void openConnection(String strDataSourceName);

    /**
     * Executes a SQL query
     *
     * @param strSQL
     */
    public void query(String strSQL);

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
    public void setField(String strTable, String strKeyName, String strKeyContents, String strFieldName, String strFieldContents);
}
