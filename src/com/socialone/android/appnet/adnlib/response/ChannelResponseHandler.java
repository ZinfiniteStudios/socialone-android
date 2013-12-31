package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Channel;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class ChannelResponseHandler extends AppDotNetResponseEnvelopeHandler<Channel> {
    protected ChannelResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Channel>>(){});
    }
}