package ca.ubc.cs.cs211.hockeypool.hpool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class HockeyGUI implements ActionListener
{

    private HockeyPool HockeyPool;
    private JFrame frame;
    private JMenuBar menuBar;
    private JPanel HockeyPoolPane;
    private JTextArea textArea;
    private JFileChooser chooser;
    private JLabel label;
    private PoolTeam pt;
    private Player p;
    private JSpinner spinner;
    JComboBox teams;
    JComboBox players;
                
    public HockeyGUI()
    {
        HockeyPool = new HockeyPool( "Untitled" );
        teams = new JComboBox();   // do i need to initialize this here?
        players = new JComboBox();  // do i need to initialize this here?

    }

    public void showHockeyPool()
    {
        frame = new JFrame( "HockeyPool " + HockeyPool.getName() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        HockeyPoolPane = new JPanel( new GridLayout( 2, 50 ) );

        textArea = new JTextArea( 2, 3 );
        frame.getContentPane().add( HockeyPoolPane, BorderLayout.CENTER );
        frame.getContentPane().add( textArea, BorderLayout.NORTH );
        initializeHockeyPool();
        initializeMenu();
        frame.pack();
        frame.setVisible( true );
    }

    public void showPopUp( String FrameName, String ButtonName,
            String TextAreaDisplayed, String tmName )
    {
        final String teamName = tmName;
        frame.setVisible( false );
        frame = new JFrame( FrameName );
        JPanel labelPane = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane = new JPanel( new GridLayout( 1, 50 ) );
        HockeyPoolPane = new JPanel( new GridLayout( 1, 50 ) );
        textArea = new JTextArea( 1, 30 );
        label = new JLabel( TextAreaDisplayed );
        label.setLabelFor( textArea );

        frame.getContentPane().add( labelPane, BorderLayout.WEST );
        frame.getContentPane().add( fieldPane, BorderLayout.LINE_END );
        frame.getContentPane().add( textArea, BorderLayout.NORTH );
        frame.getContentPane().add( HockeyPoolPane, BorderLayout.SOUTH );
        labelPane.add( label );
        fieldPane.add( textArea );

        JButton button, button2;     
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        
        
        button = new JButton( ButtonName );
        HockeyPoolPane.add( button );
        button2 = new JButton( "Return to menu" );
        HockeyPoolPane.add( button2 );
        
        
        

        if( FrameName == "Show Pool Stats" || FrameName == "Show Pool Team Stats" )
        {
            textArea.setVisible( false );
            SpinnerModel model = new SpinnerNumberModel( 0, 0, 100, 1 );
            spinner = new JSpinner( model );
            frame.getContentPane().add( spinner, BorderLayout.LINE_END );
        }

        button2.addActionListener( this );

        frame.pack();
        frame.setVisible( true );
        button.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                
                if( frame.getTitle() == "Change a Pool Name" //correct
                        && textArea.getText() != null )
                {
                    HockeyPool.setName( textArea.getText() );
                    textArea.setText( "Pool Name changed to "
                            + textArea.getText() );
                }
                if( frame.getTitle() == "Add a Pool Team" // correct
                        && textArea.getText() != null )
                {
                    String team = textArea.getText();
                    if( HockeyPool.addPoolTeam( new PoolTeam( team ) )) 
                    {

                        teams.addItem( textArea.getText() );

                        textArea.setText( "Pool Team " + team + " added" );
                    }
                    else
                        textArea.setText( "Pool Team already exists"
                                + team
                                + " Please choose a different team name" );

                }
                
                if( frame.getTitle() == "Show Pool Stats" ) // not sure if this works
                {
                    Iterator<PoolTeam> it = HockeyPool.poolTeamsIterator();
                    int leaderPoints = -1;
                    int value = ( (Integer) spinner.getValue() ).intValue();
                    String[] columnNames = { "Rank ", "Team Name ", "GP ",
                            "Goals ", "Assists ", value + "", "Points ",
                            "Points Back" };
                    Object[][] data = new Object[ HockeyPool.getNumTeams() ][ 9 ];
                    int i = 1;
                    while( it.hasNext() )
                    {
                        pt = it.next();
                        HockeyStats currentStats = pt.getMostRecentTotalStats();
                        if( leaderPoints == -1 )
                            leaderPoints = currentStats.getPoints();
                        HockeyStats daysBackStats = pt
                                .getDaysBackTotalStats( value );

                        data[ i - 1 ][ 0 ] = i;
                        data[ i - 1 ][ 1 ] = pt.getName();
                        data[ i - 1 ][ 2 ] = currentStats.getGamesPlayed();
                        data[ i - 1 ][ 3 ] = currentStats.getGoals();
                        data[ i - 1 ][ 4 ] = currentStats.getAssists();
                        data[ i - 1 ][ 5 ] = ( daysBackStats == null ) ? "N/A"
                                : daysBackStats.getPoints() + "";
                        data[ i - 1 ][ 6 ] = currentStats.getPoints();
                        data[ i - 1 ][ 7 ] = leaderPoints
                                - currentStats.getPoints();
                        i++;
                    }

                    frame.setVisible( false );
                    frame = new JFrame( "Pool Stats" );
                    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                    HockeyPoolPane = new JPanel( new GridLayout( 1, 50 ) );
                    frame.getContentPane().add( HockeyPoolPane,
                            BorderLayout.CENTER );
                    JTable table = new JTable( data, columnNames );
                    table.setPreferredScrollableViewportSize( new Dimension(
                            50, 70 ) );
                    table.setFillsViewportHeight( true );
                    JScrollPane scrollPane = new JScrollPane( table );
                    scrollPane.setPreferredSize( new Dimension( 1000, 400 ) );
                    
                    JPanel topPanel = new JPanel();
                    frame.getContentPane().add(topPanel, BorderLayout.NORTH);
                    
                    
                    HockeyPoolPane.add( scrollPane );
                    JButton button = new JButton( "Return to menu" );
                    topPanel.add( button );

                    button.addActionListener( new ActionListener(){

                        
                        public void actionPerformed( ActionEvent e )
                        {
                           frame.setVisible( false );
                           showHockeyPool();
                        }
                        
                    });
                    frame.pack();
                    frame.setVisible( true );

                }

                if( frame.getTitle() == "Change Pool Team Name" ) // this should be correct. 
                {                                                 // method supplies it with a team to which it makes changes to.
                    String team = textArea.getText();             // the InitializePoolTeamMenu method calls this method 
                    HockeyPool.getPoolTeam( teamName ).setName(   // It should have a method that sends you back to InitializePoolTeamMenu after finishing
                            team );

                    
                    textArea.setText( "Pool Team Name changed to "
                            + team );
                    
                    
                }                                      // This should be correct     
                if( frame.getTitle() == "Add Player" ) // the InitializePoolTeamMenu method calls this method 
                {                                      // method supplies it with a team to which it makes changes to
                                                       // It should have a method that sends you back to InitializePoolTeamMenu after finishing
                    String name = textArea.getText();
                    Player newPlayer = new Player( name );
                    if( HockeyPool.getAllPlayers().contains( newPlayer ) )
                        textArea.setText( "Player " + name
                                + " is already in this hockey pool" );
                    else if( HockeyPool.getPoolTeam( teamName ).addPlayer(
                            new Player( name ) ) )
                    {

                       
                        textArea.setText( "Player " + textArea.getText()
                                + " added to the team "
                                + HockeyPool.getPoolTeam( teamName ).getName() );
                    }
                    else
                        textArea
                                .setText( "Error: There is already a player named "
                                        + name + " on this team" );
                }
                if( frame.getTitle() == "Show Pool Team Stats" )// THIS IS WRONG... NEEDS FIXING.
                {                                               // the InitializePoolTeamMenu method calls this method 
                                                                // It should have a method that sends you back to InitializePoolTeamMenu after finishing
                    Iterator<Player> pIt = HockeyPool.getPoolTeam( teamName ).playersIterator();
                    int value = ( (Integer) spinner.getValue() ).intValue();
                    String[] columnNames = { "Rank", "Name", "Team", "Position", "GP", "Goals", "Assists", value+"","Points"};
                    Object[][] data = new Object[ HockeyPool.getPoolTeam( teamName ).getNumPlayers()][ 10 ];
                    int i = 1;
                    while( pIt.hasNext() )
                    {
                        p = pIt.next();
                        HockeyStats currentStats = p.getMostRecentPlayerStats();
                        HockeyStats daysBackStats = p.getDaysBackStats(value);

                        data[ i - 1 ][ 0 ] = i;
                        data[ i - 1 ][ 1 ] = p.getName();
                        data[ i - 1 ][ 2 ] = p.getTeam();
                        data[ i - 1 ][ 3 ] = p.getPos();
                        data[ i - 1 ][ 4 ] = currentStats.getGamesPlayed();
                        data[ i - 1 ][ 5 ] = currentStats.getGoals();
                        data[ i - 1 ][ 6 ] = currentStats.getAssists();
                        data[ i - 1 ][ 7 ] = ( daysBackStats == null ) ? "N/A"
                                : daysBackStats.getPoints() + "";
                        data[ i - 1 ][ 8 ] = currentStats.getPoints();
                        i++;
                    }

                    frame.setVisible( false );
                    frame = new JFrame( "Team Stats" );
                    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                    HockeyPoolPane = new JPanel( new GridLayout( 1, 50 ) );
                    frame.getContentPane().add( HockeyPoolPane,
                            BorderLayout.CENTER );
                    JTable table = new JTable( data, columnNames );
                    table.setPreferredScrollableViewportSize( new Dimension(
                            50, 70 ) );
                    table.setFillsViewportHeight( true );
                    JScrollPane scrollPane = new JScrollPane( table );
                    scrollPane.setPreferredSize( new Dimension( 1000, 400 ) );
                    
                    JPanel topPanel = new JPanel();
                    frame.getContentPane().add(topPanel, BorderLayout.NORTH);
                    
                    
                    HockeyPoolPane.add( scrollPane );
                    JButton button = new JButton( "Return to menu" );
                    topPanel.add( button );
                    
                    button.addActionListener( new ActionListener(){
                        public void actionPerformed( ActionEvent e )
                        {
                           frame.setVisible( false );
                           showHockeyPool();
                        }
                    });


                    frame.pack();
                    frame.setVisible( true );

                }
            }
        } );
    }

    private void initializeHockeyPool()
    {
        JButton button2, button3, button4, button5, button6, button7, button8, button9, button10;
        JPanel mainMenuEast = new JPanel(new GridLayout(1, 50));
        frame.getContentPane().add(mainMenuEast, BorderLayout.EAST);
        
        button2 = new JButton( "Change a Pool Name" );
        HockeyPoolPane.add( button2 );
        button2.addActionListener( new ActionListener() // shoudld be correct 
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Change a Pool Name", "Change", "Enter PoolName: ",
                        null );
            }
        } );
        
        button3 = new JButton( "Add a Pool Team" );
        HockeyPoolPane.add( button3 );
        button3.addActionListener( new ActionListener() // should be correct
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Add a Pool Team", "Add", "Enter Pool Team Name: ",
                        null );
            }
        } );
        button4 = new JButton( "Remove Pool Team" );
        HockeyPoolPane.add( button4 );
        button4.addActionListener( new ActionListener() // should be correct
        {
            public void actionPerformed( ActionEvent e )
            {
                removeTeamPopUp( "Remove Pool Team", "Remove",
                        "Select a team to remove: " );
            }
        } );
        button5 = new JButton( "Update Pool" );   // not sure if this works
        HockeyPoolPane.add( button5 );
        button5.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                HockeyPool.update();
                HockeyPool.resort();
                textArea.setText( "Pool is updated" );
            }
        } );
        button6 = new JButton( "Show Pool Stats" ); // not sure if this works
        HockeyPoolPane.add( button6 );
        button6.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Show Pool Stats", "Show",
                        "Days Back: ", null );
            }
        } );
        button7 = new JButton( "Load Pool From Object File" ); // not sure if this works
        HockeyPoolPane.add( button7 );
        button7.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog( frame );
                if( returnVal == JFileChooser.APPROVE_OPTION )
                {
                    File file = chooser.getSelectedFile();
                    try
                    {

                        HockeyPool.load( file.getAbsolutePath());
                        textArea.setText( "Pool loaded successfully" );
                    }
                    catch( Exception e1 )
                    {
                        textArea
                                .setText( "Error: Unable to load pool with this filename" );
                    }
                }
            }
        } );
        button8 = new JButton( "Load Pool From Text File" );
        HockeyPoolPane.add( button8 );
        button8.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog( frame );
                if( returnVal == JFileChooser.APPROVE_OPTION )
                {
                    File file = chooser.getSelectedFile();
                    try
                    {
                        HockeyPool = ca.ubc.cs.cs211.hockeypool.hpool.HockeyPool.loadFromTextFile(file.getPath() );
                        textArea.setText( "Pool loaded successfully" );
                    }
                    catch( Exception e1 )
                    {
                        textArea
                                .setText( "Error: Unable to load pool with this filename" );
                    }
                }
            }
        } );

        button9 = new JButton( "Save Pool as Object File" ); // not sure if this works
        HockeyPoolPane.add( button9 );
        button9.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                chooser = new JFileChooser();
                int returnVal = chooser.showSaveDialog( frame );
                if( returnVal == JFileChooser.APPROVE_OPTION )
                {

                    File file = chooser.getSelectedFile();

                    try
                    {
                        HockeyPool.save( file.getAbsolutePath() );
                        textArea.setText( "Pool saved successfully" );
                    }
                    catch( Exception e1 )
                    {
                        textArea.setText( "Error: Unable to save pool" );
                    }
                }
            }
        } );
        button10 = new JButton( "Show Pool Team Menu" ); // should be correct
        mainMenuEast.add( button10 );
        button10.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                initializeTeamSelectMenu( "Team Menu",
                        "Select a team to edit: " );
            }
        } );

    }

    private void initializeMenu()
    {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu( "File" );
        menuItem = new JMenuItem( "Quit" );
        menuItem.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                System.err.println( "Close window" );
                frame.setVisible( false );
                frame.dispose();
            }
        } );
        menu.add( menuItem );
        menuBar = new JMenuBar();
        menuBar.add( menu );
        frame.setJMenuBar( menuBar );
    }

    private void initializePoolTeamMenu( String tmName )
    {
        final String teamName = tmName;
        frame.setVisible( false );
        frame = new JFrame( teamName + " Menu" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        HockeyPoolPane = new JPanel( new GridLayout( 2, 50 ) );
        JPanel southPane = new JPanel (new GridLayout(1, 50));
        textArea = new JTextArea( 2, 3 );
        frame.getContentPane().add( HockeyPoolPane, BorderLayout.CENTER );
        frame.getContentPane().add( textArea, BorderLayout.NORTH );
        frame.getContentPane().add(southPane, BorderLayout.EAST);
        JButton button1, button2, button3, button4, button5;

        button1 = new JButton( "Change Pool Team Name" );
        HockeyPoolPane.add( button1 );
        button1.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Change Pool Team Name", "Change",
                        "Enter PoolName: ", teamName );
            }
        } );
        button2 = new JButton( "Add Player" );
        HockeyPoolPane.add( button2 );
        button2.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Add Player", "Add", "Enter Player Name: ", teamName );
            }
        } );
        button3 = new JButton( "Remove Player" );
        HockeyPoolPane.add( button3 );
        button3.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                removePlayerPopUp( "Remove Team player", "Remove",
                        "Select a player to remove: ", teamName );
            }
        } );
        button4 = new JButton( "Show Pool Team Stats" );
        HockeyPoolPane.add( button4 );
        button4.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                showPopUp( "Show Pool Team Stats", "Show", "Days Back: ",
                        teamName );
            }
        } );
        
        button5 = new JButton("Return To Menu");
        southPane.add( button5 );
        button5.addActionListener( this );

        frame.pack();
        frame.setVisible( true );
    }

    public void removeTeamPopUp( String FrameName, String ButtonName,
            String TextAreaDisplayed )
    {

        frame.setVisible( false );
        frame = new JFrame( FrameName );
        JPanel labelPane = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane1 = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane2 = new JPanel( new GridLayout( 1, 50 ) );
        HockeyPoolPane = new JPanel( new GridLayout( 1, 50 ) );
        textArea = new JTextArea( 5, 20 );
        label = new JLabel( TextAreaDisplayed );

        frame.getContentPane().add( labelPane, BorderLayout.WEST );
        frame.getContentPane().add( fieldPane1, BorderLayout.NORTH );
        frame.getContentPane().add( fieldPane2, BorderLayout.EAST );
        frame.getContentPane().add( HockeyPoolPane, BorderLayout.SOUTH );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        labelPane.add( label );
        fieldPane1.add( textArea );

        teams = makeTeamComboBox();

        fieldPane2.add( teams );
        teams.setEditable( false );

        JButton button;
        JButton button2;

        button = new JButton( "Confirm remove" );
        HockeyPoolPane.add( button );

        button.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                if( HockeyPool.getNumTeams() == 0 )
                {
                    textArea.setText( "Error: There are no pool teams." ); // this
                                                                           // works
                                                                           // if
                                                                           // some1
                                                                           // presses
                                                                           // "Remove Team"
                                                                           // button
                                                                           // in
                                                                           // the
                                                                           // main
                                                                           // menu
                }
                else
                {
                    String team = (String) teams.getSelectedItem();
                    if( HockeyPool.removePoolTeam(team))
                    {
                        textArea.setText( "Pool Team " + team
                                + " removed" );
                        teams.removeItem( team );
                    }
                    else
                        textArea.setText( "Pool " + team
                                + " does not exist." );
                }
            }
        } );

        button2 = new JButton("Return To Menu");
        HockeyPoolPane.add(button2);
        button2.addActionListener( this );

        frame.pack();
        frame.setVisible( true );
    }

    public void initializeTeamSelectMenu( String FrameName,
            String TextAreaDisplayed )
    {
        JButton button2 = new JButton("Return To Menu");
        
        frame.setVisible( false );
        teams = makeTeamComboBox();
        frame = new JFrame( FrameName );
        label = new JLabel( TextAreaDisplayed );

        JPanel labelPane = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane = new JPanel( new GridLayout( 1, 50 ) );
        JPanel buttonPane = new JPanel(new GridLayout(1, 50));

        frame.getContentPane().add( fieldPane, BorderLayout.LINE_END );
        frame.getContentPane().add( labelPane, BorderLayout.WEST );
        frame.getContentPane().add(buttonPane, BorderLayout.SOUTH);

        buttonPane.add( button2 );
        labelPane.add( label );
        fieldPane.add( teams );

        button2.addActionListener( this );
        
        teams.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                String teamToEdit = (String) teams.getSelectedItem();
                initializePoolTeamMenu( teamToEdit );
            }
        } );

        frame.pack();
        frame.setVisible( true );

    }

    public void removePlayerPopUp( String FrameName, String ButtonName,
            String TextAreaDisplayed, String tmName )
    {
        
        
        frame.setVisible( false );
        
        final String teamName = tmName;
        players = makePlayerComboBox(teamName);
        frame = new JFrame( FrameName );
        JPanel labelPane = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane1 = new JPanel( new GridLayout( 1, 50 ) );
        JPanel fieldPane2 = new JPanel( new GridLayout( 1, 50 ) );
        HockeyPoolPane = new JPanel( new GridLayout( 1, 50 ) );
        textArea = new JTextArea( 5, 20 );
        label = new JLabel( TextAreaDisplayed );

        frame.getContentPane().add( labelPane, BorderLayout.WEST );
        frame.getContentPane().add( fieldPane1, BorderLayout.NORTH );
        frame.getContentPane().add( fieldPane2, BorderLayout.EAST );
        frame.getContentPane().add( HockeyPoolPane, BorderLayout.SOUTH );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        labelPane.add( label );
        fieldPane1.add( textArea );

        

        fieldPane2.add( players );
        players.setEditable( false );

        JButton button;
        JButton button2;

        button = new JButton( "Confirm remove" );
        HockeyPoolPane.add( button );

        button.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                String player = (String) players.getSelectedItem();
                if(HockeyPool.getPoolTeam( teamName ).removePlayer( player))
                {                    
                    textArea.setText( "Player" + player + "removed" );
                    players.removeItem( player );
                }
                else
                    textArea.setText( "Error: " + player + "is not on this pool team" );
            }
        } );

        button2 = new JButton( "Return to menu" );
        HockeyPoolPane.add( button2 );

        button2.addActionListener( this);
        
           

        frame.pack();
        frame.setVisible( true );
    }

    
    public JComboBox makeTeamComboBox()
    {
        JComboBox teamBox = new JComboBox();
        Iterator<PoolTeam> tmIt = HockeyPool.poolTeamsIterator();
        while( tmIt.hasNext() )
        {
            teamBox.addItem( tmIt.next().getName() );
        }
        return teamBox;
    }

    public JComboBox makePlayerComboBox( String tmName )
    {
        JComboBox playerBox = new JComboBox();
        String team = tmName;
        Iterator<Player> pIt = HockeyPool.getPoolTeam( team ).playersIterator();
        while( pIt.hasNext() )
        {
            playerBox.addItem( pIt.next().getName() );
        }
        return playerBox;
    }

  
    @Override
    public void actionPerformed( ActionEvent e )
    {
        frame.setVisible( false );
        showHockeyPool();
    }

}

