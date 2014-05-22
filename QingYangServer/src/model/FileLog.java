package model;

/**
 * 文件上传记录
 * 
 * @author 赵庆洋
 * 
 */
public class FileLog {

	private Long id;

	private String path;

	private String tempPath;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
}
