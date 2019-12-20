package com.cdgeekcamp.redas.api.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

@Service
public class EntityManagerFactoryToResult {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public List<Object[]> sqlToResultPage(String sql, Integer page, Integer size){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createNativeQuery(sql);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> resultList = query.getResultList();
        em.close();

        return resultList;
    }

    public List<Object[]> sqlToResult(String sql){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createNativeQuery(sql);

        List<Object[]> resultList = query.getResultList();
        em.close();

        return resultList;
    }
}
