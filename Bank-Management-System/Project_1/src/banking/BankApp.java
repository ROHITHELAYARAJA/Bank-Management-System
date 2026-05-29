package banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public  class BankApp {


    private BankService service ;
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
                System.out.print(BankService.CYAN + "ENTER YOUR CHOICE: " + BankService.RESET);
                String line = br.readLine();
                if (line == null || line.isEmpty()) {
                    System.out.println(BankService.GREEN + "THANK YOU FOR USING OUR BANK! GOODBYE" + BankService.RESET);
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
                        System.out.println(BankService.GREEN + "THANK YOU FOR USING OUR BANK! GOODBYE" + BankService.RESET);
                        System.exit(0);

                    }


                    default -> {
                        System.out.println(BankService.RED + "INVALID CHOICE" + BankService.RESET);
                    }
                }
            } catch (Exception e) {
                System.out.println(BankService.RED + "INVALID CHOICE" + BankService.RESET);
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
            System.out.println(BankService.GREEN + "ACCOUNT DELETED SUCCESSFULLY" + BankService.RESET);
        else
            System.out.println(BankService.RED + "DELETION FAILED — CHECK YOUR DETAILS" + BankService.RESET);
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
            System.out.println(BankService.RED   + "USERNAME ALREADY EXISTS!"         + BankService.RESET);
        else if (acno == -1)
            System.out.println(BankService.RED   + "ACCOUNT CREATION FAILED!"         +BankService.RESET);
        else {
            System.out.println(BankService.GREEN + "CREATED! YOUR ACCOUNT NO: " + acno +BankService.RESET);
        }
    }

    private void printMainMenu() {
        System.out.println(BankService.BLUE + "\n===============================");
        System.out.println(" WELCOME TO THE BANK");
        System.out.println("===============================" + BankService.RESET);
        System.out.println("1) CREATE ACCOUNT");
        System.out.println("2) LOGIN ACCOUNT");
        System.out.println("3) DELETE ACCOUNT");
        System.out.println("4) EXIT");
    }
    private void printAccountMenu() {
        System.out.println(BankService.YELLOW + "\n-- ACCOUNT MENU --" +BankService.RESET);
        System.out.println("1) TRANSFER MONEY");
        System.out.println("2) VIEW BALANCE");
        System.out.println("3) DEPOSIT MONEY");
        System.out.println("4) WITHDRAW MONEY");
        System.out.println("5) LOGOUT");
        System.out.print(BankService.CYAN + "CHOICE: " + BankService.RESET);
    }
    private void handleLogin() throws Exception {
        System.out.println("ENTER USERNAME");
        String name = br.readLine();
        System.out.println("ENTER PASSWORD");
        String pass_code = (br.readLine());

        Account acc = service.login(name,pass_code);
        if (acc!=null) {
            System.out.println(BankService.BLUE + "WELCOME, " + acc.getCname() + " [" + acc.getActype() + "]" + BankService.RESET);
            runAccountMenu(acc);
        } else {
            System.out.println(BankService.RED + "LOGIN FAILED!" + BankService.RESET);
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
                        System.out.println(service.transfer(acc.getAcno(), to, amount)
                                ? BankService.GREEN + "TRANSFER SUCCESSFUL!" + BankService.RESET
                                : BankService.RED   + "TRANSFER FAILED!"     + BankService.RESET);
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
                                          ? BankService.GREEN + "DEPOSITED SUCCESSFULLY!" + BankService.RESET
                        : BankService.RED   + "DEPOSITED FAILED!" + BankService.RESET);
                    }

                    case 4 ->{
                        System.out.println("AMOUNT TO WITHDRAW: ");
                        int amount = readInt();
                        System.out.println(service.withdraw(acc.getAcno(), amount)
                                ? BankService.GREEN + "WITHDRAWN SUCCESSFULLY!" + BankService.RESET
                                : BankService.RED   + "WITHDRAW  FAILED!" + BankService.RESET);
                    }

                    case 5->{
                        System.out.println(BankService.GREEN+"LOGGED OUT"+ BankService.RESET);
                        return;
                    }

                    default -> System.out.println(BankService.RED + "INVALID CHOICE!" + BankService.RESET);
                }
            } catch (IOException e) {
                System.out.println(BankService.RED + "INPUT ERROR" + BankService.RESET);
                e.printStackTrace();

            }
        }

    }

    private void printBalanceTable(Account acc) {

        System.out.println(
                BankService.YELLOW +
                        "\n--------------------------------------------------" +
                        BankService.RESET
        );

        System.out.printf(
                BankService.CYAN + "%-15s %-15s %-15s %-15s%n" + BankService.RESET,
                "ACCOUNT NO",
                "NAME",
                "TYPE",
                "BALANCE"
        );

        System.out.printf(
                "%-15d %-15s %-15s " +
                        BankService.GREEN + "%-15s%n" +
                        BankService.RESET,

                acc.getAcno(),
                acc.getCname(),
                acc.getActype(),
                acc.getBalance().toPlainString()
        );

        System.out.println(
                BankService.YELLOW +
                        "--------------------------------------------------" +
                        BankService.RESET
        );
    }
    private int readInt() throws IOException {
        while(true){
            try{
                return Integer.parseInt(br.readLine().trim());
            }
            catch (NumberFormatException e){
                System.out.println(BankService.RED + "ENTER A VALID NUMBER" + BankService.RESET);
            }
        }
    }
}