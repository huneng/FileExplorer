package com.huneng.fileexplorer;

import java.io.File;

import android.os.Environment;

public class MToolBox {
	//array clear
	
	public static void memset(int a_Array[], int a_Size, int a_Value){
		for(int i = 0; i < a_Size; i++){
			a_Array[i] = a_Value;
		}
		
	}
	
	public static void memset(char a_Array[], int a_Size, char a_Value){
		for(int i = 0; i < a_Size; i++){
			a_Array[i] = a_Value;
		}	
	}
	
	public static void memset(boolean a_Array[], int a_Size, boolean a_Value){
		for(int i = 0; i < a_Size; i++){
			a_Array[i] = a_Value;
		}	
	}
	
	//get suffix of file
	public static String getSuffix(String a_Str){
		int index = a_Str.lastIndexOf('.');
		if(index == -1)
		{
			return null;
		}
		return a_Str.substring(index+1);
	}
	
	public static String getSuffix(String a_Str, char a_Flag){
		int index = a_Str.lastIndexOf(a_Flag);
		
		if(index == -1)
		{
			return null;
		}
		
		return a_Str.substring(index+1);
	}
	
	//get image resource id
	public static int getFileImage(int a_FileType){
		switch(a_FileType){
		
		case GlobalVariable.FileType_Dir:
			return R.drawable.dir;
			
		case GlobalVariable.FileType_Png:
		case GlobalVariable.FileType_Jpg:
			return R.drawable.image;
			
		default:
			return R.drawable.other_file;
		}
	}
	
	//get sd card path
	public static String getSdcardPath(){
		File l_File = Environment.getExternalStorageDirectory();
		return l_File.getPath();
	}

}
