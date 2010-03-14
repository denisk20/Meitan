package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:07:54
 */
public class ConsultantIntegrationTest extends GenericIntegrationTest<Consultant> {
    @Autowired
    private ConsultantDao consultantDao;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_creamAdmier");
    }

    @Override
    protected Dao<Consultant, Long> getDAO() {
        return consultantDao;
    }
}
