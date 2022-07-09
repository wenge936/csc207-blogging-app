package controllers.admin;

import controllers.appWide.RequestController;
import exception.UserIsAdminException;
import exception.UsernameNotFoundException;
import useCases.IAccountManager;
import useCases.IPostManager;

import java.util.Scanner;

public class DeleteUserController extends RequestController {
    /**
     * Constructor for a controller responsible for handling input related to deleting others' account.
     *
     * @param accountManager  a use case responsible for managing accounts
     * @param postManager     a use case responsible for managing posts
     */
    public DeleteUserController(IAccountManager accountManager, IPostManager postManager) {
        this.accountManager = accountManager;
        this.postManager = postManager;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getRequestDescription() {
        return "Delete an account";
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean handleRequest(String requester) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the username of the account you wish to delete: ");
            String target = scanner.nextLine();
            sleeper.sleep(200);
            accountManager.deleteUser(target);
            postManager.deletePostsWrittenBy(target);
            System.out.println("Successfully deleted user: " + target);
        } catch (UsernameNotFoundException | UserIsAdminException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
