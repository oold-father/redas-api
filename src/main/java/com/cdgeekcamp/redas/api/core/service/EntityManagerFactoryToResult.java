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

    /**
     * 通过sql语句查询结果，分页查询
     * @param sql sql语句
     * @param page 页码
     * @param size 每页条数
     * @return List
     */
    public List<Object[]> sqlToResultPage(String sql, Integer page, Integer size){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createNativeQuery(sql);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> resultList = query.getResultList();
        em.close();

        return resultList;
    }

    /**
     * 通过sql语句查询结果，不分页查询
     * @param sql sql语句
     * @return List
     */
    public List<Object[]> sqlToResult(String sql){
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createNativeQuery(sql);

        List<Object[]> resultList = query.getResultList();
        em.close();

        return resultList;
    }
}
