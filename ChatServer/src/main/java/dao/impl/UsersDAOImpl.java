package dao.impl;

import dao.UsersDAO;
import dao.dbConnection.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对用户数据访问接口的实现
 * @author livejq
 * @since 2020/4/08
 **/
public class UsersDAOImpl implements UsersDAO {
    private Connection connection;
    private PreparedStatement ptmt;

    public UsersDAOImpl() {
        try {
            connection = DBConnection.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean add(User user) throws SQLException {
        String sql = "INSERT INTO users VALUES (?,?,?,?,?,?)";
        ptmt = connection.prepareStatement(sql);
        ptmt.setString(1, user.getEmail());
        ptmt.setString(2, user.getPassword());
        ptmt.setString(3, user.getNickname());
        ptmt.setString(4, user.getGender());
        ptmt.setString(5, user.getBirth());
        ptmt.setString(6, user.getAvatar());

        return ptmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email=?";
        ptmt = connection.prepareStatement(sql);
        ptmt.setString(1, email);

        return ptmt.executeUpdate() > 0;
    }

    @Override
    public boolean updatePassword(String email, String password) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE email=?";
        ptmt = connection.prepareStatement(sql);
        ptmt.setString(1, password);
        ptmt.setString(2, email);

        return ptmt.executeUpdate() > 0;
    }

    @Override
    public List<User> get() throws SQLException {
        List<User> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        ptmt = connection.prepareStatement(sql);
        ResultSet rst = ptmt.executeQuery();
        while (rst.next()) {
            usersList.add(
                    new User(
                            rst.getString(1),
                            rst.getString(2),
                            rst.getString(3),
                            rst.getString(4),
                            rst.getString(5),
                            rst.getString(6)
                    )
            );
        }

        return usersList;
    }

    @Override
    public User get(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=?";
        try {
            ptmt = connection.prepareStatement(sql);
            ptmt.setString(1, email);
        } catch (NullPointerException e) {
            System.out.println("email为空");
            return null;
        }
        ResultSet rst = ptmt.executeQuery();
        if (rst.next()) {
            System.out.println(rst.getString(1));
            return new User(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getString(6)
            );
        }

        return null;
    }
}
