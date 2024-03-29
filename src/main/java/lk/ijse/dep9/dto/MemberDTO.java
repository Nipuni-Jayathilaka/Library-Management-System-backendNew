package lk.ijse.dep9.dto;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.validation.GroupDefinitionException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lk.ijse.dep9.dto.util.Groups;

import java.io.Serializable;


@JsonbPropertyOrder({"id","name","address","contact"})//to give the order that we want to print in the response
public class MemberDTO implements Serializable {
    @NotBlank(groups = Groups.update.class,message = "Member id cannot be empty")
    private String id;
    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[A-Za-z ]+",message = "Invalid Name")
    private String name;
    @NotBlank(message = "Address cannot be empty")
    @Pattern(regexp = "[A-Za-z0-9,:;/\\-\\\\ ]+",message = "Invalid address")
    private String address;
    @NotBlank(message = "Contact cannot be empty")
    @Pattern(regexp = "\\d{3}-\\d{7}",message = "Invalid contact number")
    private String contact;

    public MemberDTO() {
    }

    public MemberDTO(String id, String name, String address, String contact) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
