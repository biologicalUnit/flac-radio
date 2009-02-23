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

public class FlacRadioGUI extends JPanel implements Runnable{
	private final String trackName = System.currentTimeMillis()+".wav";
	private FlacPlayer flacPlayer;
	private FlacRadioDb db;
	private JButton playPause;
	private JButton loadEject;
	private JButton back;
	private JTextArea player;
	//private JPanel panel;
	private boolean paused;
	private boolean hasTrack;
	//private JTextArea lyrics;

	public FlacRadioGUI(FlacRadioDb data){

		db = data;
		//db.setVisible(true);
		flacPlayer = new FlacPlayer(trackName);
		
		setVisible(true);

		player = new JTextArea("No Track Loaded");
		player.setEditable(false);
		player.setDragEnabled(true);
		
	//	lyrics = new JTextArea("No Lyrics to Display");
	//	lyrics.setEditable(false);
	//	JScrollPane lyricsScrollPane = new JScrollPane(lyrics);

		playPause = new JButton("Play");
		paused=true;
		hasTrack=false;
		back = new JButton("|<-");
		loadEject = new JButton("Load");
		playPause.setEnabled(false);
		back.setEnabled(false);

		//placement and size for first set
		playPause.setSize(95,25);
		playPause.setLocation(10,115);
		back.setSize(94,25);
		back.setLocation(113,115);
		loadEject.setSize(95,25);
		loadEject.setLocation(215,115);

		player.setLocation(10, 10);
		player.setSize(200,100);
		player.setBorder(BorderFactory.createTitledBorder("Player 1"));
		
	//	lyricsScrollPane.setLocation(10,200);
	//	lyricsScrollPane.setSize(300,300);
		
		//panel = new JPanel();

		playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				play();
			}
		});
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rewind();
				paused = true;
				playPause.setText("Play");
			}
		});
		loadEject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ejectLoad();
			}
		});

		
		this.setBackground(Color.white);
		this.add(player);
		this.add(playPause);
		this.add(back);
		this.add(loadEject);
	//	this.add(lyricsScrollPane);
		this.setLayout(null);
		this.setVisible(true);

	}
	public void play(){
		if(paused){
			flacPlayer.play();
			paused = false;
			playPause.setText("Pause");

		}else{
			flacPlayer.pause();
			paused = true;
			playPause.setText("Play");

		}
	}

	public void rewind(){
		flacPlayer.rewind();
	}
	public void ejectLoad(){
		if(hasTrack){
			if(!paused){
				flacPlayer.pause();
				playPause.setText("Play");
			}
			paused=true;
			flacPlayer.pause();
			playPause.setEnabled(false);
			loadEject.setText("Load");
			back.setEnabled(false);
			hasTrack = false;
			player.setText("No Track Loaded");
		}else{
			db.makeThread(this);
		}
	}

	public void run(){
		player.setText(db.getArtist()+" \n "+db.getTitle());
	//	lyrics.setText(flacPlayer.getLyrics(db.getArtist(), db.getTitle()));
		ResultSet resultSet = null;
		if(db.getArtist() != null && db.getTitle() != null){
			
				resultSet = db.getDBInfo("SELECT path from MUSIC WHERE artist=\""+db.getArtist()+"\"and title=\""+db.getTitle()+"\"");
				String path = null;
				try {
				while(resultSet.next()){
					path = resultSet.getString("path");
				}
				paused=true;
				flacPlayer.load(path);
				hasTrack = true;

				loadEject.setText("Eject");
				back.setEnabled(true);
				playPause.setEnabled(true);
			}catch(Exception ex){ex.printStackTrace();}
		}else{
			player.setText("Please Select a Track to Load");
		}




	}
}
