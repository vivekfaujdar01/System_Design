package shallow;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Runner that demonstrates the SHALLOW COPY problem.
 *
 * Run with:
 *   javac src/shallow/*.java -d out/
 *   java -cp out shallow.MainShallow
 */
public class MainShallow {
    public static void main(String[] args) {

        // ── Create original employee ────────────────────────────────────────────
        Address      addr   = new Address("Bangalore", "India");
        java.util.List<String> skills = new ArrayList<>(Arrays.asList("Java", "SQL"));
        EmployeeShallow original = new EmployeeShallow("Alice", 30, "Engineering", addr, skills);

        System.out.println("=== Before Clone ===");
        System.out.println("Original : " + original);

        // ── Shallow clone ───────────────────────────────────────────────────────
        EmployeeShallow clone = original.clone();
        clone.setName("Bob");                     // Safe — String is immutable

        System.out.println("\n=== After clone.setName(\"Bob\") ===");
        System.out.println("Original : " + original);   // name still "Alice" ✅
        System.out.println("Clone    : " + clone);

        // ── Mutate nested Address through the CLONE ─────────────────────────────
        clone.getAddress().setCity("Mumbai");

        System.out.println("\n=== After clone.getAddress().setCity(\"Mumbai\") ===");
        System.out.println("Original : " + original);   // ❌ city changed to Mumbai!
        System.out.println("Clone    : " + clone);

        // ── Add skill through the CLONE ─────────────────────────────────────────
        clone.getSkills().add("Kubernetes");

        System.out.println("\n=== After clone.getSkills().add(\"Kubernetes\") ===");
        System.out.println("Original : " + original);   // ❌ skill added to original!
        System.out.println("Clone    : " + clone);

        // ── Identity checks ─────────────────────────────────────────────────────
        System.out.println("\n=== Reference Identity ===");
        System.out.println("Same Employee object?  " + (original == clone));                          // false
        System.out.println("Same Address object?   " + (original.getAddress() == clone.getAddress())); // true ⚠️
        System.out.println("Same Skills list?      " + (original.getSkills()  == clone.getSkills()));  // true ⚠️
    }
}
