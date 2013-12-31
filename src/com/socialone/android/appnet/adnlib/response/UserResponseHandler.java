package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.User;

public abstract class UserResponseHandler extends AppDotNetResponseEnvelopeHandler<User> {
    protected UserResponseHandler() {
        super(new TypeToken<ResponseEnvelope<User>>(){});
    }
}