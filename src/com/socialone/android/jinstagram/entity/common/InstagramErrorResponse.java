package com.socialone.android.jinstagram.entity.common;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.exceptions.InstagramBadRequestException;
import com.socialone.android.jinstagram.exceptions.InstagramException;
import com.socialone.android.jinstagram.exceptions.InstagramRateLimitException;


public class InstagramErrorResponse {

    @SerializedName("meta")
    private Meta errorMeta;

    public void throwException() throws InstagramException {
        String joinedMessage = errorMeta.getErrorType() + ": " + errorMeta.getErrorMessage();
        switch (errorMeta.getCode()) {
        case 400:
            throw new InstagramBadRequestException(joinedMessage);
        case 420:
            throw new InstagramRateLimitException(joinedMessage);
        }

        throw new InstagramException(joinedMessage);
    }
}
