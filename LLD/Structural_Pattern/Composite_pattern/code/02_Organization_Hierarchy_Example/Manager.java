import java.util.ArrayList;
import java.util.List;

public class Manager implements EmployeeComponent {
    private final String name;
    private final String department;
    private final List<EmployeeComponent> subordinates = new ArrayList<>();

    public Manager(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public void addSubordinate(EmployeeComponent employee) {
        subordinates.add(employee);
    }

    public void removeSubordinate(EmployeeComponent employee) {
        subordinates.remove(employee);
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "Manager of " + department; }

    @Override
    public double getSalary() {
        double totalBudget = 0;
        for (EmployeeComponent subordinate : subordinates) {
            totalBudget += subordinate.getSalary();
        }
        return totalBudget;
    }

    @Override
    public void showDetails() {
        System.out.println("\n👔 Manager: " + name + " [" + department + " Department]");
        for (EmployeeComponent subordinate : subordinates) {
            subordinate.showDetails();
        }
    }
}
