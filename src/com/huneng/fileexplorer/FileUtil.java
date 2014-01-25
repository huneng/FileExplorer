package com.huneng.fileexplorer;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {
	private static String m_sCurrentPath = GlobalVariable.ROOT_PATH;

	public static String getSubDictory(int a_Index) {
		File t_File = new File(m_sCurrentPath);
		File[] t_Files = t_File.listFiles();

		if (t_Files[a_Index].isDirectory()) {
			String l_Path = t_Files[a_Index].getPath();

			return l_Path;
		}
		return null;
	}
	
	public static String getSubDictory(String a_FileName){
		File l_File = new File(a_FileName);
		
		if(!l_File.exists())
			return null;
		if(l_File.isDirectory()){
			return l_File.getPath();
		}
		return null;
	}

	public static String getCurPath() {
		return m_sCurrentPath;
	}

	public static boolean setCurPath(String a_sPath) {
		File t_File = new File(a_sPath);

		if (t_File.isDirectory()) {
			m_sCurrentPath = t_File.getPath();
			return true;
		}
		return false;
	}

	public static String getParentPath() {
		File t_File = new File(m_sCurrentPath);
		if (m_sCurrentPath.equals("/"))
			return "/";
		return t_File.getParent();
	}

	public static List<String> getCurDirFileNames() {

		File t_File = new File(m_sCurrentPath);
		File[] t_Files = t_File.listFiles();
		List<String> l_FileNames = new LinkedList<String>();

		if (t_Files == null)
			return null;

		l_FileNames.clear();

		for (int i = 0; i < t_Files.length; i++) {
			l_FileNames.add(t_Files[i].getAbsolutePath());
		}
		sortByType(l_FileNames);
		
		return l_FileNames;
	}
	
	public static String getFileName(String a_Path){
		
		int l_Index = a_Path.lastIndexOf('/');
		
		return a_Path.substring(l_Index+1);
	}

	public static int getFileType(String a_FileName) {
		File l_File = new File(a_FileName);

		if (l_File.isDirectory())
			return GlobalVariable.FileType_Dir;

		String l_Suffix = MToolBox.getSuffix(l_File.getName());
		
		if(l_Suffix == null)
			return GlobalVariable.FileType_Other;
		
		if (l_Suffix.equals("png")) {
			return GlobalVariable.FileType_Png;
		} 
		else if (l_Suffix.equals("jpg")) {
			return GlobalVariable.FileType_Jpg;
		}
		else
			return GlobalVariable.FileType_Other;

	}

	public static void sortByName(List<String> a_FileNames) {
		Collections.sort(a_FileNames);
	}

	public static void sortByType(List<String> a_FileNames) {
		List<String> Res = new LinkedList<String>();
		
		Collections.sort(a_FileNames);
		
		//Dir
		for(int i = 0; i < a_FileNames.size(); i++){
			if(getFileType(a_FileNames.get(i)) == GlobalVariable.FileType_Dir)
				Res.add(a_FileNames.get(i));
		}
		//Png
		for(int i = 0; i < a_FileNames.size(); i++){
			if(getFileType(a_FileNames.get(i)) == GlobalVariable.FileType_Png)
				Res.add(a_FileNames.get(i));
		}
		//Jpg
		for(int i = 0; i < a_FileNames.size(); i++){
			if(getFileType(a_FileNames.get(i)) == GlobalVariable.FileType_Jpg)
				Res.add(a_FileNames.get(i));
		}
		//Other
		for(int i = 0; i < a_FileNames.size(); i++){
			if(getFileType(a_FileNames.get(i)) == GlobalVariable.FileType_Other)
				Res.add(a_FileNames.get(i));
		}
		a_FileNames.clear();
		for(int i = 0; i < Res.size(); i++)
		a_FileNames.add(Res.get(i)) ;
	}

	public static boolean removeFile(String a_FileName){
		File l_File = new File(a_FileName);
		if(l_File.exists()){
			
			if(l_File.isDirectory()){
				File[] l_Files = l_File.listFiles();
				for(int i = 0; i < l_Files.length; i++){
					removeFile(l_Files[i].getAbsolutePath());
				}
			}
			
			return l_File.delete();
		}
		
		return false;
	}
	
	
}
