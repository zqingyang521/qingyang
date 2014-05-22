package com.example.qingyangdemo.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends Base {

	private int uid;

	private String name;

	private String password;

	private boolean isRememberMe;

	private boolean isErr;

	private String msg;

	/**
	 * 解析json字符串
	 * 
	 * @param json
	 * @return
	 */
	public static User parse(String json) {

		User user = new User();

		JSONObject jsonObject;
		JSONObject userObject;
		try {
			jsonObject = new JSONObject(json);

			userObject = jsonObject.getJSONObject("response");

			boolean isError = userObject.getBoolean("isErr");

			if (!isError) {
				user.setUid(userObject.getInt("uid"));
				user.setName(userObject.getString("username"));
				user.setPassword(userObject.getString("password"));
			} else {
				user.setMsg(userObject.getString("msg"));
			}
			user.setErr(isError);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRememberMe() {
		return isRememberMe;
	}

	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isErr() {
		return isErr;
	}

	public void setErr(boolean isErr) {
		this.isErr = isErr;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
