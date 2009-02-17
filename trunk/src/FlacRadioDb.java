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

public class FlacRadioDb extends JFrame{
	private JList artistDatabase;
	private JList albumDatabase;
	private JList titleDatabase;

	private DefaultListModel artistModel;
	private DefaultListModel titleModel;
	private DefaultListModel albumModel;

	private JPanel panel;
	private Thread t1;

	public FlacRadioDb(){

		setSize(825,225);

		artistModel = new DefaultListModel();
		albumModel = new DefaultListModel();
		titleModel = new DefaultListModel();

		artistDatabase = new JList(artistModel);
		getAllArtists();
		albumDatabase = new JList(albumModel);
		titleDatabase = new JList(titleModel);

		titleDatabase.setDragEnabled(true);

		artistDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		albumDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		titleDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane artistScrollPane = new JScrollPane(artistDatabase);
		JScrollPane albumScrollPane = new JScrollPane(albumDatabase);
		JScrollPane titleScrollPane = new JScrollPane(titleDatabase);

		artistScrollPane.setLocation(0,0);
		artistScrollPane.setSize(200,200);
		artistScrollPane.setBorder(BorderFactory.createTitledBorder("Artist"));

		albumScrollPane.setLocation(210,0);
		albumScrollPane.setSize(200,200);
		albumScrollPane.setBorder(BorderFactory.createTitledBorder("Album"));

		titleScrollPane.setLocation(410,0);
		titleScrollPane.setSize(410,200);
		titleScrollPane.setBorder(BorderFactory.createTitledBorder("Title"));

		panel = new JPanel();
		artistDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				if(artistDatabase.getSelectedIndex()>=0){
					getTitlesFromArtist(artistDatabase.getSelectedValue().toString());
					getAlbumsFromArtist(artistDatabase.getSelectedValue().toString());
				}
			}

		});
		albumDatabase.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent arg0) {
				if(albumDatabase.getSelectedIndex()>=0){
					if (albumDatabase.getSelectedValue().toString().contains("All")){
						getTitlesFromArtist(artistDatabase.getSelectedValue().toString());
					}else{
						getTitlesFromAlbumArtist(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
					}
				}
			}

		});



		getContentPane().add(panel);
		panel.setBackground(Color.white);
		panel.add(artistScrollPane);
		panel.add(titleScrollPane);
		panel.add(albumScrollPane);
		panel.setLayout(null);
		artistDatabase.setSelectedIndex(0);

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
		Connection connect = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://ambiguitydesigns.com/ambigui1_music?"
					+ "user=ambigui1_wjcu887&password=flacradio");
			PreparedStatement statement = connect.prepareStatement("SELECT DISTINCT artist from music ORDER BY artist");

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
			.getConnection("jdbc:mysql://ambiguitydesigns.com/ambigui1_music?"
					+ "user=ambigui1_wjcu887&password=flacradio");
			PreparedStatement statement = connect.prepareStatement("SELECT title from music");

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
			.getConnection("jdbc:mysql://ambiguitydesigns.com/ambigui1_music?"
					+ "user=ambigui1_wjcu887&password=flacradio");

			PreparedStatement statement=connect.prepareStatement("SELECT title from music WHERE artist=\""+artist+"\"");
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
	public void getAlbumsFromArtist(String artist){
		Connection connect = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://ambiguitydesigns.com/ambigui1_music?"
					+ "user=ambigui1_wjcu887&password=flacradio");

			PreparedStatement statement=connect.prepareStatement("SELECT DISTINCT album from music WHERE artist=\""+artist+"\"");
			resultSet = statement.executeQuery();

			albumModel.removeAllElements();
			albumModel.addElement("All Albums");
			int i=0;
			while(resultSet.next()){
				albumModel.addElement(resultSet.getString("album"));
				i++;
			}
			statement.close();
			connect.close();
		}catch(Exception ex){ex.printStackTrace();}

	}
	public void getTitlesFromAlbumArtist(String artist, String album){
		Connection connect = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://ambiguitydesigns.com/ambigui1_music?"
					+ "user=ambigui1_wjcu887&password=flacradio");

			PreparedStatement statement=connect.prepareStatement("SELECT title from music WHERE artist=\""+artist+"\" and album=\""+album+"\"");
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
	
	public void makeThread(Runnable run){
		new Thread(run).start();
	}
	public static void main(String[] args) {
		FlacRadioDb db = new FlacRadioDb();
		FlacRadioGUI gui1 = new FlacRadioGUI(db);
		gui1.setVisible(true);
		FlacRadioGUI gui2 = new FlacRadioGUI(db);
		gui2.setVisible(true);
		db.setVisible(true);
	}

}
