package org.sjk.dao;

import org.sjk.dto.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
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
        jdbcTemplate.execute(initTableScript);
    }

    public void updateRelations() {
        String updateActionTableScript="alter table Akcje add foreign key (a_u_id) references Uzytkownicy(u_id);\n" +
                "alter table Akcje add foreign key (a_i_id) references IP(i_id);";
        jdbcTemplate.execute(updateActionTableScript);
    }

    public List<Action> findAllActions(long userId){
        String findActionsForUserScript="select * from Akcje where a_u_id="+userId;
        List<Action> userActions=jdbcTemplate.query(findActionsForUserScript,new BeanPropertyRowMapper<Action>(Action.class));
        return userActions;
    }

    public void removeActionForUser(String actionName, Timestamp actionTime, long userId){
        String removeUserActionScript="delete from Akcje where a_nazwa=\'"+actionName+"\' and a_czas_akcji=\'"+actionTime+
                "\' and a_u_id="+userId;
        jdbcTemplate.update(removeUserActionScript);
    }

    public void removeActionsForUser(long userId) {
        String removeUserActionsScript = "delete from Akcje where a_u_id=" + userId;
        jdbcTemplate.update(removeUserActionsScript);
    }
    public void insertAction(String actionName, Timestamp actionTime, long userId, long ipId){
        String insertActionScript="insert into Akcje(a_nazwa,a_czas_akcji,a_u_id,a_i_id) values(\'"+actionName+
                "\', \'"+actionTime+"\',"+userId+","+ipId+")";
        jdbcTemplate.update(insertActionScript);
    }
}
