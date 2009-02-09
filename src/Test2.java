import java.io.File;
import javax.media.*;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import apps.Decoder;

public class Test2{
	private File track = new File("track1.wav");
	private Player player = null;
	public Test2(){
		player = null;
	}
	public static void main(String []args){
		

	}
	public void load(String filename){
		Decoder p1 = new Decoder();
		try{
			p1.decode(filename,track.getPath());
			player = Manager.createRealizedPlayer(track.toURL());
		}catch(Exception e){e.printStackTrace();}
	}
	public void play(){
		try{
			player.start();
		}catch(Exception e){e.printStackTrace();}
	}
	public void pause(){
		try{
			player.stop();
		}catch(Exception e){e.printStackTrace();}
	}
	public double getTime(){
		double time  = player.getMediaTime().getSeconds();
		return time;
	}

	}