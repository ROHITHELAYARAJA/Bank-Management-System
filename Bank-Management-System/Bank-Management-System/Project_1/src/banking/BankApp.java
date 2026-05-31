package banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


import static banking.ConsoleColors.*;
public  class BankApp {


    private banking.BankService service ;
    private  final BufferedReader br;
    public BankApp() {
        this.service = new BankService();
        this. br = new BufferedReader(new InputStreamReader(System.in));
    }
    public static void main(String[] args) {
           new BankApp().run();
    }
    public void run(){
        String name;
        int pass_code;
        int ch;
        String actype;

        while (true) {
            printMainMenu();
            try {
                System.out.print(CYAN + "ENTER YOUR CHOICE: " + RESET);
                String line = br.readLine();
                if (line == null || line.isEmpty()) {
                    System.out.println(GREEN + "THANK YOU FOR USING OUR BANK! GOODBYE" + RESET);
                    System.exit(0);
                }
                ch = Integer.parseInt(line);

                switch (ch) {
                    case 1 -> {
                        handleCreate();
                    }

                    case 2 -> {
                       handleLogin();
                    }

                    case 3 ->{
                      handleDelete();
                    }
                    case 4 -> {
                        System.out.println(GREEN + "THANK YOU FOR USING OUR BANK! GOODBYE" + RESET);
                        System.exit(0);

                    }


                    default -> {
                        System.out.println(RED + "INVALID CHOICE" + RESET);
                    }
                }
            } catch (Exception e) {
                System.out.println(RED + "INVALID CHOICE" + RESET);
            }

        }

    }

    private void handleDelete() throws IOException {
        System.out.println("ENTER USERNAME: ");
        String name = br.readLine();
        System.out.println("ENTER PASSWORD: ");
        String pass = br.readLine();
        System.out.println("ENTER ACCOUNT NUMBER: ");
        int acno= readInt();
        if (service.deleteAccount(name, pass, acno))
            System.out.println(GREEN + "ACCOUNT DELETED SUCCESSFULLY" + RESET);
        else
            System.out.println(RED + "DELETION FAILED — CHECK YOUR DETAILS" +RESET);
    }


    private void handleCreate() throws IOException{
        System.out.println("ENTER UNIQUE USERNAME");
        String name = br.readLine();
        System.out.println("ENTER PASSWORD");
        String pass_code = br.readLine();
        System.out.println("ENTER ACCOUNT TYPE (Savings / Current)");
        String actype = br.readLine();

        int acno = service.createAccount(name,pass_code,actype);
        if      (acno == -2)
            System.out.println(RED   + "USERNAME ALREADY EXISTS!"         + RESET);
        else if (acno == -1)
            System.out.println(RED   + "ACCOUNT CREATION FAILED!"         +RESET);
        else {
            System.out.println(GREEN + "CREATED! YOUR ACCOUNT NO: " + acno +RESET);
        }
    }

    private void printMainMenu() {
        System.out.println(BLUE + "\n===============================");
        System.out.println(" WELCOME TO THE BANK");
        System.out.println("===============================" + RESET);
        System.out.println("1) CREATE ACCOUNT");
        System.out.println("2) LOGIN ACCOUNT");
        System.out.println("3) DELETE ACCOUNT");
        System.out.println("4) EXIT");
    }
    private void printAccountMenu() {
        System.out.println(YELLOW + "\n-- ACCOUNT MENU --" +RESET);
        System.out.println("1) TRANSFER MONEY");
        System.out.println("2) VIEW BALANCE");
        System.out.println("3) DEPOSIT MONEY");
        System.out.println("4) WITHDRAW MONEY");
        System.out.println("5) TRANSACTION HISTORY");
        System.out.println("6) LOGOUT");
        System.out.print(CYAN + "CHOICE: " + RESET);
    }
    private void handleLogin() throws Exception {
        System.out.println("ENTER USERNAME");
        String name = br.readLine();
        System.out.println("ENTER PASSWORD");
        String pass_code = (br.readLine());

        Account acc = service.login(name,pass_code);
        if (acc!=null) {
            System.out.println(BLUE + "WELCOME, " + acc.getCname() + " [" + acc.getActype() + "]" + RESET);
            runAccountMenu(acc);
        } else {
            System.out.println(RED + "LOGIN FAILED!" + RESET);
        }
    }

    private void runAccountMenu(Account acc) throws Exception{
        while(true){
            printAccountMenu();
            try {
                int ch = readInt();

                switch (ch) {

                    case 1-> {
                        System.out.println("RECEIVER ACCOUNT NO: ");
                        int to     = readInt();
                        System.out.println("AMOUNT: ");
                        int amount = readInt();
                        try{
                            service.transfer(acc.getAcno(), to, amount);
                            System.out.println(GREEN + "TRANSFER SUCCESSFUL!" + RESET);


                        }catch(AccountNotFoundException e){
                            System.out.println(RED + "RECEIVER ACCOUNT NOT FOUND!" + RESET);
                        }
                        catch (InsufficientBalanceException e){
                            System.out.println(RED + "INSUFFICIENT BALANCE!" + RESET);
                        }

                    }

                    case 2-> {

                        Account fresh = service.getBalance(acc.getAcno());
                        if(fresh!=null){
                            printBalanceTable(fresh);
                        }
                    }

                    case 3-> {
                        System.out.println("AMOUNT TO DEPOSIT: ");
                        int amount = readInt();
                        System.out.println(service.deposit(acc.getAcno(), amount)
                                          ? GREEN + "DEPOSITED SUCCESSFULLY!" + RESET
                        : RED   + "DEPOSITED FAILED!" + RESET);
                    }

                    case 4 ->{
                        System.out.println("AMOUNT TO WITHDRAW: ");
                        int amount = readInt();
                        System.out.println(service.withdraw(acc.getAcno(), amount)
                                ? GREEN + "WITHDRAWN SUCCESSFULLY!" + RESET
                                : RED   + "WITHDRAW  FAILED!" +RESET);
                    }
                    case 5->{
                        System.out.println("HOW MANY NUMBER OF RECENT TRANSACTIONS YOU WANT");
                        int input = readInt();
                        printTransactionHistory(acc.getAcno(),input);
                    }

                    case 6->{
                        System.out.println(GREEN+"LOGGED OUT"+ RESET);
                        return;
                    }

                    default -> System.out.println(RED + "INVALID CHOICE!" + RESET);
                }
            } catch (IOException e) {
                System.out.println(RED + "INPUT ERROR" + RESET);
                e.printStackTrace();

            }
        }

    }

    private void printBalanceTable(Account acc) {

        System.out.println(
                YELLOW +
                        "\n--------------------------------------------------" +
                        RESET
        );

        System.out.printf(
                CYAN + "%-15s %-15s %-15s %-15s%n" + RESET,
                "ACCOUNT NO",
                "NAME",
                "TYPE",
                "BALANCE"
        );

        System.out.printf(
                "%-15d %-15s %-15s " +
                        GREEN + "%-15s%n" +
                        RESET,

                acc.getAcno(),
                acc.getCname(),
                acc.getActype(),
                acc.getBalance().toPlainString()
        );

        System.out.println(
                YELLOW +
                        "--------------------------------------------------" +
                        RESET
        );
    }
    private int readInt() throws IOException {
        while(true){
            try{
                return Integer.parseInt(br.readLine().trim());
            }
            catch (NumberFormatException e){
                System.out.println(RED + "ENTER A VALID NUMBER" + RESET);
            }
        }
    }

    private void printTransactionHistory(int acc, int input) {
        List<String[]> txns = service.getRecentTransactions(acc,input);

        System.out.println(YELLOW +
                "\n============================================================" +
                RESET);
        System.out.printf(CYAN +
                        "%-10s %-12s %-12s %-12s %-20s%n" + RESET,
                "TYPE", "AMOUNT", "FROM A/C", "TO A/C", "DATE & TIME");
        System.out.println(YELLOW +
                "------------------------------------------------------------" +
                RESET);

        if(txns.isEmpty()){
            System.out.println(YELLOW+"NO TRANSACTIONS FOUND"+RESET);
        }
        else{
            for (String[] txn : txns) {
                String type = txn[0];
                String amount = txn[1];
                String senderAc = txn[2];
                String receiverAc = txn[3];
                String timestamp = txn[4];

                String color = type.equalsIgnoreCase("CREDIT") || type.equalsIgnoreCase("DEBIT") ? GREEN : RED;

                System.out.printf(color +
                                "%-10s %-12s %-12s %-12s %-20s%n" +RESET,
                        type, amount, senderAc, receiverAc, timestamp);
            }
        }
        System.out.println(YELLOW +
                "============================================================" +
                RESET);
    }
}