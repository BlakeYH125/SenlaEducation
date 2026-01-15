package org.hotel.model;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class ServiceManagement {

    @Inject
    private ServiceRepository serviceRepository;

    public ServiceManagement() {
    }

    public void addNewService(Service service) {
        serviceRepository.save(service);
    }

    public Service getService(String id) {
        return serviceRepository.getService(id);
    }

    public String getServiceName(String id) {
        return getService(id).getName();
    }

    public BigDecimal getServicePrice(String id) {
        return getService(id).getPrice();
    }

    public List<Service> getServices() {
        return serviceRepository.findAll();
    }

    public void setNewServicePrice(String id, BigDecimal newPrice) {
        serviceRepository.setNewServicePrice(getService(id), newPrice);
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
