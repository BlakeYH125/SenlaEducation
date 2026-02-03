package org.hotel.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hotel.database.HibernateUtil;
import org.hotel.model.entities.Guest;
import org.hotel.model.entities.UsedService;
import org.hotel.model.repository.UsedServiceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public final class UsedServiceDao implements UsedServiceRepository {
    @Override
    public void save(final UsedService usedServiceP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(usedServiceP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UsedService> findServicesUsedByGuest(final Guest guestP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM UsedService WHERE guestId = :guestIdParam";
            Query<UsedService> query = session.createQuery(hql, UsedService.class);
            query.setParameter("guestIdParam", guestP.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
