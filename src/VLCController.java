import java.io.IOException;

/**
 * Created by danielnguyen on 10/9/14.
 */


/**
 * Constructor will take a String representing the file path of VLC app and store in class level variable
 */
public class VLCController
{
    private String VLCPath;

    public VLCController(String inputPath)
    {
        VLCPath = inputPath;
    }

    /**
     * Opens an instance of the VLC Media Player through terminal with 'open' command
     */
    public void openVLC()
    {
        try
        {
            RedditDownloader dl = new RedditDownloader();
            Runtime.getRuntime().exec(new String[]{"open", VLCPath});
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Takes a String of all videos to play delimited by space ('\s') and uses terminal command
     * to place all videos into a playlist and starts playing automatically
     *
     * @param playlist
     */
    public void play(String playlist)
    {
        try
        {
            String cmd = VLCPath + "/Contents/MacOS/VLC " + playlist;
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e)
        {
            //TODO CATCH ALL THE EXCEPTIONS PROPERLY AKA DONT BE STUPID

            e.printStackTrace();
        }
    }
}
