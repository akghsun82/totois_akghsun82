package com.sc.cc.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.logging.Level;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.kicco.util.KiccoLogger;

public class XMLMgr {

    /** @deprecated
	 * 
	 * @param docc
	 * @param fileName
	 */
	@Deprecated
	public void writeUTF(Document doc, String fileName){
       
		StringWriter  stringOut = null;
		try
        {
			/*
			//doc.appendChild(doc.getDocumentElement());
          	
			OutputFormat outputformat = new OutputFormat();
            outputformat.setEncoding("UTF-8");
            outputformat.setIndent(4);
            outputformat.setIndenting(true);
            outputformat.setPreserveSpace(false);

            
            stringOut = new StringWriter();
            XMLSerializer    str_serial = new XMLSerializer( stringOut, outputformat );
            str_serial.asDOMSerializer();
            str_serial.serialize( doc.getDocumentElement() );
            
			KiccoLogger.getLogger().log(Level.INFO, stringOut.toString());
			*/
			FileOutputStream fileoutputstream = new FileOutputStream(new File(fileName));
            OutputFormat outputformat = new OutputFormat();
            outputformat.setEncoding("UTF-8");
            outputformat.setIndent(4);
            outputformat.setIndenting(true);
            outputformat.setPreserveSpace(false);

            XMLSerializer serializer = new XMLSerializer();
            serializer.setOutputFormat(outputformat);
            serializer.setOutputByteStream(fileoutputstream);
            serializer.asDOMSerializer();
            serializer.serialize(doc.getDocumentElement());

            stringOut = new StringWriter();
            XMLSerializer    str_serial = new XMLSerializer( stringOut, outputformat );
            str_serial.asDOMSerializer();
            str_serial.serialize( doc.getDocumentElement() );

            System.out.println(stringOut.toString());
        }
        catch (Exception ex)
        {
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
        }
        finally{
        	if(stringOut != null){
        		try{
        			stringOut.close();
        		}
        		catch(Exception e){
        			String msg = "Exception Msg:"+e.getMessage();
        			KiccoLogger.getLogger().log(Level.SEVERE, msg);
        			e.printStackTrace();
        		}
        	}

        }
	}
	
	
	/**
	 * ���� ���� Root Node ����
	 * @param doc
	 * @param tagName
	 * @return ������ RootNode ��ü
	 */
	public Element createRootElement(Document doc, String tagName) {
		Element element = null;
	
		try{
			element = doc.createElement(tagName);
		}
		catch(DOMException ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
		return element;
	}
	
	/**
	 * ���� �ִ� Root ��� ����
	 * @param doc
	 * @param tagName
	 * @param textContent
	 * @return ������ RootNode ��ü
	 */
	public Element createRootElement(Document doc, String tagName, String textContent) {
		Element element = null;
	
		try{
			element = doc.createElement(tagName);
			element.setTextContent(textContent);
		}
		catch(DOMException ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
		return element;
	}
	
	/**
	 * 	���� ���� Child ��� ����
	 * @param doc
	 * @param pElement
	 * @param tagName
	 * @return 	 * @param textContent
	 * @return ������ Node ��ü
	 */
	public Element createNonValueElement(Document doc, Element pElement, String tagName) {
		Element element = null;
	
		try{
			element = doc.createElement(tagName);
		pElement.appendChild(element);
		}
		catch(Exception ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
		return element;
	}
	
	/**
	 * ���� �ִ� Chaild Node ����
	 * @param doc
	 * @param pElement
	 * @param tagName
	 * @param textContent
	 * @return ������ Node ��ü
	 */
	public Element createElement(Document doc, Element pElement, String tagName, String textContent) {
		Element element = null;
	
		try{
			element = doc.createElement(tagName);
			element.setTextContent(textContent);
			pElement.appendChild(element);
		}
		catch(Exception ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
		return element;
	}
	/**
	 * ���� �ִ� Chaild Node ����
	 * @param doc
	 * @param pElement
	 * @param tagName
	 * @param textContent
	 * @return ������ Node ��ü
	 */
	public void setAttribute(Element element, String name, String value) {
		try{
			element.setAttribute(name, value);
		}
		catch(Exception ex){
			String msg = "Exception Msg:"+ex.getMessage();
			KiccoLogger.getLogger().log(Level.SEVERE, msg);
			ex.printStackTrace();
		}
	}


}
