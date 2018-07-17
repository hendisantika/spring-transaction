package com.hendisantika.springtransaction.entity;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-transaction
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 18/07/18
 * Time: 06.32
 * To change this template use File | Settings | File Templates.
 */
public class BankAccountInfo {
    private Long id;
    private String fullName;
    private double balance;

    public BankAccountInfo() {

    }

    // Used in JPA query.
    public BankAccountInfo(Long id, String fullName, double balance) {
        this.id = id;
        this.fullName = fullName;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
