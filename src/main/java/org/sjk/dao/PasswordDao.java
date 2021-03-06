package org.sjk.dao;

import org.sjk.dto.Password;
import org.sjk.exception.PasswordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
                "h_haslo varchar(20) UNIQUE not null, " +
                "h_u_id bigint);";
        jdbcTemplate.execute(initTableScript);
    }

    public void updateRelations() {
        String updatePasswordTableScript="alter table Hasla add foreign key (h_u_id) references Uzytkownicy(u_id);";
        jdbcTemplate.execute(updatePasswordTableScript);
    }

    public long insertPassword(String password) {
        String passwordInsertScript="insert into Hasla (h_haslo) values(?)";
        jdbcTemplate.update(passwordInsertScript,password);
        String passwordIdSelect="select h_id from Hasla where h_haslo=?";
        Long passwordId = jdbcTemplate.queryForObject(passwordIdSelect, new Object[]{password}, Long.class);
        return passwordId;
    }

    public void updatePassword(long userId, long passwordId) {
        String passwordUpdateUserId="update Hasla set h_u_id=? where h_id=?";
        jdbcTemplate.update(passwordUpdateUserId,userId,passwordId);
    }

    public void changePassword(String oldPassword, String newPassword)
            throws PasswordNotFoundException {
        if (!findOldPassword(oldPassword))
            throw new PasswordNotFoundException();
        long oldPasswordId=findOldPasswordId(oldPassword);
        String updatePasswordScript="update Hasla set h_haslo=?" +
                " where h_id=?;";
        jdbcTemplate.update(updatePasswordScript,new Object[]{newPassword,oldPasswordId});
    }

    public boolean findOldPassword(String oldPassword){
        try {
            String findOldPasswordScript = "select * from Hasla where h_haslo=?";
            Password password = jdbcTemplate.queryForObject(findOldPasswordScript,
                    new BeanPropertyRowMapper<Password>(Password.class), oldPassword);
            return true;
        } catch (EmptyResultDataAccessException e){
            return false;
        }
    }

    public boolean findPasswordOfUser(String username, String passwordText){
        try {
            String findPasswordOfUserScript = "select * from Hasla where h_haslo=? and h_u_id=" +
                    "(select u_id from Uzytkownicy where u_nazwa_uzytkownika=?);";
            Password password = jdbcTemplate.queryForObject(findPasswordOfUserScript,
                    new BeanPropertyRowMapper<Password>(Password.class), new Object[]{passwordText, username});
            return true;
        } catch(EmptyResultDataAccessException e){
            return false;
        }
    }
    public boolean findPassword(String passwordText){
        try {
            String findPasswordScript = "select * from Hasla where h_haslo=?";
            Password password = jdbcTemplate.queryForObject(findPasswordScript,
                    new BeanPropertyRowMapper<Password>(Password.class), new Object[]{passwordText});
            return true;
        } catch(EmptyResultDataAccessException e){
            return false;
        }
    }

    public long findOldPasswordId(String oldPassword){
        String findOldPasswordIdScript="select h_id from Hasla where h_haslo=?";
        try{
            long passwordId=jdbcTemplate.queryForObject(findOldPasswordIdScript,new Object[]{oldPassword},Long.class);
            return passwordId;
        } catch (EmptyResultDataAccessException e){
            return 0;
        }
    }

}
