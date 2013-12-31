package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.PostList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class PostListResponseHandler extends AppDotNetResponseEnvelopeHandler<PostList> {
    protected PostListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<PostList>>(){});
    }
}