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
	private FlacPlayer flacPlayer;
	private FlacRadioDb db;
	private JButton playPause;
	private JButton eject;
	private JButton back;
	private JLabel player;
	//private JPanel panel;
	private boolean paused;
	private boolean hasTrack;
	//private JTextArea lyrics;
	private DropTarget dt;

	public FlacRadioGUI(FlacRadioDb data){

		db = data;
		flacPlayer = new FlacPlayer(trackName);

		setVisible(true);

		player = new JLabel("No Track Loaded");

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
		playPause.setSize(95,25);
		playPause.setLocation(10,115);
		back.setSize(94,25);
		back.setLocation(113,115);
		eject.setSize(95,25);
		eject.setLocation(215,115);

		player.setLocation(10, 10);
		player.setSize(200,100);
		player.setBorder(BorderFactory.createTitledBorder("Player"));

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
		this.add(player);
		this.add(playPause);
		this.add(back);
		this.add(eject);
		//	this.add(lyricsScrollPane);
		this.setLayout(null);
		this.setVisible(true);
		dt = new DropTarget();
		this.setEnabled(true);


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
			player.setText("No Track Loaded");
		}else{
			db.makeThread(this);
		}
	}

	public void run(){
		player.setText(db.getSelectedArtist()+" \n "+db.getSelectedTitle());
		//	lyrics.setText(flacPlayer.getLyrics(db.getArtist(), db.getTitle()));
		ResultSet resultSet = null;
		if(db.getSelectedArtist() != null && db.getSelectedTitle() != null){

			resultSet = db.getDBInfo("SELECT path from MUSIC WHERE artist=\""+db.getSelectedArtist()+"\"and title=\""+db.getSelectedTitle()+"\"");
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
			player.setText("Please Select a Track to Load");
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
