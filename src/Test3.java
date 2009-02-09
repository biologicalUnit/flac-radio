import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;
import java.util.List;

import org.jaudiotagger.audio.flac.*;
import org.jaudiotagger.tag.flac.*;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;



public class Test3 extends JFrame{
	private Test2 player;
	private String[][] data;
	private JTable table;
	private JLabel label;
	public Test3(){
		player = new Test2();

		table = connect();
		setSize(500, 600);
		setTitle("Test Flac");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		getContentPane().add(panel);

		panel.setLayout(null);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setBounds(10, 100, 400, 400);
		table.setVisible(true);
		
		label = new JLabel();
		label.setBounds(10, 10, 450, 25);
		
		
		JButton load = new JButton("Load");
		load.setBounds(25, 60, 80, 30);
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				load();
			}
		});

		JButton play = new JButton("Play");
		play.setBounds(100, 60, 80, 30);
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				play();
			}
		});

		JButton pause = new JButton("Pause");
		pause.setBounds(175, 60, 80, 30);
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				pause();
			}
		});

		panel.add(load);
		panel.add(play);
		panel.add(pause);
		panel.add(table);
		panel.add(label);

	}

	public void load(){
		String file = data[table.getSelectedRow()][2];
		System.out.println(file);
		
		if(file != null){
			FlacTagReader reader = new FlacTagReader();
			RandomAccessFile fi;
			String artist = "";
			String title = "";
			try {
				fi = new RandomAccessFile(file,"r");
				FlacTag tag = reader.read(fi);
				artist = tag.getArtist().toString();
				title = tag.getTitle().toString();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.load(file);
			label.setText("LOADED: "+artist+" "+title);
		}
	}
	public void loadFromDB(String path){
		player.load(path);
	}
	public void play(){
		player.play();
	}
	public void pause(){
		player.pause();
	}
	public JTable connect(){
		Connection connect = null;
		ResultSet resultSet = null;
		try {
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
			.getConnection("jdbc:mysql://localhost/music?"
					+ "user=root&password=");
			PreparedStatement statement = connect.prepareStatement("SELECT artist,title,path from MUSIC");

			resultSet = statement.executeQuery();
			int size = 25;
			data= new String[size][3];
			String[] colNames = {"Artist","Title","Path"};
			System.out.println(size);
			
			data[0][0] = "ARTIST";
			data[0][1] = "TITLE";
			data[0][2] = "PATH";
			int i=1;
			while(resultSet.next()){
				data[i][0] = resultSet.getString("artist");
				data[i][1] = resultSet.getString("title");
				data[i][2] = resultSet.getString("path");
				i++;
			}
			JTable table = new JTable(data, colNames);
			return table;

		}catch(Exception ex){ex.printStackTrace();}
		return null;

	}
	public static void main(String[] args) {

		Test3 test = new Test3();
		test.setVisible(true);



	}


}
