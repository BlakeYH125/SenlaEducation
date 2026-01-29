package org.hotel.model.management;

import org.hotel.model.entities.Service;
import org.hotel.model.repository.ServiceRepository;
import org.hotel.model.enums.SortType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@org.springframework.stereotype.Service
public final class ServiceManagement {
    /**
     * Репозиторий для работы с услугами в БД.
     */
    private final ServiceRepository serviceRepository;

    public ServiceManagement(final ServiceRepository serviceRepositoryP) {
        this.serviceRepository = serviceRepositoryP;
    }

    public void addNewService(final Service serviceP) {
        serviceRepository.save(serviceP);
    }

    public Service getService(final String idP) {
        return serviceRepository.getService(idP);
    }

    public String getServiceName(final String idP) {
        return getService(idP).getName();
    }

    public BigDecimal getServicePrice(final String idP) {
        return getService(idP).getPrice();
    }

    public List<Service> getServices() {
        return serviceRepository.findAll();
    }

    public void setNewServicePrice(final String idP, final BigDecimal newPriceP) {
        serviceRepository.setNewServicePrice(getService(idP), newPriceP);
    }

    public List<Service> getServicesWithSort(final SortType sortTypeP) {
        List<Service> listServices = getServices();
        if (sortTypeP == SortType.PRICE) {
            listServices.sort(Comparator.comparing(Service::getPrice));
        } else if (sortTypeP == SortType.SECTION) {
            listServices.sort(Comparator.comparing(Service::getServiceSection));
        }
        return listServices;
    }

    public boolean isThereService(final String idP) {
        if (getService(idP) == null) {
            return false;
        }
        return true;
    }
}
