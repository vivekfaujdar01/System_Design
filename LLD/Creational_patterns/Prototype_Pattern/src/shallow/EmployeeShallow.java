package shallow;

import java.util.List;

/**
 * Demonstrates a SHALLOW COPY using Java's Cloneable + super.clone().
 *
 * Problem:
 *   - Primitive & String fields are safely copied (value semantics).
 *   - Mutable reference fields (Address, List) are NOT duplicated —
 *     both original and clone point to the SAME objects in memory.
 *   - Mutating the nested object through one reference affects the other.
 */
public class EmployeeShallow implements Cloneable {

    private String       name;
    private int          age;
    private String       department;
    private Address      address;   // ⚠️ mutable nested object
    private List<String> skills;    // ⚠️ mutable collection

    public EmployeeShallow(String name, int age, String department,
                           Address address, List<String> skills) {
        this.name       = name;
        this.age        = age;
        this.department = department;
        this.address    = address;
        this.skills     = skills;
    }

    /**
     * SHALLOW clone — super.clone() copies field values as-is.
     * Reference fields share the same memory address as the original.
     */
    @Override
    public EmployeeShallow clone() {
        try {
            return (EmployeeShallow) super.clone();
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
