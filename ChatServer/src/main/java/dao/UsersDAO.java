package dao;

import model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * 用户数据访问接口
 * @author livejq
 * @since 2020/4/08
 **/
public interface UsersDAO {
    boolean add(User user) throws SQLException;

    boolean delete(String email) throws SQLException;

    boolean updatePassword(String email, String password) throws SQLException;

    List<User> get() throws SQLException;

    User get(String email) throws SQLException;
}
