package com.scholastic.ereader;
import java.util.*;
//import org.apache.commons.codec.binary.Hex;
import org.perf4j.aop.Profiled;

import java.io.*;
import java.util.Properties;
//import java.util.ResourceBundle;

//import javax.ws.rs.QueryParam;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.google.gson.*;

// import javax.jws.WebService;

// @WebService (endpointInterface = "com.scholastic.ereader.bookapiImpl")

public class BookApiImpl implements BookApi {
	private static Mongo m = null;
	private static String mongohost;
	private static String mongodbname;
	private static DB mdb = null; 

@Profiled(tag = "connectMongoDB")	
static public DB connectMongoDB ()
{
	if (mdb == null) {
	 try 
	 {
		File f = new File(System.getProperty("jboss.server.config.dir"), 
            "config.properties");

		Properties prop = new Properties();
		prop.load(new FileInputStream(f));
	
		mongohost = prop.getProperty("mongohost").toString();
		mongodbname = prop.getProperty("dbname").toString();
		
		System.out.println(mongohost);
		System.out.println(mongodbname);
		m = new Mongo(mongohost, 27017);
		mdb = m.getDB(mongodbname);
	 }
	catch (Exception e) {
		e.printStackTrace();
	 }
	}
	return mdb;
}	
	
final  public String hello() {
	return "hey man";
}

@Profiled(tag = "GetContentMetaData_{$0}")
final public String GetContentMetaData(String id, String token) {
	return new ContentMetaData(id).get(connectMongoDB());
}


@Profiled(tag = "GetContentFragments_{$0}")
final public String GetContentFragments(final String id, 
										final String token, 
										final String type,
										final String refIds) {
	
	ArrayList<HashMap<String,String>> fragList = 
					new ArrayList<HashMap<String,String>>();
	try {
		//String loc = getClass().getName().replace('.', '/') +"/";

		DBCollection coll = connectMongoDB().getCollection(id);
		BasicDBObject query = new BasicDBObject();	
		String[] idList = refIds.split(",");
		System.out.println("type="+type);
		
		for (Integer i = 0; i < idList.length; i++)
		{
			query.put("fragId", idList[i]);
			DBCursor cursor = coll.find(query);
			String str;

			if (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				HashMap<String, String> frag = new HashMap<String,String>();
				//System.out.println (obj);
				
				if ( type.equals("tb") ){  // Get thumbnail images
					str = obj.getString("tb");
        	
					if (str != null) {
						/*
        				byte[] byteString = str.getBytes();
        				String hexString = Hex.encodeHex(byteString).toString();
        				System.out.println (hexString);
						 */
					
						frag.put("id", idList[i]);
						frag.put("tb", str);
						fragList.add (frag);
					}
				}
				else { // get regular fragments
					
					str = obj.getString("frags");
		        	
					if (str != null) {
						/*
        				byte[] byteString = str.getBytes();
        				String hexString = Hex.encodeHex(byteString).toString();
        				System.out.println (hexString);
						 */
					
						frag.put("id", idList[i]);
						frag.put("frag", str);
						fragList.add (frag);
					}
					
				}
 		   }
		}
	}
	catch (Exception e) {
		e.printStackTrace();
	 }
	Gson gson = new Gson();
	return gson.toJson(fragList);
}


@Profiled(tag = "GetContentContentUrls_{$0}")
  final public 
  String GetContentContentUrls(final String token, final String type) {
		
		DBCollection coll = connectMongoDB().getCollection("url_loc");
		BasicDBObject query = new BasicDBObject();	
		//System.out.println("type="+type);
		
		query.put("type",type);
		
		DBCursor cursor = coll.find(query);
		String urlString = null;
		HashMap<String, String> urlMap = new HashMap<String,String>();
		
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();

			urlString = obj.getString("base");
			System.out.println("base url="+urlString);
			urlMap.put("base", urlString);
		}

		Gson gson = new Gson();
		return gson.toJson(urlMap);
	}

@Profiled (tag = "GetWordDef_{$0}")
final public String GetWordDef (String version, String aWord) {
		
		DBCollection coll = connectMongoDB().getCollection("dict");
		HashMap<String, String> urlMap = new HashMap<String,String>();
		
		//System.out.println(coll.findOne());
		String searchWord=aWord.toLowerCase();
		BasicDBObject query = new BasicDBObject();	
		//System.out.println("type="+type);
		
		query.put("word",searchWord);
		
		DBCursor cursor = coll.find(query);
		
		String wordOrig = null;
		System.out.println("word="+searchWord);
		
		if (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			wordOrig = obj.getString("orig");
			//System.out.println("orig="+wordOrig);
			String wordUrl = '/' + version.toUpperCase() + '/'+ wordOrig + ".html";
			System.out.println(wordUrl);
			urlMap.put("wordurl", wordUrl);
			 //wordVersion = obj.getString("ver");
		}
		
		Gson gson = new Gson();
		return gson.toJson(urlMap);
	}

}