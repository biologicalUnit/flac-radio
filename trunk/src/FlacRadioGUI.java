import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FlacRadioGUI extends JPanel implements Runnable, DropTargetListener{
	private final String trackName = System.currentTimeMillis()+".wav";
	public FlacPlayer flacPlayer;
	private FlacRadioDb db;
	private JButton playPause;
	private JButton eject;
	private JButton back;
	private JLabel artistText, titleText, timeText;
	//private JPanel panel;
	private boolean paused;
	private boolean hasTrack;
	private TrackTimer timer;
	//private JTextArea lyrics;
	private DropTarget dt;
	private Font labelFont,textFont;
	private int playerID;

	public FlacRadioGUI(FlacRadioDb data, int id){
		playerID = id;
		db = data;
		flacPlayer = new FlacPlayer(trackName);
		labelFont = new Font("Serif", Font.BOLD, 18);
		textFont = new Font("Serif", Font.BOLD, 25);
		
		setVisible(true);

		artistText = new JLabel("LOAD A TRACK");
		titleText = new JLabel("Click and Drag a song here");
		timeText = new JLabel("0:00");

		playPause = new JButton("Play");
		paused=true;
		hasTrack=false;
		back = new JButton("|<-");
		eject = new JButton("Eject");
		playPause.setEnabled(false);
		back.setEnabled(false);
		eject.setEnabled(false);


		//placement and size for first set
		playPause.setSize(95,50);
		playPause.setLocation(10,115);
		playPause.setFont(labelFont);
		back.setSize(95,50);
		back.setLocation(113,115);
		back.setFont(labelFont);
		eject.setSize(95,50);
		eject.setLocation(215,115);
		eject.setFont(labelFont);

		artistText.setLocation(10, 30);
		artistText.setSize(250,20);
		artistText.setFont(labelFont);
		titleText.setLocation(10, 60);
		titleText.setSize(250, 20);
		titleText.setFont(labelFont);
		timeText.setLocation(260,30);
		timeText.setSize(50, 30);
		timeText.setFont(textFont);

		//	lyricsScrollPane.setLocation(10,200);
		//	lyricsScrollPane.setSize(300,300);

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
		eject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ejectLoad();
			}
		});


		this.add(artistText);
		this.add(titleText);
		this.add(timeText);
		this.add(playPause);
		this.add(back);
		this.add(eject);
		this.setLayout(null);
		this.setVisible(true);
		dt = new DropTarget();
		this.setEnabled(true);
		this.setBackground(Color.decode("#B3FF81"));


	}
	public void play(){
		if(paused){
			flacPlayer.play();
			Thread thread = new Thread(timer);
			timer.setCountDown(true);
			thread.start();
			paused = false;
			playPause.setText("Pause");

		}else{
			flacPlayer.pause();
			timer.setCountDown(false);
			paused = true;
			playPause.setText("Play");
		}
	}

	public void rewind(){
		timer.setCountDown(false);
		double time = flacPlayer.getTime();
		int min = (int)time/60;
		int sec = (int)time % 60;
		timer = new TrackTimer(this,min,sec);
		flacPlayer.rewind();
	}
	public void ejectLoad(){
		if(hasTrack){
			if(!paused){
				flacPlayer.pause();
				playPause.setText("Play");
			}
			timer.setCountDown(false);
			timer.setMinutes(0);
			timer.setSeconds(0);
			paused=true;
			flacPlayer.pause();
			playPause.setEnabled(false);
			eject.setEnabled(false);
			back.setEnabled(false);
			hasTrack = false;
			artistText.setText("LOAD A TRACK");
			titleText.setText("Click and Drag a Song Here");
		}else{
			db.makeThread(playerID,this);
		}
	}

	public void run(){
		artistText.setText(db.getSelectedArtist());
		titleText.setText(db.getSelectedTitle());
		ResultSet resultSet = null;
		if(db.getSelectedArtist() != null && db.getSelectedTitle() != null && db.getSelectedAlbum() != null){

			resultSet = db.getDBInfo("SELECT path from MUSIC WHERE artist=\""+db.getSelectedArtist()+"\"and title=\""+db.getSelectedTitle()+"\"and album=\""+db.getSelectedAlbum()+"\"");
			String path = null;
			try {
				while(resultSet.next()){
					path = resultSet.getString("path");
				}
				paused=true;
				flacPlayer.load(path);
				hasTrack = true;
				back.setEnabled(true);
				playPause.setEnabled(true);
				eject.setEnabled(true);
				double time = flacPlayer.getTime();
				int min = (int)time/60;
				int sec = (int)time % 60;
				timer = new TrackTimer(this,min,sec);
			}catch(Exception ex){JOptionPane.showMessageDialog(this, "Database Error.");}
		}else{
			artistText.setText("LOAD A TRACK");
			titleText.setText("Click and Drag a Song Here");
		}




	}
	public void setTimeText(String text){
		timeText.setText(text);
	}
	public void dragEnter(DropTargetDragEvent arg0) {

	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			if(hasTrack){
				ejectLoad();
				ejectLoad();
			}
			dtde.acceptDrop(1);
			ejectLoad();
		}catch(Exception e){e.printStackTrace();}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method
	}
}
