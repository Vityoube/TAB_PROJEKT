package org.sjk.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initUserScript="create table Uzytkownicy(" +
                "u_id bigint not null auto_increment primary key , " +
                "u_nazwa_uzytkownika varchar(20) not null," +
                "u_h_id bigint not null," +
                "u_status_rejestracji varchar(30)," +
                "u_status varchar(10)," +
                "u_imie varchar(30), " +
                "u_nazwisko varchar(30), " +
                "u_adres varchar(100), " +
                "u_telefon varchar(20)" +
                ");";
        System.out.println("\n"+initUserScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initUserScript);
        String updateUserTableScript="alter table Uzytkownicy add foreign key (u_h_id) references Hasla(h_id);";
        System.out.println("\n"+updateUserTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updateUserTableScript);
        String updatePasswordTableScript="alter table Hasla add foreign key (h_u_id) references Uzytkownicy(u_id);";
        System.out.println("\n"+updatePasswordTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updatePasswordTableScript);
        String updateActionTableScript="alter table Akcje add foreign key (a_u_id) references Uzytkownicy(u_id);\n" +
                "alter table Akcje add foreign key (a_i_id) references IP(i_id);";
        System.out.println("\n"+updateActionTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updateActionTableScript);
    }

}
