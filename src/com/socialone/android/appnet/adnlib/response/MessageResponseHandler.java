package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Message;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class MessageResponseHandler extends AppDotNetResponseEnvelopeHandler<Message> {
    protected MessageResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Message>>(){});
    }
}