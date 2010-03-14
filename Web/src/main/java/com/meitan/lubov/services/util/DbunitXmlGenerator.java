package com.meitan.lubov.services.util;

import com.meitan.lubov.model.util.PersistentComparator;
import com.meitan.lubov.model.util.PersistentOrderable;
import org.apache.log4j.Logger;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.impl.SessionFactoryImpl;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DbunitXmlGenerator {
	private static final Logger LOGGER = Logger.getLogger(DbunitXmlGenerator.class);
	public final static String TEST_XML_FILE_NAME = "dataset1.xml";
	public final static String ENTITY_PREFIX = "ent_";

	private String[] beanNames;
	private List<PersistentOrderable> beans = new ArrayList<PersistentOrderable>();

	private static final String SPRING_XML_FILE_NAME = "startData.xml";
	private ListableBeanFactory context = null;

	private EntityManager em;

	public DbunitXmlGenerator(String sourceRoot) throws IOException {
		Map<String, String> env = System.getenv();
		String hibernateSettingsDir = env.get("MEITAN_PROPS");
		String hibernateProps = hibernateSettingsDir + "/hibernate.properties";
		Properties props = new Properties();
		props.load(new FileInputStream(hibernateProps));
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("testMeitanDatabase", props);
		em = emf.createEntityManager();
		File outputFile = new File(sourceRoot + "/" + TEST_XML_FILE_NAME);
		context = new XmlBeanFactory(new FileSystemResource(sourceRoot + "/" + SPRING_XML_FILE_NAME));
		LOGGER.info("Using outputFileName: " + outputFile.getPath());

		setUpBeanNames();
		setUpBeans();
	}

	public static void main(String[] args) throws SQLException, IOException, DatabaseUnitException {
		DbunitXmlGenerator xmlGenerator = new DbunitXmlGenerator(args[0]);
		xmlGenerator.persistBeans();
		//xmlGenerator.generateXml(outputFile);
	}

	public void generateXml(File outputFile) throws SQLException, IOException, DatabaseUnitException {
		try {
			saveResultsToXml(outputFile);
		} catch (SQLException e) {
			LOGGER.error("Can't save results to XML", e);
			throw e;
		} catch (IOException e) {
			LOGGER.error("Can't save results to XML", e);
			throw e;
		} catch (DataSetException e) {
			LOGGER.error("Can't save results to XML", e);
			throw e;
		}
	}

	private void saveResultsToXml(File outputFile) throws SQLException, IOException, DatabaseUnitException {
		IDataSet fullDataSet = null;
		IDatabaseConnection dbConnection;
		try {
			dbConnection = getIDatabaseConnection();
		} catch (SQLException e) {
			LOGGER.error("Can't get database connection", e);
			throw e;
		}

		try {
			fullDataSet = dbConnection.createDataSet();
		} catch (SQLException e) {
			LOGGER.error("Can't create dataset", e);
			throw e;
		}
		try {

			FlatXmlDataSet.write(fullDataSet, new FileOutputStream(outputFile));
		} catch (IOException e) {
			LOGGER.error("Can't write xml file", e);
			throw e;
		} catch (DataSetException e) {
			LOGGER.error("Can't write xml file", e);
			throw e;
		}
	}

	private IDatabaseConnection getIDatabaseConnection() throws SQLException, DatabaseUnitException {
		java.sql.Connection con;
		try {
			con = ((SessionFactoryImpl) ((Session) em.getDelegate()).getSessionFactory()).getSettings()
					.getConnectionProvider().getConnection();
		} catch (SQLException e) {
			LOGGER.error("Can't get connection from sessionFactory", e);
			throw e;
		}
		IDatabaseConnection dbConnection = new DatabaseConnection(con);
		return dbConnection;
	}

	private void setUpBeanNames() {
		beanNames = context.getBeanDefinitionNames();
	}

	private void setUpBeans() {
		for (String beanName : beanNames) {
			if (!beanName.startsWith(ENTITY_PREFIX)) {
				continue;
			}
            //we assume it
			PersistentOrderable bean = (PersistentOrderable) context.getBean(beanName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("For spring configuration name " + beanName + " got bean " + bean);
			}
			beans.add(bean);
		}
	}

	private void persistBeans() {
		SessionFactory sessionFactory = ((Session) em.getDelegate()).getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
            Collections.sort(beans, new PersistentComparator());
			for (Object bean : beans) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Saving bean " + bean);
				}					LOGGER.error("Saving bean " + bean);

				session.saveOrUpdate(bean);
			}
			tx.commit();
		} catch (HibernateException e) {
			LOGGER.error("Can't generate test xml data", e);
			if (tx != null) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					LOGGER.error("can't rollback transaction", e1);
				}
			}
			throw e;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}
}