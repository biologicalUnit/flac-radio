
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FlacRadioDb extends JFrame{
	private JList artistDatabase;
	private JList albumDatabase;
	private JList titleDatabase;
	private JTable searchDatabase;
	private JTextField searchBox;
	private JComboBox choices;
	private String selectedArtist;
	private String selectedTitle, selectedAlbum;
	private JButton searchButton;
	private JButton lyricsButton;
	private JButton pushBoxButton;
	private JTextField locationField;
	private JTextField userNameField;
	private JTextField passwordField;


	private String mysqlPath, mysqlUser, mysqlPass;

	private JMenuBar menuBar;
	private JMenuItem preference;
	private JMenu file;

	private DefaultListModel artistModel;
	private DefaultListModel titleModel;
	private DefaultListModel albumModel;
	private DefaultTableModel searchModel;
	private FlacRadioGUI gui1,gui2,gui3;

	private JPanel panel;
	private Thread t1,t2,t3;

	public FlacRadioDb(){

		mysqlPath = "localhost/music";
		mysqlUser = "root";
		mysqlPass = "";

		setSize(900,700);
		this.setBackground(Color.decode("#84A8C1"));
		selectedArtist = null;
		selectedTitle = null;
		selectedAlbum = null;
		artistModel = new DefaultListModel();
		albumModel = new DefaultListModel();
		titleModel = new DefaultListModel();
		searchModel = new DefaultTableModel();
		searchBox = new JTextField(20);
		searchButton = new JButton("Search");
		lyricsButton = new JButton("Info");
		pushBoxButton = new JButton("Pushbox");
		searchDatabase = new JTable(searchModel);
		String[] comboBoxString = { "Artist", "Album", "Title"};
		choices = new JComboBox(comboBoxString);
		artistDatabase = new JList(artistModel);
		albumDatabase = new JList(albumModel);
		titleDatabase = new JList(titleModel);

		titleDatabase.setDragEnabled(true);
		searchDatabase.setDragEnabled(true);


		//Load Previous preferences from file (MySQL Path, USER, PASS)
		FileInputStream fin;		
		try
		{
			fin = new FileInputStream ("preferences");
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
			mysqlPath = reader.readLine();
			System.out.println(mysqlPath);
			mysqlUser = reader.readLine();
			System.out.println(mysqlUser);
			mysqlPass = reader.readLine();
			System.out.println(mysqlPass);
			fin.close();
		}catch(Exception e){
			mysqlPath = "143.105.16.195/wjcuflac_music";
			mysqlUser = "wjcuflac";
			mysqlPass = "flacradio";
		}
		try{
			getAllArtists();
		}catch(Exception e){
			e.printStackTrace();
		}


		artistDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		albumDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		titleDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.setTitle("F-Radio");

		JScrollPane artistScrollPane = new JScrollPane(artistDatabase);
		JScrollPane albumScrollPane = new JScrollPane(albumDatabase);
		JScrollPane titleScrollPane = new JScrollPane(titleDatabase);
		JScrollPane searchScrollPane = new JScrollPane(searchDatabase);

		artistScrollPane.setLocation(330,0);
		artistScrollPane.setSize(200,200);
		artistScrollPane.setBorder(BorderFactory.createTitledBorder("Artist"));
		artistScrollPane.setBackground(Color.decode("#84A8C1"));

		albumScrollPane.setLocation(537,0);
		albumScrollPane.setSize(200,200);
		albumScrollPane.setBorder(BorderFactory.createTitledBorder("Album"));
		albumScrollPane.setBackground(Color.decode("#84A8C1"));

		titleScrollPane.setLocation(330,200);
		titleScrollPane.setSize(410,200);
		titleScrollPane.setBorder(BorderFactory.createTitledBorder("Title"));
		titleScrollPane.setBackground(Color.decode("#84A8C1"));

		searchScrollPane.setLocation(330,430);
		searchScrollPane.setSize(410,200);
		searchScrollPane.setBorder(BorderFactory.createTitledBorder("Search Result"));
		searchScrollPane.setBackground(Color.decode("#84A8C1"));

		searchBox.setLocation(330, 400);
		searchBox.setSize(190,25);

		choices.setSelectedIndex(2);
		choices.setLocation(530, 400);
		choices.setSize(100,25);

		searchButton.setLocation(640,400);
		searchButton.setSize(100,25);

		lyricsButton.setLocation(750,20);
		lyricsButton.setSize(110,75);

		pushBoxButton.setLocation(750,120);
		pushBoxButton.setSize(110,75);

		panel = new JPanel();
		panel.setBackground(Color.decode("#84A8C1"));
		artistDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				if(artistDatabase.getSelectedIndex()>=0){
					getAlbumsFromArtist(artistDatabase.getSelectedValue().toString());
					albumDatabase.setSelectedIndex(0);
					getTitlesFromAlbumArtist(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
				}
			}

		});
		albumDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				if(albumDatabase.getSelectedIndex()>=0){
					if (albumDatabase.getSelectedValue().toString().contains("All")){
						//getTitlesFromArtist(artistDatabase.getSelectedValue().toString());
					}else{
						getTitlesFromAlbumArtist(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
					}
				}
			}

		});
		titleDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				setSelectedArtist(getArtist());
				setSelectedTitle(getTitle());
				setSelectedAlbum(getAlbum());
			}
		});

		searchDatabase.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				getSelectedSearchArtistTitleAlbum();
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				search(searchBox.getText());
			}
		});

		lyricsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {	
				getLyrics();

			}
		});

		pushBoxButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				getPushBox();
			}
		});
		

		searchModel.addColumn("Title");
		searchModel.addColumn("Artist");
		searchModel.addColumn("Album");

		gui1 = new FlacRadioGUI(this,1);
		gui2 = new FlacRadioGUI(this,2);
		gui3 = new FlacRadioGUI(this,3);
		gui1.setSize(315,175);
		gui2.setSize(315,175);
		gui3.setSize(315,175);
		gui1.setLocation(0, 0);
		gui2.setLocation(0,180);
		gui3.setLocation(0,360);
		gui1.setBorder(BorderFactory.createTitledBorder("Player 1"));
		gui2.setBorder(BorderFactory.createTitledBorder("Player 2"));
		gui3.setBorder(BorderFactory.createTitledBorder("Player 3"));
		gui1.setVisible(true);
		gui2.setVisible(true);
		gui3.setVisible(true);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		file = new JMenu("File");
		menuBar.add(file);
		preference = new JMenuItem("Preferences");
		file.add(preference);
		preference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setPreferences();
			}
		});
		
		this.add(gui1);
		this.add(gui2);
		this.add(gui3);


		getContentPane().add(panel);
		panel.add(artistScrollPane);
		panel.add(titleScrollPane);
		panel.add(albumScrollPane);
		panel.add(searchScrollPane);
		panel.add(searchBox);
		panel.add(choices);
		panel.add(searchButton);
		panel.add(lyricsButton);
		panel.add(pushBoxButton);
		panel.setLayout(null);
		artistDatabase.setSelectedIndex(0);



		gui1.setEnabled(true);
		gui1.setDropTarget(new DropTarget(gui1,gui1));
		gui2.setEnabled(true);
		gui2.setDropTarget(new DropTarget(gui2,gui2));
		gui3.setEnabled(true);
		gui3.setDropTarget(new DropTarget(gui3,gui3));
	}

	public void getSelectedSearchArtistTitleAlbum(){
		setSelectedArtist((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 1));
		setSelectedTitle((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 0));
		setSelectedAlbum((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 2));
	}

	public String getArtist(){
		if(artistDatabase.getSelectedIndex() >=0){
			return artistDatabase.getSelectedValue().toString();
		}else{
			return null;
		}
	}
	public String getTitle(){
		if(titleDatabase.getSelectedIndex() >=0){
			return titleDatabase.getSelectedValue().toString();
		}else{
			return null;
		}
	}
	public String getAlbum(){
		return albumDatabase.getSelectedValue().toString();
	}

	public void getAllArtists(){
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT DISTINCT artist from music ORDER BY artist");
		if(resultSet != null){
			try {
				while(resultSet.next()){
					artistModel.addElement(resultSet.getString("artist"));
				}
			}catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error.");}
		}

	}
	public void getAlbumsFromArtist(String artist){
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT DISTINCT album from music WHERE artist=\""+artist+"\"");
		albumModel.removeAllElements();
		try {
			int i=0;
			while(resultSet.next()){
				albumModel.addElement(resultSet.getString("album"));
				i++;
			}
		}catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error.");}

	}
	public void getTitlesFromAlbumArtist(String artist, String album){
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT title from music WHERE artist=\""+artist+"\" and album=\""+album+"\"");
		titleModel.removeAllElements();
		int i=0;
		try{
			while(resultSet.next()){
				titleModel.addElement(resultSet.getString("title"));
				i++;
			}
		}catch(SQLException e){JOptionPane.showMessageDialog(this, "Database Error.");}

	}

	public ResultSet getDBInfo(String mysqlStatement){
		Connection connect = null;
		ResultSet resultSet = null;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://"+mysqlPath+"?"
					+ "user="+mysqlUser+"&password="+mysqlPass);
			PreparedStatement statement = connect.prepareStatement(mysqlStatement);
			resultSet = statement.executeQuery();
		}catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error.");}
		return resultSet;
	}
	public void setSelectedArtist(String artist){
		selectedArtist = artist;

	}
	public void setSelectedAlbum(String album){
		selectedAlbum = album;

	}
	public void setSelectedTitle(String title){
		selectedTitle = title;
	}
	public String getSelectedTitle(){

		return selectedTitle;
	}
	public String getSelectedAlbum(){
		return selectedAlbum;
	}
	public String getSelectedArtist(){

		return selectedArtist;
	}
	public void makeThread(int playerID,Runnable run){
		if(playerID == 1){
			t1 = new Thread(run);
			t1.start();
		}
		if(playerID == 2){
			t2 = new Thread(run);
			t2.start();
		}
		if(playerID == 3){
			t3 = new Thread(run);
			t3.start();
		}
	}
	public void search(String searchField){
		ResultSet results=null;
		int i=0;
		while(searchModel.getRowCount()>0){
			searchModel.removeRow(i);
		}
		if(choices.getSelectedItem().equals("Title")){
			results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE title like \"%"+searchField+"%\"");
		}else if(choices.getSelectedItem().equals("Artist")){
			results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE artist like \"%"+searchField+"%\"");
		}
		else if(choices.getSelectedItem().equals("Album")){
			results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE album like \"%"+searchField+"%\"");
		}
		try {
			String title, artist, album;
			while(results.next()){
				title = results.getString("title");
				artist = results.getString("artist");
				album = results.getString("album");
				searchModel.addRow(new String[]{title,artist,album});
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getLyrics(){
		JFrame lyrics = new JFrame();
		lyrics.setSize(500,500);
		lyrics.setVisible(true);
		JTextArea text = new JTextArea();
		JScrollPane scrollPane= new JScrollPane(text);
		scrollPane.setSize(300,300);
		scrollPane.setLocation(5,60);
		text.setText("ARTIST: "+this.getSelectedArtist()+"\n"+this.getSelectedAlbum()+"\n"+this.getSelectedTitle()+"\n\n"+gui1.flacPlayer.getLyrics(getSelectedArtist(), getSelectedTitle()));
		text.setEditable(false);
		lyrics.add(scrollPane);
		lyrics.setVisible(true);

	}
	public void setPreferences(){
		JFrame prefs = new JFrame();
		prefs.setSize(500,500);
		prefs.setVisible(true);

		locationField = new JTextField();
		userNameField = new JTextField();
		passwordField = new JTextField();
		JLabel locationLabel = new JLabel("Location to MySql Database: ");
		JLabel userNameLabel = new JLabel("MySql Username: ");
		JLabel passwordLabel = new JLabel("MySql Password: ");
		JButton updateButton = new JButton("Update");

		locationField.setSize(150, 20);
		userNameField.setSize(150, 20);
		passwordField.setSize(150, 20);
		updateButton.setSize(100, 75);

		locationLabel.setSize(250, 20);
		userNameLabel.setSize(250, 20);
		passwordLabel.setSize(250, 20);

		locationField.setLocation(300,20);
		userNameField.setLocation(300,50);
		passwordField.setLocation(300,80);
		updateButton.setLocation(20, 120);

		locationLabel.setLocation(20,20);
		userNameLabel.setLocation(20,50);
		passwordLabel.setLocation(20,80);

		prefs.add(locationLabel);
		prefs.add(userNameLabel);
		prefs.add(passwordLabel);

		prefs.add(locationField);
		prefs.add(userNameField);
		prefs.add(passwordField);
		prefs.add(updateButton);
		prefs.setLayout(null);
		prefs.setVisible(true);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updatePrefs();
			}
		});


	}
	public void updatePrefs(){
		FileOutputStream fout;
		try
		{
			String path;
			String user;
			String pass;
			path = locationField.getText();
			user = userNameField.getText();
			pass = passwordField.getText();
			fout = new FileOutputStream ("preferences");
			new PrintStream(fout).println (path);//path
			new PrintStream(fout).println (user);//user
			new PrintStream(fout).println (pass);//password
			fout.close();		
		}
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to write to file");
		}
	}

	public void getPushBox(){
		JFrame pushbox = new JFrame();
		pushbox.setSize(500,500);
		pushbox.setVisible(true);
		JTable pushDatabase = new JTable();
		JScrollPane pushpane = new JScrollPane(pushDatabase);
		pushpane.setLocation(0,0);
		pushpane.setSize(400,400);
		pushpane.setBorder(BorderFactory.createTitledBorder("Push Box"));
		pushbox.add(pushpane);
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT DISTINCT artist,album,title from music WHERE DATE > NOW() - INTERVAL 14 DAYS");

		try {
			System.out.println(resultSet.getString("Artist"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		FlacRadioDb db = new FlacRadioDb();
		db.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		db.setVisible(true);
	}


}




