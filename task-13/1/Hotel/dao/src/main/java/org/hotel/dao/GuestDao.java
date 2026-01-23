package org.hotel.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hotel.annotations.Component;
import org.hotel.database.HibernateUtil;
import org.hotel.model.Guest;
import org.hotel.model.GuestRepository;
import org.hotel.model.GuestStatus;
import org.hotel.model.Room;

import java.util.List;

@Component
public final class GuestDao implements GuestRepository {
    @Override
    public void save(final Guest guestP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(guestP);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Guest> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Guest", Guest.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Guest> findCurrentGuestsInRoom(final Room roomP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Guest WHERE status=:statusParam AND rentRoomId=:rentRoomIdParam";
            Query<Guest> query = session.createQuery(hql, Guest.class);
            query.setParameter("statusParam", GuestStatus.SETTLED);
            query.setParameter("rentRoomIdParam", roomP.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Guest> findCurrentGuestsInHotel() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Guest where status=:statusParam";
            Query<Guest> query = session.createQuery(hql, Guest.class);
            query.setParameter("statusParam", GuestStatus.SETTLED);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Guest getGuest(final String idP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Guest.class, idP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Guest> findPreviousGuests(final Room roomP, final int limitP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Guest WHERE rentRoomId=:rentRoomIdParam AND status=:statusParam";
            Query<Guest> query = session.createQuery(hql, Guest.class);
            query.setParameter("rentRoomIdParam", roomP.getId());
            query.setParameter("statusParam", GuestStatus.EVICTED);
            query.setMaxResults(limitP);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setEvicted(final Guest guestP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            guestP.setStatus(GuestStatus.EVICTED);
            session.merge(guestP);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
