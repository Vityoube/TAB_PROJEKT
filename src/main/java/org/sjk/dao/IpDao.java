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
public class IpDao {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table if not EXISTS IP (i_id bigint not null auto_increment primary key," +
                "i_numer varchar(15) not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }
}
