package net.sf.sysrev.util.persistence.dao.jpa;

import static org.junit.Assert.*;

import net.sf.sysrev.db.DataSource;
import net.sf.sysrev.util.persistence.dao.DAO;

import org.junit.Before;
import org.junit.Test;

public class JPA_DAOTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testJPA_DAO()
	{
		DAO<Long, DataSource> dao = new JPA_DAO<Long, DataSource>();
	}

}
