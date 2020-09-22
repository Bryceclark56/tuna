package me.bc56.discord.api;

import me.bc56.discord.model.exception.DiscordApiException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiHelperTest {

    static final String fakeJSONErrorString = "{\"message\":\"I'm a little teapot\",\"code\":6}";
    static final DiscordApiException expectedException = new DiscordApiException(606, 6, "I'm a little teapot");

    @Mock
    Call<Object> mockRequest;

    @Test
    void shouldMakeSuccessfulRequest() throws DiscordApiException, IOException {
        Object expectedObject = new Object();

        Response<Object> fakeResponse = Response.success(200, expectedObject);
        when(mockRequest.execute()).thenReturn(fakeResponse);

        Object actualObject = ApiHelper.makeRequest(mockRequest);

        assertEquals(expectedObject, actualObject);
    }

    @Test
    void shouldMakeUnsuccessfulRequest() throws IOException {
        ResponseBody fakeResponseBody = ResponseBody.create(MediaType.get("application/json"), fakeJSONErrorString);
        Response<Object> fakeResponse = Response.error(606, fakeResponseBody);
        when(mockRequest.execute()).thenReturn(fakeResponse);

        DiscordApiException actualException = assertThrows(DiscordApiException.class, () ->
                ApiHelper.makeRequest(mockRequest)
        );

        assertTrue(expectedException.equals(actualException));
    }
}