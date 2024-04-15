import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import java.util.HashMap;
import java.util.Map;

/**
 * A program that implements TestRequest class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class TestRequest {

    // Test constructor
    @Test
    public void testRequestConstructor() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "testUser");
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.LOGIN, parameters);
        Assert.assertNotNull("Request object should be created.", request);
        Assert.assertEquals("Constructor should set the correct RequestType.",
                RequestResponseProtocol.RequestType.LOGIN, request.getType());
        Assert.assertEquals("Constructor should set the correct parameters.",
                parameters, request.getParameters());
    }

    // Test setters
    @Test
    public void testSetType() {
        Map<String, Object> parameters = new HashMap<>();
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.LOGIN, parameters);
        request.setType(RequestResponseProtocol.RequestType.CHANGE_PASSWORD);
        Assert.assertEquals("setType should update the RequestType.",
                RequestResponseProtocol.RequestType.CHANGE_PASSWORD, request.getType());
    }

    @Test
    public void testSetParameters() {
        Map<String, Object> originalParameters = new HashMap<>();
        originalParameters.put("username", "testUser");
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.LOGIN, originalParameters);

        Map<String, Object> newParameters = new HashMap<>();
        newParameters.put("password", "newPassword");
        request.setParameters(newParameters);

        Assert.assertEquals("setParameters should update the parameters map.",
                newParameters, request.getParameters());
        Assert.assertNotEquals("setParameters should replace the old parameters.",
                originalParameters, request.getParameters());
    }

    // Entry point to run the test suite and print results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestRequest.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
