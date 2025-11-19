package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceManagement {
    private Map<String, Service> services;

    public ServiceManagement() {
        this.services = new HashMap<>();
    }

    public void addNewService(Service service) {
        services.put(service.getId(), service);
    }

    public Service getService(String id) {
        return services.get(id);
    }

    public String getServiceName(String id) {
        return services.get(id).getName();
    }

    public double getServicePrice(String id) {
        return services.get(id).getPrice();
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setNewServicePrice(String id, double newPrice) {
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
