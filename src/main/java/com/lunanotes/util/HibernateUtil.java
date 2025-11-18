package com.lunanotes.util;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    @Getter
    private final SessionFactory sessionFactory;

    public HibernateUtil(){
        sessionFactory = buildSessionFactory();
    }
    public HibernateUtil(Configuration configuration) {
        sessionFactory = configuration.buildSessionFactory();
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void shutdown() {
        getSessionFactory().close();
    }
}
