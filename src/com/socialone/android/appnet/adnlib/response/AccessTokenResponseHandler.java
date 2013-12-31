package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.AppDotNetResponseException;
import com.socialone.android.appnet.adnlib.data.AccessToken;

import java.io.Reader;

public class AccessTokenResponseHandler extends AppDotNetResponseHandler<AccessToken> {
    private LoginResponseHandler loginResponseHandler;
    private AppDotNetClient client;

    public AccessTokenResponseHandler(AppDotNetClient client, LoginResponseHandler loginResponseHandler) {
        super(new TypeToken<AccessToken>(){});
        this.client = client;
        this.loginResponseHandler = loginResponseHandler;
    }

    @Override
    public void handleResponse(Reader reader) {
        final AccessToken response = parseResponse(reader);
        if (response.isError()) {
            onError(new AppDotNetResponseException(response.getError(), response.getErrorSlug()));
        } else {
            onSuccess(response);
        }
    }

    @Override
    public void onSuccess(AccessToken accessToken) {
        client.setToken(accessToken.getAccessToken());
        loginResponseHandler.onSuccess(accessToken);
    }

    @Override
    public void onError(Exception error) {
        loginResponseHandler.onError(error);
    }
}