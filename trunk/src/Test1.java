import java.io.File;
import javax.sound.sampled.*;
import apps.*;
public class Test1 extends Thread{
	public static void main(String []args){
		final int EXTERNAL_BUFFER_SIZE = 128000;
		Decoder p1 = new Decoder();
		File track = new File("track.wav");
		try{
			
			p1.decode("NoChildren.flac", track.getPath());
			System.out.println(track.toString());
			AudioInputStream in = AudioSystem.getAudioInputStream(track);
			AudioFormat format = in.getFormat();
			SourceDataLine	line = null;
			DataLine.Info	info = new DataLine.Info(SourceDataLine.class,format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
			int	nBytesRead = 0;
			byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
			while (nBytesRead != -1)
			{
					nBytesRead = in.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
				{
					int	nBytesWritten = line.write(abData, 0, nBytesRead);
				}
			}

			/*
			  Wait until all data are played.
			  This is only necessary because of the bug noted below.
			  (If we do not wait, we would interrupt the playback by
			  prematurely closing the line and exiting the VM.)
			 
			  Thanks to Margie Fitch for bringing me on the right
			  path to this solution.
			*/
			line.drain();

			/*
			  All data are played. We can close the shop.
			*/
			line.close();

			/*
			  There is a bug in the jdk1.3/1.4.
			  It prevents correct termination of the VM.
			  So we have to exit ourselves.
			*/
			System.exit(0);


		}catch(Exception e){e.printStackTrace();}

		
	}

		
}
