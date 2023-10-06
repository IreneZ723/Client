

import io.swagger.client.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

    import java.io.File;

public class CallPostApi {

  public static void main(String[] args) {

    DefaultApi apiInstance = new DefaultApi();
    File image = new File("src/test/resources/image.png"); // File |
    AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile |
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }
}