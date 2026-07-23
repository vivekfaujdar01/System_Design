package deep;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Runner that demonstrates the DEEP COPY — full object independence.
 *
 * Run with:
 *   javac src/deep/*.java -d out/
 *   java -cp out deep.MainDeep
 */
public class MainDeep {
    public static void main(String[] args) {

        // ── Create original employee ────────────────────────────────────────────
        Address      addr   = new Address("Bangalore", "India");
        java.util.List<String> skills = new ArrayList<>(Arrays.asList("Java", "SQL"));
        EmployeeDeep original = new EmployeeDeep("Alice", 30, "Engineering", addr, skills);

        System.out.println("=== Before Clone ===");
        System.out.println("Original : " + original);

        // ── Deep clone ──────────────────────────────────────────────────────────
        EmployeeDeep clone = original.clone();
        clone.setName("Bob");

        System.out.println("\n=== After clone.setName(\"Bob\") ===");
        System.out.println("Original : " + original);   // name still "Alice" ✅
        System.out.println("Clone    : " + clone);

        // ── Mutate nested Address through the CLONE ─────────────────────────────
        clone.getAddress().setCity("Mumbai");

        System.out.println("\n=== After clone.getAddress().setCity(\"Mumbai\") ===");
        System.out.println("Original : " + original);   // ✅ still Bangalore!
        System.out.println("Clone    : " + clone);

        // ── Add skill through the CLONE ─────────────────────────────────────────
        clone.getSkills().add("Kubernetes");

        System.out.println("\n=== After clone.getSkills().add(\"Kubernetes\") ===");
        System.out.println("Original : " + original);   // ✅ list unchanged!
        System.out.println("Clone    : " + clone);

        // ── Identity checks ─────────────────────────────────────────────────────
        System.out.println("\n=== Reference Identity ===");
        System.out.println("Same Employee object?  " + (original == clone));                          // false ✅
        System.out.println("Same Address object?   " + (original.getAddress() == clone.getAddress())); // false ✅
        System.out.println("Same Skills list?      " + (original.getSkills()  == clone.getSkills()));  // false ✅
    }
}
