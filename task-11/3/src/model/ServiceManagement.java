package model;

import annotations.Component;
import annotations.Inject;
import annotations.PostConstruct;
import dao.ServiceDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServiceManagement {
    private Map<String, Service> services;

    @Inject
    private ServiceDao serviceDao;

    public ServiceManagement() {
        this.services = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        services.clear();
        List<Service> services = serviceDao.findAll();
        for (Service service : services) {
            this.services.put(service.getId(), service);
        }
    }

    public void addNewService(Service service) {
        serviceDao.save(service);
        services.put(service.getId(), service);
    }

    public Service getService(String id) {
        return services.get(id);
    }

    public String getServiceName(String id) {
        return getService(id).getName();
    }

    public BigDecimal getServicePrice(String id) {
        return getService(id).getPrice();
    }

    public Map<String, Service> getServices() {
        return new HashMap<>(services);
    }

    public void setNewServicePrice(String id, BigDecimal newPrice) {
        serviceDao.setNewServicePrice(getService(id), newPrice);
        Service service = services.get(id);
        service.setPrice(newPrice);
    }

    public List<Service> getServicesWithSort(SortType sortType) {
        List<Service> listServices = new ArrayList<>(getServices().values());
        if (sortType == SortType.PRICE) {
            listServices.sort(Comparator.comparing(Service::getPrice));
        } else if (sortType == SortType.SECTION) {
            listServices.sort(Comparator.comparing(Service::getServiceSection));
        }
        return listServices;
    }
}
