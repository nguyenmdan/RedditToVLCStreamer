import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by danielnguyen on 10/7/14.
 */
public class RedditVideoParser extends RedditDownloader
{
    public RedditVideoParser()
    {
    }


    /**
     * Uses RegEx to find the next page URL for the subreddit and returns the URL to be used to load the next page.
     * Pattern will be used to find the following URL format:
     * http://www.reddit.com/r/[subreddit]/?count=[count]&amp;after=t3_2jsn25"
     *
     * @param rawXML
     * @param count
     * @return
     */
    public String getNextPage(String rawXML, int count)
    {
        String searchPattern = "count=" + count + "&amp;after=.*?\"";
        Pattern nextPagePattern = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);
        String retURL = "";

        //Loads saved page to use matcher
        //Finds time tag modifier on Next Page link

        Matcher match = nextPagePattern.matcher(rawXML);
        while (match.find())
        {
            retURL = match.group();
        }

        //Removes end quotation mark from time tag
        retURL = retURL.substring(0, retURL.length() - 1);
        return retURL;
    }
}