package com.sc.cc.bean;

import com.sc.cc.checkin.CheckInElement;

public class ConfigElement {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	//public static final int VERSION_UPDATE=0;
	//public static final int VERSION_NEW=1;
	//public static final int VERSION_DELETE=2;
	
	
	
	private String configName;
	private String configPath;
	private String configExt;
	private String filePath;
	private String versionFlag;
		
	public String getConfigName() {
		return configName;
	}
	public String getConfigPath() {
		return configPath;
	}
	public String getConfigPath(String vobName){
		return CheckInElement.getEnvValue("ELEMENT_ROOT."+vobName)+configPath;
	}
	public String getConfigExt() {
		return configExt;
	}
	public String getFilePath() {
		return filePath;
	}
	public String getVersionFlag() {
		return versionFlag;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	public void setConfigExt(String configExt) {
		this.configExt = configExt;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setVersionFlag(String versionFlag) {
		this.versionFlag = versionFlag;
	}
	
	
	/**
	 * 형상항목의 ClearCase 내 파일 전체경로(파일이름 및 확장자 포함)
	 * vobName에 해당하는 ElementRoot 경로를 첨부하여리턴
	 * @return
	 */
	public String getFullyQualifiedConfigName(String vobName){

		return getConfigPath(vobName)+System.getProperty("file.separator")+configName+"."+configExt;
	}
	
	private String getFullyQualifiedConfigName(){
		
		return configPath+System.getProperty("file.separator")+configName+"."+configExt;
	}
	/**
	 * 형상항목의 물리적인 파일 전체경로(파일이름 및 확장자 포함)
	 * @return
	 */
	public String getFullyQualifiedFileName(){
		return filePath+System.getProperty("file.separator")+configName+"."+configExt;
	}
	
	public String toString(){
		StringBuffer str=new StringBuffer("");
		
		str.append("getConfigName:"+this.getConfigName()+"\n");
		str.append("getConfigPath:"+this.getConfigPath()+"\n");
		str.append("getConfigExt:"+this.getConfigExt()+"\n");
		str.append("getFullyQualifiedConfigName:[VOB Element Root]\\"+this.getFullyQualifiedConfigName()+"\n");
		str.append("getFullyQualifiedFileName:"+this.getFullyQualifiedFileName()+"\n");
		str.append("getVersionFlag:"+this.getVersionFlag()+"\n");

		
		return str.toString();
	}

}
