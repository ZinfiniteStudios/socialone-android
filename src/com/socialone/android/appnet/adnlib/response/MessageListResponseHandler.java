package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.MessageList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class MessageListResponseHandler extends AppDotNetResponseEnvelopeHandler<MessageList> {
    protected MessageListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<MessageList>>(){});
    }
}