package lk.ijse.dep9.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnDTO {
    @NotBlank(message = "Member id cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}",message = "Invalid member id")
    private String memberId;
    @NotEmpty(message = "Return items cannot be empty")
    private List<@NotNull(message = "Return book cannot be empty") @Valid ReturnBookDTO> books=new ArrayList<>();
}
