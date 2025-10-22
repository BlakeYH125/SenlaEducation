public class TurretLineStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("*Изготовление танковой башни*");
        return new TankTurret();
    }
}
