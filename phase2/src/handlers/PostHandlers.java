package handlers;

import controllers.AccountController;
import controllers.PostController;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.QueryParameterUtils;
import useCases.ManagerData;
import viewModel.ViewModel;

import java.util.*;

public class PostHandlers extends Handlers {
    private PostController postController;
    private AccountController accountController;
    private ManagerData managerData;
    public PostHandlers(ManagerData managerData) {
        this.managerData = managerData;
        postController = new PostController(managerData);
        accountController = new AccountController(managerData);
    }

    /**
     * Represents HttpHandler Class
     * Handles GET request for add posting page
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void addPost(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        String templatePath = "src/templates/form.jinja";
        ViewModel viewModel = new ViewModel();
        viewModel.put("submitBtnName", "add post");
        viewModel.addFormField("title", "title", "text");
        viewModel.addFormField("content", "content", "text");
        viewModel.put("action", "/addPost");
        viewModel.put("method", "post");
        present(exchange, viewModel.getContext(), templatePath);
    }

    /**
     * Represents HttpHandler Class
     * Handles POST request for add posting form
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void addPostRedirect(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");

        exchange.getRequestReceiver().receiveFullString(new Receiver.FullStringCallback() {
                public void handle(HttpServerExchange exchange, String message) {
                    Map<String, Deque<String>> props = QueryParameterUtils.parseQueryString(message, "UTF_8");
                    String author = managerData.getCurrentUser();
                    String title = cleanText(props.get("title").getFirst());
                    String content = cleanText(props.get("content").getFirst());

                    postController.addPost(title, content, author);

                    ViewModel viewModel = new ViewModel();
                    viewModel.put("endpoint",  "");
                    String templatePath = "src/templates/redirect.jinja";
                    present(exchange, viewModel.getContext(), templatePath);
                }
            }
        );
    }

    /**
     * Represents HttpHandler Class
     * Handles DELETE request for deleting post form
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void deletePost(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        String postIdString = exchange.getQueryParameters().get("postId").getFirst();
        UUID postId = UUID.fromString(postIdString);
        postController.deletePost(postId);
    }

    /**
     * Represents HttpHandler Class
     * Handles GET request for viewing posts
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void viewPost(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        Map<String, Deque<String>> props = exchange.getQueryParameters();
        String postIdString = props.get("postId").getFirst();
        UUID postId = UUID.fromString(postIdString);
        Map<String, String> post = postController.getPost(postId);
        ViewModel viewModel = new ViewModel();
        viewModel.put("post", post);
        String templatePath = "src/templates/post.jinja";
        present(exchange, viewModel.getContext(), templatePath);
    }

    /**
     * Represents HttpHandler Class
     * Handles GET request for viewing a user profile
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void viewProfile(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");

        String templatePath;
        Map<String, Deque<String>> props = exchange.getQueryParameters();
        ViewModel viewModel = new ViewModel();
        String username = props.get("username").getFirst();

        if (!username.equals(managerData.getCurrentUser())) {
            templatePath = "src/templates/profile.jinja";
            String requester = managerData.getCurrentUser();
            List<Map<String, String>> posts = postController.getPostsWrittenBy(username);

            viewModel.put("posts", posts);
            viewModel.put("username", username);
            viewModel.put("userIsAdmin", accountController.isAdmin(username));
            viewModel.put("userIsBanned", accountController.isBanned(username));
            viewModel.put("followStatus", accountController.isFollowing(requester, username));
            viewModel.put("permissions", managerData.getCurrentUserRole());
            viewModel.put("followers", accountController.getFollowers(username).size());
            viewModel.put("following", accountController.getFollowing(username).size());
        }
        else {
            templatePath = "src/templates/redirect.jinja";
            viewModel.put("endpoint", "viewSelfProfile");
        }

        present(exchange, viewModel.getContext(), templatePath);
    }

    /**
     * Represents HttpHandler Class
     * Handles GET request for viewing a self-profile
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void viewSelfProfile(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        String templatePath = "src/templates/profile.jinja";

        ViewModel viewModel = new ViewModel();
        String username = managerData.getCurrentUser();
        List<Map<String, String>> posts = postController.getPostsWrittenBy(username);

        viewModel.put("posts", posts);

        viewModel.put("username", managerData.getCurrentUser());
        viewModel.put("isAdmin", managerData.getCurrentUserRole());

        viewModel.put("followers", accountController.getFollowers(username).size());
        viewModel.put("following", accountController.getFollowing(username).size());

        present(exchange, viewModel.getContext(), templatePath);
    }

    /**
     * Represents HttpHandler Class
     * Handles GET request for getting a user's feed
     *
     * @param exchange An HTTP server request/response exchange
     */
    public void getFeed(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        String templatePath = "src/templates/feed.jinja";

        ViewModel viewModel = new ViewModel();
        String username = managerData.getCurrentUser();
        List<Map<String, String>> posts = postController.getFollowingPosts(managerData.getCurrentUser());

        viewModel.put("posts", posts);
        viewModel.put("username", managerData.getCurrentUser());
        viewModel.put("following", accountController.getFollowing(username).size());

        present(exchange, viewModel.getContext(), templatePath);
    }

    private String cleanText(String text) {
        text = text.replace('+', ' ');
        text = text.replace("%27", "'");
        text = text.replace("%28", "(");
        text = text.replace("%29", ")");
        return text;
    }
}
