package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @typeName: PageResult
 * @classDesc: 分页接口类
 * @author：XiaoBai
 * @createTime：2019年7月18日 上午10:22:01 
 * @remarks：     
 * @version V1.0
 */
public class PageResult implements Serializable {
	
	private long total;
	private List rows;
	
	
	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
	
	
}
