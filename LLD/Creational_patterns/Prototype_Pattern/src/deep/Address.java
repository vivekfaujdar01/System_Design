package deep;

public class Address implements Cloneable {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city    = city;
        this.country = country;
    }

    /**
     * Deep-clone is trivial here — both fields are Strings (immutable).
     * super.clone() is sufficient.
     */
    @Override
    public Address clone() {
        try {
            return (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Should never happen", e);
        }
    }

    // Getters
    public String getCity()    { return city; }
    public String getCountry() { return country; }

    // Setters
    public void setCity(String city)       { this.city    = city; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}
