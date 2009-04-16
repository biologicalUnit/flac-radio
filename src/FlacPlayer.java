import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import javax.media.*;
import apps.Decoder;

public class FlacPlayer{
	private File track;
	private Player player;

	public static void main(String []args){
		FlacPlayer p = new FlacPlayer("track.wav");
		p.load("/Users/markryan/Documents/TestDb/Rebel.flac");
		double time = p.getTime();
		int min = (int)time/60;
		int sec = (int)time % 60;
		System.out.println("Time: "+min+":"+sec);
	}
	//Constructor takes the name to save decoded track as in the form of a string ex:"track1.wav"
	public FlacPlayer(String trackName){
		player = null;
		track = new File(trackName);
	}
	//Load decodes the track and creates a Realized Player
	public void load(String filename){
		Decoder p1 = new Decoder();
		try{
			p1.decode(filename,track.getPath());
			player = Manager.createRealizedPlayer(track.toURL());
		}catch(Exception e){e.printStackTrace();}
	}
	//Play starts the track from the beginning or from the last played part
	public void play(){
		try{
			player.start();
		}catch(Exception e){e.printStackTrace();}
	}
	//Stops the music from playing
	public void pause(){
		try{
			player.stop();
		}catch(Exception e){e.printStackTrace();}
	}

	//Stops the track, resets it to the beginning
	public void rewind(){
		player.stop();
		player.setMediaTime(player.RESET);
	}

	//returns the length of the track in the form of seconds
	public double getTime(){
		return player.getDuration().getSeconds();
	}
	public String getLyrics(String artist, String title){
		String text = new String();
		try{
			URL lyricURL = new URL("http://lyricwiki.org/api.php?func=getSong&artist="+artist.replace(" ","_")+"&song="+title.replace(" ","_")+"&fmt=text");
			lyricURL.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							lyricURL.openStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null){		
				text = text + inputLine + "\n";	
			}
			in.close();
		}catch(Exception e){}
		return text;
	}

}