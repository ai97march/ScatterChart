package in.co.greenwave.dao.implementation.sqlserver;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.co.greenwave.dao.ReportableDAO;
import in.co.greenwave.dao.factory.SQLServerDAOFactory;
import in.co.greenwave.modules.utility.ScatterData;

public class ReportService implements ReportableDAO {
	
	DateFormat dfDt = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat dfDtTm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Connection con = null;
	PreparedStatement pstmt = null;
	CallableStatement cstmt = null;

	@Override
	public List<ScatterData> getScatterGraphData(Date fromDate, Date toDate, String parameters, String selectedmixType,
			String recipes) {
		
		System.out.println(fromDate+"] ["+toDate+"] ["+parameters+"] ["+selectedmixType+"] ["+recipes);
		
		List<ScatterData> scatterDataList = new ArrayList<ScatterData>();
		con = null;
		cstmt = null;
		
		try {
			con = SQLServerDAOFactory.createVesuviusConnection();
			
			cstmt = con.prepareCall("{call scatterplot_1(?,?,?,?,?)}");
			cstmt.setString(1, dfDt.format(fromDate));
			cstmt.setString(2, dfDt.format(toDate));
			cstmt.setString(3, parameters);
			cstmt.setString(4, selectedmixType);
			cstmt.setString(5, recipes);
			
			cstmt.execute();
			
			ResultSet rs = cstmt.getResultSet();
//			ResultSetMetaData rsmd = rs.getMetaData();
			
			while (rs.next()) {		
				scatterDataList.add(new ScatterData(rs.getString("BatchNo"),rs.getString("Parameters"),rs.getFloat("Value"), rs.getString("color")));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				cstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return scatterDataList;
	}

	@Override
	public List<String> getParameterList() {
		List<String> parameterList = new ArrayList<String>();
		try {
			con = SQLServerDAOFactory.createVesuviusConnection();
			pstmt = con.prepareStatement("Select Parameters from dbo.udtvf_select_parameters_scatter_graph()");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				parameterList.add(rs.getString("Parameters")) ;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				con.close();
				pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return parameterList;
	}

	@Override
	public List<String> getMixTypeList(Date fromDate, Date toDate) {
		List<String> mixTypeList = new ArrayList<String>();
		try {
			con = SQLServerDAOFactory.createVesuviusConnection();
			pstmt = con.prepareStatement("select mixtype from dbo.udtvf_select_mixtype(?,?)");
			pstmt.setString(1, dfDt.format(fromDate));
			pstmt.setString(2, dfDt.format(toDate));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				mixTypeList.add(rs.getString("mixtype")) ;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return mixTypeList;
	}

	@Override
	public List<String> getRecipeList(Date fromDate, Date toDate, String mixType) {
		List<String> recipeList = new ArrayList<String>();
		try {
			con = SQLServerDAOFactory.createVesuviusConnection();
			pstmt = con.prepareStatement("select Recipe from dbo.udtvf_select_recipe(?,?,?)");
			pstmt.setString(1, dfDt.format(fromDate));
			pstmt.setString(2, dfDt.format(toDate));
			pstmt.setString(3, mixType);
//			System.out.println(dfDt.format(fromDate)+" <> "+dfDt.format(toDate)+" <> "+mixType);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				recipeList.add(rs.getString("Recipe")) ;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return recipeList;
	}
	
	
}
