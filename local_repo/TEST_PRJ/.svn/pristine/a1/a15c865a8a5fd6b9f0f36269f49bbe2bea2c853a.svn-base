/**
 * 

 */
package com.sc.cc.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import com.kicco.util.KiccoLogger;




/**
 * @author Administrator
 *
 */
public class FileUtil {
	public static void main(String args[]) throws Exception{

		String tarFilePath="D:\\temptar\\CQDOC00014732.tar";
		String destPath="D:\\temptar\\dest";
		unTarFile(tarFilePath,destPath);
	}
	
	public static void copyFile(String srcFile, String tgtFile) throws IOException{  
		
		int index = tgtFile.lastIndexOf("\\");
		if(index > 0){
			String dirPath = tgtFile.substring(0,index);
			File directory = new File(dirPath);
			if(!directory.exists()) directory.mkdirs();
		}
		
		File sourceFile = new File( srcFile );   
		File targetFile = new File( tgtFile );

		
		FileInputStream inputStream = null;  
		FileOutputStream outputStream = null; 
		BufferedInputStream bin = null;  
		BufferedOutputStream bout = null;  
		
		try {   
			  
			inputStream = new FileInputStream(sourceFile);   
			outputStream = new FileOutputStream(targetFile);   
			  
			bin = new BufferedInputStream(inputStream);   
			bout = new BufferedOutputStream(outputStream);      
			 
			int bytesRead = 0;   
			byte[] buffer = new byte[1024];   
			while ((bytesRead = bin.read(buffer, 0, 1024)) != -1) {    
				bout.write(buffer, 0, bytesRead);   
			} 
			bout.flush();
		} finally {   
			
			try{    
				if(outputStream != null) outputStream.close();    
				if(inputStream != null) inputStream.close();
				if(bin != null) bin.close();
				if(bout != null) bout.close();
			}catch(IOException ioe){}   
		} 
	}
	/**
	 * .
	 * @param path
	 * @return
	 */
	public static String getFileFullName(String path){
		String fileName = "";
		int index = path.lastIndexOf("\\");
		if(index < 0){
			fileName = path;
		}
		else{
			fileName = path.substring(index + 1);
		}
		
		return fileName;
	}
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path){
		String fileName = "";
		int index = path.lastIndexOf("\\");
		if(index >= 0){
			path = path.substring(index + 1);
		}
		index = path.lastIndexOf(".");
		if(index < 0){
			fileName = path;
		}
		else{
			fileName = path.substring(0, index);
		}
		return fileName;
	}
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileExt(String path){
		String fileExt = "";
		int index = path.lastIndexOf(".");
		if(index < 0){
			fileExt = null;
		}
		else{
			fileExt = path.substring(index + 1);
		}
		
		return fileExt;
	}
	
	private static final byte buf[] = new byte[1024];

	// 
	public static void createZipFile(String targetPath, String zipPath)
		throws Exception {
		createZipFile(targetPath, zipPath, false);
	}
	
	public static void createZipFile(String targetPath, String zipPath,
		boolean isDirCre) throws Exception {
		File fTargetPath = new File(targetPath);
		File files[] = (File[]) null;
		if (fTargetPath.isDirectory()) {
			files = fTargetPath.listFiles();
		} else {
			files = new File[1];
			files[0] = fTargetPath;
		}
		File path = new File(zipPath);
		File dir = null;
		dir = new File(path.getParent());
		if (isDirCre)
			dir.mkdirs();
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(path));
		makeZipFile(files, zipOut, "");
		zipOut.close();
	}
	public static void makeZipFile(File files[], ZipOutputStream zipOut,
			String targetDir) throws Exception {
		for (int i = 0; i < files.length; i++) {
			File compPath = new File(files[i].getPath());
			if (compPath.isDirectory()) {
				File subFiles[] = compPath.listFiles();
				makeZipFile(subFiles, zipOut, (new StringBuilder(String
						.valueOf(targetDir))).append(compPath.getName())
						.append("/").toString());
			} else {
				FileInputStream in = new FileInputStream(compPath);
				zipOut.putNextEntry(new ZipEntry((new StringBuilder(String
						.valueOf(targetDir))).append("/").append(
						files[i].getName()).toString()));
				int data;
				while ((data = in.read(buf)) > 0)
					zipOut.write(buf, 0, data);
				zipOut.closeEntry();
				in.close();
			}
		}

	}
	
	/**
	 * 
	 * @param sourceTar
	 * @param targetTar
	 * @throws Exception
	 */
	public static void createTarFile(String sourceTar, String targetTar) throws Exception{
		// Output file stream    
		FileOutputStream dest = new FileOutputStream( targetTar );     
		// Create a TarOutputStream    
		TarOutputStream out = new TarOutputStream( new BufferedOutputStream( dest ) ); 
		
		//
		out.setLongFileMode(TarOutputStream.LONGFILE_GNU);  
		
		File srcTarDir = new File(sourceTar);
		if (!srcTarDir.isDirectory())
			throw new Exception("Directory가 아닙니다");
		
		File[] filesToTar=srcTarDir.listFiles();    
   
		//makeTarFile(filesToTar, out, sourceTar);
		
		out.close();
	}
	
	/**
	public static void makeTarFile (File[] files, TarOutputStream out, String rootCtx) throws Exception{
		for (File f:files){
			if(f.isDirectory()){
				File[] subFiles = f.listFiles();
				makeTarFile(subFiles, out, rootCtx);
			}else{   
				TarEntry tarEntry=new TarEntry(f);
				tarEntry.setName(StringUtil.ksc2asc(Common.getString(f.getAbsolutePath(), rootCtx, false)));
				out.putNextEntry(tarEntry); 
				
				BufferedInputStream origin = new BufferedInputStream(new FileInputStream( f ));
				int count;       
				byte data[] = new byte[2048];       
				while((count = origin.read(data)) != -1) {          
					out.write(data, 0, count);       
				}        
				out.closeEntry();
				out.flush();       
				origin.close();   
			}
		}
	}
	**/
	
	public static void unTarFile(String tarFileName, String destDirectory) throws Exception{
		String tarFile = tarFileName;    
		String destFolder = destDirectory; 
		
		File dir = new File(destFolder);
		if(!dir.exists()) dir.mkdirs();
		
		// Create a TarInputStream    
		TarInputStream tis = new TarInputStream(new BufferedInputStream(new FileInputStream(tarFile)));    
		TarEntry entry;    
		while((entry = tis.getNextEntry()) != null) {       
			int count;       
			byte data[] = new byte[2048];  
			String fName = entry.getName();
			
			KiccoLogger.getLogger().log(Level.INFO,"GNUTAR 압축해제:"+fName);
			//System.out.println(new String(fName.getBytes("8859_1"), "euc-kr"));
			File af = new File(destFolder + "/" + fName);
			String str = af.getParent();
			File p = new File(str);
			if(!p.exists()) p.mkdirs();
			FileOutputStream fos = new FileOutputStream(destFolder + "/" + fName);       
			BufferedOutputStream dest = new BufferedOutputStream(fos);        
			while((count = tis.read(data)) != -1) {          
				dest.write(data, 0, count);       
			}        
			dest.flush();       
			dest.close();    
		}        
		tis.close();
	}
	
	// 
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists())
			return;
		cleanDirectory(directory);
		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		} else {
			return;
		}
	}
	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}
		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}
		File files[] = directory.listFiles();
		if (files == null)
			throw new IOException("Failed to list contents of " + directory);
		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception)
			throw exception;
		else
			return;
	}
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.exists())
				throw new FileNotFoundException("File does not exist: " + file);
			if (!file.delete()) {
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}
	
	public static void copyDirectory(File sourcelocation , File targetdirectory)
    throws IOException {
            //
            if (sourcelocation.isDirectory()) {              
                    //
                if (!targetdirectory.exists()) {
                    targetdirectory.mkdir();
                }
               
                String[] children = sourcelocation.list();
                for (int i=0; i<children.length; i++) {
                    copyDirectory(new File(sourcelocation, children[i]),
                            new File(targetdirectory, children[i]));
                }
            } else {
                //
                InputStream in = new FileInputStream(sourcelocation);                
                        OutputStream out = new FileOutputStream(targetdirectory);
               
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        }
}
