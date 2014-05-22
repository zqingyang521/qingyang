package com.example.qingyangdemo.common;

import android.content.Context;

import com.example.qingyangdemo.bean.Upload;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * 数据库访问的公共类
 * 
 * @author 赵庆洋
 * 
 */
public class DBManager {

	private static final String DB_NBAM = "upload";

	private DbUtils db;

	public DBManager(Context context) {
		// 创建数据库
		db = DbUtils.create(context, DB_NBAM);
	}

	/**
	 * 保存上传信息
	 * 
	 * @param upload
	 * @return
	 */
	public boolean saveUpload(Upload upload) {
		try {
			db.save(upload);
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除上传信息
	 * 
	 * @param uploadfilepath
	 * @return
	 */
	public boolean delUpload(String uploadfilepath) {

		Selector selector = Selector.from(Upload.class);

		selector.where(WhereBuilder.b("uploadfilepath", "=", uploadfilepath));

		try {
			Upload upload = db.findFirst(selector);

			db.delete(upload);
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获取上传资源Id
	 * 
	 * @param uploadfilepath
	 * @return
	 */
	public String getBindId(String uploadfilepath) {

		Selector selector = Selector.from(Upload.class);

		selector.where(WhereBuilder.b("uploadfilepath", "=", uploadfilepath));

		String bindId = "";

		try {
			Upload upload = db.findFirst(selector);

			if (upload == null) {
				return "";
			}
			bindId = upload.getSourceid();
		} catch (DbException e) {
			e.printStackTrace();
			return "";
		}
		return bindId;
	}
}
