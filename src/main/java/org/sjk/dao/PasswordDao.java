package org.sjk.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class PasswordDao {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table if not EXISTS Hasla (" +
                "h_id bigint not null auto_increment primary key, " +
                "h_haslo varchar(20) not null, " +
                "h_u_id bigint not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }

    public void updateRelations() {
        String updatePasswordTableScript="alter table Hasla add foreign key (h_u_id) references Uzytkownicy(u_id);";
        System.out.println("\n"+updatePasswordTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updatePasswordTableScript);
    }
}
