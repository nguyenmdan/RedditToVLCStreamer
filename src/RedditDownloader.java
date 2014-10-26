import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by danielnguyen on 10/19/14.
 */
public class RedditDownloader implements Downloader
{
    public RedditDownloader()
    {

    }

    /**
     * Takes a string representing a URL, builds URL with Java's URL Class, downloads the raw webpage XML, and returns
     * the XML as a single string
     *
     * @param strURL
     * @return Raw XML from desired website
     */
    @Override
    public String getRawXML(String strURL)
    {
        StringBuilder rawXML = new StringBuilder();
        try
        {
            URL downloadURL = new URL(strURL);
            URLConnection connect = downloadURL.openConnection();
            InputStream download = connect.getInputStream();
            String webLine = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(download));

            while (reader.ready())
            {
                webLine = reader.readLine();
                rawXML.append(webLine + "\r\n");
            }
        } catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(null, "Invalid Subreddit Specified", "URL Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException i)
        {
            JOptionPane.showMessageDialog(null, "Cannot Read from Website", "Reader Error", JOptionPane.ERROR_MESSAGE);
        }

        return rawXML.toString();
    }

    /**
     * Takes a string to be parsed and desired regEx, and returns single string of all pattern matches with delimiters
     * between each pattern match
     *
     * @param rawString
     * @param regEx
     * @return single string of all pattern matches with delimiters
     * between each pattern match
     */
    @Override
    public String parseXML(String rawString, String regEx)
    {
        Pattern youTubePattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

        StringBuilder sb = new StringBuilder();

        Matcher match = youTubePattern.matcher(rawString);
        while (match.find())
        {
            sb.append(match.group() + "-;-");
            match.find();
        }
        return sb.toString();
    }

    /**
     * Saves selected string to disk
     *
     * @param strToSave String to be saved to disk
     * @param filePath  File path to save to
     */
    @Override
    public void saveToDisk(String strToSave, String filePath)
    {
        try
        {
            PrintWriter saveFile = new PrintWriter(new FileWriter(filePath));
            saveFile.println(strToSave);
            saveFile.close();
        } catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Error saving to disk", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks if file path exists
     *
     * @param filePath File path to check
     * @return
     */
    @Override
    public boolean doesFileExist(String filePath)
    {
        boolean doesFileExist = false;

        File file = new File(filePath);

        if (file.exists())
        {
            doesFileExist = true;
        }

        return doesFileExist;
    }
}
