package in.co.greenwave.modules.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Collectors;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import in.co.greenwave.dao.ReportableDAO;
import in.co.greenwave.dao.factory.DAOFactory;
import in.co.greenwave.modules.utility.ScatterData;
import in.co.greenwave.modules.utility.ScatterXYData;

@ManagedBean
@ViewScoped
public class ScatterDashboard implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DAOFactory factory;
	ReportableDAO vesiviusdao;

	private Date fromDate;
	private Date toDate;
	private List<String> selectedParameter;	
	private List<String> parameterList;
	private List<String> selectedRecipe;	
	private List<String> recipeList;
	private List<String> selectedmixType;	
	private List<String> mixTypeList;
	DateFormat dfDt = new SimpleDateFormat("yyyy-MM-dd");
	boolean showData;
	private Map<String,List<ScatterXYData>> scatterDataMap;
	private String jsString;

	@PostConstruct
	private void init() {
		System.out.println("ScatterDashboard.init()..");
		/*connection with the database as per the properties file 'token.properties'*/
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("token.properties");
		Properties prop = new Properties();
		String defaultParameter = null;
		try {
			prop.load(inputStream);
			int installationDb = Integer.parseInt(prop.getProperty("installationDB"));
			defaultParameter = prop.getProperty("defaultParameter");
			factory = DAOFactory.getDAOFactory(installationDb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		vesiviusdao = factory.getReportService();

		showData = false;

		parameterList = vesiviusdao.getParameterList();
		
		fromDate = new Date();
		
		toDate = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate);
		cal.add(Calendar.DATE, -30);
		fromDate = cal.getTime();
		
//		System.out.println("defaultParameter => "+ defaultParameter);
		
//		selectedParameter = new ArrayList<String>(Arrays.asList(defaultParameter));
		
//		selectedParameter = new ArrayList<String>(Arrays.asList("Dryer Inlet Flow (CFM)", 
//				"Dryer Inlet Temperature (deg C)", "Furfural Temperature (deg C)", "Morton Mixer RTD-1 Temperature (deg C)",
//				"Surge Hopper Temperature(deg C)", "Tube Dryer Flow Diff(CFM)", "Tube Dryer Temperature Diff(deg C)"));
		
		selectedParameter = Arrays.stream(defaultParameter.split(",")).map(String::trim).collect(Collectors.toList());

		System.out.println(selectedParameter);

	}

	public void onShowGraph() {
		System.out.println("ScatterDashboard.onShowGraph()..");
		//				System.out.println("date test => "+ dfDt.format(fromDate)+" <=> "+dfDt.format(toDate));
		scatterDataMap = new LinkedHashMap<>();
		jsString = "";
		if(fromDate != null && toDate != null && selectedmixType != null && selectedRecipe != null) {
			System.out.println(fromDate+" = "+toDate+" = "+selectedmixType+" = "+selectedRecipe);
			PrimeFaces.current().executeScript("closeSearchBox();");
			//			String joinedStringWithStream = Arrays.stream(selectedParameter).collect(Collectors.joining(","));
			//			System.out.println(joinedStringWithStream);
		}

		List<ScatterData> scatterDataList = vesiviusdao.getScatterGraphData(fromDate,toDate,
				selectedParameter == null ? " ":selectedParameter.stream().collect(Collectors.joining(",")),
						selectedmixType.stream().collect(Collectors.joining(",")),
						selectedRecipe.stream().collect(Collectors.joining(",")));
		
//		scatterDataList.forEach(param -> System.out.println("scatterDataList => "+param.getParameter()));
//		System.out.println("scatterDataList => "+scatterDataList);
		
		System.out.println("selectedParameter => "+selectedParameter);
		
//		scatterDataMap.put("Dryer Inlet Flow (CFM)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Dryer Inlet Temperature (deg C)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Furfural Temperature (deg C)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Morton Mixer RTD-1 Temperature (deg C)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Surge Hopper Temperature(deg C)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Tube Dryer Flow Diff(CFM)", new ArrayList<ScatterXYData>());
//		scatterDataMap.put("Tube Dryer Temperature Diff(deg C)", new ArrayList<ScatterXYData>());

		for(ScatterData sd : scatterDataList) {
			if(scatterDataMap.containsKey(sd.getParameter())){
				scatterDataMap.get(sd.getParameter()).add(new ScatterXYData(sd.getBatchno(), sd.getValue(), sd.getColor()));
			}
			else {
				List<ScatterXYData> ScatterXYDataList = new ArrayList<ScatterXYData>();
				ScatterXYDataList.add(new ScatterXYData(sd.getBatchno(), sd.getValue(), sd.getColor()));
				scatterDataMap.put(sd.getParameter(),ScatterXYDataList);
			}
		}

//		for(Map.Entry<String,List<ScatterXYData>> me : scatterDataMap.entrySet()) {
//			System.out.println("==================================");
//			System.out.println(me.getKey());
//			System.out.println(me.getValue());
//			System.out.println("==================================");
//		}

		ObjectMapper objectMapper = new ObjectMapper();

		try {
			jsString = objectMapper.writeValueAsString(scatterDataMap);
//			System.out.println("jsString => "+jsString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String test = "const coursesData = "+jsString+";\r\n"
				+ "	        console.log(coursesData);\r\n"
				+ "	        \r\n"
				+ "	        Object.entries(coursesData).forEach(([key, items]) => {\r\n"
				+ "	        	\r\n"
				+ "	        	const config = { \r\n"
				+ "	                    type: 'scatter', \r\n"
				+ "	                    data: { \r\n"
				+ "	                        datasets: [{ \r\n"
				+ "	                            label: 'Batch Value', \r\n"
				+ "	                            data: items.map(item => ({\r\n"
				+ "	                                x: item.batchno,\r\n"
				+ "	                                y: item.value\r\n"
				+ "	                            })), \r\n"
				+ "	                            backgroundColor: 'rgba(0, 127, 255, 0.8)', \r\n"
				+ "	                        }], \r\n"
				+ "	                    }, \r\n"
				+ "	                    options: { \r\n"
				+ "	                        scales: { \r\n"
				+ "	                            x: { \r\n"
				+ "	                                type: 'category', \r\n"
				+ "	                                position: 'bottom', \r\n"
				+ "	                                title: { \r\n"
				+ "	                                    display: true, \r\n"
				+ "	                                    text: 'Batches', \r\n"
				+ "	                                }, \r\n"
				+ "	                            }, \r\n"
				+ "	                            y: { \r\n"
				+ "	                                type: 'linear', \r\n"
				+ "	                                position: 'left', \r\n"
				+ "	                                title: { \r\n"
				+ "	                                    display: true, \r\n"
				+ "	                                    text: 'Parameter Values', \r\n"
				+ "	                                }, \r\n"
				+ "	                            }, \r\n"
				+ "	                        }, \r\n"
				+ "	                        elements: { \r\n"
				+ "	                            point: { \r\n"
				+ "	                                radius: 9, \r\n"
				+ "	                                hoverRadius: 20, \r\n"
				+ "	                            }, \r\n"
				+ "	                        }, \r\n"
				+ "	                        plugins: { \r\n"
				+ "	                            title: { \r\n"
				+ "	                                display: true, \r\n"
				+ "	                                text: key, \r\n"
				+ "	                            }, \r\n"
				+ "	                        }, \r\n"
				+ "	                    }, \r\n"
				+ "	                }; \r\n"
				+ "	                const ctx = document.getElementById(key).getContext('2d'); \r\n"
				+ "	                      \r\n"
				+ "	                new Chart(ctx, config);\r\n"
				+ "	        \r\n"
				+ "	        });";

		PrimeFaces.current().executeScript("handleScatterGraph("+jsString+");");
		showData = true;
	}

	public String getChartData() {

		//		String datas = "[ {\r\n"
		//				+ "			x : 'Java',\r\n"
		//				+ "			y : 150\r\n"
		//				+ "		}, {\r\n"
		//				+ "			x : 'Python',\r\n"
		//				+ "			y : 300\r\n"
		//				+ "		}, {\r\n"
		//				+ "			x : 'JavaScript',\r\n"
		//				+ "			y : 250\r\n"
		//				+ "		}, {\r\n"
		//				+ "			x : 'C++',\r\n"
		//				+ "			y : 200\r\n"
		//				+ "		}, {\r\n"
		//				+ "			x : 'Data Structures',\r\n"
		//				+ "			y : 180\r\n"
		//				+ "		}, {\r\n"
		//				+ "			x : 'React',\r\n"
		//				+ "			y : 230\r\n"
		//				+ "		},]";

		List<Object> data = new ArrayList<>();
		data.add(new Object[]{"Java", 150});
		data.add(new Object[]{"Python", 300});
		data.add(new Object[]{"JavaScript", 250});
		data.add(new Object[]{"C++", 200});
		data.add(new Object[]{"Data Structures", 180});
		data.add(new Object[]{"React", 400});
		JsonArray jsonArray = new JsonArray();

		for (Object item : data) {
			Object[] values = (Object[]) item;
			String x = (String) values[0];
			int y = (int) values[1];

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("x", x);
			jsonObject.addProperty("y", y);

			jsonArray.add(jsonObject);
		}

		return jsonArray.toString();
	}

	public void onMixTypeSelect() {
		System.out.println("ScatterDashboard.onMixTypeSelect()..");
		selectedRecipe = new ArrayList<String>();
		System.out.println("selectedmixType change => " + selectedmixType);
		recipeList = vesiviusdao.getRecipeList(fromDate, toDate, selectedmixType.stream().collect(Collectors.joining(",")));
		//		System.out.println("date test => "+ dfDt.format(fromDate)+" <=> "+dfDt.format(event.getObject()));
		//        FacesContext facesContext = FacesContext.getCurrentInstance();
		//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		//        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
	}

	public void onDateSelect(SelectEvent<Date> event) {
		System.out.println("ScatterDashboard.onDateSelect()..");
		//		System.out.println(dfDt.format(event.getObject()));
		if(fromDate != null && toDate != null) {
			System.out.println(fromDate+" => "+toDate);
			mixTypeList = vesiviusdao.getMixTypeList(fromDate,toDate);
			System.out.println("mixTypeList "+mixTypeList);
		}
		//		System.out.println("date test => "+ dfDt.format(fromDate)+" <=> "+dfDt.format(event.getObject()));
		//        FacesContext facesContext = FacesContext.getCurrentInstance();
		//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		//        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
	}

	public void onToggleSelect(ToggleSelectEvent event) {
		System.out.println("ScatterDashboard.onToggleSelect()..");
		System.out.println("selectedmixType toggle => " + selectedmixType);
		selectedRecipe = new ArrayList<String>();
		recipeList = vesiviusdao.getRecipeList(fromDate, toDate, selectedmixType.stream().collect(Collectors.joining(",")));
		System.out.println("recipeList toggle => " + recipeList);
	}

	public void onItemSelect(SelectEvent event) {
		System.out.println("ScatterDashboard.onItemSelect()..");

	}

	public void onItemUnselect(UnselectEvent event) {
		System.out.println("ScatterDashboard.onItemUnselect()..");
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<String> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<String> parameterList) {
		this.parameterList = parameterList;
	}

	public List<String> getSelectedRecipe() {
		return selectedRecipe;
	}

	public void setSelectedRecipe(List<String> selectedRecipe) {
		this.selectedRecipe = selectedRecipe;
	}

	public List<String> getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(List<String> recipeList) {
		this.recipeList = recipeList;
	}

	public List<String> getSelectedmixType() {
		return selectedmixType;
	}

	public void setSelectedmixType(List<String> selectedmixType) {
		this.selectedmixType = selectedmixType;
	}

	public List<String> getMixTypeList() {
		return mixTypeList;
	}

	public void setMixTypeList(List<String> mixTypeList) {
		this.mixTypeList = mixTypeList;
	}

	public boolean isShowData() {
		return showData;
	}

	public void setShowData(boolean showData) {
		this.showData = showData;
	}

	public Map<String, List<ScatterXYData>> getScatterDataMap() {
		return scatterDataMap;
	}

	public void setScatterDataMap(Map<String, List<ScatterXYData>> scatterDataMap) {
		this.scatterDataMap = scatterDataMap;
	}

	public String getJsString() {
		return jsString;
	}

	public void setJsString(String jsString) {
		this.jsString = jsString;
	}

	public List<String> getSelectedParameter() {
		return selectedParameter;
	}

	public void setSelectedParameter(List<String> selectedParameter) {
		this.selectedParameter = selectedParameter;
	}
}
