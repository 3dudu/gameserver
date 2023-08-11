package com.xuegao.core.util;

import java.io.File;

public class ClassPathUtil {
	public static String getProjectPath() {
		java.net.URL url = ClassPathUtil.class.getProtectionDomain().getCodeSource()
				.getLocation();
		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		filePath = filePath.substring(0,filePath.lastIndexOf(File.separator) + 1);
		File file = new File(filePath);
		if(file.getName().indexOf("lib")!=-1){
			file=file.getParentFile();
		}
		filePath = file.getAbsolutePath();
		return filePath;
	}
	
	public static File findFile(String filename){
		String path=getProjectPath();
		File pathfile=new File(path);
		for(int i=0;i<3;i++){
			File f=findFile(filename, pathfile);
			if(f==null){
				pathfile=pathfile.getParentFile();
				if(pathfile==null){
					return null;
				}
			}else {
				return f;
			}
		}
		return null;
	}
	
	public static File findFile(String filename,File directory){
		if(directory.isDirectory()){
			File[] files=directory.listFiles();
			for(File f:files){
				if(f.isFile()&&f.getName().equals(filename)){
					return f;
				}
			}
			for(File f:files){
				if(f.isDirectory()){
					File fi= findFile(filename, f);
					if(fi!=null){
						return fi;
					}
				}
			}
		}
		return null;
	}
	//--------------目录查找 ----------
	public static File findDirectory(String directoryName){
		String path=getProjectPath();
		File pathfile=new File(path);
		for(int i=0;i<3;i++){
			File f=findDirectory(directoryName, pathfile);
			if(f==null){
				pathfile=pathfile.getParentFile();
				if(pathfile==null){
					return null;
				}
			}else {
				return f;
			}
		}
		return null;
	}
	
	public static File findDirectory(String directoryName,File directory){
		if(directory.isDirectory()){
			File[] files=directory.listFiles();
			for(File f:files){
				if(f.isDirectory()&&f.getName().equals(directoryName)){
					return f;
				}
			}
			for(File f:files){
				if(f.isDirectory()){
					File fi= findDirectory(directoryName, f);
					if(fi!=null){
						return fi;
					}
				}
			}
		}
		return null;
	}
	
}