package com.sc.cc.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import com.kicco.util.KiccoLogger;

/**
 * 
 *  UNIX untar 용, 100byte이상 tar 압축해제 불가
 * @author dooli
 *
 */
public class Tar {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String tarFilePath="D:\\temptar\\CQDOC00014732.tar";
		String destPath="D:\\temptar\\dest2";

		InputStream tarIs=getInputStream(tarFilePath);
		readTar(tarIs, destPath);
		
	}
	
	/**
	 * 
	 * @param tarFileName
	 * @return
	 * @throws Exception
	 */
	public static InputStream getInputStream(String tarFileName) throws Exception{
	      if(tarFileName.substring(tarFileName.lastIndexOf(".") + 1, tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase("gz")){
	         System.out.println("Creating an GZIPInputStream for the file");
	         return new GZIPInputStream(new FileInputStream(new File(tarFileName)));
	      }else{
	         System.out.println("Creating an InputStream for the file");
	         return new FileInputStream(new File(tarFileName));
	      }
	   }
	
	/**
	 * 
	 * @param in
	 * @param untarDir
	 * @throws IOException
	 */
	   public static void readTar(InputStream in, String untarDir) throws IOException{
	     // System.out.println("Reading TarInputStream... (using classes from http://www.trustice.com/java/tar/)");
	      TarInputStream tin = new TarInputStream(in);
	      TarEntry tarEntry = tin.getNextEntry();
	      if(!new File(untarDir).exists()) new File(untarDir).mkdirs();
	      if(new File(untarDir).exists()){
	              while (tarEntry != null){
	                 File destPath = new File(untarDir + File.separatorChar + tarEntry.getName());
	                 
	     			 KiccoLogger.getLogger().log(Level.INFO,"UNIXTAR 압축해제:"+destPath.getAbsoluteFile());
	     			
	                 File upperFolder=destPath.getParentFile();System.out.println("Processing upperFolder " + upperFolder.getAbsoluteFile());
	                 if(!upperFolder.exists()) upperFolder.mkdirs();
	                //파일인 경우
	                 if(!tarEntry.isDirectory()){
	                    FileOutputStream fout = new FileOutputStream(destPath);
	                    tin.copyEntryContents(fout);
	                    fout.close();
	                //폴더인 경우
	                 }else{
	                    destPath.mkdir();
	                 }
	                 tarEntry = tin.getNextEntry();
	              }
	              tin.close();
	      }else{
	         System.out.println("That destination directory doesn't exist! " + untarDir);
	      }
	   }

}
