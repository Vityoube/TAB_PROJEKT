package org.sjk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by vkalashnykov on 08.02.17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {
    public interface  ActionTypes{
        final String LOGIN="logowanie";
        final String LOGOUT="wylogowywanie";
        final String REGISTRATION="rejestracja";
        final String PASSWORD_CHANGE="zmiana hasłą";
        final String BAD_LOGIN_ATTEMPT="próba logowania się";
        final String BLOCK="blokada konta";
        final String UNBLOCK="konto odblokowane";
    }
    private long id;
    private String name;
    private long userId;
    private long ipId;
    private Timestamp actionTime;
}
