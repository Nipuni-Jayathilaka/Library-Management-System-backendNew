package lk.ijse.dep9.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBookDTO {
    @NotNull(message = "Issue Id cannot be empty")
    private Integer issueNoteId;
    @NotEmpty(message = "isbn cannot be empty")
    @Pattern(regexp = "\\d{3}-\\d-\\d{6}-\\d{2}-\\d",message = "Book isbn is invalid")
    private String isbn;
}
