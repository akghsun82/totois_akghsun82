package com.sc.cc.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kicco.util.Common;


public class ChangeSetItem{
	
	//��üChangeSet���� /���� ����
	public String changSetName;
	//�����׸��(����,����)
	private String itemName;
	private String parsedName;
	private String distinctPivot;
	//����
	public String version;
	public int code;
	public static final int NORMAL=0;
	public static final int ALEADY_CHECK_OUT=1;
	public static final int BLOKED_ITEM=2;
	
	private boolean isRebuildChangeSet=false;
	
	int id;
	
	public static void main(String args[]){
		ChangeSetItem item=new ChangeSetItem();
		item.changSetName=" F:\\Views\\MF_UT_view\\HY\\HKE2\\SORCB3HP\\@@TBJSM07.PL1\\.@@\\main\\MF_UT\\5";
		System.out.println(item.getItemName());;
		System.out.println(item.toString());
	}
	
	public String getItemName(){
		String temp=this.changSetName;
		if(Common.isAnyMatchStr(this.changSetName, "@@\\\\main")){
			temp=Common.getString(this.changSetName, "@@\\main", true);
		}
		temp=temp.substring(temp.lastIndexOf("\\")+1);
		return temp;
	}
	
//������ �ܺο��� �����ϴ� ��찡 �ٹݻ�	
/*	public String getVersion(){
		String tempVer;
		if(Common.isAnyMatchStr(changSetName, "CHECKEDOUT"))
			tempVer="-1";
		else
			tempVer=changSetName.substring(changSetName.lastIndexOf("\\")+1);
		return tempVer;
	}*/
	
	public void setVersion(String version){
		this.version=version;
	}

	public String getParsedElementName() {
		String line=changSetName;

		line=line.replace("M:\\MF_UT_view", "��Ƽ��");
		line=line.replace("M:\\MF_SIT_view", "��������Ƽ��");
		line=line.replace("F:\\Views\\MF_SIT_BUILD_view", "��������Ƽ�����");
		line=line.replace("F:\\Views\\MF_UAT_view","������Ƽ��");;
		line=line.replace("F:\\Views\\MF_PR_view","�Ǿ˺�");	
		line=line.replace("M:\\TEST_DEV","�׽�Ʈ���̺�");	
		line=line.replace("M:\\TEST_INT","�׽�Ʈ��Ʈ");	
		
		line=line.replace("\\.@@\\main", "");
		line=line.replace("@@\\main", "");
		line=line.replace("\\main\\", "\\");
		line=line.replace("\\MF_UT\\", "\\");
		line=line.replace("\\MF_SIT\\", "\\");
		line=line.replace("\\MF_SIT_BUILD\\", "\\");
		line=line.replace("\\MF_UAT\\", "\\");
		line=line.replace("\\MF_PR\\", "\\");		
	
		line=line.replace("\\TEST_DEV\\", "\\");	
		line=line.replace("\\TEST_INT\\", "\\");	
		
		
		//���� ���� \12\  ->\
		Pattern startPattern=Pattern.compile("\\\\\\d+\\\\");
		Matcher matcher=startPattern.matcher(line);
		if(matcher.find()){
			//System.out.println(matcher.group());
			line=line.replace(matcher.group(), "\\");
		}
		
		//�� �� ���� ����
		Pattern startPattern2=Pattern.compile("\\\\\\d+$");
		Matcher matcher2=startPattern2.matcher(line);
		if(matcher2.find()){
			//System.out.println(matcher2.group());
			line=line.replace(matcher2.group(), "");
		}
	
		line=line.replace("��Ƽ��", "M:\\MF_UT_view");
		line=line.replace("��������Ƽ��", "M:\\MF_SIT_view");
		line=line.replace("��������Ƽ�����", "F:\\Views\\MF_SIT_BUILD_view");
		line=line.replace("������Ƽ��","F:\\Views\\MF_UAT_view");
		line=line.replace("�Ǿ˺�","F:\\Views\\MF_PR_view");
		
		line=line.replace("�׽�Ʈ���̺�","M:\\TEST_DEV");	
		line=line.replace("�׽�Ʈ��Ʈ","M:\\TEST_INT");	
		//System.out.println(org);
		//System.out.println(line);
		return line;
	
	}
	
	public String getShortParseElementName(){
		String result="";
		if(Common.isAnyMatchStr(this.changSetName, "\\\\.@@\\\\main")){
			result= Common.getString(changSetName, "\\.@@\\main", true);
		}else if(Common.isAnyMatchStr(this.changSetName, "@@\\\\main"))
			result= Common.getString(changSetName, "@@\\main", true);
		return result;
	}


	public String getDistinctPivot() {
		return distinctPivot;
	}

	public void setDistinctPivot(String distinctPivot) {
		this.distinctPivot = distinctPivot;
	}
	
	
	
	public boolean isRebuildChangeSet() {
		return isRebuildChangeSet;
	}

	public void setRebuildChangeSet(boolean isRebuildChangeSet) {
		this.isRebuildChangeSet = isRebuildChangeSet;
	}

	public String toString(){
		StringBuffer str=new StringBuffer();
		str.append("itemName:"+getItemName()+"\n");
		str.append("changSetName:"+changSetName+"\n");
		str.append("version:"+version+"\n");
		str.append("parsedName:"+getParsedElementName()+"\n");
		str.append("shortParsedName:"+getShortParseElementName()+"\n");
		str.append("isRebuildChangeSet:"+isRebuildChangeSet()+"\n");
		if(code==ChangeSetItem.NORMAL)
			str.append("code:NORMAL \n");
		else if(code==ChangeSetItem.ALEADY_CHECK_OUT)
			str.append("code:ALEADY_CHECK_OUT\n");
		else if(code==ChangeSetItem.BLOKED_ITEM)
			str.append("code:BLOKED_ITEM\n");
		
		
		return str.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean isEqual = false;
		if ((o instanceof ChangeSetItem) && ((ChangeSetItem)o).distinctPivot.equals(distinctPivot) ) {
			System.out.println(((ChangeSetItem)o).distinctPivot+"   "+distinctPivot);
			isEqual = true;
		}
		return isEqual;
	}
	
	/*
	 * Set �������̽����� �����ؾ� �ϴ� �޼���
	 */
	@Override
	public int hashCode() {
		// Joshua Bloch�� �ۼ����� ���
		int result = 17;
		result = 37 * result + distinctPivot.hashCode();
		result = 37 + result + id;
		return result;
	}
	
	/**
	 * @deprecated
	 * @param line
	 * @return
	 */
	private String changeSetParse(String line){
		// TODO Auto-generated method stub
		//String line=" F:\\Views\\MF_SIT_BUILD_view\\YS\\HKF5\\SORCB2RR@@\\main\\MF_PR\\MF_UT\\1\\TBDAEM2.PL1\\main\\MF_SIT\\2";
		String org=line;
		line=line.replace("M:\\MF_UT_view", "��Ƽ��");
		line=line.replace("M:\\MF_SIT_view", "��������Ƽ��");
		line=line.replace("F:\\Views\\MF_SIT_BUILD_view", "��������Ƽ�����");
		line=line.replace("F:\\Views\\MF_UAT_view","������Ƽ��");;
		line=line.replace("F:\\Views\\MF_PR_view","�Ǿ˺�");	
		line=line.replace("M:\\TEST_DEV","�׽�Ʈ���̺�");	
		line=line.replace("M:\\TEST_INT","�׽�Ʈ��Ʈ");	
		
		line=line.replace("\\.@@\\main", "");
		line=line.replace("@@\\main", "");
		line=line.replace("\\main\\", "\\");
		line=line.replace("\\MF_UT\\", "\\");
		line=line.replace("\\MF_SIT\\", "\\");
		line=line.replace("\\MF_SIT_BUILD\\", "\\");
		line=line.replace("\\MF_UAT\\", "\\");
		line=line.replace("\\MF_PR\\", "\\");		
	
		line=line.replace("\\TEST_DEV\\", "\\");	
		line=line.replace("\\TEST_INT\\", "\\");	
		
		
		//���� ���� \12\  ->\
		Pattern startPattern=Pattern.compile("\\\\\\d+\\\\");
		Matcher matcher=startPattern.matcher(line);
		if(matcher.find()){
			//System.out.println(matcher.group());
			line=line.replace(matcher.group(), "\\");
		}
		
		//�� �� ���� ����
		Pattern startPattern2=Pattern.compile("\\\\\\d+$");
		Matcher matcher2=startPattern2.matcher(line);
		if(matcher2.find()){
			//System.out.println(matcher2.group());
			line=line.replace(matcher2.group(), "");
		}
	
		line=line.replace("��Ƽ��", "M:\\MF_UT_view");
		line=line.replace("��������Ƽ��", "M:\\MF_SIT_view");
		line=line.replace("��������Ƽ�����", "F:\\Views\\MF_SIT_BUILD_view");
		line=line.replace("������Ƽ��","F:\\Views\\MF_UAT_view");
		line=line.replace("�Ǿ˺�","F:\\Views\\MF_PR_view");
		
		line=line.replace("�׽�Ʈ���̺�","M:\\TEST_DEV");	
		line=line.replace("�׽�Ʈ��Ʈ","M:\\TEST_INT");	
		//System.out.println(org);
		//System.out.println(line);
		return line;
	}
}