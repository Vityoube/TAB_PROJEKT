package org.sjk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by vkalashnykov on 08.02.17.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("session")
public class User {
    public interface RegistrationStatuses{
        final String PENDING="w_trakcie";
        final String REGISTERED="zerejestrowany";
        final String BLOCKED="zablokowany";
    }
    public interface UserStatuses{
        final String ADMIN="admin";
        final String USER="user";
    }
    private long id;
    private String userName;
    private long passwordId;
    @Pattern(regexp="([0-9a-zA-Z&!])+((\\.)([a-zA-z0-9&!])+)*@([0-9A-Za-z])+((\\.)([A-Za-z0-9])+)+")
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    @Pattern(regexp = "(\\+[1-9][0-9]{0,2}\\-([0-9]){3}\\-([0-9]){3}\\-([0-9]){3})|(\\+[1-9][0-9]{0,2}\\-[1-9][0-9]\\-([0-9]){2}\\-([0-9]){2}\\-([0-9]){3})")
    private String phone;
    private String registrationStatus;
    private String userStatus;
    private boolean online;
    private Timestamp blockTime;
}
