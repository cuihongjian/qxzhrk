package zhrk.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseQxRecMessage<M extends BaseQxRecMessage<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("ID", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("ID");
	}

	public M setMsid(java.lang.Integer msid) {
		set("MSID", msid);
		return (M)this;
	}
	
	public java.lang.Integer getMsid() {
		return getInt("MSID");
	}

	public M setUserid(java.lang.Integer userid) {
		set("USERID", userid);
		return (M)this;
	}
	
	public java.lang.Integer getUserid() {
		return getInt("USERID");
	}

	public M setRectime(java.util.Date rectime) {
		set("RECTIME", rectime);
		return (M)this;
	}
	
	public java.util.Date getRectime() {
		return get("RECTIME");
	}

}
