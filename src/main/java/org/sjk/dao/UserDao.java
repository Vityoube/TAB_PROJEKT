package org.sjk.dao;

import org.sjk.dto.Action;
import org.sjk.dto.User;
import org.sjk.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                "u_h_id bigint," +
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
        jdbcTemplate.execute(initUserScript);
    }

    public void updateRelations() {
        String updateUserTableScript="alter table Uzytkownicy add foreign key (u_h_id) references Hasla(h_id);";
        jdbcTemplate.execute(updateUserTableScript);
    }

    public long insertUser(User user, String password,String ip) throws PasswordExistsException, UserExistsException {
        long userCounts=jdbcTemplate.queryForObject("select count(*) as uzytkownicy_count from Uzytkownicy where u_nazwa_uzytkownika=?",
                new Object[]{user.getUserName()},Long.class);
        if (userCounts == 0) {
            if (passwordDao.findPassword(password))
                throw new PasswordExistsException();
            String userInsertScript="insert into Uzytkownicy(u_nazwa_uzytkownika," +
                    "u_status_rejestracji, u_status, u_imie, u_nazwisko, u_adres," +
                    "u_telefon,u_online,u_email) values(?,?,?,?,?,?,?,?,?);";
            jdbcTemplate.update(userInsertScript,new Object[]{
                    user.getUserName(), user.getRegistrationStatus(),
                    user.getUserStatus(),user.getFirstName(),user.getLastName(),
                    user.getAddress(), user.getPhone(),user.isOnline(),user.getEmail()
            });
            long passwordId=passwordDao.insertPassword(password);
            String updateUserScript="update Uzytkownicy set u_h_id=? where u_nazwa_uzytkownika=?;";
            jdbcTemplate.update(updateUserScript,passwordId,user.getUserName());
            String userIdQuery="select u_id " +
                    "from Uzytkownicy where u_nazwa_uzytkownika=?";
            long userId=jdbcTemplate.queryForObject(userIdQuery,
                    new Object[]{user.getUserName()},Long.class);

            long ipId=ipDao.findIPId(ip);
            passwordDao.updatePassword(userId,passwordId);
            actionDao.insertAction(Action.ActionTypes.REGISTRATION,new Timestamp(System.currentTimeMillis()),userId,ipId);
            return userId;
        }
        throw new UserExistsException();

    }

    public void registerUser(String username,String password, String ip){
        String userRegisterScript="update Uzytkownicy set u_status_rejestracji=? where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(userRegisterScript,new Object[]{User.RegistrationStatuses.REGISTERED,username});
        String updatePasswordScript="update Hasla set h_haslo=? where h_u_id=(select u_id from Uzytkownicy where u_nazwa_uzytkownika=?);";
        jdbcTemplate.update(updatePasswordScript,new Object[]{password,username});
        long ipId=jdbcTemplate.queryForObject("select i_id from IP where i_numer=?",new Object[]{ip},Long.class);
        long userId=jdbcTemplate.queryForObject("select u_id from Uzytkownicy where u_nazwa_uzytkownika=?",
                new Object[]{username},Long.class);
        actionDao.insertAction(Action.ActionTypes.REGISTRATION_END,new Timestamp(System.currentTimeMillis()),userId,ipId);
    }

    public boolean findUserByUsername(String username){
        try {
            String userFindScript = "select * from Uzytkownicy " +
                    "where u_nazwa_uzytkownika=?";
            User user = jdbcTemplate.queryForObject(userFindScript, new BeanPropertyRowMapper<User>(User.class), username);
            return true;
        } catch(EmptyResultDataAccessException e){
            return false;
        }
    }

    public User findUserById(long userId){
        String userFindScript = "select * from Uzytkownicy " +
                "where u_id=?";
        User user = jdbcTemplate.queryForObject(userFindScript, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user=User.builder()
                        .id(resultSet.getLong("u_id"))
                        .userName(resultSet.getString("u_nazwa_uzytkownika"))
                        .userStatus(resultSet.getString("u_status"))
                        .email(resultSet.getString("u_email"))
                        .registrationStatus(resultSet.getString("u_status_rejestracji"))
                        .firstName(resultSet.getString("u_imie"))
                        .lastName(resultSet.getString("u_nazwisko"))
                        .address(resultSet.getString("u_adres"))
                        .online(resultSet.getBoolean("u_online"))
                        .passwordId(resultSet.getLong("u_h_id"))
                        .phone(resultSet.getString("u_telefon"))
                        .blockTime(resultSet.getTimestamp("u_czas_blokady")).build();
                return user;
            }
        }, userId);
        return user;
    }

    public long loginUser(String username,String password, String ip)
            throws IpNotFoundException, UserNotFoundException, BadCredentialException, UserBlockedException {
        if (!ipDao.findIP(ip))
            throw new IpNotFoundException();
        if (!findUserByUsername(username))
            throw new UserNotFoundException();
        if (!passwordDao.findPasswordOfUser(username, password)){
            actionDao.insertAction(Action.ActionTypes.BAD_LOGIN_ATTEMPT,new Timestamp(System.currentTimeMillis()),findUserId(username),
                    ipDao.findIPId(ip));
            throw new BadCredentialException();
        }
        if (isUserBlocked(username,ip)){
            actionDao.insertAction(Action.ActionTypes.BAD_LOGIN_ATTEMPT,new Timestamp(System.currentTimeMillis()),findUserId(username),ipDao.findIPId(ip));
            throw new UserBlockedException();
        }
        String userLoginScript="update Uzytkownicy set u_online=true " +
                "where u_nazwa_uzytkownika=?";
        jdbcTemplate.update(userLoginScript,new Object[]{username});
        String getCurrentUserIdScript="select u_id from Uzytkownicy " +
                "where u_nazwa_uzytkownika=?";
        long currentUserId=jdbcTemplate.queryForObject(getCurrentUserIdScript,
                new Object[]{username},Long.class);
        actionDao.insertAction(Action.ActionTypes.LOGIN,new Timestamp(System.currentTimeMillis()),currentUserId,
                ipDao.findIPId(ip));
        return currentUserId;
    }

    private long findUserId(String username) {
        String findUserIdScript="select u_id from Uzytkownicy where u_nazwa_uzytkownika=?";
        long userId=jdbcTemplate.queryForObject(findUserIdScript,new Object[]{username},Long.class);
        return userId;
    }


    public void logoutUser(String username,String ip){
        String userLogoutScript="update Uzytkownicy set u_online=false " +
                "where u_nazwa_uzytkownika=?";
        jdbcTemplate.update(userLogoutScript,new Object[]{username});
        actionDao.insertAction(Action.ActionTypes.LOGOUT,new Timestamp(System.currentTimeMillis()),findUserId(username),
                ipDao.findIPId(ip));
    }

    public boolean isUserOnline(User user){
        String getUserOnlineStatusScript="select u_online from Uzytkownicy where u_id=?";
        try {
            boolean userOnline = jdbcTemplate.queryForObject(getUserOnlineStatusScript, new Object[]{user.getId()}, Boolean.class);
            return userOnline;
        } catch(EmptyResultDataAccessException e){
            return false;
        }
    }

    public long changePasswordForUser(String username,
                                      String oldPassword, String newPassword, String ip)
            throws PasswordNotFoundException {
        passwordDao.changePassword(oldPassword,newPassword);
        String getCurrentUserIdScript="select u_id from Uzytkownicy " +
                "where u_nazwa_uzytkownika=?";
        long currentUserId=jdbcTemplate.queryForObject(getCurrentUserIdScript,
                new Object[]{username},Long.class);
        actionDao.insertAction(Action.ActionTypes.PASSWORD_CHANGE,new Timestamp(System.currentTimeMillis()),currentUserId,
                ipDao.findIPId(ip));
        return currentUserId;
    }


    public boolean isUserAdmin(long userId) {
        try {
            String findUserStatusScript = "select u_status from Uzytkownicy where u_id=?";
            String currentUserStatus =
                    jdbcTemplate.queryForObject(findUserStatusScript,
                            new Object[]{userId}, String.class);
            return false;
        } catch(EmptyResultDataAccessException e){
            return true;
        }
    }

    public boolean isUserRegistered(long userId){
        try {
            String findUserRegisterStatusScript = "select u_status_rejestracji from Uzytkownicy where u_id=?";
            String currentUserRegisterStatus =
                    jdbcTemplate.queryForObject(findUserRegisterStatusScript,
                            new Object[]{userId}, String.class);
            return false;
        } catch (EmptyResultDataAccessException e){
            return true;
        }
    }

    public void blockUser(String username, String ip){
        String blockUserScript="update Uzytkownicy set u_status_rejestracji=? where u_nazwa_uzytkownika=?;";
        Timestamp blockTime=new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(blockUserScript, new Object[]{User.RegistrationStatuses.BLOCKED,username});
        String setBlockTimeScript="update Uzytkownicy set u_czas_blokady=? where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(setBlockTimeScript, new Object[]{blockTime,username});
        actionDao.insertAction(Action.ActionTypes.BLOCK,blockTime,findUserId(username),ipDao.findIPId(ip));
    }

    public boolean isUserBlocked(String username,String ip){
        String findUserRegisterStatusScript="select u_status_rejestracji from Uzytkownicy where u_nazwa_uzytkownika=?;";
        try {
            String currentUserRegisterStatus =
                    jdbcTemplate.queryForObject(findUserRegisterStatusScript,
                            new Object[]{username}, String.class);
            if (User.RegistrationStatuses.BLOCKED.equals(currentUserRegisterStatus)) {
                if (new Timestamp(System.currentTimeMillis()).getTime() - getUserBlockTime(username) >= 30 * 1000) {
                    unblockUser(username, ip);
                    return false;
                }
                return true;
            }
            return false;
        } catch(EmptyResultDataAccessException e){
            return false;
        }

    }

    private void unblockUser(String username,String ip) {
        String unblockUserScript="update Uzytkownicy set u_status_rejestracji=? where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(unblockUserScript,new Object[]{User.RegistrationStatuses.REGISTERED,username});
        Timestamp blockTime=new Timestamp(System.currentTimeMillis());
        String setUnblockUserTime="update Uzytkownicy set u_czas_blokady=null where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(setUnblockUserTime,new Object[]{username});
        actionDao.insertAction(Action.ActionTypes.UNBLOCK,blockTime,findUserId(username),ipDao.findIPId(ip));
    }

    private long getUserBlockTime(String username) {
        String getUserBlockTimeScript="select u_czas_blokady from Uzytkownicy where u_nazwa_uzytkownika=?";
        Timestamp blockTimestamp=jdbcTemplate.queryForObject(getUserBlockTimeScript,new Object[]{username},Timestamp.class);
        long blockTime=blockTimestamp.getTime();
        return blockTime;
    }


    public void insertDefaultUser() throws PasswordExistsException {
        User admin=User.builder()
                .userName("admin")
                .registrationStatus(User.RegistrationStatuses.REGISTERED)
                .userStatus(User.UserStatuses.ADMIN)
                .email("admin@sjk.org")
                .build();
        String userInsertScript="insert into Uzytkownicy(u_nazwa_uzytkownika," +
                "u_status_rejestracji, u_status, u_imie, u_nazwisko, u_adres," +
                "u_telefon,u_online,u_email) values(?,?,?,?,?,?,?,?,?);";
        jdbcTemplate.update(userInsertScript,new Object[]{
                admin.getUserName(),admin.getRegistrationStatus(),
                admin.getUserStatus(),admin.getFirstName(),admin.getLastName(),
                admin.getAddress(), admin.getPhone(),admin.isOnline(),admin.getEmail()
        });
        long passwordId=passwordDao.insertPassword("admin");
        String updateUserScript="update Uzytkownicy set u_h_id=?"+
                " where u_nazwa_uzytkownika=?;";
        jdbcTemplate.update(updateUserScript,new Object[]{passwordId,admin.getUserName()});
        String userIdQuery="select u_id " +
                "from Uzytkownicy where u_nazwa_uzytkownika=?";
        long userId=jdbcTemplate.queryForObject(userIdQuery,
                new Object[]{admin.getUserName()},Long.class);
        try {
            InetAddress ip=InetAddress.getLocalHost();
            long ipId=ipDao.findIPId(ip.getHostAddress());passwordDao.updatePassword(userId,passwordId);
            actionDao.insertAction(Action.ActionTypes.REGISTRATION,new Timestamp(System.currentTimeMillis()),userId,ipId);
            registerUser("admin","admin",ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
