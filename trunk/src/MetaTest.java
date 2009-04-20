import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jaudiotagger.audio.flac.*;
import org.jaudiotagger.tag.flac.*;

public class MetaTest {




	public static void main(String[] args){
		String path = "/Volumes/WD Passport/FlacFiles";
		File dir = new File(path);
		MetaTest meta = new MetaTest();
		meta.scanDb(dir);

	}
	public void scanDb(File dir){
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				scanDb(new File(dir, children[i]));
			}

			Connection connect = null;
			String[] list = dir.list();
			String output = "";

			for (int i = 0; i < list.length; i++)  {
				if(list[i].contains(".flac")){
					FlacTagReader reader = new FlacTagReader();
					RandomAccessFile fi;
					String artist = "";
					try {
						fi = new RandomAccessFile(dir.toString()+"/"+list[i],"r");
						FlacTag tag = reader.read(fi);
						DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			            java.util.Date date = new java.util.Date();
			            String currentDate = dateFormat.format(date);

						output += tag.getFirstArtist()+" "+tag.getFirstTitle()+"\n";
						Class.forName("com.mysql.jdbc.Driver").newInstance();
						connect = DriverManager.getConnection("jdbc:mysql://localhost/music?"
								+ "user=root&password=");
						String state1= "SELECT path FROM music WHERE path=\""+dir.toString()+"/"+list[i].toString()+"\"";
						state1.replace("'", "''");
						PreparedStatement statement1 = connect.prepareStatement(state1);
						ResultSet result = statement1.executeQuery();
						if(!result.first()){
							String state = "INSERT INTO music VALUES (\""+tag.getFirstTitle()+"\", \""+tag.getFirstArtist()+"\", \""+tag.getFirstAlbum()+"\", \""+dir.toString()+"/"+list[i].toString()+"\", \""+currentDate+"\", \""+tag.getFirstGenre()+"\")";
							state.replace("'", "\'");
							PreparedStatement statement = connect.prepareStatement(state);
							statement.execute();
						}
					} catch (Exception e) {
						e.printStackTrace();}
				}
			}
		}
	}
}
