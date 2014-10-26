import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StreamerInterface extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldSubreddit;
    private JTextField textFieldNumOfVideos;
    private JTextField textFieldVLCDir;
    private JButton buttonFileDir;
    private JLabel labelLoading;

    public StreamerInterface()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonFileDir.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                chooseVLCDir();
            }
        });

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private boolean dirChosen = false;
    private int numOfVideos = 0;
    private String VLCDir = "";

    /**
     * Opens an instance of FileChooser for user to direct to VLC's file path.
     * Program will then take the file path, append with '.app' to denote app file type extension, and will
     * push entire path to textBox for later use.
     */
    private void chooseVLCDir()
    {
        final JFileChooser chooseVLCDir = new JFileChooser();
        chooseVLCDir.setCurrentDirectory(new File("/Applications/"));
        chooseVLCDir.setDialogTitle("Navigate to VLC Media Player");
        int option = chooseVLCDir.showDialog(StreamerInterface.this, "Open");
        if (option == JFileChooser.APPROVE_OPTION)
        {
            dirChosen = true;
            File VLC = chooseVLCDir.getSelectedFile();
            VLCDir = VLC.getAbsolutePath() + ".app";
            textFieldVLCDir.setText(VLCDir);
        }
    }

    /**
     * On OK button press, input validation will be performed on all user input fields to ensure valid inputs were done.
     * On failed validation, dialogbox will prompt user to fix issues.
     * If all inputs are valid, program will run
     */
    private void onOK()
    {
        // Validate input
        boolean validNumber = false;
        if (dirChosen)
        {
            try
            {
                validNumber = true;
                Integer.parseInt(textFieldNumOfVideos.getText().toString());
            } catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Please specify a number of videos", "Improper number of videos", JOptionPane.ERROR_MESSAGE);
            }
            if (!textFieldSubreddit.getText().toString().isEmpty() && validNumber)
            {
                runScraper();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please Specify a Subreddit", "Subreddit not specified", JOptionPane.ERROR_MESSAGE);
            }
        }

        else
        {
            JOptionPane.showMessageDialog(this, "Please Select VLC Media Player", "VLC Directory not Specified", JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * Close program
     */
    private void onCancel()
    {
        dispose();
    }

    final String SAVE_FILE_DIR = "data/";
    final String DB_URL = "jdbc:derby:VIDEOS";
    static int numOfVideosMax;

    /**
     * Opens a connection and creates an Apache Derby database named VIDEOS
     */
    private void createDatabase()
    {
        //Creates Database and drops existing tables
        DB db = new DB();

        try
        {
            db.dbConn = DriverManager.getConnection(DB_URL + ";create=true");
            db.dbStatement = db.dbConn.createStatement();
            db.openConnection("VIDEOS");
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error opening database\nProgram will now close", "Critical Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

    }


    /**
     * Creates a table with subreddit name with single column of 250 chars to store YouTube URLs
     *
     * @param tableName
     */
    private void createTable(String tableName)
    {
        DB db = new DB();

        try
        {
            db.dbConn = DriverManager.getConnection(DB_URL);
            db.dbStatement = db.dbConn.createStatement();
            db.openConnection("VIDEOS");
            dropTable(tableName);
            db.dbStatement.execute("CREATE TABLE " + tableName + " (url CHAR(250))");
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error opening database\nProgram will now close", "Critical Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        } catch (NullPointerException e)
        {
            //Table does not exist to be dropped, nonissue
            e.printStackTrace();
        }
    }

    /**
     * Drops a table with the given name.
     *
     * @param tableName
     */
    private void dropTable(String tableName)
    {
        DB db = new DB();
        try
        {
            db.dbConn = DriverManager.getConnection(DB_URL);
            db.dbStatement = db.dbConn.createStatement();
            db.openConnection("VIDEOS");
            db.dbStatement.execute("DROP TABLE " + tableName);
        } catch (SQLException e)
        {
            System.out.println("Table does not exist");
        }


    }


    /**
     * Creates a database and table with subreddit name, then loops through subreddit and enters found YouTube
     * videos into database. Method will then build a string of video URLs deliited with space ('\s') and will
     * pass string to next method to open and run VLC app and stream videos
     */
    private void runScraper()
    {
        String subreddit = textFieldSubreddit.getText().toString();
        createDatabase();
        createTable(subreddit);
        //TODO DELETE THIS LINE AFTER DEBUGGING
        DB db = new DB();
        try
        {
            db.dbConn = DriverManager.getConnection(DB_URL);
            db.dbStatement = db.dbConn.createStatement();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        db.openConnection("VIDEOS");

        RedditDownloader dl = new RedditDownloader();


        numOfVideosMax = Integer.parseInt(textFieldNumOfVideos.getText().toString());
        final String BASE_URL = "http://www.reddit.com/r/" + subreddit;
        final String REGEX_PATTERN = "https?://www.youtube.com/watch\\?v=.*?\"|https?://youtu.be/.*?\"";
        String timeTag = "";
        String pageURL = "";
        String listDelimURL = "";
        int pageCounter = 0;

        File file = new File(SAVE_FILE_DIR);
        if (!file.exists())
        {
            file.mkdir();
        }

        String saveFileName = SAVE_FILE_DIR + "webpagedata.xml";
        String rawXML = dl.getRawXML(BASE_URL);

        RedditVideoParser parse = new RedditVideoParser();

        //TODO FIX BUG IN LOOP, PAGE NOT ITERATING PROPERLY
        while (numOfVideos < numOfVideosMax)
        {
            listDelimURL = dl.parseXML(rawXML, REGEX_PATTERN);
            addToDB(listDelimURL, subreddit);

            pageCounter++;
            timeTag = parse.getNextPage(rawXML, pageCounter * 25);
            pageURL = BASE_URL + "/?" + timeTag;
            rawXML = dl.getRawXML(pageURL);
            dl.saveToDisk(rawXML, saveFileName);
        }
        String playlist = dbToString(subreddit);
        VLCController vlc = new VLCController(textFieldVLCDir.getText().toString());
        vlc.openVLC();
        vlc.play(playlist);

        dropTable(subreddit);
        dispose();
    }


    /**
     * Performs SQL query to get all URLs stored in table to build a string of urls delimited with space character
     *
     * @param tableName
     * @return
     */
    private String dbToString(String tableName)
    {
        StringBuilder sb = new StringBuilder();
        DB db = new DB();
        try
        {
            db.dbConn = DriverManager.getConnection(DB_URL);
            db.dbStatement = db.dbConn.createStatement();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        db.openConnection("VIDEOS");

        db.query("SELECT * FROM " + tableName);
        while (db.moreRecords())
        {
            sb.append(db.getField("URL") + " ");
        }
        return sb.toString();
    }

    //TODO: LOADING STATUS

    /**
     * Takes a string of delimited URLs parsed from subreddit's XML and adds all URLs as new records into table
     *
     * @param strDelimURL
     * @param subredditTableName
     */
    private void addToDB(String strDelimURL, String subredditTableName)
    {
        try
        {
            DB db = new DB();
            db.dbConn = DriverManager.getConnection(DB_URL);
            db.dbStatement = db.dbConn.createStatement();
            String[] rawURL = strDelimURL.split("-;-");
            int index = 0;
            while (numOfVideos < numOfVideosMax && index < rawURL.length)
            {
                db.addRecord(subredditTableName, "url", rawURL[index].trim().substring(0, rawURL[index].length() - 1));
                numOfVideos++;
                index++;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e)
        {
            JOptionPane.showMessageDialog(this, "Cannot Find Subreddit\nProgram will now close", "Critical Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }


    /**
     * Creates an instance of GUI dialog
     *
     * @param args
     */
    public static void main(String[] args)
    {
        StreamerInterface dialog = new StreamerInterface();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
