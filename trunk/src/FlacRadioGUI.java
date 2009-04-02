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
	private JLabel artistText, titleText;
	//private JPanel panel;
	private boolean paused;
	private boolean hasTrack;
	//private JTextArea lyrics;
	private DropTarget dt;

	public FlacRadioGUI(FlacRadioDb data){

		db = data;
		flacPlayer = new FlacPlayer(trackName);

		setVisible(true);

		artistText = new JLabel("LOAD A TRACK");
		titleText = new JLabel("Click and Drag a song here");
		//	lyrics = new JTextArea("No Lyrics to Display");
		//	lyrics.setEditable(false);
		//	JScrollPane lyricsScrollPane = new JScrollPane(lyrics);

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
		back.setSize(95,50);
		back.setLocation(113,115);
		eject.setSize(95,50);
		eject.setLocation(215,115);

		artistText.setLocation(10, 30);
		artistText.setSize(300,15);
		titleText.setLocation(10, 60);
		titleText.setSize(300, 15);

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


		this.setBackground(Color.white);
		this.add(artistText);
		this.add(titleText);
		this.add(playPause);
		this.add(back);
		this.add(eject);
		//	this.add(lyricsScrollPane);
		this.setLayout(null);
		this.setVisible(true);
		dt = new DropTarget();
		this.setEnabled(true);
		this.setBackground(Color.decode("#B3FF81"));


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
			eject.setEnabled(false);
			back.setEnabled(false);
			hasTrack = false;
			artistText.setText("LOAD A TRACK");
			titleText.setText("Click and Drag a Song Here");
		}else{
			db.makeThread(this);
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
			}catch(Exception ex){JOptionPane.showMessageDialog(this, "Database Error.");}
		}else{
			artistText.setText("LOAD A TRACK");
			titleText.setText("Click and Drag a Song Here");
		}




	}
	public void dragEnter(DropTargetDragEvent arg0) {
		System.out.println("Hello");

	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			dtde.acceptDrop(1);
			ejectLoad();
		}catch(Exception e){e.printStackTrace();}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		System.out.println("Worlds");
	}
}