import io.swagger.client.*;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;

public class DefaultApiExample {

  public static void main(String[] args) {

    DefaultApi apiInstance = new DefaultApi();
    String albumID = "albumID_example"; // String | path  parameter is album key to retrieve
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }
  }
}