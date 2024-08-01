package in.co.greenwave.dao;

import java.util.Date;
import java.util.List;

import in.co.greenwave.modules.utility.ScatterData;

public interface ReportableDAO {
	
	public List<String> getParameterList();
	public List<String> getMixTypeList(Date fromDate, Date toDate);
	public List<String> getRecipeList(Date fromDate, Date toDate, String mixType);
	public List<ScatterData> getScatterGraphData(Date fromDate, Date toDate, String parameters, String selectedmixType,
			String recipes);
}
