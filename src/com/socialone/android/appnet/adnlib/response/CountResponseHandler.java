package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Count;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class CountResponseHandler extends AppDotNetResponseEnvelopeHandler<Count> {
    protected CountResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Count>>(){});
    }
}