package controllers.admin;

import controllers.appWide.RequestController;
import exception.UserIsAdminException;
import exception.UsernameNotFoundException;

import useCases.IAccountManager;

import java.util.Scanner;

public class BanUserController extends RequestController {
    /**
     * a use case responsible for managing accounts
     */
    final private IAccountManager accountManager;

    /**
     * Constructor for a controller responsible for handling input related to account banning.
     *
     * @param accountManager  a use case responsible for managing accounts
     */
    public BanUserController(IAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getRequestDescription() {
        return "ban an account";
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean handleRequest(String requester) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the username of the account you wish to ban: ");
            String target = scanner.nextLine();
            sleeper.sleep(200);
            if (accountManager.ban(target)) {
                System.out.println("Successfully banned account: " + target + ".");
            } else {
                System.out.println("Unsuccessful ban, account " + target + " was already banned.");
            }
        } catch (UsernameNotFoundException |  UserIsAdminException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}