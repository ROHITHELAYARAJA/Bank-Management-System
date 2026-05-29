package banking;

import java.math.BigDecimal;
import java.sql.*;

public class BankDAO {
    private final Connection conn;
    public BankDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public boolean insertAccount(int acno,String username,String hashedPassword,String actype) throws SQLException{

        String sql = "INSERT INTO customer (acno,cname,balance,pass_code,actype) VALUES (?,?,?,?,?)";
        try(  PreparedStatement pst = conn.prepareStatement(sql);){
            pst.setInt(1,acno);
            pst.setString(2, username);
            pst.setBigDecimal(3, BigDecimal.valueOf(0));
            pst.setString(4, hashedPassword);
            pst.setString(5, actype);
            int rows = pst.executeUpdate();
            return (rows ==1);
    }
    }

    public String getHashedPassword(String name) throws SQLException{
        String sql = "SELECT pass_code FROM customer WHERE cname = ?";
        try(  PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1,name);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return rs.getString("pass_code");
            }
        }
        return null;
    }

    public Account findByUsername(String name) throws SQLException{
        String sql = "SELECT * FROM customer WHERE cname = ?";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1,name);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return new Account(
                        rs.getInt("acno"),
                        rs.getString("cname"),
                        rs.getBigDecimal("balance"),
                        rs.getString("actype"));
            }
            return null;
        }
    }

    public Account findByAccountNumber(int acno) throws SQLException{
        String sql = "SELECT * FROM customer WHERE acno = ?";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1,acno);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return new Account(
                        rs.getInt("acno"),
                        rs.getString("cname"),
                        rs.getBigDecimal("balance"),
                        rs.getString("actype"));
            }
            return null;

        }
    }

    public boolean updateBalance(int acno,BigDecimal amount) throws SQLException {
        String sql = "UPDATE customer SET balance=? WHERE acno=?";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setBigDecimal(1,amount);
            pst.setInt(2,acno);
            return pst.executeUpdate()==1;
        }
    }

    public void logTransaction (int senderAc,int receiverAc,int amount ,String type) throws SQLException{
        String sql = "INSERT INTO transactions(sender_ac,receiver_ac,amount,type) VALUES (?,?,?,?)";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1,senderAc);
            pst.setInt(2,receiverAc);
            pst.setInt(3,amount);
            pst.setString(4,type);
            pst.executeUpdate();
        }
    }

    public void commit() throws SQLException{
        conn.commit();
    }
    public void rollback() throws SQLException{
        conn.rollback();
    }

    public void setAutoCommit(boolean val) throws SQLException{
        conn.setAutoCommit(val);
    }

    public boolean deleteAccount(int acno) throws SQLException {
        String sql = "DELETE FROM customer WHERE acno = ?";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, acno);
            return pst.executeUpdate()==1;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean acnoExists(int acno) throws SQLException {
        String sql = "SELECT acno FROM customer WHERE acno = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, acno);
            return pst.executeQuery().next();
        }
    }

}
