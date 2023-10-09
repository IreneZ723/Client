package Example;

import io.swagger.client.*;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.*;

import java.io.File;

public class DefaultPost {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        apiInstance.getApiClient().setBasePath("http://54.185.208.245:8080/javaServlet_war");
        File image = new File("albumImageTest.png");
        AlbumsProfile profile = new AlbumsProfile();
        try {
            long start = System.currentTimeMillis();

            apiInstance.newAlbumWithHttpInfo(image, profile);
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#post" + e.getCode());
            e.printStackTrace();
        }
    }
}