public class Designer implements EmployeeComponent {
    private final String name;
    private final double salary;

    public Designer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "UI/UX Designer"; }
    @Override public double getSalary() { return salary; }

    @Override
    public void showDetails() {
        System.out.println("  🎨 Designer: " + name + " | Role: " + getRole() + " | Salary: ₹" + salary);
    }
}
