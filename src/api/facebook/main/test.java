package api.facebook.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;

	
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.utils.URIBuilder;

import api.facebook.method.GetSeeds;
import api.facebook.method.GraphAPI;
import api.facebook.util.RollTableName;

public class test
{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphAPI api=new GetSeeds();
		String time="2015-07-18T11:51:31+0000";
		System.out.println(api.string2Timestamp(time, null));
		
		RollTableName Roll=new RollTableName("comments");
		
		String tableName=Roll.construct();
		System.out.println(tableName);
		
		String node="mykmt?nr";
		URI uri=null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setHost("graph.facebook.com")
			.setPath("/"+node)
			.setParameter("access_token", "1628625687408087"+"|"+"878b93ffea9985a0835dc9442756e05b")
			.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(uri);
	}

}
