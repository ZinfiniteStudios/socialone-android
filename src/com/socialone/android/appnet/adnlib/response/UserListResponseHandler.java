package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.UserList;

public abstract class UserListResponseHandler extends AppDotNetResponseEnvelopeHandler<UserList> {
    protected UserListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<UserList>>(){});
    }
}