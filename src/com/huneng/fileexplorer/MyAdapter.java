package com.huneng.fileexplorer;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	public static final int ListMode_Edit = 0;
	public static final int ListMode_Default = 1;

	private List<String> m_sFileNames;
	private boolean m_CheckArray[];
	public int m_ListMode;
	private Context m_Context;

	public MyAdapter(Context a_Context) {
		m_Context = a_Context;
		m_sFileNames = null;
		m_CheckArray = null;
		m_ListMode = ListMode_Default;
	}

	public MyAdapter(Context a_Context, List<String> a_FileNames) {
		m_Context = a_Context;

		setFileNames(a_FileNames);

		m_ListMode = ListMode_Default;
	}

	@Override
	public int getCount() {
		return m_sFileNames.size();
	}

	@Override
	public Object getItem(int a_Index) {

		return m_sFileNames.get(a_Index);
	}

	@Override
	public long getItemId(int a_Pos) {
		return 0;
	}

	public List<String> getFileNames() {

		return m_sFileNames;
	}

	public void setFileNames(List<String> a_FileNames) {
		m_ListMode = ListMode_Default;

		if (a_FileNames == null) {
			m_sFileNames.clear();
			m_CheckArray = null;
			return;
		}

		m_sFileNames = a_FileNames;

		m_CheckArray = new boolean[m_sFileNames.size()];
		MToolBox.memset(m_CheckArray, m_CheckArray.length, false);
	}

	public boolean setCheck(int a_Index, boolean a_Value) {
		if (m_CheckArray == null)
			return false;

		m_CheckArray[a_Index] = a_Value;

		return m_CheckArray[a_Index];
	}

	public void setListMode(int a_ListMode) {
		m_ListMode = a_ListMode;
		
		if(m_ListMode == ListMode_Default){
			for(int i = 0; i < m_CheckArray.length; i++)
				m_CheckArray[i] = false;
			
		}
	}

	@Override
	public View getView(int a_Position, View a_ConvertView, ViewGroup a_Parent) {

		if (m_sFileNames == null)
			return null;

		ViewHolder t_Holder = null;

		if (a_ConvertView == null) {
			t_Holder = new ViewHolder();

			LayoutInflater l_Inflater = LayoutInflater.from(m_Context);

			a_ConvertView = l_Inflater.inflate(R.layout.listitem, null);

			t_Holder.tvFileName = (TextView) a_ConvertView
					.findViewById(R.id.tv_FileName);

			t_Holder.cbCheckBox = (CheckBox) a_ConvertView
					.findViewById(R.id.cb_FileCheck);

			a_ConvertView.setTag(t_Holder);

		} else {
			t_Holder = (ViewHolder) a_ConvertView.getTag();
		}

		String l_FilePath = m_sFileNames.get(a_Position);
		String l_FileName = FileUtil.getFileName(l_FilePath);
		
		t_Holder.tvFileName.setText(l_FileName);

		int l_FileType = FileUtil.getFileType(l_FilePath);

		int l_ResourceID = MToolBox.getFileImage(l_FileType);

		
		Drawable l_Drawable = m_Context.getResources()
				.getDrawable(l_ResourceID);

		int x = 50 * MainActivity.m_xd;
		l_Drawable.setBounds(0, 0, x - 5, x - 5);
		t_Holder.tvFileName.setCompoundDrawablePadding(5);
		t_Holder.tvFileName.setCompoundDrawables(l_Drawable, null, null, null);
		

		if (m_ListMode == ListMode_Edit)
			t_Holder.cbCheckBox.setVisibility(View.VISIBLE);
		else
			t_Holder.cbCheckBox.setVisibility(View.INVISIBLE);
		
		t_Holder.cbCheckBox
				.setOnCheckedChangeListener(new MyOnCheckedChangeListener(
						m_CheckArray, a_Position));
		t_Holder.cbCheckBox.setChecked(m_CheckArray[a_Position]);

		return a_ConvertView;
	}

	public List<String> getCheckedList(){
		List<String> l_List = new LinkedList<String>();
		for(int i = 0; i < m_CheckArray.length; i++){
			if(m_CheckArray[i]){
				l_List.add(m_sFileNames.get(i));
			}
		}
		return l_List;
	} 
	
	public List<String> getUnCheckedList(){
		List<String> l_List = new LinkedList<String>();
		for(int i = 0; i < m_CheckArray.length; i++){
			if(!m_CheckArray[i]){
				l_List.add(m_sFileNames.get(i));
			}
		}
		return l_List;
	} 
	
	
	
	private final class ViewHolder {
		public TextView tvFileName;
		public CheckBox cbCheckBox;
	}

	public class MyOnCheckedChangeListener implements OnCheckedChangeListener {
		private boolean[] m_CheckArray;
		private int m_Pos;

		public MyOnCheckedChangeListener(boolean[] a_Array, int a_Pos) {
			m_CheckArray = a_Array;
			m_Pos = a_Pos;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			m_CheckArray[m_Pos] = isChecked;
		}

	}
}
