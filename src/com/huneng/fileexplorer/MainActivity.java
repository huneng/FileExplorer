package com.huneng.fileexplorer;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	MyAdapter m_Adapter;
	ListView m_ListView;
	Menu m_OptionMenu;
	MenuItem m_RemoveItem;
	MenuItem m_BackItem;
	public static int m_xd, m_yd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FileUtil.setCurPath(GlobalVariable.ROOT_PATH);
		
		setContentView(R.layout.activity_main);

		ListView m_ListView = (ListView) findViewById(R.id.lv_FileList);
		List<String> l_List = FileUtil.getCurDirFileNames();

		m_Adapter = new MyAdapter(this);
		m_Adapter.setFileNames(l_List);

		m_ListView.setAdapter(m_Adapter);

		m_ListView.setOnItemClickListener(new MyOnItemClickListener(this));
		m_ListView.setOnItemLongClickListener(new MyOnItemLongClickListener(this));
		
		DisplayMetrics l_Metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(l_Metrics);

		m_xd = (int) (l_Metrics.widthPixels / l_Metrics.xdpi);
		m_yd = (int) (l_Metrics.heightPixels / l_Metrics.ydpi);

	}

	public void updateFileList(String a_DirPath) {
		boolean l_Rc = FileUtil.setCurPath(a_DirPath);
		
		if (!l_Rc)
			return;

		List<String> l_FileList = FileUtil.getCurDirFileNames();

		m_Adapter.setFileNames(l_FileList);
		m_Adapter.notifyDataSetChanged();

		if (FileUtil.getCurPath().equals(GlobalVariable.ROOT_PATH))
			m_BackItem.setEnabled(false);
		else
			m_BackItem.setEnabled(true);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(m_Adapter.m_ListMode == MyAdapter.ListMode_Edit){
				m_Adapter.setListMode(MyAdapter.ListMode_Default);
				m_Adapter.notifyDataSetChanged();
				return true;
			}
			else{
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu a_Menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, a_Menu);

		m_OptionMenu = a_Menu;

		m_BackItem = a_Menu.findItem(R.id.action_back);
		m_RemoveItem = a_Menu.findItem(R.id.action_removefile);

		m_BackItem.setEnabled(false);

		return super.onCreateOptionsMenu(a_Menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem a_Item) {
		switch (a_Item.getItemId()) {
		case R.id.action_back:
			String l_Path = FileUtil.getParentPath();
			updateFileList(l_Path);
			break;
		case R.id.action_removefile:
			List<String> l_List = m_Adapter.getCheckedList();
			
			for(int i = 0; i < l_List.size(); i++)
				FileUtil.removeFile(l_List.get(i));
			
			//m_Adapter.setListMode(MyAdapter.ListMode_Default);
			
			List<String> l_FileLists = FileUtil.getCurDirFileNames();
			
			m_Adapter.setFileNames(l_FileLists);
			m_Adapter.notifyDataSetChanged();
			
			m_RemoveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			
			m_BackItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			break;

		}
		return super.onOptionsItemSelected(a_Item);
	}

	public class MyOnItemClickListener implements OnItemClickListener {

		MainActivity m_Parent;

		public MyOnItemClickListener(MainActivity a_Parent) {
			m_Parent = a_Parent;
		}

		@Override
		public void onItemClick(AdapterView<?> a_AdapterView, View a_View,
				int a_Pos, long a_Id) {
			
			String l_DirPath = FileUtil.getSubDictory(m_Parent.m_Adapter
					.getFileNames().get(a_Pos));

			if (l_DirPath == null)
				return;

			m_Parent.updateFileList(l_DirPath);
		}

	}

	public class MyOnItemLongClickListener implements OnItemLongClickListener {

		MainActivity m_Parent;

		MyOnItemLongClickListener(MainActivity a_Parent) {
			m_Parent = a_Parent;
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> a_AdapterView,
				View a_View, int a_Pos, long a_id) {
			MenuItem l_RemoveItem = m_Parent.m_RemoveItem;
			MenuItem l_BackItem = m_Parent.m_BackItem;

			l_RemoveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			l_BackItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			
			
			CheckBox l_CheckBox = (CheckBox)a_View.findViewById(R.id.cb_FileCheck);
			l_CheckBox.setChecked(true);
			
			m_Parent.updateFileList(FileUtil.getCurPath());
			
			m_Parent.m_Adapter.setListMode(MyAdapter.ListMode_Edit);
			
			return true;
		}

	}
}
