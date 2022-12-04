package lk.ijse.dep9.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lk.ijse.dep9.dto.util.Groups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO implements Serializable {
    @NotBlank(groups = Groups.update.class,message = "Book ISBN cannot be empty")
    @Pattern(regexp = "\\d{3}-\\d-\\d{6}-\\d{2}-\\d",message = "Book ISBN is invalid")
    private String isbn;
    @NotBlank(message = "Book Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9 ]+",message = "Book name is invalid")
    private String bookName;
    @NotBlank(message = "Book author cannot be empty")
    @Pattern(regexp = "[a-zA-Z ]+",message = "Book author is invalid")
    private String author;
    @NotNull(message = "Contact cannot be null")
    @Min(value = 1,message = "Enter at least one contact")
    private Integer copies;


}
