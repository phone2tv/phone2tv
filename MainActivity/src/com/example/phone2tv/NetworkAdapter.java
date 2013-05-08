package com.example.phone2tv;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.util.Log;

import com.example.phone2tv.TvStationProgram;
import org.json.*;;

public class NetworkAdapter 
{	
	public static String mCmdHttp="http://";
	public String mCmdIp="172.16.0.124";
	public String mCmdPort="3000";
	public static String mCommandStr[] = {
		//"tv_stations.json/?cmd=detail" ,
		"tv_groups/1.json?cmd=detail",
		"tv_stations/" ,
		"discusses/"   ,
		"sessions.json",
		"tv_programs/" ,
		"discuss_relationships?auth_token=",
		"user_programships.json?auth_token=",
		"users/",       
		"user_programships/",
		"discuss_commentships.json?auth_token=",
		"user_checkinships.json?auth_token"};
	
	private static final String TAG = "NetworkAdapter";
	private static String mFormat[]= {
		"yyyy-MM-dd'T'HH:mm:ss" 
	};
	public NetworkAdapter(String ip , String port)
	{
		mCmdIp   = ip;
		mCmdPort = port;
	}
	
	public boolean getCatalog(ArrayList<String> catalog)
	{
		return true;
	}
	
	public boolean getTvStationByCatlog(int catalogIndex , ArrayList<String> tv)
	{
		return true;
	}
	
	public Program getProrgamById(JSONObject programJson) throws Exception
	{
		Program oneProgram = new Program();
		try
		{
			oneProgram.setIndex(programJson.getInt("id"));
			int discussCount = programJson.getInt("discuss_count");
			JSONArray discussJson = programJson.getJSONArray("latest_discusses");
			Comment oneComment = null;
			for(int i = 0 ; i < discussCount; i++)
			{
				oneComment = new Comment();
				
				oneComment.setId(discussJson.getJSONObject(i).getInt("id"));
				oneComment.setOwnerName(discussJson.getJSONObject(i).getString("user_name"));
				oneComment.setContent(discussJson.getJSONObject(i).getString("content"));
				oneComment.setOwnerId(discussJson.getJSONObject(i).getInt("user_id"));
				oneComment.setPostTime(parseString(discussJson.getJSONObject(i).getString("time")));
				oneComment.setProgramId(discussJson.getJSONObject(i).getInt("tv_program_id"));
				String srcId = discussJson.getJSONObject(i).getString("src_id");
				oneComment.setLikeCount(discussJson.getJSONObject(i).getInt("like"));
				if(!srcId.contentEquals("null"))
				{
					oneComment.setSrcId(discussJson.getJSONObject(i).getInt("src_id"));
				}
				else
					oneComment.setSrcId(-1);
				oneProgram.addComment(oneComment);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return oneProgram;
	}
	
	public ArrayList<Program> getSubscribePrograms(JSONArray json)
	{
		ArrayList<Program> subscribes= new ArrayList<Program>();
		
		int size = json.length();
		JSONObject oneJson = null;
		Program oneProgram = null;
		for(int i = 0 ; i < size ; i++)
		{
			try
			{
				oneJson = json.getJSONObject(i);
				oneProgram = new Program();
				oneProgram.setProgramName(oneJson.getString("name"));
				oneProgram.setIndex(oneJson.getInt("id"));
				oneProgram.setSubscribeCount(oneJson.getInt("watch_count"));
				oneProgram.setDiscussCount(oneJson.getInt("discuss_count"));
				subscribes.add(oneProgram);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
			
		}
		return subscribes;
	}
	
	public ArrayList<User> getFollowers(JSONArray usersJsonArray)
	{
		ArrayList<User> users = new ArrayList<User>();
		User oneUser = null;
		int userSize = usersJsonArray.length();
		JSONObject userJson = null;
		for(int i = 0 ; i < userSize ; i++)
		{
			try
			{
				userJson = usersJsonArray.getJSONObject(i);
				oneUser = new User();
				oneUser.setUserName(userJson.getString("name"));
				oneUser.setFollowersCount(userJson.getInt("follower_count"));
				oneUser.setFolloweesCount(userJson.getInt("followee_count"));
				oneUser.setCheckinCount(userJson.getInt("checkin_count"));
				oneUser.setDiscussCount(userJson.getInt("discuss_count"));
				
				if(0 != oneUser.getCheckinCount())
				{
					JSONObject programJson = userJson.getJSONObject("latest_checkin");
					oneUser.setCheckinProgram(getCheckinProgram(programJson));
				}
				users.add(oneUser);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return users;
	}
	
	public Program getCheckinProgram(JSONObject json) throws Exception
	{
		Program program = new Program();
		try
		{
			program.setIndex(json.getInt("id"));
			program.setProgramName(json.getString("name"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return program;
		
	}
	public JSONArray requestFollowers(String authToken , String userId) throws Exception
	{
		JSONArray followersJson = null;
		String cmdLine = null;
		cmdLine = mCommandStr[7];
		cmdLine += userId;
		cmdLine += ".json/?cmd=followers_detail";
		try
		{
			String followersStr = requestUrl("GET" , cmdLine);
			followersJson = new JSONArray(followersStr); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return followersJson ;
	}
	
	public JSONObject requestCheckin(int programId , 
	                                 String authToken ,
	                                 String userId ) throws Exception
	{
		JSONObject postResponse = null;
		String cmdLine = null;
		cmdLine = mCommandStr[10] + authToken;
		String postContent = null;
		JSONObject contentJson = new JSONObject();
		String postMethod = "POST";
		try
		{
			contentJson.put("user", userId);
			String programStr = String.valueOf(programId);
			contentJson.put("program" , programStr);
			contentJson.put("commit", "checkin");
			postContent = contentJson.toString();
			postUrl(postMethod , postContent , cmdLine);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return postResponse;
	}
	
	public JSONObject requestPostSubscribeOper(int programId , 
											   String authToken ,
											   String usrId ,
											   boolean bWatch) throws Exception
    {
		
		JSONObject postResponse = null;
		JSONObject postJson = new JSONObject();
		String strProgram = String.valueOf(programId);
		String cmdLine = null;

		cmdLine = mCommandStr[6] + authToken;
	
		
		String postContent = null;
		try
		{
			String method = "POST";
			postJson.put("user", usrId);
			postJson.put("program", strProgram);
			if(bWatch)
			{
				postJson.put("commit", "watch");
				
			}
			else
			{
				postJson.put("commit" , "unwatch");
				postJson.put("_method", "delete");
			}
			
			postContent = postJson.toString();
			
			
			postUrl(method , postContent , cmdLine);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return postResponse;
    }
	
	public JSONObject requestPostShortComment(int targetId , 
											  String usrId , 
											  String authToken , 
											  int operId)throws Exception
	{
		JSONObject postResponse = null;
		String cmdLine = mCommandStr[9] + authToken;
		JSONObject postJson = new JSONObject();
		try
		{
			postJson.put("user" , usrId);
			
			String targetStr = String.valueOf(targetId);
			postJson.put("discuss", targetStr);
			
			postJson.put("comment_type", "1");
			
			postJson.put("commit", "like");
			
			postUrl("POST"  , postJson.toString() , cmdLine);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return postResponse;
	}
	
	public JSONObject requestPostComment(int programId , 
										 int srcId , 
										 String authToken , 
										 String usrId , 
										 String content)throws Exception
	{
		JSONObject postResponse = null;
		JSONObject discussJson = new JSONObject();
		JSONObject postJson = new JSONObject();
		String cmdLine = mCommandStr[5] + authToken;
		String postContent = null;
		try
		{
			Log.d(TAG , usrId);
			
			discussJson.put("user_id", usrId);
			discussJson.put("topic", "null");
			discussJson.put("content", content);
			
			String programIdStr = String.valueOf(programId);
			postJson.put("program_id",programIdStr);
			
			String srcIdStr = null;
			if(srcId != -1)
			{
				srcIdStr = String.valueOf(srcId);
				postJson.put("src", srcIdStr);
			}
			
			postJson.put("commit", "Create");
			postJson.put("discuss", discussJson);
			postContent = postJson.toString();
			postUrl("POST"  , postContent , cmdLine);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return postResponse;
		
	}
	
	public JSONArray requestSubscribeProgramByUser(String usrId , String authToken) throws Exception
	{
		JSONArray json = null;
		String     jsonStr = null;
		String cmdLine = mCommandStr[7];
		cmdLine += usrId;
		cmdLine +=".json?cmd=watch_programs";
		try
		{
			jsonStr = requestUrl("GET" , cmdLine);
			json = new JSONArray(jsonStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return json;
	}
	
	public JSONObject requestProgramById(int programId , int start , int end ) throws Exception
	{
		String cmdLine = mCommandStr[4];
		cmdLine += programId;
		cmdLine += ".json/?discuss&from=";
		cmdLine += start;
		cmdLine += "&to=";
		cmdLine += end;
		JSONObject program = null;
		try
		{
			String  str = requestUrl("GET" , cmdLine);
			program = new JSONObject(str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		return program;
		
	}
	
	public JSONObject requestProgramById(int programId) throws Exception 
	{
		//return requestProgramById(programId , 1 , 10);
		String cmdLine = mCommandStr[4];
		cmdLine += programId;
		cmdLine += ".json/?cmd=detail";
		JSONObject program = null;
		try
		{
			String  str = requestUrl("GET" , cmdLine);
			program = new JSONObject(str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		return program;
	}
	
	public JSONArray requestAllTvStations() throws Exception 
	{
	
		String strJson = requestUrl("GET" , mCommandStr[0]);
		try
		{
			JSONArray tv = new JSONArray(strJson);
			return tv;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	public JSONObject requestProgramByTvstation(int tvIndex , int dayOffset) throws Exception
	{
	
		JSONObject programs = null;
		String cmdLine = mCommandStr[1];
		cmdLine += tvIndex;
		cmdLine +=".json/?offset=";
		cmdLine += dayOffset;
		Log.d(TAG , cmdLine);
		String strJson = requestUrl("GET" , cmdLine);
		try{
			programs = new JSONObject(strJson);
			return programs;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public JSONObject requestLogin(String userName , String password) throws Exception
	{
		JSONObject token = null;
		JSONObject userInfo = new JSONObject();
		JSONObject login = new JSONObject();
		try
		{
			userInfo.put("email" , userName);
			userInfo.put("password" , password);
			login.put("user", userInfo);
			String cmdLine = mCommandStr[3];
			String content = login.toString();
			String loginStr = postUrl("POST" , content , cmdLine);
			token = new JSONObject(loginStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		

		return token;
	}
	
	
	public ArrayList<TvStationProgram> getAllTvStations(JSONArray jsonArray) throws Exception
	{
		ArrayList<TvStationProgram> tv = new ArrayList<TvStationProgram>();
		try
		{
			Log.d(TAG , "getAllTvStations");
			for(int i = 0  ; i < jsonArray.length() ; i++)
			{
				TvStationProgram oneTv = new TvStationProgram();
				JSONObject oneTvJsonObj = jsonArray.getJSONObject(i);
				
				oneTv.setTvIndex(oneTvJsonObj.getInt("station_id"));
				oneTv.setTvName(oneTvJsonObj.getString("station_name"));
				JSONArray programArray = oneTvJsonObj.getJSONArray("programs");
				if( 0 != programArray.length())
				{
					JSONObject currentProgramJsonObj = programArray.getJSONObject(0);
					Program currentProgram = new Program();
					currentProgram.setProgramName(currentProgramJsonObj.getString("name"));
					currentProgram.setIndex(currentProgramJsonObj.getInt("program_id"));
					currentProgram.setBeginTime(parseString(currentProgramJsonObj.getString("begin")));
					currentProgram.setEndTime(parseString(currentProgramJsonObj.getString("end")));
					currentProgram.setSubscribeCount(currentProgramJsonObj.getInt("watch_count"));
					currentProgram.setDiscussCount(currentProgramJsonObj.getInt("discuss_count"));
					oneTv.addProgram(currentProgram);
				}
				tv.add(oneTv);
			}
		}
		catch(Exception e)
		{
	
			e.printStackTrace();
			throw e;
		}
		return tv;
	}
	
	//Default ISO8601 format
	Date parseStringToLocale(String strDate)
	{
		return parseString(strDate , mFormat[0] , true);
	}
	
	Date parseString(String strDate)
	{
		return parseString(strDate , mFormat[0] , false);
	}
	
	Date parseString(String strDate , String format , boolean isUTC)
	{
		/*String format ="yyyy-MM-dd'T'HH:mm:ss";*/
		Date date = null;
		SimpleDateFormat simpleDate = new SimpleDateFormat(format , Locale.CHINA);
		try
		{
			date= simpleDate.parse(strDate);
			Date temp = date;
			if(isUTC)
			{
				int minuOffset = date.getTimezoneOffset();
				Calendar cal = simpleDate.getCalendar();
				cal.add(Calendar.MINUTE,-minuOffset);
				temp = cal.getTime();
			}
			return temp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return date;
	}
	
	public TvStationProgram getProgramsByTvStation(JSONObject jsonObj) throws Exception
	{
		TvStationProgram oneTv = new TvStationProgram();
		ArrayList<Program> programs = null;
		try
		{
			oneTv.setTvIndex(jsonObj.getInt("station_id"));
			oneTv.setTvName(jsonObj.getString("station_name"));
		
			JSONArray programsJson = jsonObj.getJSONArray("programs");
			int size = programsJson.length();
			programs = new ArrayList<Program>();
			for(int i = 0 ; i < size ; i++)
			{
				Program one = new Program();
				one.setIndex(programsJson.getJSONObject(i).getInt("program_id"));
				one.setProgramName(programsJson.getJSONObject(i).getString("name"));
				one.setTvId(oneTv.getTvIndex());
				one.setTvName(oneTv.getTvName());
				one.setBeginTime(parseString(programsJson.getJSONObject(i).getString("begin")));
				one.setEndTime(parseString(programsJson.getJSONObject(i).getString("end")));
				one.setDiscussCount(programsJson.getJSONObject(i).getInt("discuss_count"));
				one.setSubscribeCount(programsJson.getJSONObject(i).getInt("watch_count"));
				programs.add(one);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		oneTv.setPrograms(programs);
		return oneTv;
		
	}
	
	public String postUrl(String method , String postContent , String url) throws Exception
	{
		String postUrl = null;
		String cmdStr = mCmdHttp + mCmdIp + ":" + mCmdPort + "/" + url;
		Log.d(TAG , cmdStr);
		try
		{
			byte[] requestStringBytes = postContent.getBytes("UTF-8");
			URL targetUrl = new URL(cmdStr);
			HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection(); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(requestStringBytes.length));
			conn.setRequestProperty("Content-type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			if(!method.equals("DELETE"))
			{
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(requestStringBytes);
				outputStream.close();
			}
			Log.d(TAG , postContent);
			Log.d(TAG , cmdStr);
			//get response
			int responseCode = conn.getResponseCode();
			if(conn.HTTP_OK == responseCode)
			{
				  StringBuffer sb = new StringBuffer();
	              String readLine;
	              BufferedReader responseReader;
	              responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	              while ((readLine = responseReader.readLine()) != null) 
	              {
	               sb.append(readLine).append("\n");
	              }
	              responseReader.close();
	              postUrl = sb.toString();
	              Log.d(TAG , sb.toString());
	              return postUrl;
			}
			else
			{
				Log.d(TAG , "response null respon code :" + responseCode);
				return null;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	public String requestUrl(String method , String url ) throws Exception
	{
		String cmdStr = mCmdHttp + mCmdIp +":" + mCmdPort + "/" + url;
		Log.d(TAG , cmdStr);
		try
		{
			URL targetUrl = new URL(cmdStr);
			Log.d(TAG , cmdStr);
			HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection(); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.connect();
			InputStream is = conn.getInputStream();  
			byte[] data = readStream(is);
			String jsonStr = new String(data);
			return jsonStr;
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	protected byte[] readStream(InputStream is) throws Exception
	{
		System.out.printf("readStream\n");
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream(); 
		
		try
		{
		
			int ch;  
			while ((ch = is.read()) != -1) 
			{  
				bytestream.write(ch);  
			}  
			
			bytestream.close();  
			byte imgdata[] = bytestream.toByteArray();  
			return imgdata;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
