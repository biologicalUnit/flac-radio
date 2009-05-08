import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;

import javax.swing.*;

public class FlacRadioGUI extends JPanel implements Runnable, DropTargetListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private String artist,album,title;
	private ImageIcon playButtonIcon;
	private ImageIcon playDisabledIcon;
	private ImageIcon pauseIcon;
	private ImageIcon backIcon;
	private ImageIcon backDisabledIcon;
	private ImageIcon ejectIcon;
	private ImageIcon ejectDisabledIcon;
	private ImageIcon noteIcon;
	//private String buttonSource;
	//private JTextArea lyrics;
	private int playPauseCount;

	private Font labelFont,textFont;
	private int playerID;

	public FlacRadioGUI(FlacRadioDb data, int id){
		playerID = id;
		db = data;
		flacPlayer = new FlacPlayer(id+".wav");
		labelFont = new Font("Serif", Font.BOLD, 18);
		textFont = new Font("Serif", Font.BOLD, 25);
		playPauseCount = 0;
		//buttonSource = "images/Play.gif";
		playButtonIcon = new ImageIcon("images/Play.gif", "Button");
		playDisabledIcon = new ImageIcon("images/PlayDisabled.gif", "Button");
		pauseIcon = new ImageIcon("images/Pause.gif", "Button");
		backIcon = new ImageIcon("images/Back.gif", "Button");
		backDisabledIcon = new ImageIcon("images/BackDisabled.gif", "Button");
		ejectIcon = new ImageIcon("images/Eject.gif", "Button");
		ejectDisabledIcon = new ImageIcon("images/EjectDisabled.gif", "Button");
		noteIcon = new ImageIcon("images/Note.gif", "BG");

		artistText = new JLabel("LOAD A TRACK");
		titleText = new JLabel("click and drag a song here");
		timeText = new JLabel("0:00");

		playPause = new JButton("", playButtonIcon);
		paused=true;
		hasTrack=false;
		back = new JButton("", backIcon);
		eject = new JButton("", ejectIcon);
		playPause.setEnabled(false);
		back.setEnabled(false);
		eject.setEnabled(false);


		//placement and size for first set
		playPause.setSize(95,50);
		playPause.setLocation(10,115);
		playPause.setFont(labelFont);
		playPause.setDisabledIcon(playDisabledIcon);
		back.setSize(95,50);
		back.setLocation(113,115);
		back.setFont(labelFont);
		back.setDisabledIcon(backDisabledIcon);
		eject.setSize(95,50);
		eject.setLocation(215,115);
		eject.setFont(labelFont);
		eject.setDisabledIcon(ejectDisabledIcon);

		artistText.setLocation(10, 30);
		artistText.setSize(250,20);
		artistText.setFont(labelFont);
		titleText.setLocation(10, 60);
		titleText.setSize(250, 20);
		titleText.setFont(labelFont);
		timeText.setLocation(260,30);
		timeText.setSize(50, 30);
		timeText.setFont(textFont);

		playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				play();
			}
		});
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rewind();
			}
		});
		eject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ejectLoad();
			}
		});
		this.setFocusable(true);
		if(id == 1){
			playPause.setMnemonic(KeyEvent.VK_1);
			back.setMnemonic(KeyEvent.VK_2);
			eject.setMnemonic(KeyEvent.VK_3);
		}
		if(id == 2){
			playPause.setMnemonic(KeyEvent.VK_4);
			back.setMnemonic(KeyEvent.VK_5);
			eject.setMnemonic(KeyEvent.VK_6);
		}
		if(id == 3){
			playPause.setMnemonic(KeyEvent.VK_7);
			back.setMnemonic(KeyEvent.VK_8);
			eject.setMnemonic(KeyEvent.VK_9);
		}

		this.add(artistText);
		this.add(titleText);
		this.add(timeText);
		this.add(playPause);
		this.add(back);
		this.add(eject);
		this.setLayout(null);
		this.setVisible(true);
		this.setEnabled(true);
		this.setBackground(Color.decode("#5874FF"));


	}
	public void play(){
		if(paused){
			flacPlayer.play();

			Thread thread = new Thread(timer);
			timer.setCountDown(true);
			thread.start();
			paused = false;
			playPause.setIcon(pauseIcon);
			//playPause.setText("Pause");
			if(playPauseCount == 0){
				db.logSong(artist,album,title);
			}
			playPauseCount++;

		}else{
			flacPlayer.pause();
			timer.setCountDown(false);
			paused = true;
			playPause.setIcon(playButtonIcon);
			//playPause.setText("Play");
		}
	}

	public void rewind(){
		timer.setCountDown(false);
		double time = flacPlayer.getTime();
		int min = (int)time/60;
		int sec = (int)time % 60;
		timer = new TrackTimer(this,min,sec);
		this.setBackground(Color.decode("#5874FF"));
		flacPlayer.rewind();
		paused = true;
		//playPause.setText("Play");
	}
	public void ejectLoad(){
		if(hasTrack){
			if(!paused){
				flacPlayer.pause();
				//playPause.setText("Play");
			}
			timer.setCountDown(false);
			timer.setMinutes(0);
			timer.setSeconds(0);
			playPauseCount = 0;
			paused=true;
			flacPlayer.pause();
			playPause.setEnabled(false);
			eject.setEnabled(false);
			back.setEnabled(false);
			hasTrack = false;
			artistText.setText("LOAD A TRACK");
			titleText.setText("click and drag a song here");
			timeText.setText("0:00");
			this.setBackground(Color.decode("#5874FF"));
		}else{
			db.makeThread(playerID,this);
		}
	}

	public void run(){
		artist = db.getSelectedArtist();
		album = db.getSelectedAlbum();
		title = db.getSelectedTitle();
		artistText.setText(artist);
		titleText.setText(title);
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
				this.setBackground(Color.decode("#84A8C1"));
				double time = flacPlayer.getTime();
				int min = (int)time/60;
				int sec = (int)time % 60;
				timer = new TrackTimer(this,min,sec);
			}catch(Exception ex){JOptionPane.showMessageDialog(this, "Database Error.");}
		}else{
			artistText.setText("LOAD A TRACK");
			titleText.setText("click and drag a song here");
			timeText.setText("0:00");

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
	public void endTrack(){

	}
	
	public void paintComponent(Graphics g){
		g.drawImage(noteIcon.getImage(), 80, 10, this);
	}
	
}
