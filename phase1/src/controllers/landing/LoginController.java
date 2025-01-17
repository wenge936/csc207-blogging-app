package controllers.landing;

import controllers.account.*;
import controllers.admin.*;
import controllers.appWide.RequestController;
import controllers.appWide.RequestFacade;
import exception.IncorrectPasswordException;
import exception.UsernameNotFoundException;
import exception.AccountBannedException;
import useCases.IAccountManager;
import useCases.ICommentManager;
import useCases.IPostManager;

import java.util.Scanner;

public class LoginController extends RequestController {
    /**
     * a request facade containing request controllers for admins
     */
    final private RequestFacade adminRequestFacade;
    /**
     * a request facade containing request controllers for regular accounts
     */
    final private RequestFacade accountRequestFacade;

    /**
     * Constructor for a controller responsible for reading input to log users in.
     *
     * @param accountManager a use case responsible for managing accounts
     * @param postManager    a use case responsible for managing posts
     * @param commentManager a user case responsible for managing comments
     */
    public LoginController(IAccountManager accountManager, IPostManager postManager, ICommentManager commentManager){
        this.accountManager = accountManager;
        accountRequestFacade = new RequestFacade(new RequestController[]{
                new ViewHistoryController(accountManager),
                new DeleteSelfController(accountManager, postManager, commentManager),
                new FollowController(accountManager),
                new UnfollowController(accountManager),
                new ViewFollowerController(accountManager),
                new ViewFollowingController(accountManager),
                new ViewSelfProfileController(postManager, commentManager),
                new ViewFeedController(postManager, accountManager, commentManager),
                new ViewProfileController(accountManager, postManager, commentManager),
                new LogoutController(),
        });
        adminRequestFacade = new RequestFacade(new RequestController[]{
                new BanUserController(accountManager),
                new UnbanUserController(accountManager),
                new PromoteUserController(accountManager),
                new CreateAdminController(accountManager),
                new DeleteUserController(accountManager, postManager, commentManager),
                new DeleteSelfController(accountManager, postManager, commentManager),
                new ViewHistoryController(accountManager),
                new FollowController(accountManager),
                new UnfollowController(accountManager),
                new ViewFollowerController(accountManager),
                new ViewFollowingController(accountManager),
                new ViewSelfProfileController(postManager, commentManager),
                new ViewFeedController(postManager, accountManager, commentManager),
                new ViewProfileController(accountManager, postManager, commentManager),
                new LogoutController(),
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getRequestDescription() {
        return "Log in to your account";
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean handleRequest(String requester) {
        Scanner scanner = new Scanner(System.in);
        presenter.inlinePrint("Enter your username: ");
        String username = scanner.nextLine();
        presenter.inlinePrint("Enter your password: ");
        String password = scanner.nextLine();
        sleeper.sleep(200);
        try {
            accountManager.login(username, password);
            presenter.blockPrint("Successfully logged in.");
            if (accountManager.isAdmin(username)) {
                adminRequestFacade.setRequester(username);
                adminRequestFacade.presentRequest();
                adminRequestFacade.setRequester(null);
            } else {
                accountRequestFacade.setRequester(username);
                accountRequestFacade.presentRequest();
                accountRequestFacade.setRequester(null);
            }
        } catch (IncorrectPasswordException | UsernameNotFoundException | AccountBannedException e) {
            presenter.blockPrint(e.getMessage());
        }
        return false;
    }
}
