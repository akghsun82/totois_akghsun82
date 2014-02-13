package com.sc.cc.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * InputStream�� OutputStream�� �̿��� �⺻���� ���� ���� �ڵ�.
	 * �� �ڵ�� �⺻���� Stream�� ������ �� �����ְ� ������ ���ɻ� �ɰ��� ������ �Ȱ� �ִ�.
	 * ����ũ��(��Ȯ�ϰԴ� ��Ʈ���� ����)��ŭ while���� ���鼭 ���Ӿ��� �а��⸦ �ݺ��ϰ� �ִµ� �����׽�Ʈ
	 * �̴� CPU, DISK��ο��� �δ��� �ִ� ����� �ʷ��Ѵ�.
	 * @param file
	 * @param toDir
	 * @throws Exception
	 */
	public static void fileCopy(File file, File toDir) throws Exception{
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;
		try{
			inputStream = new FileInputStream(file);
			outputStream = new FileOutputStream(toDir);
	
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
			    outputStream.write(buffer, 0, bytesRead);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(outputStream!=null) outputStream.close();
			if(inputStream!=null) inputStream.close();
		}
	}
	
	/**
	 * Stream�� Buffer Filter�� �����Ͽ� ������ ���.
	 * ���Ͱ��� ������� ����� �����Ҹ� �Ѱ�? �׷��ٰ� �Ҽ����ְ� �ƴ϶�� �Ҽ��� �ִ�. �� �� ����� ��Ʈ������
	 * �����͸� �����ϴµ� �׻� cpu�� ������ �ʿ�� �Ѵ�. �� ��Ʈ���� ó���ϴµ��� cpu�� ����ؼ� ����� ó��
	 * ����� �Ѵٴ°��̴�.(��� cpu������� �� �ȵ��� ������.. )
	 * @param file
	 * @param toDir
	 * @throws Exception
	 */
	public static void fileCopyWithBuf(File file, File toDir) throws Exception{
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;
		
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		
		try{
			inputStream = new FileInputStream(file);
			outputStream = new FileOutputStream(toDir);
			
			bin = new BufferedInputStream(inputStream);
			bout = new BufferedOutputStream(outputStream);

			int bytesRead = 0;
			byte[] buffer = new byte[1024];

			while ((bytesRead = bin.read(buffer, 0, 1024)) != -1) {
			    bout.write(buffer, 0, bytesRead);
			}

		}catch(Exception e){
			e.printStackTrace();
			throw e;
			
		}finally{
			if(bout !=null) bout.close();
			if(bin !=null) bin.close();
			
			if(outputStream!=null) outputStream.close();
			if(inputStream!=null) inputStream.close();
		}
	}
	
	/**
	 * JDK 1.4�̻���� ��� �����ϸ� transferTo() �޼ҵ带 ȣ���ϸ� ���������� OS�� ����Ƽ��IO ����� Ȱ��
	 * @param file
	 * @param toDir
	 * @throws Exception
	 */
	public static void fileCopyWitNio(File file, File toDir) throws Exception{
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;
		
		FileChannel fcin =  null;
		FileChannel fcout = null;
		
		try{
			inputStream = new FileInputStream(file);
			outputStream = new FileOutputStream(toDir);

			fcin =  inputStream.getChannel();
			fcout = outputStream.getChannel();

			long size = fcin.size();
			   
			fcin.transferTo(0, size, fcout);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
	
		}finally{
			if(fcout!=null) fcout.close();
			if(fcin!=null) fcin.close();
			
			if(outputStream!=null) outputStream.close();
			if(inputStream!=null) inputStream.close();
		}
	}
	
	public static boolean deleteDirectory(File path) {
        if(!path.exists()) {
            return false;
        }
         
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }         
        return path.delete();
    }
}
