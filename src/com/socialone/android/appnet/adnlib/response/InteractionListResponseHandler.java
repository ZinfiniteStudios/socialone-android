package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.InteractionList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class InteractionListResponseHandler extends AppDotNetResponseEnvelopeHandler<InteractionList> {
    protected InteractionListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<InteractionList>>(){});
    }
}
