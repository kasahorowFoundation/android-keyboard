package github;

import deployment.DeploymentUrl;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class DeploymentsList
        extends RestRequestPerformer<DeploymentsList.Request, DeploymentsList.Response[]> {

    public DeploymentsList(String username, String password) {
        super(username, password, Response[].class);
    }

    @Override
    protected HttpUriRequest createHttpRequest(Request request, String requestJsonAsString) {
        return new HttpGet(
                DeploymentUrl.KASAHOROW_GITHUB_API_DEPLOYMENTS_URL + "?sha=" + request.sha);
    }

    public static class Request {
        public final String sha;

        public Request(String sha) {
            this.sha = sha;
        }
    }

    public static class Response {
        public final String id;
        public final String environment;

        public Response(String id, String environment) {
            this.id = id;
            this.environment = environment;
        }
    }
}
