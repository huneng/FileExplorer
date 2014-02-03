package com.huneng.fileexplorer;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	enum EditMode {
		Copy, Move, Rename, None
	};

	MyAdapter m_Adapter;
	ListView m_ListView;

	Menu m_OptionMenu;

	MenuItem m_RemoveItem;
	MenuItem m_BackItem;
	MenuItem m_CancelItem;
	MenuItem m_OkItem;
	MenuItem m_MoveItem;
	MenuItem m_CopyItem;
	List<String> m_RenameList;

	EditMode m_EditFlag;
	public static int m_xd, m_yd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FileUtil.setCurPath(MToolBox.getSdcardPath());

		setContentView(R.layout.activity_main);

		ListView m_ListView = (ListView) findViewById(R.id.lv_FileList);
		List<String> l_List = FileUtil.getCurDirFileNames();

		m_Adapter = new MyAdapter(this);
		m_Adapter.setFileNames(l_List);

		m_ListView.setAdapter(m_Adapter);

		m_ListView.setOnItemClickListener(new MyOnItemClickListener(this));
		m_ListView.setOnItemLongClickListener(new MyOnItemLongClickListener(
				this));

		DisplayMetrics l_Metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(l_Metrics);

		m_xd = (int) (l_Metrics.widthPixels / l_Metrics.xdpi);
		m_yd = (int) (l_Metrics.heightPixels / l_Metrics.ydpi);

		m_EditFlag = EditMode.None;

	}

	public void updateFileList(String a_DirPath) {
		boolean l_Rc = FileUtil.setCurPath(a_DirPath);

		if (!l_Rc)
			return;

		List<String> l_FileList = FileUtil.getCurDirFileNames();

		m_Adapter.setFileNames(l_FileList);
		m_Adapter.notifyDataSetChanged();

		if (FileUtil.getCurPath().equals(Global.ROOT_PATH))
			m_BackItem.setEnabled(false);
		else
			m_BackItem.setEnabled(true);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (m_Adapter.m_ListMode == MyAdapter.ListMode_Edit) {
				m_Adapter.setListMode(MyAdapter.ListMode_Default);
				m_Adapter.notifyDataSetChanged();
				displayBackAction();
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void updateActionBar(int a_back, int a_remove, int a_copy,
			int a_move, int a_cancel, int a_ok) {
		m_BackItem.setShowAsAction(a_back);
		m_RemoveItem.setShowAsAction(a_remove);
		m_CopyItem.setShowAsAction(a_copy);
		m_MoveItem.setShowAsAction(a_move);
		m_CancelItem.setShowAsAction(a_cancel);
		m_OkItem.setShowAsAction(a_ok);
	}

	public void displayBackAction() {
		updateActionBar(MenuItem.SHOW_AS_ACTION_ALWAYS,
				MenuItem.SHOW_AS_ACTION_NEVER, MenuItem.SHOW_AS_ACTION_NEVER,
				MenuItem.SHOW_AS_ACTION_NEVER, MenuItem.SHOW_AS_ACTION_NEVER,
				MenuItem.SHOW_AS_ACTION_NEVER);
	}

	public void displayEditActions() {
		updateActionBar(MenuItem.SHOW_AS_ACTION_NEVER,
				MenuItem.SHOW_AS_ACTION_ALWAYS, MenuItem.SHOW_AS_ACTION_ALWAYS,
				MenuItem.SHOW_AS_ACTION_ALWAYS, MenuItem.SHOW_AS_ACTION_NEVER,
				MenuItem.SHOW_AS_ACTION_NEVER);
	}

	public void displayJudgeActions() {
		updateActionBar(MenuItem.SHOW_AS_ACTION_ALWAYS,
				MenuItem.SHOW_AS_ACTION_NEVER, MenuItem.SHOW_AS_ACTION_NEVER,
				MenuItem.SHOW_AS_ACTION_NEVER, MenuItem.SHOW_AS_ACTION_ALWAYS,
				MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu a_Menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, a_Menu);

		m_OptionMenu = a_Menu;

		m_BackItem = a_Menu.findItem(R.id.action_back);
		m_RemoveItem = a_Menu.findItem(R.id.action_removefile);
		m_MoveItem = a_Menu.findItem(R.id.action_move);
		m_CopyItem = a_Menu.findItem(R.id.action_copy);
		m_OkItem = a_Menu.findItem(R.id.action_ok);
		m_CancelItem = a_Menu.findItem(R.id.action_cancel);

		m_BackItem.setEnabled(FileUtil.getCurPath().equals("/"));

		displayBackAction();

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

			for (int i = 0; i < l_List.size(); i++)
				FileUtil.removeFile(l_List.get(i));

			// m_Adapter.setListMode(MyAdapter.ListMode_Default);

			List<String> l_FileLists = FileUtil.getCurDirFileNames();

			m_Adapter.setFileNames(l_FileLists);
			m_Adapter.notifyDataSetChanged();

			displayBackAction();

			break;

		case R.id.action_copy:

		case R.id.action_move:
			if (a_Item.getItemId() == R.id.action_copy)
				m_EditFlag = EditMode.Copy;
			else
				m_EditFlag = EditMode.Move;

			displayJudgeActions();
			
			m_RenameList = m_Adapter.getCheckedList();
			m_Adapter.setListMode(MyAdapter.ListMode_Default);
			m_Adapter.notifyDataSetChanged();
			break;

		case R.id.action_ok:
			if (FileUtil.getCurPath().startsWith(MToolBox.getSdcardPath())) {
				Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.progress_dialog);

				TextView l_ProgressJobName = (TextView) dialog
						.findViewById(R.id.tv_PrgressJob);
				ProgressBar l_ProgressBar = (ProgressBar) dialog
						.findViewById(R.id.pb_FileEdit);
				ProgressBar l_ProgressBar1 = (ProgressBar) dialog
						.findViewById(R.id.pb_LargeFile);

				if (m_EditFlag == EditMode.Copy) {
					l_ProgressBar1.setVisibility(View.VISIBLE);
					l_ProgressBar1.setProgress(0);
					dialog.setTitle("Copy File");
				} else {
					l_ProgressBar1.setVisibility(View.GONE);
					dialog.setTitle("Move File");
				}
				dialog.show();
				int l_Size = m_RenameList.size();
				l_ProgressBar.setMax(l_Size);
				l_ProgressBar.setProgress(0);

				for (int i = 0; i < l_Size; i++) {

					String l_FilePath = m_RenameList.get(i);
					String l_FileName = FileUtil.getFileName(l_FilePath);
					l_ProgressJobName.setText(l_FileName);

					if (m_EditFlag == EditMode.Copy) {
						FileUtil.copyFile(m_RenameList.get(i), l_ProgressBar1);
					} else {
						FileUtil.renameFile(l_FilePath, FileUtil.getCurPath()
								+ "/" + l_FileName);
					}
					l_ProgressBar.setProgress(i);
				}
				dialog.dismiss();
			} else {
				Toast.makeText(this, "Can't operate file here",
						Toast.LENGTH_LONG).show();
			}

			m_EditFlag = EditMode.None;
		case R.id.action_cancel:
			displayBackAction();
			m_Adapter.setFileNames(FileUtil.getCurDirFileNames());
			m_Adapter.notifyDataSetChanged();
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
			String l_FilePath = m_Parent.m_Adapter.getFileNames().get(a_Pos);
			int l_Type = FileUtil.getFileType(l_FilePath);

			if (l_Type == Global.FileType_Dir) {
				m_Parent.updateFileList(l_FilePath);
			} else if (l_Type == Global.FileType_Other) {
				return;
			} else {
				FileUtil.openFile(l_FilePath, m_Parent);
			}
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

			m_Parent.displayEditActions();

			CheckBox l_CheckBox = (CheckBox) a_View
					.findViewById(R.id.cb_FileCheck);
			l_CheckBox.setChecked(true);

			m_Parent.updateFileList(FileUtil.getCurPath());

			m_Parent.m_Adapter.setListMode(MyAdapter.ListMode_Edit);

			return true;
		}

	}
}
