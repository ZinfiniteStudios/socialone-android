package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.AccessToken;
import com.socialone.android.appnet.adnlib.data.Token;

import java.io.Reader;

public abstract class LoginResponseHandler extends AppDotNetResponseHandler<AccessToken> {
    protected LoginResponseHandler() {
        super(new TypeToken<AccessToken>(){});
    }

    @Override
    protected <T> T parseResponse(Reader reader) {
        return null;
    }

    @Override
    public void handleResponse(Reader reader) {}

    @Override
    public void onSuccess(AccessToken accessToken) {
        onSuccess(accessToken.getAccessToken(), accessToken.getToken());
    }

    public abstract void onSuccess(String accessToken, Token token);
}