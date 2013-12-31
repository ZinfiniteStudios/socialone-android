package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class PostResponseHandler extends AppDotNetResponseEnvelopeHandler<Post> {
    protected PostResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Post>>(){});
    }
}