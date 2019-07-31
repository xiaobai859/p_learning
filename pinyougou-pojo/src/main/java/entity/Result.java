package entity;

import java.io.Serializable;

/**
 * @typeName: Result
 * @classDesc: 返回结果
 * @author：XiaoBai
 * @createTime：2019年7月18日 下午2:24:52 
 * @remarks：     
 * @version V1.0
 */
public class Result implements Serializable {
	// 是否成功
	private boolean success;
	// 返回信息
	private String message;
	
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
