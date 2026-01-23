package org.hotel.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hotel.annotations.Component;
import org.hotel.database.HibernateUtil;
import org.hotel.model.Guest;
import org.hotel.model.UsedService;
import org.hotel.model.UsedServiceRepository;

import java.util.List;

@Component
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
