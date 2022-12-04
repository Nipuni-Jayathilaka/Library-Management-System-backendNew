package lk.ijse.dep9.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueNoteDTO {
    @Null(message = "Issue Note ID cannot have a value")
    private Integer id;
    @NotNull(message = "Date cannot be  empty")
    private LocalDate date;
    @NotBlank(message = "Member id cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}",message = "Invalid member id")
    private String memberId;
    @NotEmpty(message = "Books cannot be empty")
    private ArrayList<
            @NotNull(message = "isbn cannot be null value")
                    @Pattern(regexp = "\\d{3}-\\d-\\d{6}-\\d{2}-\\d",message = "Invalid ISBN")
            String> books=new ArrayList<>();
}
