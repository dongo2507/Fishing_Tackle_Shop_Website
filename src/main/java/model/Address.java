
package model;

public class Address {

    private String id;
    private String street;
    private String ward;
    private String district;
    private String city;
    private boolean isDefault;
    private Customer customer;

    public Address() {
    }

    public Address(String id, String street, String ward,
                   String district, String city,
                   boolean isDefault, Customer customer) {
        this.id = id;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
