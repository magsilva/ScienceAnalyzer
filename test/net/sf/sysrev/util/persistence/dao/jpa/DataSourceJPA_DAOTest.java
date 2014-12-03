package net.sf.sysrev.util.persistence.dao.jpa;

import static org.junit.Assert.*;

import net.sf.sysrev.db.DataSource;
import net.sf.sysrev.util.persistence.dao.DataSourceDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DataSourceJPA_DAOTest
{
	@Autowired
	private DataSourceDAO dao;
	
	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testPersist()
	{
		DataSource ds = new DataSource();
		ds.setName("Test");
		ds.setAbstract("Abstract");
		ds.setContent("Content");
		dao.persist(ds);
	}

	@Test
	public void testRemove()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testFindById()
	{
		fail("Not yet implemented");
	}

}
