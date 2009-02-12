import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jaudiotagger.audio.flac.*;
import org.jaudiotagger.tag.flac.*;

public class MetaTest {
	public static void main(String[] args){
		String path = "/Users/markryan/Documents/TestDB";
		File dir = new File(path);
		Connection connect = null;
		String[] list = dir.list();
		String output = "";

		for (int i = 0; i < list.length; i++)  {
			if(list[i].contains(".flac")){
				FlacTagReader reader = new FlacTagReader();
				RandomAccessFile fi;
				String artist = "";
				try {
					fi = new RandomAccessFile(path+"/"+list[i],"r");
					FlacTag tag = reader.read(fi);
					output += tag.getFirstArtist()+" "+tag.getFirstTitle()+"\n";
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					connect = DriverManager.getConnection("jdbc:mysql://localhost/music?"+"user=root&password=");
					PreparedStatement statement1 = connect.prepareStatement("SELECT path FROM music WHERE path='"+path+"/"+list[i].toString()+"'");
					ResultSet result = statement1.executeQuery();
					if(!result.first()){
						PreparedStatement statement = connect.prepareStatement("INSERT INTO music VALUES (\""+tag.getFirstTitle()+"\", \""+tag.getFirstArtist()+"\", \""+tag.getFirstAlbum()+"\", \""+path+"/"+list[i].toString()+"\")");
						statement.execute();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();}
			}
		}

		System.out.println(output);
	}
}
