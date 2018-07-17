package com.hendisantika.springtransaction.controller;

import com.hendisantika.springtransaction.dao.BankAccountDAO;
import com.hendisantika.springtransaction.entity.BankAccountInfo;
import com.hendisantika.springtransaction.exception.BankTransactionException;
import com.hendisantika.springtransaction.form.SendMoneyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-transaction
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 18/07/18
 * Time: 06.36
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MainController {

    @Autowired
    private BankAccountDAO bankAccountDAO;

    @GetMapping("/")
    public String showBankAccounts(Model model) {
        List<BankAccountInfo> list = bankAccountDAO.listBankAccountInfo();

        model.addAttribute("accountInfos", list);

        return "accountsPage";
    }

    @GetMapping("/sendMoney")
    public String viewSendMoneyPage(Model model) {

        SendMoneyForm form = new SendMoneyForm(1L, 2L, 700d);

        model.addAttribute("sendMoneyForm", form);

        return "sendMoneyPage";
    }


    @PostMapping("/sendMoney")
    public String processSendMoney(Model model, SendMoneyForm sendMoneyForm) {

        System.out.println("Send Money: " + sendMoneyForm.getAmount());

        try {
            bankAccountDAO.sendMoney(sendMoneyForm.getFromAccountId(), //
                    sendMoneyForm.getToAccountId(), //
                    sendMoneyForm.getAmount());
        } catch (BankTransactionException e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "/sendMoneyPage";
        }
        return "redirect:/";
    }

}