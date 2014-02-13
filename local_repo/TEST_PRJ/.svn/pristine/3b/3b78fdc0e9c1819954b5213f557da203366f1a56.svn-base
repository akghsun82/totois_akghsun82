package com.sc.cc.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kicco.util.KiccoLogger;


public class XMLParseMgr {

	private SAXParser parser;
	private String uri;
	private BuildHandler handle;
	private SAXParserFactory fac=SAXParserFactory.newInstance();
	
	public XMLParseMgr(String uri,BuildHandler handler)throws Exception{
		this.parser=fac.newSAXParser();
		this.handle=handler;
		this.uri=uri;
	}
	public XMLParseMgr(String uri)throws Exception{
		this.parser=fac.newSAXParser();
		this.handle=new BuildHandler();
		this.uri=uri;
	}
	
	public ArrayList<ConfigElement> getConfigElements(){
		return this.handle.getConfigElements();
	}

	public void startParse() throws Exception{
		this.parser.parse(this.uri, this.handle);
	}
	
	public static void main(String args[])throws Exception{
		String uri="./CQDOC00000001.xml";

		try{
			XMLParseMgr sax=new XMLParseMgr(uri);
			sax.startParse();
			
			ArrayList<ConfigElement> configElements=sax.handle.getConfigElements();
			
			for(int ii=0;ii<configElements.size();ii++){
				ConfigElement element=configElements.get(ii);
				KiccoLogger.getLogger().log(Level.INFO,"CONFIG ELEMENT\n"+element.toString());
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

}

////////////////////

class BuildHandler extends DefaultHandler{
	
	private String nowElementPointer;
	private ArrayList<ConfigElement> configElements;
	private ConfigElement configElement;
	private String fileExt="";//형상항목의 확장자

	@Override
	public void startDocument(){
		KiccoLogger.getLogger().log(Level.INFO,"XML 파싱 시작");
		configElements=new ArrayList<ConfigElement>();
	}
	
	@Override
	public void endDocument(){
		KiccoLogger.getLogger().log(Level.INFO,"XML 파싱 종료");
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		//System.out.print("<"+qName+">");
		nowElementPointer=qName;
		
		if("CCELEMENT".equals(qName)){
			configElement=new ConfigElement();				
		}
		
		if("NAME".equals(qName)){
			attributes.getQName(0);//0번째 속성이름
			fileExt=(attributes.getValue(0)!=null)?attributes.getValue(0):"";//0번째 속성의 값. null이면 공백세팅
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName){
		if("CCELEMENT".equals(qName)){
			configElements.add(configElement);
		}
			
		nowElementPointer="";//현재 Element 포인터 초기화
		fileExt="";
		//System.out.println("</"+qName+">");
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		//System.out.println("dd"+nowElementPointer);
		if ("VERSION_FLAG".equals(nowElementPointer)){
			String tempStr=(configElement.getVersionFlag()==null)?"":configElement.getVersionFlag();
			configElement.setVersionFlag(tempStr + String.valueOf(ch,start,length));
		}else if ("NAME".equals(nowElementPointer)){
			String tempStr=(configElement.getConfigName()==null)?"":configElement.getConfigName();
			configElement.setConfigName(tempStr+String.valueOf(ch,start,length));
			configElement.setConfigExt(fileExt);
		}else if ("CONFIG_PATH".equals(nowElementPointer)){
			String tempStr=(configElement.getConfigPath()==null)?"":configElement.getConfigPath();
			configElement.setConfigPath(tempStr+String.valueOf(ch,start,length));
		}else if ("FILE_PATH".equals(nowElementPointer)){
			String tempStr=(configElement.getFilePath()==null)?"":configElement.getFilePath();
			configElement.setFilePath(tempStr+String.valueOf(ch,start,length));
		}

	}
	
	public ArrayList<ConfigElement> getConfigElements(){
		return this.configElements;
	}

}
