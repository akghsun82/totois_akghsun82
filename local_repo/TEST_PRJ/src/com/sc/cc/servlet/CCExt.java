package com.sc.cc.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kicco.util.KiccoLogger;
import com.sc.cc.checkin.CheckInElement;


/**
 * Servlet implementation class CCExt
 */

public class CCExt extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Properties props;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CCExt() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(){
    	/*
    	String filePath=null;
    	try{
			props=new Properties();
			filePath=CheckInElement.PROP_FILE_NAME;			
			props.load(new FileInputStream(filePath));
			
    	}catch(Exception e){
    		e.printStackTrace();
    		KiccoLogger.getLogger().log(Level.INFO,"프로퍼티 파일 로딩 실패:"+e.getMessage());
    	}
    	KiccoLogger.getLogger().log(Level.INFO,"프로퍼티 파일 로딩:"+filePath);
    	*/
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int isSuc=1;
		try{

			String vobName=request.getParameter("VOB_NAME");
			String activityNo=request.getParameter("ACTIVITY_NO");		
			//String viewDir=props.getProperty("VIEW_DIR."+vobName).trim();
			//String soution=props.getProperty("SOLUTION."+vobName).trim();

			String viewDir=CheckInElement.getEnvValue("VIEW_DIR."+vobName).trim();;
			String soution=CheckInElement.getEnvValue("SOLUTION."+vobName).trim();;
			
			KiccoLogger.getLogger().log(Level.INFO,
						"VOB_NAME:"+vobName
						+", ACTIVITY_NO:"+activityNo
						+", VIEW_DIR:"+viewDir);
			
			//vob별로 작업 직렬화:vob이름과 동일한 클래스 생성 및 생성된 클래스별로 syncronized
			synchronized (Class.forName("com.sc.cc.checkin."+vobName)){
				CheckInElement checkIn=(CheckInElement) Class.forName("com.sc.cc.checkin."+vobName).newInstance();
				isSuc=checkIn.exec(vobName,activityNo,viewDir);
			}
			
		}catch(Exception e){
			isSuc=1;
			e.printStackTrace();
		}finally{
		
		}
		KiccoLogger.getLogger().log(Level.INFO,"isSuc:["+isSuc+"]");
		PrintWriter out=null;
		try{
			out=response.getWriter();
			String rsultStr="";
			if(isSuc==0){
				rsultStr="<?xml version=\"1.0\" encoding=\"UTF-8\"?><RESPONSE><CODE>0</CODE><MESSAGE>success</MESSAGE></RESPONSE>";
			}else{
				rsultStr="<?xml version=\"1.0\" encoding=\"UTF-8\"?><RESPONSE><CODE>1</CODE><MESSAGE>fail</MESSAGE></RESPONSE>";
			}
			out.write(rsultStr+"");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(out!=null) out.close();
		}
	}

}
