import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FlacRadioGUI extends JFrame{
	private FlacPlayer flacPlayer1;
	private FlacPlayer flacPlayer2;
	private JButton playPause1;
	private JButton playPause2;
	private JButton loadEject1;
	private JButton loadEject2;
	private JButton back1;
	private JButton back2;
	private JTextArea player1;
	private JTextArea player2;
	private JList artistDatabase;
	private JList titleDatabase;
	private DefaultListModel artistModel;
	private DefaultListModel titleModel;
	private JPanel panel;
	private boolean paused1;
	private boolean paused2;
	private boolean hasTrack1;
	private boolean hasTrack2;

	public FlacRadioGUI(){
		flacPlayer1 = new FlacPlayer("track1.wav");
		flacPlayer2 = new FlacPlayer("track2.wav");

		setSize(800,600);
		setVisible(true);

		player1 = new JTextArea("No Track Loaded");
		player2 = new JTextArea("No Track Loaded");
		player1.setEditable(false);
		player2.setEditable(false);
		player1.setDragEnabled(true);
		player2.setDragEnabled(true);

		artistModel = new DefaultListModel();
		titleModel = new DefaultListModel();

		artistDatabase = new JList(artistModel);
		getAllArtists();
		titleDatabase = new JList(titleModel);
		//getAllTitles();

		artistDatabase.setDragEnabled(true);
		titleDatabase.setDragEnabled(true);

		artistDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		titleDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane artistScrollPane = new JScrollPane(artistDatabase);
		JScrollPane titleScrollPane = new JScrollPane(titleDatabase);

		playPause1 = new JButton("Play");
		paused1=true;
		hasTrack1=false;
		back1 = new JButton("|<-");
		loadEject1 = new JButton("Load");
		playPause1.setEnabled(false);
		back1.setEnabled(false);

		//placement and size for first set
		playPause1.setSize(95,25);
		playPause1.setLocation(10,215);
		back1.setSize(94,25);
		back1.setLocation(113,215);
		loadEject1.setSize(95,25);
		loadEject1.setLocation(215,215);

		playPause2 = new JButton("Play");
		paused2=true;
		hasTrack2=false;
		back2 = new JButton("|<-");
		loadEject2 = new JButton("Load");
		playPause2.setEnabled(false);
		back2.setEnabled(false);

		// placement and size for second set
		playPause2.setSize(95,25);
		playPause2.setLocation(360,215);
		back2.setSize(94,25);
		back2.setLocation(463,215);
		loadEject2.setSize(95,25);
		loadEject2.setLocation(565,215);

		player1.setLocation(10, 10);
		player2.setLocation(360,10);
		player1.setSize(300,200);
		player2.setSize(300,200);
		player1.setBorder(BorderFactory.createTitledBorder("Player 1"));
		player2.setBorder(BorderFactory.createTitledBorder("Player 2"));

		artistScrollPane.setLocation(10,275);
		artistScrollPane.setSize(200,275);
		artistScrollPane.setBorder(BorderFactory.createTitledBorder("Artist"));

		titleScrollPane.setLocation(210,275);
		titleScrollPane.setSize(200,275);
		titleScrollPane.setBorder(BorderFactory.createTitledBorder("Title"));

		panel = new JPanel();
		artistDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				getTitlesFromArtist(artistDatabase.getSelectedValue().toString());
			}

		});
		playPause1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				play1();
			}
		});
		playPause2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				play2();
			}
		});
		back1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rewind1();
				paused1 = true;
				playPause1.setText("Play");
			}
		});
		back2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rewind2();
				paused2 = true;
				playPause2.setText("Play");
			}
		});
		loadEject1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				loadEject1();
			}
		});
		loadEject2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				loadEject2();
			}
		});

		getContentPane().add(panel);
		panel.setBackground(Color.white);
		panel.add(player1);
		panel.add(player2);
		panel.add(artistScrollPane);
		panel.add(titleScrollPane);
		panel.add(playPause1);
		panel.add(back1);
		panel.add(loadEject1);
		panel.add(playPause2);
		panel.add(back2);
		panel.add(loadEject2);
		panel.setLayout(null);

	}
	public void play1(){
		if(paused1){
			flacPlayer1.play();
			paused1 = false;
			playPause1.setText("Pause");

		}else{
			flacPlayer1.pause();
			paused1 = true;
			playPause1.setText("Play");

		}
	}
	public void play2(){
		if(paused2){
			flacPlayer2.play();
			playPause2.setText("Pause");
			paused2=false;
		}else{
			flacPlayer2.pause();
			playPause2.setText("Play");
			paused2=true;
		}
	}
	public void rewind1(){
		flacPlayer1.rewind();
	}
	public void rewind2(){
		flacPlayer2.rewind();
	}
	public void loadEject1(){
		if(hasTrack1){
			if(!paused1){
				flacPlayer1.pause();
			}
			paused1=true;
			flacPlayer1.pause();
			playPause1.setEnabled(false);
			loadEject1.setText("Load");
			back1.setEnabled(false);
			hasTrack1 = false;
			player1.setText("No Track Loaded");
		}
		else{
			Connection connect = null;
			ResultSet resultSet = null;
			if(artistDatabase.getSelectedIndex() >=0 && titleDatabase.getSelectedIndex()>=0){
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connect = DriverManager.getConnection("jdbc:mysql://localhost/music?"
						+ "user=root&password=");
				PreparedStatement statement = connect.prepareStatement("SELECT path from MUSIC WHERE artist=\""+artistDatabase.getSelectedValue().toString()+"\"and title=\""+titleDatabase.getSelectedValue().toString()+"\"");
				resultSet = statement.executeQuery();
				String path = null;
				while(resultSet.next()){
					path = resultSet.getString("path");
				}
				paused1=true;
				flacPlayer1.load(path);
				hasTrack1 = true;
				player1.setText(artistDatabase.getSelectedValue().toString()+" - "+titleDatabase.getSelectedValue().toString());
				loadEject1.setText("Eject");
				back1.setEnabled(true);
				playPause1.setEnabled(true);
				statement.close();
				connect.close();
			}catch(Exception ex){ex.printStackTrace();}
			}else{
				player1.setText("Please Select a Track to Load");
			}
		}
	}
	public void loadEject2(){
		if(hasTrack2){
			if(!paused2){
				flacPlayer2.pause();
			}
			paused2=true;
			playPause2.setEnabled(false);
			loadEject2.setText("Load");
			back2.setEnabled(false);
			hasTrack2 = false;
			player2.setText("No Track Loaded");
		}
		else{
			Connection connect = null;
			ResultSet resultSet = null;
			if(artistDatabase.getSelectedIndex() >=0 && titleDatabase.getSelectedIndex()>=0){
				try {
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					connect = DriverManager.getConnection("jdbc:mysql://localhost/music?"
							+ "user=root&password=");
					PreparedStatement statement = connect.prepareStatement("SELECT path from MUSIC WHERE artist=\""+artistDatabase.getSelectedValue().toString()+"\"and title=\""+titleDatabase.getSelectedValue().toString()+"\"");
					System.out.println(artistDatabase.getSelectedValue().toString()+"\"and title=\""+titleDatabase.getSelectedValue().toString());
					resultSet = statement.executeQuery();
					String path = null;
					while(resultSet.next()){
						path = resultSet.getString("path");
					}
					paused2=true;
					flacPlayer2.pause();
					flacPlayer2.load(path);
					hasTrack2 = true;
					player2.setText(artistDatabase.getSelectedValue().toString()+" - "+titleDatabase.getSelectedValue().toString());
					loadEject2.setText("Eject");
					back2.setEnabled(true);
					playPause2.setEnabled(true);
					statement.close();
					connect.close();
				}catch(Exception ex){ex.printStackTrace();}
			}else{
				player2.setText("Please Select a Track to Load");
			}
		}
	}
	public void getAllArtists(){
		Connection connect = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://localhost/music?"
					+ "user=root&password=");
			PreparedStatement statement = connect.prepareStatement("SELECT DISTINCT artist from MUSIC ORDER BY artist");

			resultSet = statement.executeQuery();
			while(resultSet.next()){
				artistModel.addElement(resultSet.getString("artist"));
			}
			statement.close();
			connect.close();
		}catch(Exception ex){ex.printStackTrace();}

	}
	public void getAllTitles(){
		Connection connect = null;
		ResultSet resultSet = null;
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://localhost/music?"
					+ "user=root&password=");
			PreparedStatement statement = connect.prepareStatement("SELECT title from MUSIC");

			resultSet = statement.executeQuery();
			while(resultSet.next()){
				titleModel.addElement(resultSet.getString("title"));
			}
			statement.close();
			connect.close();
		}catch(Exception ex){ex.printStackTrace();}

	}

	public void getTitlesFromArtist(String artist){
		Connection connect = null;
		ResultSet resultSet = null;
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://localhost/music?"
					+ "user=root&password=");
			PreparedStatement statement = connect.prepareStatement("SELECT title from MUSIC WHERE artist=\""+artist+"\"");

			resultSet = statement.executeQuery();

			titleModel.removeAllElements();
			int i=0;
			while(resultSet.next()){
				titleModel.addElement(resultSet.getString("title"));
				i++;
			}
			statement.close();
			connect.close();
		}catch(Exception ex){ex.printStackTrace();}

	}

	public static void main(String[] args) {
		FlacRadioGUI gui = new FlacRadioGUI();
		gui.setVisible(true);
	}

}
