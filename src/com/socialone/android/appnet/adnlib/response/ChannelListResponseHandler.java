package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ChannelList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class ChannelListResponseHandler extends AppDotNetResponseEnvelopeHandler<ChannelList> {
    protected ChannelListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<ChannelList>>(){});
    }
}