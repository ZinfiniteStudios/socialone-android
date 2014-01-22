package com.socialone.android.jinstagram.realtime;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.entity.common.Meta;

import java.util.List;

public class SubscriptionsListResponse {

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("data")
    private List<SubscriptionResponseData> data;

    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * @return the data
     */
    public List<SubscriptionResponseData> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<SubscriptionResponseData> data) {
        this.data = data;
    }

}
