package in.co.greenwave.modules.utility;

public class ScatterXYData {
	String batchno;
	float value;
	String color;
	
	public ScatterXYData(String batchno,float value, String color) {
		super();
		this.batchno = batchno;
		this.value = value;
		this.color = color;
	}
	
	public ScatterXYData() {

	}

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
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
		return "ScatterXYData [batchno=" + batchno + ", value=" + value + ", color=" + color + "]";
	}
	
}
