package com.acj.acjsignature.mobile.androidws.dto.request;

import com.acj.acjsignature.mobile.androidws.util.Constants;
import com.acj.acjsignature.mobile.androidws.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.EMAIL_INVALID)
    @Size(min = Constants.EMAIL_MIN_LENGTH, max = Constants.EMAIL_MAX_LENGTH,
          message = "El email debe tener entre 3 y 100 caracteres")
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    @Size(min = 6, max = Constants.PASSWORD_MAX_LENGTH,
          message = "La contrasena debe tener entre 6 y 255 caracteres")
    private String password;
}
