package model;

import annotations.Component;
import annotations.Inject;
import dao.ServiceDao;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class ServiceManagement {

    @Inject
    private ServiceDao serviceDao;

    public ServiceManagement() {
    }

    public void addNewService(Service service) {
        serviceDao.save(service);
    }

    public Service getService(String id) {
        return serviceDao.getService(id);
    }

    public String getServiceName(String id) {
        return getService(id).getName();
    }

    public BigDecimal getServicePrice(String id) {
        return getService(id).getPrice();
    }

    public List<Service> getServices() {
        return serviceDao.findAll();
    }

    public void setNewServicePrice(String id, BigDecimal newPrice) {
        serviceDao.setNewServicePrice(getService(id), newPrice);
    }

    public List<Service> getServicesWithSort(SortType sortType) {
        List<Service> listServices = getServices();
        if (sortType == SortType.PRICE) {
            listServices.sort(Comparator.comparing(Service::getPrice));
        } else if (sortType == SortType.SECTION) {
            listServices.sort(Comparator.comparing(Service::getServiceSection));
        }
        return listServices;
    }

    public boolean isThereService(String id) {
        if (getService(id) == null) {
            return false;
        }
        return true;
    }
}
