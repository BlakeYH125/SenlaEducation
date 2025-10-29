public class Tank implements IProduct{
    private IProductPart tankBody;
    private IProductPart tankEngine;
    private IProductPart tankTurret;

    @Override
    public void installFirstPart(IProductPart productPart) {
        this.tankBody = productPart;
        System.out.println("Установлен танковый корпус.");
    }

    @Override
    public void installSecondPart(IProductPart productPart) {
        this.tankEngine = productPart;
        System.out.println("Установлен танковый двигатель.");
    }

    @Override
    public void installThirdPart(IProductPart productPart) {
        this.tankTurret = productPart;
        System.out.println("Установлена танковая башня.");
    }


}
