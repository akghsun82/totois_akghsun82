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
	
	public static final int FOR_PRE_CHECK_OUT=1;//머지 하고자 하는 엘리먼트가 상위에 체크아웃되어 있는지 확인하는 조회 수정테스트
	public static final int FOR_COUNTING_HIGH=2;//상위 스트림의 엘리먼트 갯수 카운팅을 위한 체인지셋 조회
	public static final int FOR_COUNTING_LOW=3;//하위 스트림의 앨리먼트 갯수 카운팅을위한 체인지셋 조회
	public static final int FOR_XML_CHNAGESET=4;//xml을 만들기 위한 용도의 체인지셋 조회
	
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
		
//		//1.해당액티비티에서 파일만 가져오기
//		Iterator<String> itr=ccMgr.getChangeSetFiles(activityName, pVobName,viewDir);
//
//		while(itr.hasNext()){
//			String str=(String)itr.next();
//			System.out.println("--파일만 "+str);
//			
//		}
//		//2.업데이트
//		ccMgr.cofigItemUpdate(itr);
//		
//		//3.머지
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
		System.out.println(isRebuild+"   "+isRebuild+"-->FALSE로 처리");
		
		//재빌드 여부 고려하지 않음
		isRebuild=false;
		//
		System.out.println("\n===============재빌드일경우 XML CHANESET");
		for(int ii=0;isRebuild && ii<xmlChangeSetList.size();ii++){
			System.out.println("xmlChangeSetList:"+xmlChangeSetList.get(ii).toString());
		}
		
		
		//ArrayList<ChangeSetItem> xmlChangeSetList=null;
		//if(isRebuild)xmlChangeSetList=new ArrayList<ChangeSetItem>();
		
		
		
		//formViewDir에서 fromActivity의 ChangeSet갖고오기
		//chageset이름:F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR@@\main\MF_PR\MF_UT\1\TBDAEM2.PL1\main\MF_SIT
		//버전:2
		System.out.println("\n===============formViewDir에서 fromActivity의 ChangeSet갖고오기");
		ArrayList<ChangeSetItem> forChkoutList=this.getAllChangeSet(fromActivity,pVobName,fromViewDir,CCMgr.FOR_PRE_CHECK_OUT);
		if(isRebuild) forChkoutList=xmlChangeSetList;
		for(int ii=0;ii<forChkoutList.size();ii++){
			System.out.println("fromChangeSets:"+forChkoutList.get(ii).toString());
		}
		
		
		//ChangeSet의 폴더, 파일별로 상위 스트림에서 기존 체크아웃 된 건 있는 지 확인
		System.out.println("\n===============intView에서 ChangeSet의 폴더, 파일별로 상위 스트림에서 기존 체크아웃 된 건 있는 지 확인");
		ArrayList<ChangeSetItem> resultItems=new ArrayList<ChangeSetItem>();
		resultItems=this.isAnyChekOutItem(forChkoutList, fromViewDir,toViewDir);
		for(int ii=0;ii<resultItems.size();ii++){
			System.out.println("resultItems:"+resultItems.get(ii).toString());
		}
		//기존 체크아웃 된건이 있으면 리턴
		if(resultItems.size()>0) return resultItems;
		resultItems=new ArrayList<ChangeSetItem>();
		
		//머지 수행
		System.out.println("\n===============머지 수행");
		this.mergePerActivity(fromActivity, toActivity, pVobName, toViewDir);
		
		//fromActivity의 파일내역과 toActivity의 파일 내역 비교
		
		//파일내역만 추출(이전액티비티)
		//changeSet이름:F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR@@\main\MF_PR\MF_UT\1\TBDAEM2.PL1\main\MF_SIT
		System.out.println("\n===============파일내역만 추출(이전액티비티)");
		ArrayList<ChangeSetItem> fromChangeSets=this.getAllChangeSet(fromActivity,pVobName,fromViewDir,CCMgr.FOR_COUNTING_LOW);
		if(isRebuild) fromChangeSets=xmlChangeSetList;
		for(int ii=0;ii<fromChangeSets.size();ii++){
			System.out.println(fromChangeSets.get(ii).toString());
		}
		//파일내역만 추출(나중액티비티)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT_BUILD\CHECKEDOUT.13218 인데
		//.13218은 제외
		System.out.println("\n===============파일내역만 추출(나중액티비티)");
		ArrayList<ChangeSetItem> toChangeSets=this.getAllChangeSet(toActivity,pVobName,toViewDir,CCMgr.FOR_COUNTING_HIGH);
		for(int ii=0;ii<toChangeSets.size();ii++){
			System.out.println(toChangeSets.get(ii).toString());
		}
		
		//두 액티비티의 파일 내역 비교
		//(fromActivity기준으로 toActivity에 없는 건은 Cross로 인해 merge가 안된건이거나, 상위 폴더삭제로 반영이 안된 파일임)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT(이전)
		//F:\Views\MF_SIT_BUILD_view\YS\HKF5\SORCB2RR\TBDAEM2.PL1@@\main\MF_SIT_BUILD(나중)
		System.out.println("\n===============두 액티비티의 파일 내역 비교");
		ArrayList<ChangeSetItem>unBlockedList=new ArrayList<ChangeSetItem>();
		for(int ii=0;ii<fromChangeSets.size();ii++){
			//System.out.println(fromFileList);
			boolean isBlocked=true;
			
			ChangeSetItem fromFile=fromChangeSets.get(ii);			
			fromFile.code=ChangeSetItem.BLOKED_ITEM;			
			//이전 액티비티 파일이 모두 나중 액티비티에서 발견되어야 한다.
			//발견되지 않은 파일은 blocking당한것임.
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
		
		//머지를 수행 했다면, 현재 액티비티의 체크아웃된 폴더는 무조건 체크인 해야 함.
		/*System.out.println("\n===============지를 수행 했다면, 현재 액티비티의 체크아웃된 폴더는 무조건 체크인 해야 함.");
		System.out.println("toFolderList.size"+toFolderList.size()+"_");
		for(int ii=0;ii<toFolderList.size();ii++){
			this.checkIn(toFolderList, toViewDir);
		}*/
		
		//체크아웃 실패내역 확인 및 undoCheckout여부 결정
		//결과리스트가 0이면 성공, 1이상이면 블라킹 당한 파일이 1이상임을 의미
		//System.out.println("\n===============체크아웃 실패내역 확인 및 undoCheckout여부 결정");
		//System.out.println("resultItems.size"+resultItems.size()+"_");
		//if(resultItems.size()>0) {
		//	this.unDoChCheckOut(toActivity, toViewDir);
		//}
		
		return resultItems;
	}
	
	/**
	 * 기능: 액티비티에 해당하는 체인지셋중 파일만 갖고오기<br>
	 * @deprecated
	 * @param activityName 액티비티이름
	 * @param pVobName PVOB이름
	 * @param viewDir  cleartool명령이 수행되는 뷰 경로
	 * @return액티비티에 해당하는 체인지셋 파일 집합
	 * @throws Exception
	 */
	public Iterator<String> getChangeSetFiles(String activityName,String pVobName,String viewDir) throws Exception{
		//체크아웃된것이 있으면 에러를 내야 한다.....UT, SIT
		
		//testact1, MainFrame
	    /**
			activityName="activity:[CQ_ID]@\\[PVOB]";
			cleartool lsact -l activity_name@\PVOB_name
			cleartool desc -l activity:activity_name@pvob_name

	        activity "SCFB00000002"d
			  careate 2011-01-14T13:59:41+09:00 by Administrator.없음@Pass
			  owner: PASS\Administrator
			  group: PASS\없음
			  stream: SCFB_UT@\SCFB_PVOB
			  current view: SCFB_UT
			  title: SCFB00000001_가이드작성1
			  change set versions:
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\SCFB_UT\2
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\SCFB_UT\1
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\1\ReadMe.txt\main\SCFB_INT\SCFB_UT\2
			    M:\SCFB_UT\SCFB_CVOB\src\sysinfo.cpp@@\main\SCFB_INT\SCFB_UT\1
			    M:\SCFB_UT\SCFB_CVOB\src@@\main\SCFB_INT\1\ReadMe.txt\main\SCFB_INT\SCFB_UT\1
			    clearquest record id: CQTDB00000033
  				clearquest record State: UT작업중
	       */
		
		Iterator resultItr=null;
		String cmd="cleartool desc -l activity:"+activityName+"@\\"+pVobName;
		KiccoLogger.log(Level.INFO, "CMD ="+ cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal != 0 ) throw new Exception("명령수행중 실패했습니다. -"+cmd);
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
	        		
	        		//해당 액티비티내 한건이라도 체크아웃된건이 있으면 out
	        		if(Common.isAnyMatchStr(line, "CHECKEDOUT"))
	        			return null;
	        		
	        		String tempStr=Common.getString(line, "@@\\main", true);
	        		if(!this.isDirCofigItem(tempStr,viewDir)){//디렉토리는 제외
	        			resultList.add(tempStr);
	        			KiccoLogger.getLogger().log(Level.INFO,"체인지셋:"+tempStr);
	        		}
	        	}

			}
			
			//최신파일명 획득을 위해 중복 파일명 제거
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
	 * 파일/폴더여부에 상관없이, 중복여부에 상관없이 모든 체인지셋을 리턴
	 * @param activityName
	 * @param pVobName
	 * @param viewDir
	 * @return
	 * @throws Exception
	 */

	public ArrayList<ChangeSetItem> getAllChangeSet(String activityName,String pVobName,String viewDir,int forInt) throws Exception{
		//체크아웃된것이 있으면 에러를 내야 한다.....UT, SIT
		ArrayList<ChangeSetItem> distinctList=new ArrayList<ChangeSetItem>();
		ArrayList<ChangeSetItem> versionedDList=new ArrayList<ChangeSetItem>();
		
		String cmd="cleartool desc -l activity:"+activityName+"@\\"+pVobName;
		System.out.println("CMD ="+ cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal != 0 ) throw new Exception("명령수행중 실패했습니다. -"+cmd);
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
	        		//해당 액티비티내 한건이라도 체크아웃된건이 있으면 out
	        		if( (forInt==CCMgr.FOR_PRE_CHECK_OUT 
	        				|| forInt==CCMgr.FOR_COUNTING_LOW
	        				|| forInt==CCMgr.FOR_XML_CHNAGESET)
	        				&& Common.isAnyMatchStr(line, "CHECKEDOUT"))
	        			throw new Exception("해당 스트림에 체크아웃된 건이 있습니다.");
	        		
	        		String tempStr="";

	        		ChangeSetItem item=new ChangeSetItem();
	        		item.changSetName=line;
	        		
	        		if(forInt==CCMgr.FOR_XML_CHNAGESET){		        		
		        		tempStr=Common.getString(line, "@@\\main", true);
		        		if(!this.isDirCofigItem(tempStr,viewDir)){//디렉토리는 제외
		        			item=new ChangeSetItem();
		        			item.changSetName=line;
		        			item.setDistinctPivot(tempStr);
		        			item.version=line.substring(line.lastIndexOf("\\")+1);
		        			
		        			resultList.add(item);
		        		}
		        		
	        		//사전체크아웃된건을 점검하기위한 체인지셋 조회
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
	        		
	        		//머지 비교시 상위 스트림의 체인지셋 조회
	        		}else if(forInt==CCMgr.FOR_COUNTING_HIGH){
	        			//첫번 머지시도시에는 전체 엘리먼트 모두 체크아웃 상태, 
	        			//재빌드후 머지시도시는 기존 체크인된건 빼고 조회
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
	        		//머지 비교시 하위스트림의 체인지셋 조회
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
				
			//최신파일명 획득을 위해 중복 파일명 제거
			HashSet<ChangeSetItem> hs = new HashSet<ChangeSetItem>(resultList);
			Iterator<ChangeSetItem> resultItr=hs.iterator();
			
			
			while(resultItr.hasNext()){
				distinctList.add(resultItr.next());
			}
			
			if(forInt!=CCMgr.FOR_XML_CHNAGESET){
				return distinctList;
				
			//XML체인지셋조회를 위해서는 최종버전 세팅이 필요	
			}else{
				//distinc별로 loop를 돌아 버전이 크면 큰 버전을 세팅(final list와 result list버전비교)
				for(int ii=0;ii<distinctList.size();ii++){
					ChangeSetItem finalItem=distinctList.get(ii);
					
					for(int jj=0;jj<resultList.size();jj++){
						ChangeSetItem listItem=resultList.get(jj);
						//final과 list의 이름이 같은건에 대해서 버전 비교, 버전이 큰쪽 거 세팅
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
	 * 기능:해당 View위치에서 하위 위치에 있는 동일 액티비티이름의 changeset들을 현재 view의 스트림으로 merge하는 명령
	 *          merge후 checkin까지 같이 수행하며, 하나라도 실패시 exeption발생 시킴
	 *          나중버전이 상위스트림에 머지된후 이전 버전이 상위스트림에 머지를 시도할 경우 exitval=0에 output,errput모두 공백
	 * @param activityName 액티비티이름
	 * @param pVobName PVOB이름
	 * @param mergeViewDir  cleartool명령이 수행되는 뷰 경로
	 * @return 0:머지성공, 1:머지실패, 2:이전버전이 나중버전보다 나중에 머지시도
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
				
				//1.set activity 수행
				//System.out.println("MKACT CMD:"+mkActivityCmd);
				//this.winCommander.execCommandWithReturn(mkActivityCmd,mergeViewDir);
				System.out.println("SET ACTIVITY CMD ="+ setActivityCmd);
				this.winCommander.execCommandWithReturn(setActivityCmd,mergeViewDir);
				
				//2.merge수행(activity단위로)
				System.out.println("MERGE CMD ="+ mergeCmd);
				cmdResultMap1=this.winCommander.execCommandWithReturn(mergeCmd,mergeViewDir);
				int exitVal1=(Integer) cmdResultMap1.get("EXIT_VAL");
				if(exitVal1==0){
					ArrayList outputList=(ArrayList)cmdResultMap1.get("OUTPUT");
					if(outputList.size()==0) resultCode=2;//상위버전이 나중에 머지 시동하는 경우
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
		 * 해당 ItemName이 targetViewDir에서 checkout되어 있는지 확인함. 이때 ItemName이 폴더일 경우 해당 폴더의 하부 파일이 체크아웃되어
		 * 있느지 확인하여 체크아웃되어 있는 item의 이름을 리턴
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
	
	리턴:M:\TEST_INT\TEST_C\API\HKF5\SORC22RP\SOCRS02.PL1
			 */
			
			ArrayList<ChangeSetItem> chekOutItems=new ArrayList<ChangeSetItem>();
			String isCheckOutCmd=null;
			Map<Object, Object> cmdResultMap=null;
		
			try{
				for(int ii=0;ii<items.size();ii++){
					String parsedName=items.get(ii).getParsedElementName();
					//하위 스트림에서 조회한 내역별로 상위 스트림에 체크아웃되어 잇는지를 확인 시 골뱅이 앞까지만으로 확인
					//그리고 하위스트림뷰이름을 상위스트림 뷰 이름으로 바꾼 후 조회
					parsedName=parsedName.replace(fromViewDir, toViewDir);
					/** -r옵션 제거***/
					isCheckOutCmd="cleartool lsco -d -short -cview -me \""+parsedName+"\"";//to act
				
					//lsco명령을 수행 후 그 안에 checkout버전 구문이 있어야 만 chckout건 있음으로 간주
					System.out.println("IS ANY CHEKCOUT ="+ isCheckOutCmd);
					cmdResultMap=this.winCommander.execCommandWithReturn(isCheckOutCmd,toViewDir);
					int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
					ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
					ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
					if(exitVal!=0){
						for (int kk=0;kk<errList.size();kk++){
						}
						//cleartool: Error: Pathname not found: "M:\TEST_INT\TEST_C\AP2\HKE2\SORCB2HP\MyFolder".
						//신규이거나 상위에 파일이 없음.
					}else{
						if(outList.size()==0){
							//로그없음=체크아웃된 건 없음
							
						
						}else if(outList.size()==1){
							String line=(String)outList.get(0);
							
							//로그가 한줄이며, 체크아웃리턴값과 입력값이 동일하면 체크아웃상태
							//디렉토리 하위 체크아웃파일이 출력되었을 경우는 무시
							if(line.equals(parsedName)){								
								ChangeSetItem item=new ChangeSetItem();
								item.changSetName=line;
								item.code=ChangeSetItem.ALEADY_CHECK_OUT;
								chekOutItems.add(item);
							}else{
								// do nothing
							}
						}
						
						//체크아웃된 파일이면 한줄 출력, 폴더옵션이면 폴더하위에 체크아웃되어있는 파일 모두 출력함. 
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
		 * @return undo checkout된 리스트
		 * @throws Exception
		 */
		public int  mergeCheckIn(ArrayList<String>unDoChecksList,String mergeViewDir,String toActivity,String pVobName) throws Exception{
			String unDoCheckout;
			String checkInCmd="cleartool ci -nc -identical activity:"+toActivity+"@\\"+pVobName;
		
			Map<Object, Object> cmdResultMap;
			int unDoCount=0;
			try{
				
				KiccoLogger.log(Level.INFO, "UNDO CHECKOUT 건수 ="+ unDoChecksList.size());
				//컴파일 실패한 건만  undo checkout
				for(int ii=0;ii<unDoChecksList.size();ii++){
					String fileName=(String)unDoChecksList.get(ii);
					unDoCheckout="cleartool unco -rm \""+mergeViewDir+"\\"+fileName+"\"";//mergeview dir=to view
					KiccoLogger.log(Level.INFO, "UNDO CHECKOUT CMD ="+ unDoCheckout);
					this.winCommander.execCommandWithReturn(unDoCheckout,mergeViewDir);
					unDoCount++;
				}			
				//나머지(컴파일 성공한건) 체크인(액티비티 단위로)
				KiccoLogger.log(Level.INFO, "CHECK IN CMD ="+ checkInCmd);
				cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,mergeViewDir);
				int exitVal1=(Integer) cmdResultMap.get("EXIT_VAL");
				if(exitVal1!=0) throw new Exception("체크인 실패:\n[수행명령] "+checkInCmd);
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
	 * @return undo checkout된 리스트
	 * @throws Exception
	 */
	public void  mergeCheckIn(String mergeViewDir,String toActivity,String pVobName) throws Exception{

		String checkInCmd="cleartool ci -nc -identical activity:"+toActivity+"@\\"+pVobName;
		Map<Object, Object> cmdResultMap;
		try{
			/********************************
			//컴파일 실패한 건만  undo checkout
			for(int ii=0;ii<unDoChecksList.size();ii++){
				String fileName=(String)unDoChecksList.get(ii);
				unDoCheckout="cleartool unco -rm \""+mergeViewDir+"\\"+fileName+"\"";//mergeview dir=to view
				KiccoLogger.log(Level.INFO, "UNDO CHECKOUT CMD ="+ unDoCheckout);
				this.winCommander.execCommandWithReturn(unDoCheckout,mergeViewDir);
				unDoCount++;
			}			
			//나머지(컴파일 성공한건) 체크인(액티비티 단위로)
			 * ***********************************/
			
			KiccoLogger.log(Level.INFO, "CHECK IN CMD ="+ checkInCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,mergeViewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal!=0) throw new Exception("체크인 실패:\n[수행명령] "+checkInCmd);
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
			
			//1.set activity 수행
			//System.out.println("MKACT CMD:"+mkActivityCmd);
			//this.winCommander.execCommandWithReturn(mkActivityCmd,mergeViewDir);
			//KiccoLogger.getLogger().log(Level.INFO,"SET ACTIVITY CMD ="+ setActivityCmd);
			//this.winCommander.execCommandWithReturn(setActivityCmd,viewDir);
			
			//
			cmdResultMap=this.winCommander.execCommandWithReturn(unDoCheckOutCmd,viewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			if(exitVal!=0) throw new Exception("UNDO CHECKOUT 실패 ["+toActivity+"]");
	
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
		
			//나머지(컴파일 성공한건) 체크인(액티비티 단위로)
			KiccoLogger.log(Level.INFO, "[체크인명령]:"+ checkInCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkInCmd,viewDir);
			int exitVal1=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal1!=0) throw new Exception("체크인 실패:\n[수행명령] "+checkInCmd);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return unDoCount;

	}	
	/**
	 * 형상항목별로 체크아웃
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
				//체크아웃 수행 시 에러발생
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
	 * 형상항목별로 체크아웃(aleady checkout은 스킵함. 호출시 유의바람)
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
			KiccoLogger.getLogger().log(Level.INFO,"[체크아웃명령]:"+checkOutCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkOutCmd,viewDir);
			
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			
			//체크아웃 수행 시 에러발생
			if(exitVal!=0) {
				if(Common.isAnyMatchStr((String)errList.get(0), "is already checked out")){
					//do noting-성공으로 간주한다.(
				}else{
					//undocheckout수행
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
			KiccoLogger.log(Level.INFO, "[신규버전생성명령]:"+ checkOutCmd);
			cmdResultMap=this.winCommander.execCommandWithReturn(checkOutCmd,viewDir);
			
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			ArrayList outList=(ArrayList)cmdResultMap.get("OUTPUT");
			ArrayList errList=(ArrayList)cmdResultMap.get("ERROR");
			
			//체크아웃 수행 시 에러발생
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
		//버전파일카피하기
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
				
				System.out.println("로컬복사 대상폴더:"+folderPath);
				File targetFolder=new File(folderPath);
				targetFolder.mkdirs();
				
				//COPY명령 수행
				File targetFile=new File(fileFullPath);
				System.out.println("로컬복사 대상파일:"+fileFullPath);

				if(targetFile.exists()) targetFile.delete();//기존 건이 있으면 삭제하지 않으면 cleeatool에서 err발생
				System.out.println("COPY CMD ="+ copyCmd);
				cmdResultMap=this.winCommander.execCommandWithReturn(copyCmd,viewDir);
				exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
				
				if(exitVal==0){
					ArrayList outputList=(ArrayList)cmdResultMap.get("OUTPUT");
	
				}else{
					throw new Exception("CC내 파일을 로컬로 복사하는 중 에러가 발생했습니다.");
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
	 * 기능: 형상항목이 디렉토리인지 검사
	 * @param configItemPath 뷰위치 포함 대상 형상항목명 -예 D:\Views\MF_PR_view\AP2\HKE2\SORCB2HP
	 * @param viewDir  cleartool명령이 수행되는 뷰 경로
	
	 * @return 디렉토리이면 true, 아니면 false
	 * @throws Exception
	 */
	public boolean isDirCofigItem(String configItemPath,String viewDir) throws Exception{
		//cleartool file D:\Views\MF_PR_view\AP2\HKE2\SORCB2HP
		boolean result=false;
		String cmd="cleartool file \""+configItemPath+"\"";
		System.out.println(cmd);
		Map<Object, Object> cmdResultMap=this.winCommander.execCommandWithReturn(cmd,viewDir);
		int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
		if(exitVal==1) throw new Exception("명령수행중 실패했습니다. -"+cmd);
		
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
	//	 * 기능: 해당 형상항목을 업데이트 한다.(스냅샷 뷰)
	//	 * @param items 뷰위치 포함 형상항목들 -예: D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1
	//	 * @return 성공여부
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
		 * 기능: 해당 형상항목을 업데이트 한다.(스냅샷 뷰)
		 * @param items 뷰위치 포함 형상항목들 -예: D:\Views\MF_PR_view\API\HKE1\SORCB2HP\SBBKS52.PL1
		 * @return 성공여부
		 */
		public boolean cofigItemUpdate(String fileName, String viewDirectory ){
	
			String cmd=null;
	
			cmd="cleartool update \""+fileName+"\"";
			//KiccoLogger.getLogger().log(Level.INFO,"업데이트: "+cmd + "뷰디렉토리:"+viewDirectory );
			try{
				Map<Object, Object> cmdResultMap = this.winCommander.execCommandWithReturn(cmd, viewDirectory);
				int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
				if(exitVal != 0 )
					throw new Exception("명령수행중 실패했습니다. -"+cmd);
			}
			catch(Exception ex){
				String msg = "Exception Msg:"+ex.getMessage();
				KiccoLogger.getLogger().log(Level.SEVERE, msg);
				ex.printStackTrace();
			}
	
			return true;
		}



	/**
	 * CleaCase 대상 폴더내 .contrib파일 삭제(view private인 파일만)
	 * @param targetDir 대상 폴저
	 * @param isRecursive 하위폴더까지 뒤져서 처리여부
	 * @throws Exception
	 */
	public void deleteContribFile(String targetDir, boolean isRecursive) throws Exception{
		//System.out.println();
		//System.out.println("Target: "+targetDir);
		File dir=new File(targetDir);
		
		if(!dir.isDirectory()) throw new Exception("해당위치가 폴더가 아닙니다.");
		
		//contrib파일 추출 필터
		FileFilter myFilters=new FileFilter(){
			public boolean accept(File pathname) {
				String fileNames=pathname.getName();
				return (!pathname.isDirectory()) && Common.isAnyMatchStr(fileNames, "\\.contrib");

			}
			
		};
		
		//폴더 추출 필터
		FileFilter subDirFilter=new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
			
		};
		
		//contrib파일 삭제
		File[] contribs=dir.listFiles(myFilters);
		for(int ii=0;ii<contribs.length;ii++){
			//this.isViewPrivate(contribs[ii]);
			//System.out.println("delete "+contribs[ii].getName());
			contribs[ii].delete();
		}
		
		//하위 폴더 모두 뒤져서 삭제
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
		KiccoLogger.getLogger().log(Level.INFO,"[뷰프라이빗확인명령]: "+cmd  );
		try{
			Map<Object, Object> cmdResultMap = this.winCommander.execCommandWithReturn(cmd, viewDir);
			int exitVal=(Integer) cmdResultMap.get("EXIT_VAL");
			if(exitVal != 0 )
				throw new Exception("명령수행중 실패했습니다. -"+cmd);
			ArrayList<String> arrList=(ArrayList<String>)cmdResultMap.get("OUTPUT");
			if(arrList.size()==0) isViewPrivate=false;
			else{
				if(Common.isAnyMatchStr((String)arrList.get(0), "CHECKEDOUT")) 
					isViewPrivate=false;//체크아웃은 순수view private이 아님으로 함.
				else// 해당 형상항목명이 찍히거나, hijacked 파일의 경우
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
		KiccoLogger.getLogger().log(Level.INFO, "뷰프라이빗여부:"+isViewPrivate);
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
		
		//최신파일명 획득을 위해 중복 파일명 제거
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
	

	 //Set 인터페이스에서 구현해야 하는 메서드

	@Override
	public int hashCode() {
		// Joshua Bloch의 작성법을 사용
		int result = 17;
		result = 37 * result + path.hashCode();
		result = 37 + result + id;
		return result;
	}
	
}*/