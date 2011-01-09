package com.meitan.lubov.services;

import com.meitan.lubov.services.util.sync.MeitanSyncronizerImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Date: Jan 8, 2011
 * Time: 6:07:59 PM
 *
 * @author denisk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
@Ignore
public class MeitanSyncTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Test
	public void getCategories() throws IOException, XPathExpressionException {
		MeitanSyncronizerImpl syncronizer = applicationContext.getBean(MeitanSyncronizerImpl.class);
		syncronizer.setUrl("file:///media/Windows_data/Mama/Meitan-Mama/Web/src/test/resources/meitan.ru.catalog.html");
		syncronizer.sync();
	}

}
