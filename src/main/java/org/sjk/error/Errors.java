package org.sjk.error;

/**
 * Created by vkalashnykov on 12.02.17.
 */
public enum Errors {
    IP_NOT_FOUND("Strona niedostępna"),
    UNSUFFICIENT_PRIVILEGE("Niewsytarczające uprawnienia."),
    USER_ONLINE("Użytkownik z takim imeniem juz jest zalogowany."),
    USER_NOT_FOUND("Uzytkownik nie jest odnaleziony"),
    BAD_CREDENTIALS("Niepoprawne hasłó"),
    PASSWORD_EXISTS("To hasło juz zajęte."),
    USER_EXISTS("Nazwa użytkownika zajęta, proszę sprobować inną."),
    USERNAME_NOT_NULL("Nazwa użytkownika nie moze byc pusta."),
    EMAIL_NOT_NULL("Email nie może być pusty."),
    USER_BLOCKED("Bład podczas próby logowania sie. Proszę sprobować później.");


    private final String errorDescripton;

    Errors(String errorDescription) {
        this.errorDescripton=errorDescription;
    }

    public String getErrorDescripton() {
        return errorDescripton;
    }
}
