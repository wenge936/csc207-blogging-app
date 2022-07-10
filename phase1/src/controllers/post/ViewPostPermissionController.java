package controllers.post;

import controllers.appWide.RequestController;
import controllers.appWide.RequestFacade;
import controllers.appWide.ReturnController;
import controllers.comment.AddCommentController;
import presenters.ProfilePresenter;
import useCases.ICommentManager;
import useCases.IPostManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import dataMapper.DataMapper;

public class ViewPostPermissionController extends RequestController {
    /**
     * a use case responsible for managing posts
     */
    DataMapper postModel;
    DataMapper commentModel;
    ICommentManager commentManager;

    /**
     * Constructor for a controller responsible for reading input to view a post.
     *
     * @param postModel      a data mapper that helps map posts into a data structure usable by presenters
     * @param postManager    a use case responsible for managing posts
     * @param commentModel   a data mapper that helps map comments into a data structure usable by presenters
     * @param commentManager a use case responsible for managing comments
     */
    public ViewPostPermissionController(DataMapper postModel, IPostManager postManager, DataMapper commentModel,
                                        ICommentManager commentManager) {
        this.postManager = postManager;
        this.postModel = postModel;
        this.commentModel = commentModel;
        this.commentManager = commentManager;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String getRequestDescription() {
        return "View post";
    }

    /**
     * @inheritDoc
     */
    @Override
    protected boolean handleRequest(String requester) {
        try {
            ArrayList<HashMap<String, String>> posts = postModel.getModel();
            ProfilePresenter profilePresenter = new ProfilePresenter();
            profilePresenter.present(posts);
            Scanner scanner = new Scanner(System.in);
            presenter.inlinePrint("Enter the number of the post you wish to view or 0 to return to your profile: ");
            int request = Integer.parseInt(scanner.nextLine());
            if (request == 0) {
                return false;
            } else if (request  <= posts.size()) {
                int postNumber = request - 1;
                HashMap<String, String> post = posts.get(postNumber);
                // TODO: refactor by adding a method in PostPresenter that presents a single post
                System.out.println("Written by: " + post.get("author"));
                System.out.println("Title: " + post.get("title"));
                System.out.println("Content: " + post.get("content"));
                System.out.println("Time posted: " + post.get("timePosted"));
                RequestFacade postRequests = new RequestFacade(new RequestController[]{
                        new DeletePostController(postModel, postManager),
                        new AddCommentController(commentModel, commentManager, requester),
                        new ReturnController()
                });
                postRequests.setRequester(posts.get(postNumber).get("id"));
                postRequests.presentRequest();
            } else {
                presenter.blockPrint("The number input is invalid.");
                handleRequest(requester);
            }
        } catch (NumberFormatException e) {
            presenter.blockPrint("The number input is invalid.");
            handleRequest(requester);
        }
        return false;
    }
}
