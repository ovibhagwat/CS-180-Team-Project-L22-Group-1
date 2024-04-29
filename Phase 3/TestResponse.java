import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
/**
 * A program that implements TestResponse class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class TestResponse {

    @Test
    public void testResponseConstructorForSuccess() {
        RequestResponseProtocol.Response response =
                new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        Assert.assertNotNull("Response object should be created.", response);
        Assert.assertEquals("Constructor should set the correct ResponseType.",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertNull("ErrorCode should be null for SUCCESS type.", response.getErrorCode());
        Assert.assertNull("Data should be null for SUCCESS type.", response.getData());
    }

    @Test
    public void testResponseConstructorForData() {
        RequestResponseProtocol.Response response = new RequestResponseProtocol.Response(RequestResponseProtocol.
                ResponseType.DATA, "Data content");
        Assert.assertNotNull("Response object should be created.", response);
        Assert.assertEquals("Constructor should set the correct ResponseType for DATA.",
                RequestResponseProtocol.ResponseType.DATA, response.getType());
        Assert.assertEquals("Constructor should set the correct data.",
                "Data content", response.getData());
    }

    @Test
    public void testResponseConstructorForError() {
        RequestResponseProtocol.Response response = new RequestResponseProtocol.Response(RequestResponseProtocol.
                ResponseType.ERROR, RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        Assert.assertNotNull("Response object should be created.", response);
        Assert.assertEquals("Constructor should set the correct ResponseType for ERROR.",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Constructor should set the correct ErrorCode.",
                RequestResponseProtocol.ErrorCode.USER_NOT_FOUND, response.getErrorCode());
    }

    @Test
    public void testSetType() {
        RequestResponseProtocol.Response response =
                new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        response.setType(RequestResponseProtocol.ResponseType.ERROR);
        Assert.assertEquals("setType should update the ResponseType.",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
    }

    @Test
    public void testSetErrorCode() {
        RequestResponseProtocol.Response response = new RequestResponseProtocol.Response(RequestResponseProtocol.
                ResponseType.ERROR, RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        response.setErrorCode(RequestResponseProtocol.ErrorCode.INVALID_PASSWORD);
        Assert.assertEquals("setErrorCode should update the ErrorCode.",
                RequestResponseProtocol.ErrorCode.INVALID_PASSWORD, response.getErrorCode());
    }

    @Test
    public void testSetData() {
        RequestResponseProtocol.Response response = new RequestResponseProtocol.
                Response(RequestResponseProtocol.ResponseType.DATA, "Initial data");
        response.setData("Updated data");
        Assert.assertEquals("setData should update the data stored in the response.",
                "Updated data", response.getData());
    }

    // Entry point to run the test suite and print results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestResponse.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
