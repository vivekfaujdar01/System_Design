public class Developer implements EmployeeComponent {
    private final String name;
    private final double salary;

    public Developer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override public String getName() { return name; }
    @Override public String getRole() { return "Software Engineer"; }
    @Override public double getSalary() { return salary; }

    @Override
    public void showDetails() {
        System.out.println("  💻 Developer: " + name + " | Role: " + getRole() + " | Salary: ₹" + salary);
    }
}
