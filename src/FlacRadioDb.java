import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FlacRadioDb extends JFrame{
	private JList artistDatabase;
	private JList albumDatabase;
	private JList titleDatabase;
	private JList searchDatabase;
	private JTextField searchBox;
	private JComboBox choices;
	private String selectedArtist;
	private String selectedTitle;
	private JButton searchButton;

	private DefaultListModel artistModel;
	private DefaultListModel titleModel;
	private DefaultListModel albumModel;
	private FlacRadioGUI gui1,gui2,gui3;
	
	private JPanel panel;
	private Thread t1;

	public FlacRadioDb(){

		setSize(850,700);
		selectedArtist = null;
		selectedTitle = null;
		artistModel = new DefaultListModel();
		albumModel = new DefaultListModel();
		titleModel = new DefaultListModel();
		searchBox = new JTextField(20);
		searchButton = new JButton("Search");
		String[] comboBoxString = { "Artist", "Album", "Title"};
		choices = new JComboBox(comboBoxString);
		artistDatabase = new JList(artistModel);
		getAllArtists();
		albumDatabase = new JList(albumModel);
		titleDatabase = new JList(titleModel);

		titleDatabase.setDragEnabled(true);
		

		artistDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		albumDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		titleDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.setTitle("FlacRadioDb");

		JScrollPane artistScrollPane = new JScrollPane(artistDatabase);
		JScrollPane albumScrollPane = new JScrollPane(albumDatabase);
		JScrollPane titleScrollPane = new JScrollPane(titleDatabase);
		JScrollPane searchScrollPane = new JScrollPane(searchDatabase);
		
		artistScrollPane.setLocation(330,0);
		artistScrollPane.setSize(200,200);
		artistScrollPane.setBorder(BorderFactory.createTitledBorder("Artist"));

		albumScrollPane.setLocation(530,0);
		albumScrollPane.setSize(200,200);
		albumScrollPane.setBorder(BorderFactory.createTitledBorder("Album"));

		titleScrollPane.setLocation(330,200);
		titleScrollPane.setSize(410,200);
		titleScrollPane.setBorder(BorderFactory.createTitledBorder("Title"));
		
		searchScrollPane.setLocation(330,430);
		searchScrollPane.setSize(410,200);
		searchScrollPane.setBorder(BorderFactory.createTitledBorder("Search Result"));
		
		searchBox.setLocation(330, 400);
		searchBox.setSize(190,25);
		searchBox.setText("Input Search");
		
		choices.setSelectedIndex(2);
		choices.setLocation(530, 400);
		choices.setSize(100,25);
		
		searchButton.setLocation(640,400);
		searchButton.setSize(100,25);
		
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




		gui1 = new FlacRadioGUI(this);
		gui2 = new FlacRadioGUI(this);
		gui3 = new FlacRadioGUI(this);
		gui1.setSize(310,175);
		gui2.setSize(310,175);
		gui3.setSize(310,175);
		gui1.setLocation(0, 0);
		gui2.setLocation(0,180);
		gui3.setLocation(0,360);
		gui1.setVisible(true);
		gui2.setVisible(true);
		gui3.setVisible(true);
		this.add(gui1);
		this.add(gui2);
		this.add(gui3);
		
		getContentPane().add(panel);
		panel.setBackground(Color.white);
		panel.add(artistScrollPane);
		panel.add(titleScrollPane);
		panel.add(albumScrollPane);
		panel.add(searchScrollPane);
		panel.add(searchBox);
		panel.add(choices);
		panel.add(searchButton);
		panel.setLayout(null);
		artistDatabase.setSelectedIndex(0);
		
		gui1.setEnabled(true);
		gui1.setDropTarget(new DropTarget(gui1,gui1));
		gui2.setEnabled(true);
		gui2.setDropTarget(new DropTarget(gui2,gui2));
		gui3.setEnabled(true);
		gui3.setDropTarget(new DropTarget(gui3,gui3));
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
		try {
			while(resultSet.next()){
				artistModel.addElement(resultSet.getString("artist"));
			}
		}catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error.");}

	}

	public void getTitlesFromArtist(String artist){
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT title from music WHERE artist=\""+artist+"\"");
		titleModel.removeAllElements();
		try {
			int i=0;
			while(resultSet.next()){
				titleModel.addElement(resultSet.getString("title"));
				i++;
			}
		}catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error.");}

	}
	public void getAlbumsFromArtist(String artist){
		ResultSet resultSet = null;
		resultSet = getDBInfo("SELECT DISTINCT album from music WHERE artist=\""+artist+"\"");
		albumModel.removeAllElements();
		albumModel.addElement("All Albums");
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
			.getConnection("jdbc:mysql://143.105.16.195/wjcuflac_music?"+"user=wjcuflac&password=flacradio");
			PreparedStatement statement = connect.prepareStatement(mysqlStatement);
			resultSet = statement.executeQuery();
		}catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error.");}
		return resultSet;
	}
	public void setSelectedArtist(){
		selectedArtist = getArtist();

	}
	public void setSelectedTitle(){
		selectedTitle = getTitle();
		System.out.println(selectedTitle);
	}
	public String getSelectedTitle(){
		setSelectedTitle();
		return selectedTitle;
	}
	public String getSelectedArtist(){
		setSelectedArtist();
		return selectedArtist;
	}
	public void makeThread(Runnable run){
		new Thread(run).start();
	}
	public static void main(String[] args) {
		FlacRadioDb db = new FlacRadioDb();

		db.setVisible(true);
	}



}




