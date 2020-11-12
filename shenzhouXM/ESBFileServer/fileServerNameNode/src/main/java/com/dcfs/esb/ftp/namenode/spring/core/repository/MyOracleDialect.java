package com.dcfs.esb.ftp.namenode.spring.core.repository;

import org.hibernate.MappingException;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * Created by Administrator on 2014/12/29.
 */
public class MyOracleDialect extends Oracle10gDialect {
    public MyOracleDialect() {
        super();
        registerHibernateType(1, "string");//NOSONAR
        registerHibernateType(-9, "string");//NOSONAR
        registerHibernateType(-16, "string");//NOSONAR
        registerHibernateType(3, "double");//NOSONAR

        registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.DECIMAL, StandardBasicTypes.DOUBLE.getName());
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {//NOSONAR
        return "";
    }
}
