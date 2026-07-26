public class Main {
    public static void main(String[] args) {
        EmployeeComponent dev1 = new Developer("Rahul Sharma", 1200000);
        EmployeeComponent designer1 = new Designer("Priya Verma", 900000);

        Manager frontendManager = new Manager("Amit Patel", "Frontend Lead");
        frontendManager.addSubordinate(dev1);
        frontendManager.addSubordinate(designer1);

        EmployeeComponent dev2 = new Developer("Vikram Singh", 1500000);
        EmployeeComponent dev3 = new Developer("Neha Gupta", 1400000);

        Manager backendManager = new Manager("Suresh Kumar", "Backend Lead");
        backendManager.addSubordinate(dev2);
        backendManager.addSubordinate(dev3);

        Manager vpEngineering = new Manager("Rajesh Iyer", "VP Engineering");
        vpEngineering.addSubordinate(frontendManager);
        vpEngineering.addSubordinate(backendManager);

        System.out.println("=== COMPANY ORGANIZATION CHART ===");
        vpEngineering.showDetails();

        System.out.println("\n=== SALARY BUDGET CALCULATIONS ===");
        System.out.println("Frontend Team Budget: ₹" + frontendManager.getSalary());
        System.out.println("Backend Team Budget:  ₹" + backendManager.getSalary());
        System.out.println("Total VP Engineering Org Budget: ₹" + vpEngineering.getSalary());
    }
}
