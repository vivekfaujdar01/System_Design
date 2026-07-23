package deep;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates a DEEP COPY using Java's Cloneable.
 *
 * Every mutable nested field is explicitly cloned so that the original
 * and the copy are completely independent — mutating one never affects the other.
 */
public class EmployeeDeep implements Cloneable {

    private String       name;
    private int          age;
    private String       department;
    private Address      address;   // mutable → must be deep-cloned
    private List<String> skills;    // mutable → must be deep-cloned

    public EmployeeDeep(String name, int age, String department,
                        Address address, List<String> skills) {
        this.name       = name;
        this.age        = age;
        this.department = department;
        this.address    = address;
        this.skills     = skills;
    }

    /**
     * DEEP clone:
     *   1. super.clone() creates a shallow field-copy of the Employee.
     *   2. We then replace each mutable reference with its own independent copy.
     */
    @Override
    public EmployeeDeep clone() {
        try {
            EmployeeDeep copy = (EmployeeDeep) super.clone();  // step 1 — shallow base

            // step 2 — deep-clone every mutable nested field
            copy.address = this.address.clone();               // new Address object
            copy.skills  = new ArrayList<>(this.skills);       // new List with same elements

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Should never happen — class implements Cloneable", e);
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String       getName()       { return name; }
    public int          getAge()        { return age; }
    public String       getDepartment() { return department; }
    public Address      getAddress()    { return address; }
    public List<String> getSkills()     { return skills; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setName(String name)             { this.name       = name; }
    public void setAge(int age)                  { this.age        = age; }
    public void setDepartment(String department) { this.department = department; }
    public void setAddress(Address address)      { this.address    = address; }
    public void setSkills(List<String> skills)   { this.skills     = skills; }

    @Override
    public String toString() {
        return String.format(
            "Employee{name='%s', age=%d, dept='%s', address=%s, skills=%s}",
            name, age, department, address, skills
        );
    }
}
