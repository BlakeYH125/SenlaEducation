public class TankAssemblyLine implements IAssemblyLine{
    private ILineStep bodyLineStep;
    private ILineStep engineLineStep;
    private ILineStep turretLineStep;

    public TankAssemblyLine(ILineStep bodyLineStep, ILineStep engineLineStep, ILineStep turretLineStep) {
        this.bodyLineStep = bodyLineStep;
        this.engineLineStep = engineLineStep;
        this.turretLineStep = turretLineStep;
    }

    @Override
    public IProduct assebleProduct(IProduct product) {
        product.installFirstPart(bodyLineStep.buildProductPart());
        product.installSecondPart(engineLineStep.buildProductPart());
        product.installThirdPart(turretLineStep.buildProductPart());
        return product;
    }
}
