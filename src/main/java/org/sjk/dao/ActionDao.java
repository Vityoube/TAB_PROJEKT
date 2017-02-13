package org.sjk.dao;

import org.sjk.dto.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class ActionDao {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table if not EXISTS Akcje(a_id bigint not null auto_increment primary key, " +
                "a_nazwa varchar(30) not null, " +
                "a_u_id bigint not null, " +
                "a_i_id bigint not null, " +
                "a_czas_akcji timestamp not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }

    public void updateRelations() {
        String updateActionTableScript="alter table Akcje add foreign key (a_u_id) references Uzytkownicy(u_id);\n" +
                "alter table Akcje add foreign key (a_i_id) references IP(i_id);";
        System.out.println("\n"+updateActionTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updateActionTableScript);
    }

    public List<Action> findAllActions(long userId){
        String findActionsForUserScript="select * from Akcje where a_u_id=?";
        System.out.println("\n"+findActionsForUserScript.toUpperCase()+"\n");
        List<Action> userActions=jdbcTemplate.query(findActionsForUserScript,new BeanPropertyRowMapper<Action>(Action.class),userId);
        return userActions;
    }

    public void removeActionForUser(String actionName, Timestamp actionTime, long userId){
        String removeUserActionScript="delete from Akcje where a_nazwa=? and a_czas_akcji=? and a_u_id=?";
        System.out.println("\n"+removeUserActionScript.toUpperCase()+"\n");
        jdbcTemplate.update(removeUserActionScript,new Object[]{actionName,actionTime,userId});
    }

    public void removeActionsForUser(long userId){
        String removeUserActionsScript="delete from Akcje where a_u_id=?";
        System.out.println("\n"+removeUserActionsScript.toUpperCase()+"\n");
        jdbcTemplate.update(removeUserActionsScript,new Object[]{userId});
    }

    public void insertAction(String actionName, Timestamp actionTime, long userId, long ipId){
        String insertActionScript="insert into Akcje(a_nazwa,a_czas_akcji,a_u_id,a_i_id) values(?,?,?,?);";
        System.out.println("\n"+insertActionScript.toUpperCase()+"\n");
        jdbcTemplate.update(insertActionScript,new Object[]{actionName,actionTime,userId,ipId});
    }
}
