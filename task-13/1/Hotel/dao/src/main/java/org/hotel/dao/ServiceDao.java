package org.hotel.dao;

import org.hibernate.Session;
import org.hotel.annotations.Component;
import org.hotel.database.HibernateUtil;
import org.hotel.model.entities.Service;
import org.hotel.model.repository.ServiceRepository;


import java.math.BigDecimal;
import java.util.List;

@Component
public final class ServiceDao implements ServiceRepository {
    @Override
    public void save(final Service serviceP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(serviceP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Service> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Service", Service.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setNewServicePrice(final Service serviceP, final BigDecimal priceP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            serviceP.setPrice(priceP);
            session.merge(serviceP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Service getService(final String idP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Service.class, idP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
