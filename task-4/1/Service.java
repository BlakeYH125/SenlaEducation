public class Service implements Priceable{
    private String name;
    private double price;
    private ServiceSection serviceSection;

    public Service(String name, double price, ServiceSection serviceSection) {
        this.name = name;
        this.price = price;
        this.serviceSection = serviceSection;
    }

    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public ServiceSection getServiceSection() {
        return serviceSection;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + ", стоимость: " + price;
    }
}
