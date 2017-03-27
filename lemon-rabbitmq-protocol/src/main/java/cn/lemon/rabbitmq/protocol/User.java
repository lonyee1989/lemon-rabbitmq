package cn.lemon.rabbitmq.protocol;

public class User {

	private Integer id;
	private String name;
	private String remark;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String toString() {
		return "User [id="+ id+", name="+name +", remark="+remark +"]";
	}
}
