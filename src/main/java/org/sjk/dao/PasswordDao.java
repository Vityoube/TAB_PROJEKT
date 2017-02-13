package org.sjk.dao;

import org.sjk.dto.Password;
import org.sjk.exception.PasswordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
                "h_u_id bigint not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }

    public void updateRelations() {
        String updatePasswordTableScript="alter table Hasla add foreign key (h_u_id) references Uzytkownicy(u_id);";
        System.out.println("\n"+updatePasswordTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updatePasswordTableScript);
    }

    public long insertPassword(String password) {
        String passwordInsertScript="insert into Hasla (h_haslo) values(?)";
        System.out.println("\n"+passwordInsertScript.toUpperCase()+"\n");
        jdbcTemplate.update(passwordInsertScript,password);
        String passwordIdSelect="select h_id from Hasla where h_haslo=?";
        System.out.println("\n"+passwordIdSelect.toUpperCase()+"\n");
        Long passwordId = jdbcTemplate.queryForObject(passwordIdSelect, new Object[]{password}, Long.class);
        return passwordId;
    }

    public void updatePassword(long userId, long passwordId) {
        String passwordUpdateUserId="update Hasla set h_u_id=? where h_id=?";
        System.out.println("\n"+passwordUpdateUserId.toUpperCase()+"\n");
        jdbcTemplate.update(passwordUpdateUserId,userId,passwordId);
    }

    public void changePassword(String oldPassword, String newPassword)
            throws PasswordNotFoundException {
        if (!findOldPassword(oldPassword))
            throw new PasswordNotFoundException();
        String updatePasswordScript="update Hasla set h_haslo=?" +
                " where h_halso=?;";
        System.out.println("\n"+updatePasswordScript.toUpperCase()+"\n");
        jdbcTemplate.update(updatePasswordScript,newPassword,oldPassword);
    }

    public boolean findOldPassword(String oldPassword){
        String findOldPasswordScript="select * from Hasla where h_halso=?";
        System.out.println("\n"+findOldPasswordScript.toUpperCase()+"\n");
        Password password=jdbcTemplate.queryForObject(findOldPasswordScript,
                new BeanPropertyRowMapper<Password>(Password.class),oldPassword);
        if (password==null)
            return false;
        return true;
    }

    public boolean findPasswordOfUser(String username, String passwordText){
        String findPasswordOfUserScript="select * from Hasla where h_haslo=? and h_u_id=" +
                "(select u_id from Uzytkownicy where u_nazwa_uzytkownika=?);";
        System.out.println("\n"+findPasswordOfUserScript.toUpperCase()+"\n");
        Password password=jdbcTemplate.queryForObject(findPasswordOfUserScript,
                new BeanPropertyRowMapper<Password>(Password.class),new Object[]{passwordText,username});
        if (password==null)
            return false;
        return true;
    }
    public boolean findPassword(String passwordText){
        String findPasswordScript="select * from Hasla where h_haslo=?";
        System.out.println("\n"+findPasswordScript.toUpperCase()+"\n");
        Password password=jdbcTemplate.queryForObject(findPasswordScript,
                new BeanPropertyRowMapper<Password>(Password.class),new Object[]{passwordText});
        if (password==null)
            return false;
        return true;
    }

}
