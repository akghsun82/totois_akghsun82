package com.sc.cc.bean;

import java.util.*;
import java.io.*;

class StreamGobbler extends Thread{
    InputStream is;
    ArrayList<String> arrList;
    String type;
     
    StreamGobbler(ArrayList<String> arrList, String type){
       // this.is = is;
        this.arrList=arrList;
        this.type = type;

    }
    
    @Override
	public void run(){
        try{

           /* InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null){
                System.out.println(type + ">" + line);    
               }*/
            
        	Iterator<String> itr=this.arrList.iterator();
        	while(itr.hasNext()){
        		String line=(String)itr.next();
        		System.out.println(type + ">" + line);
        	}
          } catch (Exception ioe){
                ioe.printStackTrace();  
          }
    }
}

public class WindowsCommander{
	public final boolean VERBOSE=true;
	public static void main(String args[]){
        if (args.length < 1)
        {
            System.out.println("USAGE: java WindowsCommander <cmd>");
            System.exit(1);
        }
        
		WindowsCommander winCommander=new WindowsCommander();
		winCommander.execCommand(args[0],"c:\\tempK");
	}
	
	public void execCommand(String winCmd, String viewDirectory ){
		ArrayList<String> errArrList=new ArrayList<String>();
		ArrayList<String> outArrList=new ArrayList<String>();
		try{     
			//CMD /C dir
			/*
			 * 일반적으로 시작 실행을 통해서 실행할 수 있는 명려어는 제한도어 잇다ㅣ 기본적으로 시스템환경변수(%path%)에 등록된 경로내에 존재하는 프로그램과 appPath레시
			 * 스트리에 등록되어 잇는 프로그램나 실행할 수 잇따.자신이 자주사용하는 프로그램을 특정폴더에 모아두고 시스템환경변수에 포함
			 */
	            String osName = System.getProperty("os.name" );
	            String[] cmd = new String[3];
	            
	            if( osName.equals( "Windows 95" ) ){
	                cmd[0] = "command.com" ;
	                cmd[1] = "/C" ;
	                cmd[2] = winCmd;
            }else {
            	//if( osName.equals( "Windows NT" ) )
                cmd[0] = "cmd.exe" ;
                cmd[1] = "/C" ;
                 cmd[2] = winCmd;
            }
            
            Runtime rt = Runtime.getRuntime();
            System.out.println("Executing " + cmd[0] + " " + cmd[1]  + " " + cmd[2]);
            Process proc = null;
            if(viewDirectory != null && viewDirectory.length() != 0){
            	File file = new File(viewDirectory);
                proc = rt.exec(cmd,null,file);
            }
            else{
            	proc = rt.exec(cmd);
            }
            InputStream errInputStream=proc.getErrorStream();
            InputStream inputStream=proc.getInputStream();
            
            //성공inputStream을 arraylist로 변환(재사용을 위해)
			InputStreamReader isr = new InputStreamReader(inputStream);
	        BufferedReader br = new BufferedReader(isr);
	        String line=null;
	        while ( (line = br.readLine()) != null){
	        	//System.out.println("line: "+line);
	        	outArrList.add(line);
	           }
	        
	        //성공inputStream을 arraylist로 변환(재사용을 위해)
			InputStreamReader isr2 = new InputStreamReader(errInputStream);
	        BufferedReader br2 = new BufferedReader(isr2);
	        String line2=null;
	        while ( (line2 = br2.readLine()) != null){
	        	//System.out.println("line: "+line2);
	        	errArrList.add(line2);
	           }
            
            // any error message?
            //StreamGobbler errorGobbler = new StreamGobbler(errInputStream, "ERROR");            
            StreamGobbler errorGobbler = new StreamGobbler(errArrList, "ERROR");            
                     
            // any output?
            //StreamGobbler outputGobbler = new StreamGobbler(inputStream, "OUTPUT");
            StreamGobbler outputGobbler = new StreamGobbler(outArrList, "OUTPUT");
                         
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);   
            
        } catch (Throwable t){
            t.printStackTrace();
          }
	}
	
	
	
	
	public  Map<Object,Object> execCommandWithReturn(String winCmd, String viewDirectory) throws Exception{
		InputStream errInputStream=null;
		InputStream inputStream=null;
		ArrayList<String> errArrList=new ArrayList<String>();
		ArrayList<String> outArrList=new ArrayList<String>();
		int exitVal=0;
		
		try{            
            String osName = System.getProperty("os.name" );
            String[] cmd = new String[3];
            
            if( osName.equals( "Windows 95" ) ){
                cmd[0] = "command.com" ;
                cmd[1] = "/C" ;
                cmd[2] = winCmd;
            }else {
            //if( osName.equals( "Windows NT" ) )
                cmd[0] = "cmd.exe" ;
                cmd[1] = "/C" ;
                cmd[2] = winCmd;
            }


            Runtime rt = Runtime.getRuntime();
           // System.out.println("Executing " + cmd[0] + " " + cmd[1]  + " " + cmd[2]);
             Process proc=null;            
            if(viewDirectory != null && viewDirectory.length() != 0){
            	File file = new File(viewDirectory);
                proc = rt.exec(cmd,null,file);
            }
            else{
            	proc = rt.exec(cmd);
            }
            
            errInputStream=proc.getErrorStream();
            inputStream=proc.getInputStream();
 
            //성공inputStream을 arraylist로 변환(재사용을 위해)
			InputStreamReader isr = new InputStreamReader(inputStream);
	        BufferedReader br = new BufferedReader(isr);
	        String line=null;
	        while ( (line = br.readLine()) != null){
	        	//System.out.println("line: "+line);
	        	outArrList.add(line);
	           }
	        
	        //성공inputStream을 arraylist로 변환(재사용을 위해)
			InputStreamReader isr2 = new InputStreamReader(errInputStream);
	        BufferedReader br2 = new BufferedReader(isr2);
	        String line2=null;
	        while ( (line2= br2.readLine()) != null){
	        	//System.out.println("line: "+line2);
	        	errArrList.add(line2);
	           }


             // any error message?
             //StreamGobbler errorGobbler = new StreamGobbler(errInputStream, "ERROR");            
             StreamGobbler errorGobbler = new StreamGobbler(errArrList, "ERROR");            
                      
             // any output?
             //StreamGobbler outputGobbler = new StreamGobbler(inputStream, "OUTPUT");
             StreamGobbler outputGobbler = new StreamGobbler(outArrList, "OUTPUT");

                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
           exitVal = proc.waitFor();
           //System.out.println("ExitValue: " + exitVal);   
            
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        //결과정리
        Map<Object,Object> cmdResultMap = new Properties();
        cmdResultMap.put("EXIT_VAL", exitVal);
        cmdResultMap.put("OUTPUT", outArrList);
        cmdResultMap.put("ERROR", errArrList);

        return cmdResultMap;
        

	}
	
	
	
	
}

 