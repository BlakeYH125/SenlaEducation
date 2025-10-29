public class BodyLineStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("*Изготовление танкового корпуса*");
        return new TankBody();
    }
}
