package model;

import java.util.Date;

public class Customer{
    private String customerId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Date createdAt;
    public Customer(){
   
    }
    public Customer (String customerId,String fullName,
                 String email, String password,
                 String phone, Date createdAt){
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createdAt = createdAt;
    }
    public String getCustomerId(){
        return customerId;
    }
    public void setCustomerId(String customerId){
        this.customerId = customerId;
    }
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
}