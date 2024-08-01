package in.co.greenwave.dao.factory;

import in.co.greenwave.dao.ReportableDAO;

public abstract class DAOFactory {

	  // List of DAO types supported by the factory
	  public static final int SQLSERVER = 1;
	  public static final int ORACLE = 2;
	  public static final int POSTGRES = 3;
	 
	  

	  // There will be a method for each DAO that can be 
	  // created. The concrete factories will have to 
	  // implement these methods.
	  public abstract ReportableDAO getReportService();
	  
	

	  public static DAOFactory getDAOFactory(
	      int whichFactory) {
	  
	    switch (whichFactory) {
	      case SQLSERVER: 
	          return new SQLServerDAOFactory();
	     // case ORACLE    : 
	          //return new OracleDAOFactory(); 
//	      case POSTGRES:
//	      		return new PostgresDAOFactory();
//	     
	      default           : 
	          return null;
	    }
	  }
	}
