package com.sc.cc.bean;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kicco.util.Common;
import com.kicco.util.KiccoLogger;
import com.kicco.util.StringUtil;



public class CCMgr{
	WindowsCommander winCommander;
	
	public static final int FOR_PRE_CHECK_OUT=1;//���� �ϰ��� �ϴ� ������Ʈ�� ������ üũ�ƿ��Ǿ� �ִ��� Ȯ���ϴ� ��ȸ �����׽�Ʈ
	public static final int FOR_COUNTING_HIGH=2;//���� ��Ʈ���� ������Ʈ ���� ī������ ���� ü������ ��ȸ
	public static final int FOR_COUNTING_LOW=3;//���� ��Ʈ���� �ٸ���Ʈ ���� ī���������� ü������ ��ȸ
	public static final int FOR_XML_CHNAGESET=4;//xml�� ����� ���� �뵵�� ü������ ��ȸ
	
	public CCMgr(){
		winCommander=new WindowsCommander();
	}
	
	public static void main(String args[]) throws Exception{
	
		String 	activityName=args[0];//import testact1
		String pVobName=args[1];
		String viewDir=args[2];;//MainFrame


		CCMgr ccMgr=new CCMgr();

		ArrayList<ChangeSetItem> items=ccMgr.getAllChangeSet(activityName, pVobName, viewDir, CCMgr.FOR_XML_CHNAGESET);
		for(int ii=0;ii<items.size();ii++){
			ChangeSetItem item=items.get(ii);
			System.out.println(item);
		}
		//File toDir=new File("elements");
		//toDir.mkdir();
		ccMgr.versionItemCopy(items, "elements", viewDir);
		
		ArrayList<String>resultList=new ArrayList<String>();
		for(int ii=0;ii<items.size();ii++){
			ChangeSetItem item=items.get(ii);
			resultList.add(item.getItemName());
		}
		Iterator<String> utItr=resultList.iterator();
//		
//		String deleteTarget=args[0];
//		CCMgr ccMgr=new CCMgr();
//		ccMgr.deleteContribFile(deleteTarget,true);
		
//		//1.�ش��Ƽ��Ƽ���� ���ϸ� ��������
//		Iterator<String> itr=ccMgr.getChangeSetFiles(activityName, pVobName,viewDir);
//
//		while(itr.hasNext()){
//			String str=(String)itr.next();
//			System.out.println("--���ϸ� "+str);
//			
//		}
//		//2.������Ʈ
//		ccMgr.cofigItemUpdate(itr);
//		
//		//3.����
//		ccMgr.merge(activityName,pVobName,viewDir);
		
	
	}

	/**
	 * 
	 * @param fromActivityName
	 * @param toActivityName
	 * @param pVobName
	 * @param viewDir
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ChangeSetItem> checkedMerge(String fromActivity, String toActivity, String pVobName, String fromViewDir,String toViewDir
							,boolean isRebuild,ArrayList<ChangeSetItem> xmlChangeSetList) throws Exception{
		System.out.println(fromActivity+"  "+toActivity);
		System.out.println(fromViewDir+"   "+toViewDir);
		System.out.println(isRebuild+"   "+isRebuild+"-->FALSE�� ó��");
		
		//����� ���� ������� ����
		isRebuild=false;
		//
		System.out.println("\n===============������ϰ�� XML CHANESET");
		for(int ii=0;isRebuild && ii<xmlChangeSetList.size();ii++){
			System.out.println("xmlChangeSetList:"+xmlChangeSetList.get(ii).toString());
		}
		
		
		//ArrayList<ChangeSetItem> xmlChangeSetList=null;
		//if(isRebuild)xmlChangeSetList=new ArrayList<ChangeSetItem>();
		
		
		
		//formViewDir���� fromActivity�� ChangeSet�������
		//chageset�̸�:F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR@@\main\MF_PR\MF_UT\1\TBDAEM2.PL1\main\MF_SIT
		//����:2
		System.out.println("\n===============formViewDir���� fromActivity�� ChangeSet�������");
		ArrayList<ChangeSetItem> forChkoutList=this.getAllChangeSet(fromActivity,pVobName,fromViewDir,CCMgr.FOR_PRE_CHECK_OUT);
		if(isRebuild) forChkoutList=xmlChangeSetList;
		for(int ii=0;ii<forChkoutList.size();ii++){
			System.out.println("fromChangeSets:"+forChkoutList.get(ii).toString());
		}
		
		
		//ChangeSet�� ����, ���Ϻ��� ���� ��Ʈ������ ���� üũ�ƿ� �� �� �ִ� �� Ȯ��
		System.out.println("\n===============intView���� ChangeSet�� ����, ���Ϻ��� ���� ��Ʈ������ ���� üũ�ƿ� �� �� �ִ� �� Ȯ��");
		ArrayList<ChangeSetItem> resultItems=new ArrayList<ChangeSetItem>();
		resultItems=this.isAnyChekOutItem(forChkoutList, fromViewDir,toViewDir);
		for(int ii=0;ii<resultItems.size();ii++){
			System.out.println("resultItems:"+resultItems.get(ii).toString());
		}
		//���� üũ�ƿ� �Ȱ��� ������ ����
		if(resultItems.size()>0) return resultItems;
		resultItems=new ArrayList<ChangeSetItem>();
		
		//���� ����
		System.out.println("\n===============���� ����");
		this.mergePerActivity(fromActivity, toActivity, pVobName, toViewDir);
		
		//fromActivity�� ���ϳ����� toActivity�� ���� ���� ��
		
		//���ϳ����� ����(������Ƽ��Ƽ)
		//changeSet�̸�:F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR@@\main\MF_PR\MF_UT\1\TBDAEM2.PL1\main\MF_SIT
		System.out.println("\n===============���ϳ����� ����(������Ƽ��Ƽ)");
		ArrayList<ChangeSetItem> fromChangeSets=this.getAllChangeSet(fromActivity,pVobName,fromViewDir,CCMgr.FOR_COUNTING_LOW);
		if(isRebuild) fromChangeSets=xmlChangeSetList;
		for(int ii=0;ii<fromChangeSets.size();ii++){
			System.out.println(fromChangeSets.get(ii).toString());
		}
		//���ϳ����� ����(���߾�Ƽ��Ƽ)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT_BUILD\CHECKEDOUT.13218 �ε�
		//.13218�� ����
		System.out.println("\n===============���ϳ����� ����(���߾�Ƽ��Ƽ)");
		ArrayList<ChangeSetItem> toChangeSets=this.getAllChangeSet(toActivity,pVobName,toViewDir,CCMgr.FOR_COUNTING_HIGH);
		for(int ii=0;ii<toChangeSets.size();ii++){
			System.out.println(toChangeSets.get(ii).toString());
		}
		
		//�� ��Ƽ��Ƽ�� ���� ���� ��
		//(fromActivity�������� toActivity�� ���� ���� Cross�� ���� merge�� �ȵȰ��̰ų�, ���� ���������� �ݿ��� �ȵ� ������)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT(����)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT_BUILD(����)
		System.out.println("\n===============�� ��Ƽ��Ƽ�� ���� ���� ��");
		ArrayList<ChangeSetItem>unBlockedList=new ArrayList<ChangeSetItem>();
		for(int ii=0;ii<fromChangeSets.size();ii++){
			//System.out.println(fromFileList);
			boolean isBlocked=true;
			
			ChangeSetItem fromFile=fromChangeSets.get(ii);			
			fromFile.code=ChangeSetItem.BLOKED_ITEM;			
			//���� ��Ƽ��Ƽ ������ ��� ���� ��Ƽ��Ƽ���� �߰ߵǾ�� �Ѵ�.
			//�߰ߵ��� ���� ������ blocking���Ѱ���.
			for(int jj=0;jj<toChangeSets.size();jj++){
				ChangeSetItem toFile=toChangeSets.get(jj);
				System.out.println(fromFile.changSetName+"-----"+toFile.changSetName);
				System.out.println(fromFile.getItemName()+"-----"+toFile.getItemName());
				if(fromFile.getItemName().equals(toFile.getItemName())){
					isBlocked=false;
					break;
				}
			}
			if(isBlocked) resultItems.add(fromFile);
			else{
				fromFile.code=ChangeSetItem.NORMAL;
				unBlockedList.add(fromFile);
			}
		}
		
		for(int ii=0;ii<resultItems.size();ii++){
			System.out.println("resultItems:"+resultItems.get(ii).toString());
		}
		for(int ii=0;ii<unBlockedList.size();ii++){
			System.out.println("unBlockedList:"+unBlockedList.get(ii).toString());
		}
		
		//������ ���� �ߴٸ�, ���� ��Ƽ��Ƽ�� üũ�ƿ��� ������ ������ üũ�� �ؾ� ��.
		/*System.out.println("\n===============���� ���� �ߴٸ�, ���� ��Ƽ��Ƽ�� üũ�ƿ��� ������ ������ üũ�� �ؾ� ��.");
		System.out.println("toFolderList.size"+toFolderList.size()+"_");
		for(int ii=0;ii<toFolderList.size();ii++){
			this.checkIn(toFolderList, toViewDir);
		}*/
		
		//üũ�ƿ� ���г��� Ȯ�� �� undoCheckout���� ����
		//�������Ʈ�� 0�̸� ����, 1�̻��̸� ���ŷ ���� ������ 1�̻����� �ǹ�
		//System.out.println("\n===============üũ�ƿ� ���г��� Ȯ�� �� undoCheckout���� ����");
		//System.out.println("resultItems.size"+resultItems.size()+"_");
		//if(resultItems.size()>0) {
		//	this.unDoChCheckOut(toActivity, toViewDir);
		//}
		
		return resultItems;
	}
	
	/**
	 * ���: ��Ƽ��Ƽ�� �ش��ϴ� ü�������� ���ϸ� �������<br>
	 * @deprecated
	 * @param activityName ��Ƽ��Ƽ�̸�
	 * @param pVobName PVOB�̸�
	 * @param viewDir  cleartool����� ����Ǵ� �� ���
	 * @return��Ƽ��Ƽ�� �ش��ϴ� ü������ ���� ����
	 * @throws Exception
	 */
	public Iterator<String> getChangeSetFiles(String activityName,String pVobName,String viewDir) throws Exception{
		//üũ�ƿ��Ȱ��� ������ ������ ���� �Ѵ�.....UT, SIT
		
		//testact1, MainFrame
	    /**
			activityName="activity:[CQ_ID]@\\[PVOB]";
			cleartool lsact -l activity_name@\PVOB_name
			cleartool desc -l activity:activity_name@pvob_name

	        activity "SCFB00000002"d
			  careate 2011-01-14T13:59:41+09:00 by Administrator.����@Pass
			  owner: PASS\Administrator
			  group: PASS\����
			  stream: SCFB_UT@\SCFB_PVOB
			  current view: SCFB_UT
			  title: SCFB00000001_���̵��ۼ�1
			  change set versions:
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\SCFB_UT\2
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\SCFB_UT\1
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\1\ReadMe.txt\main\SCFB_INT\SCFB_UT\2
			    M:\SCFB_UT\SCFB_CVOB\src\sysinfo.cpp@@\main\SCFB_INT\SCFB_UT\1
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\1\ReadMe.txt\main\SCFB_INT\SCFB_UT\1
			    clearquest record id: CQTDB00000033
  				clearquest record State: UT�۾���
	       */
		
		Iterator resultItr=null;
		String cmd="cleartool desc -l activity:"+activityName+"@\\"+pVobName;
		KiccoLogger.log(Level.INFO, "CMD ="+ cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal != 0 ) throw new Exception("��ɼ����� �����߽��ϴ�. -"+cmd);
		ArrayList<String> arrList=(ArrayList<String>) cmdResultMap.get("OUTPUT");
		
		try{
			ArrayList<String> resultList=new ArrayList<String>();

			Iterator<String> itr = arrList.iterator();
			boolean startFlag=false;
			
			while(itr.hasNext()){
				String line=StringUtil.ltrim(StringUtil.rtrim((String)itr.next()));
				
				if(Common.isAnyMatchStr(line, "change set versions:")){
	        		startFlag=true;
	        		continue;
	        	}
				if(Common.isAnyMatchStr(line, "clearquest record id")){
	        		break;
	        	}
	        	if(startFlag){
	        		
	        		//�ش� ��Ƽ��Ƽ�� �Ѱ��̶� üũ�ƿ��Ȱ��� ������ out
	        		if(Common.isAnyMatchStr(line, "CHECKEDOUT"))
	        			return null;
	        		
	        		String tempStr=Common.getString(line, "@@\\main", true);
	        		if(!this.isDirCofigItem(tempStr,viewDir)){//���丮�� ����
	        			resultList.add(tempStr);
	        			KiccoLogger.getLogger().log(Level.INFO,"ü������:"+tempStr);
	        		}
	        	}

			}
			
			//�ֽ����ϸ� ȹ���� ���� �ߺ� ���ϸ� ����
			ArrayList<String> resultArrayList = new ArrayList<String>(); 
			HashSet<String> hs = new HashSet<String>(resultList);
			resultItr=hs.iterator();
			
			
			
	     } catch (Exception oe){
	            oe.printStackTrace();  
	            throw oe;
	     }

		return resultItr;
		
		//D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1@@\main\MF_PR\CHECKEDOUT.1969
		//D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBGM32.PL1@@\main\MF_PR\CHECKEDOUT.1968
		//D:\Views\MF_PR_view\API\HKE1\New Text Document.txt@@\main\MF_PR\1
	}
	
	/**
	 * ����/�������ο� �������, �ߺ����ο� ������� ��� ü�������� ����
	 * @param activityName
	 * @param pVobName
	 * @param viewDir
	 * @return
	 * @throws Exception
	 */

	public ArrayList<ChangeSetItem> getAllChangeSet(String activityName,String pVobName,String viewDir,int forInt) throws Exception{
		//üũ�ƿ��Ȱ��� ������ ������ ���� �Ѵ�.....UT, SIT
		ArrayList<ChangeSetItem> distinctList=new ArrayList<ChangeSetItem>();
		ArrayList<ChangeSetItem> versionedDList=new ArrayList<ChangeSetItem>();
		
		String cmd="cleartool desc -l activity:"+activityName+"@\\"+pVobName;
		System.out.println("CMD ="+ cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal != 0 ) throw new Exception("��ɼ����� �����߽��ϴ�. -"+cmd);
		ArrayList<String> arrList=(ArrayList<String>) cmdResultMap.get("OUTPUT");
		ArrayList<ChangeSetItem> resultList=new ArrayList<ChangeSetItem>();
		
		try{
			
			Iterator<String> itr = arrList.iterator();
			boolean startFlag=false;
			
			while(itr.hasNext()){
				String line=StringUtil.ltrim(StringUtil.rtrim((String)itr.next()));
				
				if(Common.isAnyMatchStr(line, "change set versions:")){
	        		startFlag=true;
	        		continue;
	        	}
				if(Common.isAnyMatchStr(line, "clearquest record id")){
	        		break;
	        	}
				
	        	if(startFlag){
	        		//�ش� ��Ƽ��Ƽ�� �Ѱ��̶� üũ�ƿ��Ȱ��� ������ out
	        		if( (forInt==CCMgr.FOR_PRE_CHECK_OUT 
	        				|| forInt==CCMgr.FOR_COUNTING_LOW
	        				|| forInt==CCMgr.FOR_XML_CHNAGESET)
	        				&& Common.isAnyMatchStr(line, "CHECKEDOUT"))
	        			throw new Exception("�ش� ��Ʈ���� üũ�ƿ��� ���� �ֽ��ϴ�.");
	        		
	        		String tempStr="";

	        		ChangeSetItem item=new ChangeSetItem();
	        		item.changSetName=line;
	        		
	        		if(forInt==CCMgr.FOR_XML_CHNAGESET){		        		
		        		tempStr=Common.getString(line, "@@\\main", true);
		        		if(!this.isDirCofigItem(tempStr,viewDir)){//���丮�� ����
		        			item=new ChangeSetItem();
		        			item.changSetName=line;
		        			item.setDistinctPivot(tempStr);
		        			item.version=line.substring(line.lastIndexOf("\\")+1);
		        			
		        			resultList.add(item);
		        		}
		        		
	        		//����üũ�ƿ��Ȱ��� �����ϱ����� ü������ ��ȸ
	        		}else if(forInt==CCMgr.FOR_PRE_CHECK_OUT){
	        			item.setDistinctPivot(item.getParsedElementName());
	        			
		        		String tempVer;
		        		if(Common.isAnyMatchStr(line, "CHECKEDOUT"))
		        			tempVer="";
		        		else
		        			tempVer=line.substring(line.lastIndexOf("\\")+1);
		        		item.version=tempVer;
		        		item.code=ChangeSetItem.NORMAL;
		        		
	        			resultList.add(item);	        			
	        		
	        		//���� �񱳽� ���� ��Ʈ���� ü������ ��ȸ
	        		}else if(forInt==CCMgr.FOR_COUNTING_HIGH){
	        			//ù�� �����õ��ÿ��� ��ü ������Ʈ ��� üũ�ƿ� ����, 
	        			//������� �����õ��ô� ���� üũ�εȰ� ���� ��ȸ
	        			if(Common.isAnyMatchStr(line, "CHECKEDOUT")){
	        				item.setDistinctPivot(item.getShortParseElementName());
	        				
	    	        		String tempVer;
	    	        		if(Common.isAnyMatchStr(line, "CHECKEDOUT"))
	    	        			tempVer="";
	    	        		else
	    	        			tempVer=line.substring(line.lastIndexOf("\\")+1);
	    	        		item.version=tempVer;
	    	        		item.code=ChangeSetItem.NORMAL;
	    	        		
	            			resultList.add(item);
	        			}
	        		//���� �񱳽� ������Ʈ���� ü������ ��ȸ
	        		}else if(forInt==CCMgr.FOR_COUNTING_LOW){
	        			item.setDistinctPivot(item.getShortParseElementName());
	        			
		        		String tempVer;
		        		if(Common.isAnyMatchStr(line, "CHECKEDOUT"))
		        			tempVer="";
		        		else
		        			tempVer=line.substring(line.lastIndexOf("\\")+1);
		        		item.version=tempVer;
		        		item.code=ChangeSetItem.NORMAL;
		        		
	        			resultList.add(item);
	        		}
	        	}
			}
				
			//�ֽ����ϸ� ȹ���� ���� �ߺ� ���ϸ� ����
			HashSet<ChangeSetItem> hs = new HashSet<ChangeSetItem>(resultList);
			Iterator<ChangeSetItem> resultItr=hs.iterator();
			
			
			while(resultItr.hasNext()){
				distinctList.add(resultItr.next());
			}
			
			if(forInt!=CCMgr.FOR_XML_CHNAGESET){
				return distinctList;
				
			//XMLü��������ȸ�� ���ؼ��� �������� ������ �ʿ�	
			}else{
				//distinc���� loop�� ���� ������ ũ�� ū ������ ����(final list�� result list������)
				for(int ii=0;ii<distinctList.size();ii++){
					ChangeSetItem finalItem=distinctList.get(ii);
					
					for(int jj=0;jj<resultList.size();jj++){
						ChangeSetItem listItem=resultList.get(jj);
						//final�� list�� �̸��� �����ǿ� ���ؼ� ���� ��, ������ ū�� �� ����
						if(finalItem.changSetName.equals(listItem.changSetName)){
							if(Integer.parseInt(listItem.version)>Integer.parseInt(finalItem.version)){
								finalItem.version=listItem.version;
							}
							versionedDList.add(finalItem);
						}
					}
				}
				return versionedDList;
			}
	     } catch (Exception oe){
	            oe.printStackTrace();  
	            throw oe;
	     }

	}
	
	/**
	 * ���:�ش� View��ġ���� ���� ��ġ�� �ִ� ���� ��Ƽ��Ƽ�̸��� changeset���� ���� view�� ��Ʈ������ merge�ϴ� ���
	 *          merge�� checkin���� ���� �����ϸ�, �ϳ��� ���н� exeption�߻� ��Ŵ
	 *          ���߹����� ������Ʈ���� �������� ���� ������ ������Ʈ���� ������ �õ��� ��� exitval=0�� output,errput��� ����
	 * @param activityName ��Ƽ��Ƽ�̸�
	 * @param pVobName PVOB�̸�
	 * @param mergeViewDir  cleartool����� ����Ǵ� �� ���
	 * @return 0:��������, 1:��������, 2:���������� ���߹������� ���߿� �����õ�
	 * @throws Exception
	 */
	public int mergePerActivity(String fromActivity,String toActivity ,String pVobName, String mergeViewDir) throws Exception{
			String setActivityCmd,mergeCmd=null;
			Map<Object, Object> cmdResultMap1=null;
			int resultCode;
		
			try{
				String mkActivityCmd="cleartool mkact "+toActivity;//to act
				setActivityCmd="cleartool setact "+toActivity;//to act
				mergeCmd="cleartool findmerge activity:"+fromActivity+"@\\"+pVobName+" -fcsets -nc -merge";//from act
				
				//1.set activity ����
				//System.out.println("MKACT CMD:"+mkActivityCmd);
				//this.winCommander.execCommandWithReturn(mkActivityCmd,mergeViewDir);
				System.out.println("SET ACTIVITY CMD ="+ setActivityCmd);
				this.winCommander.execCommandWithReturn(setActivityCmd,mergeViewDir);
				
				//2.merge����(activity������)
				System.out.println("MERGE CMD ="+ mergeCmd);
				cmdResultMap1=this.winCommander.execCommandWithReturn(mergeCmd,mergeViewDir);
				int exitVal1=(Integer) cmdResultMap1.get("EXIT_VAL");
				if(exitVal1==0){
					ArrayList outputList=(ArrayList)cmdResultMap1.get("OUTPUT");
					if(outputList.size()==0) resultCode=2;//���������� ���߿� ���� �õ��ϴ� ���
					else resultCode=0;
				}else{
					resultCode=1;		
				}
				return resultCode;
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}

		/*
		 * OUTPUT>Needs Merge "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP" [(automatic) to \main\MF_PR\1 from \main\MF_PR\MF_SIT\1 (base also \main\MF_PR\1)]
			OUTPUT>Created branch "MF_SIT_BUILD" from "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP" version "\main\MF_PR\1".
			OUTPUT>Making dir "AP2\HKE2\SORCB2HP".
			OUTPUT>Checked out "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP" from version "\main\MF_PR\MF_SIT_BUILD\0".
			OUTPUT>  Attached activity:
			OUTPUT>    activity:merge_test_build@\MainFrame  "merge_test_build"
			OUTPUT>********************************
			OUTPUT><<< directory 1: D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP@@\main\MF_PR\1
			OUTPUT>>>> directory 2: D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP@@\main\MF_PR\MF_SIT\1
			OUTPUT>>>> directory 3: D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP
			OUTPUT>********************************
			OUTPUT>-----------[ directory 1 ]-------------|---------[ added directory 2 ]---------
			OUTPUT>                                      -| mergetest.txt  --02-21T20:46 dooli
			OUTPUT>*** Automatic: Applying ADDITION from directory 2
			OUTPUT>Loading "AP2\HKE2\SORCB2HP\mergetest.txt" (0 bytes).
			OUTPUT>Recorded merge of "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP".
			OUTPUT>Needs Merge "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" [(automatic) to \main\MF_PR\1 from \main\MF_PR\MF_SIT\1 (base also \main\MF_PR\1)]
			OUTPUT>Created branch "MF_SIT_BUILD" from "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" version "\main\MF_PR\1".
			OUTPUT>Checked out "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" from version "\main\MF_PR\MF_SIT_BUILD\0".
			OUTPUT>  Attached activity:
			OUTPUT>    activity:merge_test_build@\MainFrame  "merge_test_build"
			OUTPUT>Trivial merge: "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" is same as base "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1@@\main\MF_PR\1".
			OUTPUT>Copying "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1@@\main\MF_PR\MF_SIT\1" to output file.
			OUTPUT>Moved contributor "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" to "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1.contrib".
			OUTPUT>Output of merge is in "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1".
			OUTPUT>Recorded merge of "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1".
		 */
		/*
		 * Checked in "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP\SBGD061.PL1" version "\main\MF_PR\MF_SIT_BUILD\1".
			  Attached activity:
			    activity:merge_test_build@\MainFrame  "merge_test_build"
			Making dir "AP2\HKE2\SORCB2HP".
			Checked in "D:\Views\MF_SIT_BUILD_view\AP2\HKE2\SORCB2HP" version "\main\MF_PR\MF_SIT_BUILD\1".
			  Attached activity:
			    activity:merge_test_build@\MainFrame  "merge_test_build"
			Making dir "AP2\HKE2".
			Checked in "D:\Views\MF_SIT_BUILD_view\AP2\HKE2" version "\main\MF_PR\MF_SIT_BUILD\1".
			  Attached activity:
			    activity:merge_test_build@\MainFrame  "merge_test_build"
		 */
	}
	
	/**
		 * �ش� ItemName�� targetViewDir���� checkout�Ǿ� �ִ��� Ȯ����. �̶� ItemName�� ������ ��� �ش� ������ �Ϻ� ������ üũ�ƿ��Ǿ�
		 * �ִ��� Ȯ���Ͽ� üũ�ƿ��Ǿ� �ִ� item�� �̸��� ����
		 * @param itemName
		 * @param toViewDir
		 * @return
		 * @throws Exception
		 */
		public ArrayList<ChangeSetItem> isAnyChekOutItem(ArrayList<ChangeSetItem> items,String fromViewDir, String toViewDir) throws Exception{
			System.out.println("isAnyChekOutItem"+toViewDir);
			/**
			 * M:5\TEST_INT\TEST_C\AP2\HKE2\SORCB2HP>cleartool lsco M:\TEST_INT\TEST_C\API\HKF5\SORC22RP\SOCRS02.PL1
			 * 
	--03-18T17:16  buildforge checkout version "M:\TEST_INT\TEST_C\API\HKF5\SORC22RP\SOCRS02.PL1" from \main\TEST_INT\2 (reserved)
	  Attached activity:
	    activity:activity110312.140353@\TEST  "999"
	
	����:M:\TEST_INT\TEST_C\API\HKF5\SORC22RP\SOCRS02.PL1
			 */
			
			ArrayList<ChangeSetItem> chekOutItems=new ArrayList<ChangeSetItem>();
			String isCheckOutCmd=null;
			Map<Object, Object> cmdResultMap=null;
		
			try{
				for(int ii=0;ii<items.size();ii++){
					String parsedName=items.get(ii).getParsedElementName();
					//���� ��Ʈ������ ��ȸ�� �������� ���� ��Ʈ���� üũ�ƿ��Ǿ� �մ����� Ȯ�� �� ����� �ձ��������� Ȯ��
					//�׸��� ������Ʈ�����̸��� ������Ʈ�� �� �̸����� �ٲ� �� ��ȸ
					parsedName=parsedName.replace(fromViewDir, toViewDir);
					/** -r�ɼ� ����***/
					isCheckOutCmd="cleartool lsco -d -short -cview -me \""+parsedName+"\"";//to act
				
					//lsco����� ���� �� �� �ȿ� checkout���� ������ �־�� �� chckout�� �������� ����
					System.out.println("IS ANY CHEKCOUT ="+ isCheckOutCmd);
					cmdResultMap=this.winCommander.execCommandWithReturn(isCheckOutCmd,toViewDir);
					int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
					ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
					ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
					if(exitVal!=0){
						for (int kk=0;kk<errList.size();kk++){
						}
						//cleartool: Error: Pathname not found: "M:\TEST_INT\TEST_C\AP2\HKE2\SORCB2HP\MyFolder".
						//�ű��̰ų� ������ ������ ����.
					}else{
						if(outList.size()==0){
							//�α׾���=üũ�ƿ��� �� ����
							
						
						}else if(outList.size()==1){
							String line=(String)outList.get(0);
							
							//�αװ� �����̸�, üũ�ƿ����ϰ��� �Է°��� �����ϸ� üũ�ƿ�����
							//���丮 ���� üũ�ƿ������� ��µǾ��� ���� ����
							if(line.equals(parsedName)){								
								ChangeSetItem item=new ChangeSetItem();
								item.changSetName=line;
								item.code=ChangeSetItem.ALEADY_CHECK_OUT;
								chekOutItems.add(item);
							}else{
								// do nothing
							}
						}
						
						//üũ�ƿ��� �����̸� ���� ���, �����ɼ��̸� ���������� üũ�ƿ��Ǿ��ִ� ���� ��� �����. 
						/*for(int jj=0;jj<outList.size();jj++){
							
							String line=(String)outList.get(jj);
							ChangeSetItem item=new ChangeSetItem();
							item.changSetName=line;
							item.code=ChangeSetItem.ALEADY_CHECK_OUT;
							chekOutItems.add(item);
						}*/
					}
				}
				
			}catch(Exception e){
				throw e;
			}
				
			return chekOutItems;
		}

		/**
		 * 
		 * @param unDoChecksList
		 * @param mergeViewDir
		 * @param toActivity
		 * @param pVobName
		 * @return undo checkout�� ����Ʈ
		 * @throws Exception
		 */
		public int  mergeCheckIn(ArrayList<String>unDoChecksList,String mergeViewDir,String toActivity,String pVobName) throws Exception{
			String unDoCheckout;
			String checkInCmd="cleartool ci -nc -identical activity:"+toActivity+"@\\"+pVobName;
		
			Map<Object, Object> cmdResultMap;
			int unDoCount=0;
			try{
				
				KiccoLogger.log(Level.INFO, "UNDO CHECKOUT �Ǽ� ="+ unDoChecksList.size());
				//������ ������ �Ǹ�  undo checkout
				for(int ii=0;ii<unDoChecksList.size();ii++){
					String fileName=(String)unDoChecksList.get(ii);
					unDoCheckout="cleartool unco -rm \""+mergeViewDir+"\\"+fileName+"\"";//mergeview dir=to view
					KiccoLogger.log(Level.INFO, "UNDO CHECKOUT CMD ="+ unDoCheckout);
					this.winCommander.execCommandWithReturn(unDoCheckout,mergeViewDir);
					unDoCount++;
				}			
				//������(������ �����Ѱ�) üũ��(��Ƽ��Ƽ ������)
				KiccoLogger.log(Level.INFO, "CHECK IN CMD ="+ checkInCmd);
				cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,mergeViewDir);
				int exitVal1=(Integer) cmdResultMap.get("EXIT_VAL");
				if(exitVal1!=0) throw new Exception("üũ�� ����:\n[������] "+checkInCmd);
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			return unDoCount;

		}	
		
	/**
	 * 
	 * @param unDoChecksList
	 * @param mergeViewDir
	 * @param toActivity
	 * @param pVobName
	 * @return undo checkout�� ����Ʈ
	 * @throws Exception
	 */
	public void  mergeCheckIn(String mergeViewDir,String toActivity,String pVobName) throws Exception{

		String checkInCmd="cleartool ci -nc -identical activity:"+toActivity+"@\\"+pVobName;
		Map<Object, Object> cmdResultMap;
		try{
			/********************************
			//������ ������ �Ǹ�  undo checkout
			for(int ii=0;ii<unDoChecksList.size();ii++){
				String fileName=(String)unDoChecksList.get(ii);
				unDoCheckout="cleartool unco -rm \""+mergeViewDir+"\\"+fileName+"\"";//mergeview dir=to view
				KiccoLogger.log(Level.INFO, "UNDO CHECKOUT CMD ="+ unDoCheckout);
				this.winCommander.execCommandWithReturn(unDoCheckout,mergeViewDir);
				unDoCount++;
			}			
			//������(������ �����Ѱ�) üũ��(��Ƽ��Ƽ ������)
			 * ***********************************/
			
			KiccoLogger.log(Level.INFO, "CHECK IN CMD ="+ checkInCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,mergeViewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal!=0) throw new Exception("üũ�� ����:\n[������] "+checkInCmd);
		}catch(Exception e){
			throw e;
		}
	}	
	
	/**
	 * @deprecated
	 * @param toActivity
	 * @param viewDir
	 * @throws Exception
	 */
	public void unDoChCheckOut(String toActivity, String viewDir) throws Exception{
		Map cmdResultMap;
		String unDoCheckOutCmd;
		String setActivityCmd;
		
		try{
			//String mkActivityCmd="cleartool mkact "+toActivity;//to act
			//setActivityCmd="cleartool setact "+toActivity;//to act
			unDoCheckOutCmd="cleartool unco -rm -cact ";//from act
			
			//1.set activity ����
			//System.out.println("MKACT CMD:"+mkActivityCmd);
			//this.winCommander.execCommandWithReturn(mkActivityCmd,mergeViewDir);
			//KiccoLogger.getLogger().log(Level.INFO,"SET ACTIVITY CMD ="+ setActivityCmd);
			//this.winCommander.execCommandWithReturn(setActivityCmd,viewDir);
			
			//
			cmdResultMap=this.winCommander.execCommandWithReturn(unDoCheckOutCmd,viewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			if(exitVal!=0) throw new Exception("UNDO CHECKOUT ���� ["+toActivity+"]");
	
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param items
	 * @param viewDir
	 * @throws Exception
	 */
	public void checkIn(ArrayList<ChangeSetItem> items, String viewDir) throws Exception{
		Map cmdResultMap;
		String unDoCheckOut;
		try{
			for(int ii=0;ii<items.size();ii++){
				ChangeSetItem item=items.get(ii);
				unDoCheckOut="cleartool ci  \""+viewDir+"\\"+Common.getString(item.changSetName,"@@\\main",true)+"\"";//mergeview dir=to view
				System.out.println("CHECK IN ="+ unDoCheckOut);
				
				cmdResultMap=this.winCommander.execCommandWithReturn(unDoCheckOut,viewDir);
				int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
				ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
				ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
				if(exitVal!=0) throw new Exception();
				else{
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public int  checkInItem(String activitNo,String viewDir,String pVobName) throws Exception{
		String checkInCmd="cleartool ci -nc -identical activity:"+activitNo+"@\\"+pVobName;
	
		Map<Object, Object> cmdResultMap;
		int unDoCount=0;
		try{
		
			//������(������ �����Ѱ�) üũ��(��Ƽ��Ƽ ������)
			KiccoLogger.log(Level.INFO, "[üũ�θ��]:"+ checkInCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,viewDir);
			int exitVal1=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal1!=0) throw new Exception("üũ�� ����:\n[������] "+checkInCmd);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return unDoCount;

	}	
	/**
	 * �����׸񺰷� üũ�ƿ�
	 * @param itemName
	 * @param activityNo
	 * @param viewDir
	 * @throws Exception
	 */
	public void setActivity(String activityNo, String viewDir) throws Exception{
		Map cmdResultMap;
		String unDoCheckOut;
		
		String setActivityCmd;
		
		try{
			setActivityCmd="cleartool setact "+activityNo;//to act
			KiccoLogger.getLogger().log(Level.INFO,"setActivityCmd:"+setActivityCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(setActivityCmd,viewDir);
			
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			KiccoLogger.getLogger().log(Level.INFO,"setActivityCmd result:["+exitVal+"]");

			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
				//üũ�ƿ� ���� �� �����߻�
			if(exitVal!=0) {
				throw new Exception();
			}else{
				
			}

		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * �����׸񺰷� üũ�ƿ�(aleady checkout�� ��ŵ��. ȣ��� ���ǹٶ�)
	 * @param itemName
	 * @param activityNo
	 * @param viewDir
	 * @throws Exception
	 */
	public void checkOutItem(String itemName,String activityNo, String viewDir) throws Exception{
		Map cmdResultMap;
		String unDoCheckOut;
		
		String checkOutCmd;
		
		try{
			checkOutCmd="cleartool co -nc "+itemName;//mergeview dir=to view
			KiccoLogger.getLogger().log(Level.INFO,"[üũ�ƿ����]:"+checkOutCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkOutCmd,viewDir);
			
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			
			//üũ�ƿ� ���� �� �����߻�
			if(exitVal!=0) {
				if(Common.isAnyMatchStr((String)errList.get(0), "is already checked out")){
					//do noting-�������� �����Ѵ�.(
				}else{
					//undocheckout����
					this.unDoChCheckOut(activityNo, viewDir);
					throw new Exception();
				}
			}else{
				
			}

		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @param itemName
	 * @param activityNo
	 * @param viewDir
	 * @throws Exception
	 */
	public void mkElement(String itemName,String viewDir) throws Exception{
		Map cmdResultMap;
		String unDoCheckOut;
		
		String checkOutCmd;
		
		try{
			checkOutCmd="cleartool mkelem -nc -mkpath \""+itemName+"\"";//mergeview dir=to view
			KiccoLogger.log(Level.INFO, "[�űԹ����������]:"+ checkOutCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkOutCmd,viewDir);
			
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			
			//üũ�ƿ� ���� �� �����߻�
			if(exitVal!=0) {
				throw new Exception();
			}else{
				
			}

		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public int versionItemCopy(ArrayList<ChangeSetItem> items,String elementsRootPath, String viewDir) throws Exception{
		//��������ī���ϱ�
		//cleartool get -to f:\aaa\SBGD073.PL1 M:\MF_UT_view\AP2\HKE2\SORCB2HP\SBGD073.PL1@@\main\MF_PR\MF_SIT\MF_UT\2
		String copyCmd=null;
		Map<Object, Object> cmdResultMap=null;
		int exitVal;
		int resultCode=0;
		
		String changeSetName;
		String fileFullPath;
		String folderPath;

		
		try{	

			for(int ii=0;ii<items.size();ii++){
				ChangeSetItem item=items.get(ii);
				
				fileFullPath=elementsRootPath+Common.getString(item.getShortParseElementName(), viewDir, false);
				folderPath=fileFullPath.substring(0,fileFullPath.lastIndexOf("\\"));
				
				changeSetName=item.changSetName;
				
				copyCmd="cleartool get -to "+fileFullPath+" "+changeSetName;
				
				System.out.println("���ú��� �������:"+folderPath);
				File targetFolder=new File(folderPath);
				targetFolder.mkdirs();
				
				//COPY��� ����
				File targetFile=new File(fileFullPath);
				System.out.println("���ú��� �������:"+fileFullPath);

				if(targetFile.exists()) targetFile.delete();//���� ���� ������ �������� ������ cleeatool���� err�߻�
				System.out.println("COPY CMD ="+ copyCmd);
				cmdResultMap=this.winCommander.execCommandWithReturn(copyCmd,viewDir);
				exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
				
				if(exitVal==0){
					ArrayList outputList=(ArrayList)cmdResultMap.get("OUTPUT");
	
				}else{
					throw new Exception("CC�� ������ ���÷� �����ϴ� �� ������ �߻��߽��ϴ�.");
					//resultCode=-1;	
					//break;
				}

			}
		}catch(Exception e){
			throw e;
		}
		return resultCode;
			
	}
	
	/**
	 * ���: �����׸��� ���丮���� �˻�
	 * @param configItemPath ����ġ ���� ��� �����׸�� -�� D:\Views\MF_PR_view\AP2\HKE2\SORCB2HP
	 * @param viewDir  cleartool����� ����Ǵ� �� ���
	
	 * @return ���丮�̸� true, �ƴϸ� false
	 * @throws Exception
	 */
	public boolean isDirCofigItem(String configItemPath,String viewDir) throws Exception{
		//cleartool file D:\Views\MF_PR_view\AP2\HKE2\SORCB2HP
		boolean result=false;
		String cmd="cleartool file \""+configItemPath+"\"";
		System.out.println(cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal==1) throw new Exception("��ɼ����� �����߽��ϴ�. -"+cmd);
		
		ArrayList<String> arrList=(ArrayList<String>)cmdResultMap.get("OUTPUT");
		while(arrList.iterator().hasNext()){
			String line=(String)arrList.iterator().next();
			if(Common.isAnyMatchStr(line, "directory"))
				result=true;
			else
				result=false;
			break;
		}
	    return result;
	}



		//	/**
	//	 * ���: �ش� �����׸��� ������Ʈ �Ѵ�.(������ ��)
	//	 * @param items ����ġ ���� �����׸�� -��: D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1
	//	 * @return ��������
	//	 */
	//	public boolean cofigItemUpdate(Iterator<String> items){
	//		//cleartool update D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1
	//		/*
	//		 * .
	//			Done loading "\API\HKE1\SORCB2HP\SBBKS52.PL1" (1 objects, copied 0 KB).
	//			Log has been written to "D:\Views\MF_PR_view\\update.2011-02-19T160021+0900.updt".
	//		 */
	//		String cmd=null;
	//		while(items.hasNext()){
	//			String fileName=(String)items.next();
	//			//cmd="cleartool update \""+fileName+"\"";
	//			//this.winCommander.execCommand(cmd);
	//			this.cofigItemUpdate(fileName);
	//		}
	//		return true;
	//	}
		/**
		 * ���: �ش� �����׸��� ������Ʈ �Ѵ�.(������ ��)
		 * @param items ����ġ ���� �����׸�� -��: D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1
		 * @return ��������
		 */
		public boolean cofigItemUpdate(String fileName, String viewDirectory ){
	
			String cmd=null;
	
			cmd="cleartool update \""+fileName+"\"";
			//KiccoLogger.getLogger().log(Level.INFO,"������Ʈ: "+cmd + "����丮:"+viewDirectory );
			try{
				Map<Object, Object> cmdResultMap = this.winCommander.execCommandWithReturn(cmd, viewDirectory);
				int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
				if(exitVal != 0 )
					throw new Exception("��ɼ����� �����߽��ϴ�. -"+cmd);
			}
			catch(Exception ex){
				String msg = "Exception Msg:"+ex.getMessage();
				KiccoLogger.getLogger().log(Level.SEVERE, msg);
				ex.printStackTrace();
			}
	
			return true;
		}



	/**
	 * CleaCase ��� ������ .contrib���� ����(view private�� ���ϸ�)
	 * @param targetDir ��� ����
	 * @param isRecursive ������������ ������ ó������
	 * @throws Exception
	 */
	public void deleteContribFile(String targetDir, boolean isRecursive) throws Exception{
		//System.out.println();
		//System.out.println("Target: "+targetDir);
		File dir=new File(targetDir);
		
		if(!dir.isDirectory()) throw new Exception("�ش���ġ�� ������ �ƴմϴ�.");
		
		//contrib���� ���� ����
		FileFilter myFilters=new FileFilter(){
			public boolean accept(File pathname) {
				String fileNames=pathname.getName();
				return (!pathname.isDirectory()) && Common.isAnyMatchStr(fileNames, "\\.contrib");

			}
			
		};
		
		//���� ���� ����
		FileFilter subDirFilter=new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
			
		};
		
		//contrib���� ����
		File[] contribs=dir.listFiles(myFilters);
		for(int ii=0;ii<contribs.length;ii++){
			//this.isViewPrivate(contribs[ii]);
			//System.out.println("delete "+contribs[ii].getName());
			contribs[ii].delete();
		}
		
		//���� ���� ��� ������ ����
		if(isRecursive){
			File[] subDirs=dir.listFiles(subDirFilter);
			for(int ii=0;ii<subDirs.length;ii++){
				//System.out.println(ii+ " subdir: "+subDirs[ii]);
				deleteContribFile(subDirs[ii].getAbsolutePath(),isRecursive);
			}
		}
		
	}
	
	/**
	 * 
	 * @param itemName
	 * @param viewDir
	 * @return
	 */
	public boolean isViewPrivate(String itemName,String viewDir){
		boolean isViewPrivate=false;
		String cmd=null;

		cmd="cleartool ls -d -view_only \""+itemName+"\"";
		KiccoLogger.getLogger().log(Level.INFO,"[�������̺�Ȯ�θ��]: "+cmd  );
		try{
			Map<Object, Object> cmdResultMap = this.winCommander.execCommandWithReturn(cmd, viewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal != 0 )
				throw new Exception("��ɼ����� �����߽��ϴ�. -"+cmd);
			ArrayList<String> arrList=(ArrayList<String>)cmdResultMap.get("OUTPUT");
			if(arrList.size()==0) isViewPrivate=false;
			else{
				if(Common.isAnyMatchStr((String)arrList.get(0), "CHECKEDOUT")) 
					isViewPrivate=false;//üũ�ƿ��� ����view private�� �ƴ����� ��.
				else// �ش� �����׸���� �����ų�, hijacked ������ ���
					isViewPrivate=true;
			}
			
			/**
			if(arrList.size()>0 && Common.isAnyMatchStr((String)arrList.get(0), itemName))
				isViewPrivate=true;	
			else
				isViewPrivate=false;
				**/
		}
		catch(Exception ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
		KiccoLogger.getLogger().log(Level.INFO, "�������̺�����:"+isViewPrivate);
		return isViewPrivate;
	
	}

}



/*
public static void main(String args[])throws Exception{
	Iterator resultItr=null;
	ArrayList<String> arrList=new ArrayList<String>();
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo1.cpp@@\\main\\SCFB_INT\\SCFB_UT\\1");
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo2.cpp@@\\main\\SCFB_INT\\SCFB_UT\\1");
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo3.cpp@@\\main\\SCFB_INT\\SCFB_UT\\1");
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo1.cpp@@\\main\\SCFB_INT\\SCFB_UT\\2");
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo2.cpp@@\\main\\SCFB_INT\\SCFB_UT\\2");
	arrList.add("M:\\SCFB_UT\\SCFB_CVOB\\src\\sysinfo3.cpp@@\\main\\SCFB_INT\\SCFB_UT\\2");
	
	try{
		ArrayList<DistinctByPath> resultList=new ArrayList<DistinctByPath>();

		Iterator<String> itr = arrList.iterator();
		boolean startFlag=false;
		
		while(itr.hasNext()){
			String line=StringUtil.ltrim(StringUtil.rtrim((String)itr.next()));
			String tempStr=Common.getString(line, "@@", true);
			
			DistinctByPath distinct=new DistinctByPath();
			distinct.fullPath=line;  distinct.path=tempStr;
			resultList.add(distinct);
		}
		
		//�ֽ����ϸ� ȹ���� ���� �ߺ� ���ϸ� ����
		ArrayList<DistinctByPath> resultArrayList = new ArrayList<DistinctByPath>(); 
		HashSet<DistinctByPath> hs = new HashSet<DistinctByPath>(resultList);
		resultItr=hs.iterator();
		while(resultItr.hasNext()){
			DistinctByPath di=(DistinctByPath)resultItr.next();
			System.out.println(di.path+"    "+di.fullPath);
		}
		
     } catch (Exception oe){
            oe.printStackTrace();  
            throw oe;
     }

}
*/
/*
class DistinctByPath {
	String path;
	String fullPath;
	int id;
	
	@Override
	public boolean equals(Object o) {
		boolean isEqual = false;
		if ((o instanceof DistinctByPath) && ((DistinctByPath)o).path.equals(path)) {
			System.out.println("o: "+((DistinctByPath)o).path+"   "+((DistinctByPath)o).fullPath);
			System.out.println("this :"+this.path+ "------"+this.fullPath);
			isEqual = false;
		}
		return isEqual;
	}
	

	 //Set �������̽����� �����ؾ� �ϴ� �޼���

	@Override
	public int hashCode() {
		// Joshua Bloch�� �ۼ����� ���
		int result = 17;
		result = 37 * result + path.hashCode();
		result = 37 + result + id;
		return result;
	}
	
}*/