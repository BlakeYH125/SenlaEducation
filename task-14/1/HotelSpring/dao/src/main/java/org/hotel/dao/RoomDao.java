package org.hotel.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hotel.database.HibernateUtil;
import org.hotel.model.entities.Room;
import org.hotel.model.repository.RoomRepository;
import org.hotel.model.enums.Status;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Repository
public final class RoomDao implements RoomRepository {
    @Override
    public void save(final Room roomP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(roomP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Room> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Room", Room.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setAvailable(final Room roomP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            roomP.setStatus(Status.AVAILABLE);
            roomP.setReleasedIn(null);
            session.merge(roomP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStatus(final Room roomP, final Date releasedInP, final Status statusP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            roomP.setStatus(statusP);
            roomP.setReleasedIn(releasedInP);
            session.merge(roomP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNewRoomPrice(final Room roomP, final BigDecimal priceP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            roomP.setPrice(priceP);
            session.merge(roomP);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Room getRoom(final String idP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Room.class, idP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Room> findFreeRoomsByDate(final Date dateP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Room WHERE releasedIn<:releasedInParam OR status=:statusParam";
            Query<Room> query = session.createQuery(hql, Room.class);
            query.setParameter("releasedInParam", dateP);
            query.setParameter("statusParam", Status.AVAILABLE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
