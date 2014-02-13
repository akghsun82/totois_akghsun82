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

		
		vobRoot=FTP_FILE_PATH+fileSepar+this.vobName;//VOB ��θ�
		activityRoot=vobRoot+fileSepar+activityNo; // acitivity ��θ�
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
			System.out.println(vobName+"����:"+strStartTime);

			
			while(true){//2�е��� 
				endTime = System.currentTimeMillis();
				if(endTime-startTime>(1000*60*2))
					break;
				else
					continue;
			}	
			String strEndTime=CheckInElement.formatTime(endTime);
			System.out.println(vobName+"��:"+strEndTime);
			**/
			
			//���� ���� ����
			unTar(vobRoot+fileSepar+activityNo+".tar",activityRoot);
			
			//XML�Ľ�
			XMLParseMgr xmlMgr=new XMLParseMgr(activityRoot+fileSepar+activityNo+".xml");
			xmlMgr.startParse();		
			ArrayList<ConfigElement> configElements=xmlMgr.getConfigElements();
			
			//üũ�ƿ� �� add to src control �� üũ��
			putConfigItemToCC(activityNo,viewDir,configElements);
			
			KiccoLogger.getLogger().log(Level.INFO,"�۾��Ϸ�:["+resultCode+"]");
			
		}catch(Exception e){
			resultCode=1;
			e.printStackTrace();
			KiccoLogger.getLogger().log(Level.SEVERE,e.getMessage());
			throw e;
			
		}finally{
			//���� ����
			if("true".equalsIgnoreCase(getEnvValue("IS_EXTRACT_DELETE"))){
				FileHandler.deleteDirectory(new File(vobRoot+fileSepar+activityNo));
				KiccoLogger.getLogger().log(Level.INFO,"��������:["+vobRoot+fileSepar+activityNo+"]");
			}
			
		}		
		return resultCode;
	}
	
	/**
	 * tar�������� ���� �� ����
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
				throw new Exception("������ TARFILE������ �ƴմϴ�.["+tarFileType+"]");
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
	 * ��ü �ϰ� üũ�ƿ�(���Ϻ��� �ۿ� �ȵǹǷ� snyc�� �ɾ��)-�������� set activity
	 * @param activiNo
	 * @param viewDir
	 * @param configElements
	 * @throws Exception
	 */
	private void putConfigItemToCC(String activityNo,String viewDir, ArrayList<ConfigElement> configElements) throws Exception{
		
		KiccoLogger.getLogger().log(Level.INFO,"�����׸��� CC�� �ֱ� ����");
	
		CCMgr ccMgr=new CCMgr();
		try{
			//0.set activity
			ccMgr.setActivity(activityNo, viewDir);
			
			for(int ii=0;ii<configElements.size();ii++){
				KiccoLogger.getLogger().log(Level.INFO,"[�����׸�]--------------"+configElements.get(ii).getFullyQualifiedConfigName(vobName)+"-------");
				ArrayList<String> parentCheckOutList=new ArrayList<String>();
				boolean isNewItem=false;
				ConfigElement item=configElements.get(ii);
				String coItemName=item.getFullyQualifiedConfigName(vobName);
				
				//1.�ű� ������ ��� �ű������� ���������� chekOut��
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
					coItemName=coFolderName; //�������� �ʴ� ������ ���� ���� üũ�ƿ�
					isNewItem=true;
				}
				
				//2.���� ����(Ȥ�� �ű��� ��������) üũ�ƿ�(aleady checkout�� �� �� ����-alea)
				ccMgr.checkOutItem(coItemName, activityNo, viewDir);
				
				//3. �ű�����, ���� ���� copy
				this.copyFiles(item);
				
				//4.�ű����� mkelemnt(add to version control)-������������ ����
	//			for(int kk=parentCheckOutList.size()-1;kk>=0;kk--){
	//					KiccoLogger.getLogger().log(Level.INFO,"mkelements:"+kk+parentCheckOutList.get(kk));
	//					ccMgr.mkElement(parentCheckOutList.get(kk),viewDir);
	//			}
				if(isNewItem)ccMgr.mkElement(item.getFullyQualifiedConfigName(vobName),viewDir);
			}
			
			//5.��Ƽ��Ƽ üũ��
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
		KiccoLogger.getLogger().log(Level.INFO,"����");
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
			
			//���Ͻý��� ���ϰ��
			configElementFile=new File(extractRoot+fileSepar+element.getFullyQualifiedFileName());
			toDir=new File(element.getConfigPath(this.vobName));
			//��������� ���ϰ��
			targetFile=new File(element.getFullyQualifiedConfigName(this.vobName));
			
			KiccoLogger.getLogger().log(Level.INFO,"[�����׸񺹻�]:"+configElementFile.getAbsolutePath()
											+"-->"+toDir.getAbsolutePath());
			
			if(!configElementFile.exists()) throw new Exception("�ش������� �������� �ʽ��ϴ�. "+configElementFile);
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
	 * �ش� file�� ���� ������ �����ϴ��� üũ, ����ñ��� recurisveȣ�� �Ͽ� ArrayList�� �������
	 * �ش������� �������� �ʴ� ���� ������ ����Ʈ�� ����
	 * @param file
	 * @param folderList
	 * @throws Exception
	 */
	private void setNewParent(File file, ArrayList<String>folderList,int idx,String viewDir) throws Exception{
		CCMgr ccMgr=new CCMgr();
		KiccoLogger.getLogger().log(Level.INFO,"[�ű� ����(����)���� Ȯ��]:"+file.getAbsolutePath());
		try{
			File parentFile=file.getParentFile();
			//����(����)�� ���ų�, �����ϴ��� viewpriavte�̸� �������� ����
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
     * <p>Description : �и���(ms)������ �ð��� �ú��ʷ� ����<p>
     *
     * @param     {long}    lTime �̸��ʴ��� �ð�
     * @return     �����õ� ���ڿ�
     * @throws 
     * @since     2008-09-22
     **************************************************************/
    public static String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return (c.get(Calendar.HOUR_OF_DAY) + "�� " + c.get(Calendar.MINUTE) + "�� " + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND) + "��");
    }    // end function formatTime()
	
    public static String  getEnvValue(String key){
    	
		Properties props=new Properties();
    	try{	
			props.load(new FileInputStream(CheckInElement.PROP_FILE_NAME));
			
    	}catch(Exception e){
    		e.printStackTrace();
    		KiccoLogger.getLogger().log(Level.INFO,"������Ƽ ���� �ε� ����:"+e.getMessage());
    	}
    	//KiccoLogger.getLogger().log(Level.INFO,"������Ƽ ���� �ε�:"+CheckInElement.PROP_FILE_NAME);
    	String value=props.getProperty(key);
    	if(null==value) value="";
    	return value;
    }
}
