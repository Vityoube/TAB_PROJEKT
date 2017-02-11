package org.sjk.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class ActionDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table Akcje(a_id bigint not null auto_increment primary key, " +
                "a_nazwa varchar(20) not null, " +
                "a_u_id bigint not null," +
                "a_i_id bigint not null," +
                "a_czas_akcji timestamp not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }
}
