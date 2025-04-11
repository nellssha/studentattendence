package model;
public class Instructor {
    private int id;
    private String name;
    private String email;
    private String username;
    private String department;
    
    public Instructor() {
    }
    
    public Instructor(int id, String name, String email, String username, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.department = department;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return name;
    }
}