package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.Token;

public abstract class TokenResponseHandler extends AppDotNetResponseEnvelopeHandler<Token> {
    protected TokenResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Token>>(){});
    }
}