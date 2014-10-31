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
    private String osName;

    public VLCController(String inputPath, final String OS_NAME)
    {
        VLCPath = inputPath;
        osName = OS_NAME;
    }

    /**
     * Opens an instance of the VLC Media Player through terminal with 'open' command
     */

    //Runtime.getRuntime().exec
    public void openVLC()
    {
        String path = "";
        if (osName.startsWith("Mac"))
        {
            path = VLCPath;
            try
            {
                Runtime.getRuntime().exec(new String[]{"open", path});

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else if (osName.startsWith("Windows"))
        {
            path = "\"" + VLCPath + "\"";
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
        String cmd = "";
        if (osName.startsWith("Mac"))
        {
            cmd = VLCPath + "/Contents/MacOS/VLC " + playlist;
            try
            {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else if (osName.startsWith("Windows"))
        {
            try
            {
                //TODO: CHANGE ALL SPACE TO \s FOR WINDOWS
                Runtime.getRuntime().exec(new String[]{VLCPath, playlist});
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
