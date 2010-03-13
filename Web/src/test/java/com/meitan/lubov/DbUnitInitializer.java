package com.meitan.lubov;

import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Date: Feb 7, 2010
 * Time: 8:56:19 AM
 *
 * @author Lieven Doclo
 */
public class DbUnitInitializer {
	private Logger logger = Logger.getLogger(getClass());

	private List<Resource> dataSetLocations;

	private DataSource dataSource;

	public List<Resource> getDataSetLocations() {
		return dataSetLocations;
	}

	public void setDataSetLocations(List<Resource> dataSetLocations) {
		this.dataSetLocations = dataSetLocations;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private IDataSet getDBUnitDataSet() {
		List<IDataSet> dbUnitDataSets = new ArrayList<IDataSet>(dataSetLocations.size());
		for (Resource dataSetLocation : dataSetLocations) {
			try {
				dbUnitDataSets.add(new XmlDataSet(dataSetLocation.getInputStream()));
			} catch (Exception e) {
				logger.warn("Could not load dataset " + dataSetLocation.getFilename() + ", ignoring", e);
			}
		}
		try {
			return new CompositeDataSet(dbUnitDataSets.toArray(new IDataSet[dbUnitDataSets.size()]));
		} catch (DataSetException e) {
			logger.warn("Could not create composite dataset");
			throw new RuntimeException("Could not create composite dataset", e);
		}
	}

	@PostConstruct
	public void populateDatabase() throws Exception {
		Assert.assertNotNull("DataSource was null", dataSource);
		Assert.assertNotNull("DataSetLocations were null", dataSetLocations);
		//DatabaseOperation.CLEAN_INSERT.execute(new DatabaseDataSourceConnection(dataSource), getDBUnitDataSet());
	}

	//todo what for?

	public static void main(String[] args) throws Exception {
		// database connection
		Class driverClass = Class.forName("org.hsqldb.jdbcDriver");
		Connection jdbcConnection = DriverManager.getConnection("jdbc:hsqldb:sample", "sa", "");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(connection);
		partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
		partialDataSet.addTable("BAR");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

		// full database export
		IDataSet fullDataSet = connection.createDataSet();
		FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
		IDataSet depDataSet = connection.createDataSet(depTableNames);
		FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml"));
	}
}

