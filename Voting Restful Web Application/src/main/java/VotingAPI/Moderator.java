package VotingAPI;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;


//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;

public class Moderator{

    int id;

    private String name;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date created_at;


    public Moderator()
    {

    }
    public int getid()
    {
        return id;
    }
    public void setid(int id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public String getEmail()
    {
        return email;
    }
    public String getPassword()
    {
        return password;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public void setCreated_at()
    {
        this.created_at = java.util.Calendar.getInstance().getTime();
    }
    public Date getCreated_at()
    {
        return created_at;
    }
}