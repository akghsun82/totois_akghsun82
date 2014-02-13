package com.sc.cc.checkin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;

import com.kicco.util.KiccoLogger;
import com.sc.cc.bean.CCMgr;
import com.sc.cc.bean.ConfigElement;
import com.sc.cc.bean.FileHandler;
import com.sc.cc.bean.FileUtil;
import com.sc.cc.bean.Tar;
import com.sc.cc.bean.XMLParseMgr;



public class CheckInElement {
		public static final String fileSepar=System.getProperty("file.separator");
		public static final String FTP_FILE_PATH=getEnvValue("FTP_FILE_PATH");
		public final static String PROP_FILE_NAME="F:\\SCFB_BUILD\\BUILD_SCRIPT_APP\\cc_ext_env.properties";
		
		private final String UNIX_TAR_TYPE="UNIXTAR";
		private final String GNU_TAR_TYPE="GNUTAR";
		
		//public final static String XML_ROOT=fileSepar+getEnvValue("XML_ROOT");
		
		private String vobName;
		private String vobRoot;
		private String activityRoot;
		private String extractRoot;

	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String vobName=args[0];
		String activityNo=args[1];
		String viewDir=args[2];
		
		CheckInElement checkIn=new CheckInElement();
		checkIn.exec(vobName,activityNo,viewDir);
	}
	
	
	/**
	 * 
	 * @param vobName
	 * @param activityNo
	 * @throws Exception
	 */
	public  int  exec(String vobName, String activityNo, String viewDir) throws Exception{
		this.vobName=vobName;
		int resultCode=0;

		
		vobRoot=FTP_FILE_PATH+fileSepar+this.vobName;//VOB 경로명
		activityRoot=vobRoot+fileSepar+activityNo; // acitivity 경로명
		extractRoot=activityRoot+getEnvValue("EXTRACT_ROOT."+this.vobName);

		try{
			
			KiccoLogger.getLogger().log(Level.INFO,"viewDir:"+viewDir+"\n"
					+"vobRoot:"+vobRoot+"\n"
					+"activityRoot:"+activityRoot+"\n"
					+"extractRoot:"+extractRoot+"\n");
			
			/**
			long startTime=System.currentTimeMillis();	
			long endTime;
			String strStartTime=CheckInElement.formatTime(startTime);
			System.out.println(vobName+"시작:"+strStartTime);

			
			while(true){//2분동안 
				endTime = System.currentTimeMillis();
				if(endTime-startTime>(1000*60*2))
					break;
				else
					continue;
			}	
			String strEndTime=CheckInElement.formatTime(endTime);
			System.out.println(vobName+"끝:"+strEndTime);
			**/
			
			//압축 파일 해제
			unTar(vobRoot+fileSepar+activityNo+".tar",activityRoot);
			
			//XML파싱
			XMLParseMgr xmlMgr=new XMLParseMgr(activityRoot+fileSepar+activityNo+".xml");
			xmlMgr.startParse();		
			ArrayList<ConfigElement> configElements=xmlMgr.getConfigElements();
			
			//체크아웃 및 add to src control 및 체크인
			putConfigItemToCC(activityNo,viewDir,configElements);
			
			KiccoLogger.getLogger().log(Level.INFO,"작업완료:["+resultCode+"]");
			
		}catch(Exception e){
			resultCode=1;
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
			
		}finally{
			//폴더 삭제
			if("true".equalsIgnoreCase(getEnvValue("IS_EXTRACT_DELETE"))){
				FileHandler.deleteDirectory(new File(vobRoot+fileSepar+activityNo));
				KiccoLogger.getLogger().log(Level.INFO,"폴더삭제:["+vobRoot+fileSepar+activityNo+"]");
			}
			
		}		
		return resultCode;
	}
	
	/**
	 * tar압축파일 해제 및 삭제
	 * @param tarFileName
	 * @param unTarDir
	 * @throws Exception
	 */
	public void unTar(String tarFileName, String unTarDir) throws Exception{
		try{
			String tarFileType=getEnvValue("TAR.TYPE."+this.vobName);
			KiccoLogger.getLogger().log(Level.INFO,"TAR FILE TYPE:["+tarFileType+"]");			
			
			if(UNIX_TAR_TYPE.equals(tarFileType)){
				InputStream tarIs=Tar.getInputStream(tarFileName);
				Tar.readTar(tarIs, unTarDir);
				
			}else if(GNU_TAR_TYPE.equals(tarFileType)){
				FileUtil.unTarFile(tarFileName, unTarDir);
				
			}else{
				throw new Exception("지정된 TARFILE형식이 아닙니다.["+tarFileType+"]");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
		}finally{
			File tarFile=new File(tarFileName);
			if("true".equalsIgnoreCase(getEnvValue("IS_TAR_DELETE")))
				tarFile.delete();

		}
	}
	
	/**
	 * 전체 일괄 체크아웃(파일별로 밖에 안되므로 snyc를 걸어야)-업무별로 set activity
	 * @param activiNo
	 * @param viewDir
	 * @param configElements
	 * @throws Exception
	 */
	private void putConfigItemToCC(String activityNo,String viewDir, ArrayList<ConfigElement> configElements) throws Exception{
		
		KiccoLogger.getLogger().log(Level.INFO,"형상항목을 CC에 넣기 시작");
	
		CCMgr ccMgr=new CCMgr();
		try{
			//0.set activity
			ccMgr.setActivity(activityNo, viewDir);
			
			for(int ii=0;ii<configElements.size();ii++){
				KiccoLogger.getLogger().log(Level.INFO,"[형상항목]--------------"+configElements.get(ii).getFullyQualifiedConfigName(vobName)+"-------");
				ArrayList<String> parentCheckOutList=new ArrayList<String>();
				boolean isNewItem=false;
				ConfigElement item=configElements.get(ii);
				String coItemName=item.getFullyQualifiedConfigName(vobName);
				
				//1.신규 파일인 경우 신규폴더의 상위폴더를 chekOut함
				File itemFile=new File(item.getFullyQualifiedConfigName(vobName));
				KiccoLogger.getLogger().log(Level.INFO, itemFile.getAbsolutePath()+": "+itemFile.exists());
				if(itemFile.exists()==false ||ccMgr.isViewPrivate(item.getFullyQualifiedConfigName(vobName), viewDir)){
					parentCheckOutList.add(0,itemFile.getAbsolutePath());
					this.setNewParent(itemFile,parentCheckOutList,1,viewDir);
					
					for(int kk=0; kk<parentCheckOutList.size();kk++){
						KiccoLogger.getLogger().log(Level.INFO,"list["+kk+"]:"+parentCheckOutList.get(kk));
					}
					
					int idx=parentCheckOutList.size()-1;
					int lasdIdx=parentCheckOutList.get(idx).lastIndexOf("\\");
					String coFolderName=parentCheckOutList.get(idx).substring(0,lasdIdx);
					KiccoLogger.getLogger().log(Level.INFO,"checkOutFolder:"+coFolderName);
					coItemName=coFolderName; //존재하지 않는 폴더의 상위 폴더 체크아웃
					isNewItem=true;
				}
				
				//2.기존 파일(혹은 신규의 상위폴더) 체크아웃(aleady checkout이 될 수 있음-alea)
				ccMgr.checkOutItem(coItemName, activityNo, viewDir);
				
				//3. 신규파일, 기존 파일 copy
				this.copyFiles(item);
				
				//4.신규파일 mkelemnt(add to version control)-폴더계층별로 수행
	//			for(int kk=parentCheckOutList.size()-1;kk>=0;kk--){
	//					KiccoLogger.getLogger().log(Level.INFO,"mkelements:"+kk+parentCheckOutList.get(kk));
	//					ccMgr.mkElement(parentCheckOutList.get(kk),viewDir);
	//			}
				if(isNewItem)ccMgr.mkElement(item.getFullyQualifiedConfigName(vobName),viewDir);
			}
			
			//5.액티비티 체크인
			ccMgr.checkInItem(activityNo, viewDir,vobName+"_PVOB" );
		}catch(Exception e){
			throw e;
			
		}finally{			
			//6.activity Non
			ccMgr.setActivity("-none", viewDir);
		}

	}

	/**
	 * 
	 * @param configElements
	 * @throws Exception
	 */
	private void copy(ArrayList<ConfigElement> configElements) throws Exception{
		KiccoLogger.getLogger().log(Level.INFO,"복사");
		try{
			
			for(int ii=0;ii<configElements.size();ii++){
				this.copyFiles(configElements.get(ii));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
		}finally{
			
		}
	}
	
	/**
	 * 
	 * @param element
	 * @throws Exception
	 */
	private void copyFiles(ConfigElement element) throws Exception{
		try{
			File configElementFile;
			File toDir;
			File targetFile;
			
			//파일시스템 파일경로
			configElementFile=new File(extractRoot+fileSepar+element.getFullyQualifiedFileName());
			toDir=new File(element.getConfigPath(this.vobName));
			//형상관리소 파일경로
			targetFile=new File(element.getFullyQualifiedConfigName(this.vobName));
			
			KiccoLogger.getLogger().log(Level.INFO,"[형상항목복사]:"+configElementFile.getAbsolutePath()
											+"-->"+toDir.getAbsolutePath());
			
			if(!configElementFile.exists()) throw new Exception("해당파일이 존재하지 않습니다. "+configElementFile);
			if(!toDir.exists()){
				toDir.mkdirs();
			}
			
			FileHandler.fileCopyWithBuf(configElementFile,targetFile );
		}catch(Exception e){
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
		}
	}

	
	/** 
	 * 해당 file의 상위 폴더가 존재하는지 체크, 존재시까지 recurisve호출 하여 ArrayList에 집어넣음
	 * 해당파일의 존재하지 않는 상위 폴더의 리스트를 세팅
	 * @param file
	 * @param folderList
	 * @throws Exception
	 */
	private void setNewParent(File file, ArrayList<String>folderList,int idx,String viewDir) throws Exception{
		CCMgr ccMgr=new CCMgr();
		KiccoLogger.getLogger().log(Level.INFO,"[신규 파일(폴더)여부 확인]:"+file.getAbsolutePath());
		try{
			File parentFile=file.getParentFile();
			//파일(폴더)가 없거나, 존재하더라도 viewpriavte이면 없음으로 간주
			if(!parentFile.exists() || ccMgr.isViewPrivate(parentFile.getAbsolutePath(), viewDir)){
				folderList.add(parentFile.getAbsolutePath());
				this.setNewParent(parentFile,folderList,idx++,viewDir);
			}
		}catch(Exception e){
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
		}finally{
			
		}
	}
	
    /**************************************************************
     * <p>Description : 밀리초(ms)단위의 시간을 시분초로 포멧<p>
     *
     * @param     {long}    lTime 미리초단위 시간
     * @return     포매팅된 문자열
     * @throws 
     * @since     2008-09-22
     **************************************************************/
    public static String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return (c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분 " + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND) + "초");
    }    // end function formatTime()
	
    public static String  getEnvValue(String key){
    	
		Properties props=new Properties();
    	try{	
			props.load(new FileInputStream(CheckInElement.PROP_FILE_NAME));
			
    	}catch(Exception e){
    		e.printStackTrace();
    		KiccoLogger.getLogger().log(Level.INFO,"프로퍼티 파일 로딩 실패:"+e.getMessage());
    	}
    	//KiccoLogger.getLogger().log(Level.INFO,"프로퍼티 파일 로딩:"+CheckInElement.PROP_FILE_NAME);
    	String value=props.getProperty(key);
    	if(null==value) value="";
    	return value;
    }
}
