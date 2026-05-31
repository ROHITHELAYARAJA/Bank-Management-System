package banking;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;

import static banking.ConsoleColors.*;

public class BankService {

    private  BankDAO dao;

    public BankService() {
        this.dao = new BankDAO();
    }




    public  int createAccount(String username, String password, String actype) {
        if (username.isEmpty() || password.isEmpty() || actype.isEmpty()) {
            System.out.println(RED + "ALL FIELDS ARE REQUIRED!" + RESET);
            return -1;
        }
        if(!actype.equalsIgnoreCase("Savings") && !actype.equalsIgnoreCase("Current")) {
            System.out.println(RED + "INVALID ACCOUNT TYPE" + RESET);
            return -1;
        }
        if(username.length() < 3 || username.length() > 20){
            System.out.println(RED + "USERNAME MUST BE 3-20 CHARACTERS" + RESET);
            return -1;
        }
        if(!passwordCheck(password)){
            System.out.println(RED + "PASSWORD MUST CONTAINS a-z,A-Z,0-9,(!@#$)=> ANY ONE OF SPECIAL CHARACTER INCLUDED" + RESET);
            return -1;
        }

        try {
            int acno = generateAccountNumber();
            String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt(12));
            if(dao.insertAccount(acno,username,hashedPassword,actype)){
                return acno;
            }
        } catch (SQLIntegrityConstraintViolationException e){
            return -2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private boolean passwordCheck(String password) {
        HashSet<Character> set = new HashSet<>();
        for (char c : password.toCharArray()) {
            set.add(c);
        }
        int points = 0;
        for (char x : set) {
            if(x>='a' && x<='z'){
                points++;
            }
            else if(x>='A' && x<='Z'){
                points+=2;
            }
            else if(x>='0' && x<='9'){
                points+=3;
            }
            else{
                points+=5;
            }
        }

        return points>=11;
    }

    private  int generateAccountNumber() throws Exception {
        Random rand = new Random();
        int acno;
        do {
            acno = 100000 + rand.nextInt(900000);
        } while (dao.acnoExists(acno));
        return acno;
    }


    public  Account getBalance(int acno) {

        try {
            return dao.findByAccountNumber(acno);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public  boolean transfer(int senderAc, int receiverAc, int amount) throws AccountNotFoundException, InsufficientBalanceException, SQLException{

        if (receiverAc == 0 || amount == 0) {
            System.out.println(RED + "ALL FIELDS ARE REQUIRED!" + RESET);
            return false;
        }
        if(amount<0){
            System.out.println(RED + "INVALID ACCOUNT AMOUNT" + RESET);
            return false;
        }
        try {
            Account sender = dao.findByAccountNumber(senderAc);
            if (sender == null ) {
                throw new AccountNotFoundException(senderAc);
            }
            Account receiver = dao.findByAccountNumber(receiverAc);

            if (receiver == null ) {
                throw new AccountNotFoundException(receiverAc);
            }
            if (!checkLimit(sender, amount)) {
                throw new InsufficientBalanceException(sender.getBalance(), amount);
            }
            dao.setAutoCommit(false);
            dao.updateBalance(senderAc, sender.getBalance().subtract(BigDecimal.valueOf(amount)));

            dao.updateBalance(receiverAc, receiver.getBalance().add(BigDecimal.valueOf(amount)));
            dao.logTransaction(senderAc, receiverAc, amount, "DEBIT");
            dao.logTransaction(receiverAc, senderAc, amount, "CREDIT");
            dao.commit();
            dao.setAutoCommit(true);
            return true;
        }
        catch (Exception e){
            try {
                dao.rollback();
                dao.setAutoCommit(true);
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        }
    }

    private  boolean checkLimit(Account sender, int amount) {
        if (sender.getActype().equalsIgnoreCase("Savings") && sender.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            System.out.println(RED + "INSUFFICIENT SAVING BALANCE!" + RESET);

            return false;
        }

        if (sender.getActype().equalsIgnoreCase("Current") && sender.getBalance()
                .subtract(BigDecimal.valueOf(amount))
                .compareTo(BigDecimal.valueOf(-1000)) < 0){
            System.out.println(RED + "OVERDRAFT LIMIT EXCEEDED FOR CURRENT ACCOUNT" + RESET);

            return false;
        }
        return true;
    }

    public  boolean deposit(int Ac, int amount) {
     try{
         Account acc = dao.findByAccountNumber(Ac);
         if(acc==null ||amount<=0 )
             return false;
         dao.updateBalance(Ac, acc.getBalance().add(BigDecimal.valueOf(amount)));
         dao.logTransaction(Ac,0,amount,"DEPOSIT");
         return true;
     } catch (SQLException e) {
         e.printStackTrace();
         return false;
     }
    }

    public  boolean withdraw(int Ac, int amount) {
        try{
            Account acc = dao.findByAccountNumber(Ac);
            if(acc==null ||amount<=0 )
                return false;
            if(!checkLimit(acc,amount)){
                return false;
            }
            dao.updateBalance(Ac, acc.getBalance().subtract(BigDecimal.valueOf(amount)));
            dao.logTransaction(Ac,0,amount,"WITHDRAW");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccount(String username, String password, int acno) {
        try {
            Account acc  = login(username, password);
            if (acc == null || acc.getAcno() != acno) return false;
            return dao.deleteAccount(acno);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Account login(String username, String password) {
        try {
            String hash = dao.getHashedPassword(username);
            if (hash == null) return null;
            if (!BCrypt.checkpw(password, hash)) return null;
            return dao.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String[]> getRecentTransactions(int acno,int limit){
        try{
            return dao.getRecentTransactions(acno,limit);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}