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
	 * InputStream과 OutputStream을 이용한 기본적인 파일 복사 코드.
	 * 위 코드는 기본적인 Stream의 사용법을 잘 보여주고 있지만 성능상에 심각한 문제를 안고 있다.
	 * 파일크기(정확하게는 스트림의 길이)만큼 while문을 돌면서 끊임없이 읽고쓰기를 반복하고 있는데 수정테스트
	 * 이는 CPU, DISK모두에게 부담을 주는 결과를 초래한다.
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
	 * Stream에 Buffer Filter를 연결하여 성능을 향상.
	 * 위와같은 방법으로 충분히 만족할만 한가? 그렇다고 할수도있고 아니라고 할수도 있다. 위 두 방식은 스트림으로
	 * 데이터를 전송하는데 항상 cpu의 연산을 필요로 한다. 즉 스트림을 처리하는동안 cpu가 계속해서 명령을 처리
	 * 해줘야 한다는것이다.(비록 cpu사용율은 얼마 안될지 모르지만.. )
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
	 * JDK 1.4이상부터 사용 가능하며 transferTo() 메소드를 호출하면 내부적으로 OS의 네이티브IO 기능을 활용
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
