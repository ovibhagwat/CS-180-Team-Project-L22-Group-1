/**
 * A program that implements Response Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */
public interface ResponseInterface {
    /**
     * Retrieves the type of the response.
     * This method returns the type of the response, indicating the category of the response or
     * the nature of the response received from the server.
     *
     * @return The response type.
     */
    public RequestResponseProtocol.ResponseType getType();

    /**
     * Sets the type of the response.
     * This method allows setting the type of the response to specify how the response should be
     * interpreted or handled.
     *
     * @param type The response type to set.
     */
    public void setType(RequestResponseProtocol.ResponseType type);

    /**
     * Retrieves the error code associated with the response.
     * This method is used when the response indicates an error, providing more details about
     * the specific error that occurred.
     *
     * @return The error code of the response.
     */
    public RequestResponseProtocol.ErrorCode getErrorCode();

    /**
     * Sets the error code for the response.
     * This method allows specifying an error code for the response to detail the specific error
     * type when the response indicates a failure.
     *
     * @param errorCode The error code to set.
     */
    public void setErrorCode(RequestResponseProtocol.ErrorCode errorCode);

    /**
     * Retrieves the data object from the response.
     * This method returns the data included in the response, which may contain various types
     * of objects depending on the response's purpose.
     *
     * @return The data contained in the response.
     */
    public Object getData();

    /**
     * Sets the data object in the response.
     * This method allows setting the data in the response, which can be used to transport various
     * types of information from the server to the client.
     *
     * @param data The data object to set in the response.
     */
    public void setData(Object data);
}
