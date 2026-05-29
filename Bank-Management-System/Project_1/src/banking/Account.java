package banking;

import java.math.BigDecimal;

public class Account {
    private final int acno;
    private final String cname;
    private BigDecimal balance;
    private final String actype;

    public  Account(int acno,String cname,BigDecimal balance,String actype){
        this.acno=acno;
        this.cname=cname;
        this.balance=balance;
        this.actype=actype;
    }

    public int getAcno() {
        return acno;
    }
    public String getCname() {
        return cname;
    }
    public BigDecimal getBalance(){
        return (BigDecimal) balance;
    }
    public String getActype(){
        return actype;
    }
}
