package in.co.greenwave.modules.utility;

public class ScatterData {
	
	String batchno;
	String parameter;
	float value;
	String color;
	
	public ScatterData(String batchno, String parameter, float value, String color) {
		super();
		this.batchno = batchno;
		this.parameter = parameter;
		this.value = value;
		this.color = color;
	}
	
	public ScatterData() {

	}

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "ScatterData [batchno=" + batchno + ", parameter=" + parameter + ", value=" + value + ", color=" + color
				+ "]";
	}
	
	

}
