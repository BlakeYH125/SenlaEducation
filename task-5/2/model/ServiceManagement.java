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
        services.put(service.getName(), service);
    }

    public Service getService(String name) {
        return services.get(name);
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setNewServicePrice(String name, double newPrice) {
        name = name.toLowerCase();
        Service service = services.get(name);
        service.setPrice(newPrice);
    }

    public List<Service> getServicesWithSort(SortType sortType) {
        List<Service> listServices = new ArrayList<>(services.values());
        if (sortType == SortType.PRICE) {
            listServices.sort(Comparator.comparing(Service::getPrice));
        } else if (sortType == SortType.SECTION) {
            listServices.sort(Comparator.comparing(Service::getServiceSection));
        }
        return listServices;
    }
}
