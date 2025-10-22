public class TankMain {
    public static void main(String[] args) {
        ILineStep bodyLineStep = new BodyLineStep();
        ILineStep engineLineStep = new EngineLineStep();
        ILineStep turretLineStep = new TurretLineStep();

        IAssemblyLine tankAssemblyLine = new TankAssemblyLine(bodyLineStep, engineLineStep, turretLineStep);

        IProduct unAssembledtank = new Tank();
        IProduct assembledTank = tankAssemblyLine.assebleProduct(unAssembledtank);
        if (assembledTank != null) {
            System.out.println("Танк собран.");
        }
    }
}
