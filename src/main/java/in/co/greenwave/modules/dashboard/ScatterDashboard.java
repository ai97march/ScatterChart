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

	/** Multiple DAO's */
	DAOFactory factory;
	ReportableDAO vesiviusdao;

	/** This variable is used to map "From Date" of user input form data*/
	private Date fromDate;
	/** This variable is used to map "To Date" of user input form data*/
	private Date toDate;
	/** List to store "Selected Parameter" of user input form data*/
	private List<String> selectedParameter;	
	/** List to show parameter list in user input form*/
	private List<String> parameterList;
	/** List to store "Selected Recipe" of user input form data*/
	private List<String> selectedRecipe;
	/** List to show recipe list in user input form*/
	private List<String> recipeList;
	/** List to store "Selected MixType" of user input form data*/
	private List<String> selectedmixType;
	/** List to show mix type list in user input form*/
	private List<String> mixTypeList;
	/** For formatting date type to string type or vice-versa*/
	DateFormat dfDt = new SimpleDateFormat("yyyy-MM-dd");
	/** A boolean variable to render scatter chart*/
	boolean showData;
	/** This variable is used to map chart related data from database*/
	private Map<String,List<ScatterXYData>> scatterDataMap;
	/** This variable is used to convert scatterDataMap values to json string*/
	private String jsString;

	@PostConstruct
	private void init() {
		System.out.println("ScatterDashboard.init()..");
		/*get configuration related values from properties file 'token.properties'*/
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
		
		/* initialize fromDate*/
		fromDate = new Date();
		/* initialize toDate*/
		toDate = new Date();
		
		/* Create a Calendar instance and set it to the specified date (toDate).
		 * Then, subtract 30 days from this date to calculate the fromDate. */
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate);
		cal.add(Calendar.DATE, -30);
		fromDate = cal.getTime();
		
//		System.out.println("defaultParameter => "+ defaultParameter);
		
//		selectedParameter = new ArrayList<String>(Arrays.asList(defaultParameter));
		
//		selectedParameter = new ArrayList<String>(Arrays.asList("Dryer Inlet Flow (CFM)", 
//				"Dryer Inlet Temperature (deg C)", "Furfural Temperature (deg C)", "Morton Mixer RTD-1 Temperature (deg C)",
//				"Surge Hopper Temperature(deg C)", "Tube Dryer Flow Diff(CFM)", "Tube Dryer Temperature Diff(deg C)"));
		
		/* For converting comma separated defaultParameter into list type*/
		selectedParameter = Arrays.stream(defaultParameter.split(",")).map(String::trim).collect(Collectors.toList());

		System.out.println(selectedParameter);

	}

	public void onShowGraph() {
		System.out.println("ScatterDashboard.onShowGraph()..");
		//				System.out.println("date test => "+ dfDt.format(fromDate)+" <=> "+dfDt.format(toDate));
		scatterDataMap = new LinkedHashMap<>();
		jsString = "";
		/*if fromDate, toDate, selectedmixType, and selectedRecipe are not null then it will execute a script to close
		 * user input form*/
		if(fromDate != null && toDate != null && selectedmixType != null && selectedRecipe != null) {
			System.out.println(fromDate+" = "+toDate+" = "+selectedmixType+" = "+selectedRecipe);
			PrimeFaces.current().executeScript("closeSearchBox();");
			//			String joinedStringWithStream = Arrays.stream(selectedParameter).collect(Collectors.joining(","));
			//			System.out.println(joinedStringWithStream);
		}

		/* To retrieve all scatter data from database*/
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

		/* Formatting the data of scatterDataList into scatterDataMap in a desired way so that later on
		 * we could map the data of scatterDataMap to json string*/
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
			/* Converting the scatterDataMap to jsString*/
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
        /* This javascript function will generate scatter chart graph based on jsString*/
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

	/* Ajax call to set recipe list based on Mix Type Select */
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

	/* Ajax call to set Mix Type List based on fromDate and toDate */
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

	/* Ajax call to set Recipe List based on fromDate, toDate and selectedmixType */
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
