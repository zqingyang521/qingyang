package model;

/**
 * 文件的模板类
 * 
 * @author 赵庆洋
 * 
 */
public class PcFile {

	// �ļ����
	private String fileName;

	// �Ƿ����ļ���
	private boolean directory;

	// �Ƿ����ļ�
	private boolean file;

	// �ļ���·��
	private String filePath;

	// ������
	private String totalSpace;

	// ��������
	private String freeSpace;

	// �ļ���С
	private long length;

	// ���ڵ�
	private String parent;

	// ���ڵ�·��
	private String parentPath;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

}
