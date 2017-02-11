package org.sjk.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class PasswordDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table Hasla (" +
                "h_id bigint not null auto_increment primary key, " +
                "h_haslo varchar(20) not null, " +
                "h_u_id bigint not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
//        String addForeignKeyScript="alter table Hasla add foreign key (h_u_id) references Uzytkownicy(u_id);";
//        System.out.println("\n"+addForeignKeyScript.toUpperCase()+"\n");
//        jdbcTemplate.execute(addForeignKeyScript);
    }

}
