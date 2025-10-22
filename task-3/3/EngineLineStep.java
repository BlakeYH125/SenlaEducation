public class EngineLineStep implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("*Изготовление танкового двигателя*");
        return new TankEngine();
    }
}
