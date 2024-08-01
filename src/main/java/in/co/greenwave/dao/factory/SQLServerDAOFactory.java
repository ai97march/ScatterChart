package in.co.greenwave.dao.factory;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import in.co.greenwave.dao.ReportableDAO;
import in.co.greenwave.dao.implementation.sqlserver.ReportService;



public class SQLServerDAOFactory extends DAOFactory {

	public static Connection createVesuviusConnection() {
		// Use DRIVER and DBURL to create a connection
		// Recommend connection pool implementation/usage
		DataSource dataSource = null;
		Connection con = null;
		try {
			Context initContext  = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup("vesuviusdata");
			con = dataSource.getConnection();
		}catch(NamingException e){
			System.out.println("source caught exception");
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}	
		return con;
	}

	@Override
	public ReportableDAO getReportService() {
		return new ReportService();
	}

}
