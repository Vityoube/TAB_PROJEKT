package org.sjk.dao;

import org.sjk.dto.IP;
import org.sjk.exception.UnsufficientPrivilegeExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class IpDao {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDao;

    @PostConstruct
    public void initTable(){
        String initTableScript="create table if not EXISTS IP (i_id bigint not null auto_increment primary key," +
                "i_numer varchar(15) UNIQUE not null);";
        System.out.println("\n"+initTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initTableScript);
    }

    public boolean findIP(String ipNumber) {
        try {
            String findIpScript = "select i_numer from IP where i_numer=?";
            System.out.println("\n" + findIpScript.toUpperCase() + "\n");
            String ip = jdbcTemplate.queryForObject(findIpScript,
                    new Object[]{ipNumber},String.class);
            return true;
        } catch (EmptyResultDataAccessException e){
            return false;
        }
    }

    public long insertIP(String ipNumber, long userId)
            throws UnsufficientPrivilegeExeption {
        try {
            if (!userDao.isUserAdmin(userId))
                throw new UnsufficientPrivilegeExeption();
            String insertIpScript = "insert into IP(i_numer) values(?);";
            System.out.println("\n" + insertIpScript.toUpperCase() + "\n");
            jdbcTemplate.update(insertIpScript, ipNumber);
            String selectIpIdScript = "select i_id from IP where i_numer=?";
            System.out.println("\n" + selectIpIdScript.toUpperCase() + "\n");
            long ipId = jdbcTemplate.queryForObject(selectIpIdScript,
                    new Object[]{ipNumber}, Long.class);
            return ipId;
        } catch (EmptyResultDataAccessException e){
            return -1;
        }
    }

    public long findIPId(String ipNumber){
        try {
            String findIpScript = "select i_id from IP where i_numer=?";
            System.out.println("\n" + findIpScript.toUpperCase() + "\n");
            long ipId = jdbcTemplate.queryForObject(findIpScript, new Object[]{ipNumber}, Long.class);
            return ipId;
        } catch(EmptyResultDataAccessException e){
            return -1;
        }
    }


    public void insertDefaultIP() {
        InetAddress ip;
        try {
            ip=InetAddress.getLocalHost();
            String serverIpNumber=ip.getHostAddress();
            System.out.println("Server ip: "+ip);
            String insertIpScript = "insert into IP(i_numer) values(?);";
            System.out.println("\n" + insertIpScript.toUpperCase() + "\n");
            jdbcTemplate.update(insertIpScript,serverIpNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
