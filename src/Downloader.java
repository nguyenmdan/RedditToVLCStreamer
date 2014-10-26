/**
 * Created by danielnguyen on 10/19/14.
 */
public interface Downloader
{
    /**
     * Takes a string representing a URL, builds URL with Java's URL Class, downloads the raw webpage XML, and returns
     * the XML as a single string
     *
     * @param strURL
     * @return Raw XML from desired website
     */
    public String getRawXML(String strURL);

    /**
     * Takes a string to be parsed and desired regEx, and returns single string of all pattern matches with delimiters
     * between each pattern match
     *
     * @param rawString
     * @param regEx
     * @return single string of all pattern matches with delimiters
     * between each pattern match
     */
    public String parseXML(String rawString, String regEx);

    /**
     * Saves seelcted string to disk
     *
     * @param strToSave String to be saved to disk
     * @param filePath  File path to save to
     */
    public void saveToDisk(String strToSave, String filePath);

    /**
     * Checks if file path exists
     *
     * @param filePath File path to check
     * @return
     */
    public boolean doesFileExist(String filePath);

}
