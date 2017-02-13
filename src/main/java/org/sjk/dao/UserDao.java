package org.sjk.dao;

import org.sjk.dto.Action;
import org.sjk.dto.User;
import org.sjk.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Timestamp;

/**
 * Created by vkalashnykov on 11.02.17.
 */
@Component
public class UserDao {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordDao passwordDao;
    @Autowired
    private IpDao ipDao;
    @Autowired
    private ActionDao actionDao;

    @PostConstruct
    public void initTable(){
        String initUserScript="create table if not EXISTS Uzytkownicy(" +
                "u_id bigint not null auto_increment primary key , " +
                "u_nazwa_uzytkownika varchar(20) UNIQUE not null," +
                "u_h_id bigint not null," +
                "u_status_rejestracji varchar(30)," +
                "u_status varchar(10)," +
                "u_imie varchar(30), " +
                "u_nazwisko varchar(30), " +
                "u_adres varchar(100), " +
                "u_telefon varchar(20)," +
                "u_online boolean, " +
                "u_email varchar(50) not null," +
                "u_czas_blokady timestamp" +
                ");";
        System.out.println("\n"+initUserScript.toUpperCase()+"\n");
        jdbcTemplate.execute(initUserScript);
    }

    public void updateRelations() {
        String updateUserTableScript="alter table Uzytkownicy add foreign key (u_h_id) references Hasla(h_id);";
        System.out.println("\n"+updateUserTableScript.toUpperCase()+"\n");
        jdbcTemplate.execute(updateUserTableScript);
    }

    public long insertUser(User user, String password,String ip) throws PasswordExistsException {
        if (passwordDao.findPassword(password))
            throw new PasswordExistsException();
        String userInsertScript="insert into Uzytkownicy(u_nazwa_uzytkownika," +
                "u_status_rejestracji, u_status, u_imie, u_nazwisko, u_adres," +
                "u_telefon,u_online,u_email) values(?,?,?,?,?,?,?,?,?);";
        System.out.println("\n"+userInsertScript.toUpperCase()+"\n");
        jdbcTemplate.update(userInsertScript,new Object[]{
                user.getUserName(),user.getRegistrationStatus(),
                user.getUserStatus(),user.getFirstName(),user.getLastName(),
                user.getAddress(), user.getPhone(),user.getEmail()
        });
        long passwordId=passwordDao.insertPassword(password);
        String updateUserScript="update Uzytkownicy set u_h_id="+passwordId+
                " where u_nazwa_uzytkownika=?;";
        System.out.println("\n"+userInsertScript.toUpperCase()+"\n");
        jdbcTemplate.update(userInsertScript,passwordId,user.getUserName());
        String userIdQuery="select u_id " +
                "from Uzytkownicy where u_nazwa_uzytkownika=?";
        System.out.println("\n"+userIdQuery.toUpperCase()+"\n");
        long userId=jdbcTemplate.queryForObject(userIdQuery,
                new Object[]{user.getUserName()},Long.class);

        long ipId=ipDao.findIPId(ip);
        passwordDao.updatePassword(userId,passwordId);
        actionDao.insertAction(Action.ActionTypes.REGISTRATION,new Timestamp(System.currentTimeMillis()),userId,ipId);
        return userId;
    }

    public boolean findUserByUsername(String username){
        String userFindScript="select * from Uzytkownicy " +
                "where u_nazwa_uzytkownika=?";
        System.out.println("\n"+userFindScript.toUpperCase()+"\n");
        User user=jdbcTemplate.queryForObject(userFindScript,new BeanPropertyRowMapper<User>(User.class),username);
        if (user==null)
            return false;
        return true;
    }

    public long loginUser(String username,String password, String ip)
            throws IpNotFoundException, UserOnlineException, UserNotFoundException, BadCredentialException, UserBlockeException {
        if (!ipDao.findIP(ip))
            throw new IpNotFoundException();
        if (isUserOnline(username))
            throw new UserOnlineException();
        if (!findUserByUsername(username))
            throw new UserNotFoundException();
        if (!passwordDao.findPasswordOfUser(username, password)){
            actionDao.insertAction(Action.ActionTypes.BAD_PASSWORD,new Timestamp(System.currentTimeMillis()),findUserId(username),
                    ipDao.findIPId(ip));
            throw new BadCredentialException();
        }
        if (isUserBlocked(username,ip)){
            actionDao.insertAction(Action.ActionTypes.BLOCK,new Timestamp(System.currentTimeMillis()),findUserId(username),ipDao.findIPId(ip));
            throw new UserBlockeException();
        }
        String userLoginScript="update Uzytkownicy set u_online=true " +
                "where u_nazwa_uzytkownika=?";
        System.out.println("\n"+userLoginScript.toUpperCase()+"\n");
        jdbcTemplate.update(userLoginScript,username);
        String getCurrentUserIdScript="select u_id from Uzytkownicy " +
                "where u_nazwa_uzytkownika=?";
        System.out.println("\n"+getCurrentUserIdScript.toUpperCase()+"\n");
        long currentUserId=jdbcTemplate.queryForObject(getCurrentUserIdScript,
                new Object[]{username},Long.class);
        actionDao.insertAction(Action.ActionTypes.LOGIN,new Timestamp(System.currentTimeMillis()),currentUserId,
                ipDao.findIPId(ip));
        return currentUserId;
    }

    private long findUserId(String username) {
        String findUserIdScript="select u_id from Uzytkownicy where u_nazwa_uzytkownika=?";
        System.out.println("\n"+findUserIdScript.toUpperCase()+"\n");
        long userId=jdbcTemplate.queryForObject(findUserIdScript,new Object[]{username},Long.class);
        return userId;
    }


    public void logoutUser(String username,String ip){
        String userLogoutScript="udpate Uzytkownicy set u_online=false " +
                "where u_nazwa_uzytkownika=?";
        System.out.println("\n"+userLogoutScript.toUpperCase()+"\n");
        jdbcTemplate.update(userLogoutScript,username);
        actionDao.insertAction(Action.ActionTypes.LOGOUT,new Timestamp(System.currentTimeMillis()),findUserId(username),
                ipDao.findIPId(ip));
    }

    public long changePasswordForUser(String username,
                                      String oldPassword, String newPassword, String ip)
            throws PasswordNotFoundException {
        passwordDao.changePassword(oldPassword,newPassword);
        String getCurrentUserIdScript="select u_id from Uzytkownicy " +
                "where u_nazwa_uzytkownika=?";
        System.out.println("\n"+getCurrentUserIdScript.toUpperCase()+"\n");
        long currentUserId=jdbcTemplate.queryForObject(getCurrentUserIdScript,
                new Object[]{username},Long.class);
        actionDao.insertAction(Action.ActionTypes.PASSWORD_CHANGE,new Timestamp(System.currentTimeMillis()),currentUserId,
                ipDao.findIPId(ip));
        return currentUserId;
    }


    public boolean isUserAdmin(long userId) {
        String findUserStatusScript="select u_status from Uzytkownicy where u_id=?";
        System.out.println("\n"+findUserStatusScript.toUpperCase()+"\n");
        String currentUserStatus=
                jdbcTemplate.queryForObject(findUserStatusScript,
                        new Object[]{userId},String.class);
        if (User.UserStatuses.ADMIN.equals(currentUserStatus))
            return true;
        return false;
    }

    public boolean isUserRegistered(long userId){
        String findUserRegisterStatusScript="select u_status_rejestracji from Uzytkownicy where u_id=?";
        System.out.println("\n"+findUserRegisterStatusScript.toUpperCase()+"\n");
        String currentUserRegisterStatus=
                jdbcTemplate.queryForObject(findUserRegisterStatusScript,
                        new Object[]{userId},String.class);
        if (User.RegistrationStatuses.REGISTERED.equals(currentUserRegisterStatus))
            return true;
        return false;
    }

    public boolean isUserOnline(String username){
        String findUserOnlineStatusScript="select u_online from Uzytkownicy where u_nazwa_uzytkownika=?;";
        System.out.println("\n"+findUserOnlineStatusScript.toUpperCase()+"\n");
        boolean userOnline=jdbcTemplate.queryForObject(findUserOnlineStatusScript,new Object[]{username},Boolean.class);
        return userOnline;
    }

    public void BlockUser(String username, String ip){
        String blockUserScript="update Uzytkownicy set u_status_rejestracji=? where u_nazwa_uzytkownika=?;\n" +
                "update Uzytkownicy set u_czas_blokady=? where u_nazwa_uzytkownika=?;";
        System.out.println("\n"+blockUserScript.toUpperCase()+"\n");
        jdbcTemplate.update(blockUserScript, User.RegistrationStatuses.BLOCKED,username,new Timestamp(System.currentTimeMillis()),
                username);
        actionDao.insertAction(Action.ActionTypes.BLOCK,new Timestamp(System.currentTimeMillis()),findUserId(username),ipDao.findIPId(ip));
    }

    public boolean isUserBlocked(String username,String ip){
        String findUserRegisterStatusScript="select u_status_rejestracji from Uzytkownicy where u_nazwa_uzytkownika=?;";
        System.out.println("\n"+findUserRegisterStatusScript.toUpperCase()+"\n");
        String currentUserRegisterStatus=
                jdbcTemplate.queryForObject(findUserRegisterStatusScript,
                        new Object[]{username},String.class);
        if (User.RegistrationStatuses.BLOCKED.equals(currentUserRegisterStatus)) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - getUserBlockTime(username) >= 30 * 10000) {
                unblockUser(username,ip);
                return false;
            }
            return true;
        }
        return false;

    }

    private void unblockUser(String username,String ip) {
        String unblockUserScript="update Uzytkownicy set u_status_rejestracji=? where u_nazwa_uzytkownika=?;\n" +
                "update Uzytkownicy set u_czas_blokady=? where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(unblockUserScript,username);
        actionDao.insertAction(Action.ActionTypes.UNBLOCK,new Timestamp(System.currentTimeMillis()),findUserId(username),ipDao.findIPId(ip));
    }

    private long getUserBlockTime(String username) {
        String getUserBlockTimeScript="select u_czas_blokady from Uzytkownicy where u_nazwa_uzytkownika=?";
        Timestamp blockTimestamp=jdbcTemplate.queryForObject(getUserBlockTimeScript,new Object[]{username},Timestamp.class);
        long blockTime=blockTimestamp.getTime();
        return blockTime;
    }


}
