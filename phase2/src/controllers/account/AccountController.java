package controllers.account;

import useCases.AccountManager;
import useCases.ManagerData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AccountController {
    // NEW account controller for WEB
    // replaces the account package
    private AccountManager accountManager;
    private ManagerData managerData;

    public AccountController(ManagerData managerData) {

        accountManager = managerData.getAccountManager();
        this.managerData = managerData;
    }

    public List<String> viewHistory() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<String> dates = new ArrayList<>();
        for (LocalDateTime date : accountManager.getUserHistory(managerData.getCurrentUser())) {
            dates.add(formatter.format(date));
        }
        return dates;
    }
}