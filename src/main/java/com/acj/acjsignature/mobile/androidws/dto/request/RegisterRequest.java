package com.acj.acjsignature.mobile.androidws.dto.request;

import com.acj.acjsignature.mobile.androidws.util.Constants;
import com.acj.acjsignature.mobile.androidws.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.EMAIL_INVALID)
    @Size(max = Constants.EMAIL_MAX_LENGTH, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    @Size(min = 6, max = Constants.PASSWORD_MAX_LENGTH,
          message = "La contrasena debe tener entre 6 y 255 caracteres")
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = MessageConstants.PASSWORD_PATTERN_MESSAGE)
    private String password;

    @NotBlank(message = MessageConstants.DNI_REQUIRED)
    @Pattern(regexp = Constants.DNI_PATTERN, message = MessageConstants.DNI_PATTERN)
    private String dni;

    @NotBlank(message = MessageConstants.FIRST_NAME_REQUIRED)
    @Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH,
          message = MessageConstants.FIRST_NAME_SIZE)
    private String firstName;

    @NotBlank(message = MessageConstants.LAST_NAME_REQUIRED)
    @Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH,
          message = MessageConstants.LAST_NAME_SIZE)
    private String lastName;
}
