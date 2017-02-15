package org.sjk.error;

/**
 * Created by vkalashnykov on 12.02.17.
 */
public enum Errors {
    IP_NOT_FOUND("Strona niedostępna"),
    UNSUFFICIENT_PRIVILEGE("Niewystarczające uprawnienia."),
    USER_NOT_FOUND("Uzytkownik nie jest odnaleziony"),
    BAD_CREDENTIALS("Niepoprawne hasłó"),
    PASSWORD_EXISTS("To hasło juz zajęte."),
    USER_EXISTS("Nazwa użytkownika zajęta, proszę sprobować inną."),
    USERNAME_NOT_NULL("Nazwa użytkownika nie moze byc pusta."),
    EMAIL_NOT_NULL("Email nie może być pusty."),
    USER_BLOCKED("Bład podczas próby logowania sie. Proszę sprobować później."),
    PASSWORD_TOO_SHORT("Hasło musi liczyc conajmniej 8 znaków"),
    PASSWORD_TOO_LONG("Hasło za długie"),
    PASSWORD_NOT_SAME("Hasła nie są takie same"),
    PASSWORD_NOT_NULL("Hasło nie może być puste"),
    WRONG_PASSWORD("Błędne hasło.");


    private final String errorDescription;

    Errors(String errorDescription) {
        this.errorDescription =errorDescription;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
