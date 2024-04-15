import java.util.Map;
/**
 * A program that implements Request Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */
public interface RequestInterface {
    // get the requestType of request
    public RequestResponseProtocol.RequestType getType();

    // set the requestType of request
    public void setType(RequestResponseProtocol.RequestType type);

    // get the parameters of request
    public Map<String, Object> getParameters();

    // set the parameters of request
    public void setParameters(Map<String, Object> parameters);
}
