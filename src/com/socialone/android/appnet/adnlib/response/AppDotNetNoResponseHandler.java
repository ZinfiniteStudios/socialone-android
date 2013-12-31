package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.IAppDotNetObject;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public class AppDotNetNoResponseHandler extends AppDotNetResponseEnvelopeHandler {
    public AppDotNetNoResponseHandler() {
        super(new TypeToken<ResponseEnvelope>(){});
    }

    @Override
    public void onSuccess(IAppDotNetObject responseData) {}
}