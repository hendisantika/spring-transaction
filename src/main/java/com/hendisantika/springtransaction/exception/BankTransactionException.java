package com.hendisantika.springtransaction.exception;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-transaction
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 18/07/18
 * Time: 06.33
 * To change this template use File | Settings | File Templates.
 */
public class BankTransactionException extends Exception {
    private static final long serialVersionUID = -3128681006635769411L;

    public BankTransactionException(String message) {
        super(message);
    }
}
