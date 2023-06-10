/* (C)2023 */
package com.github.caiosilva.hibp.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Breach {

    @SerializedName("Name")
    private String name;

    @SerializedName("Title")
    private String title;

    @SerializedName("Domain")
    private String domain;

    @SerializedName("BreachDate")
    private Date breachDate;

    @SerializedName("AddedDate")
    private Date addedDate;

    @SerializedName("ModifiedDate")
    private Date modifiedDate;

    @SerializedName("PwnCount")
    private long pwnCount;

    @SerializedName("Description")
    private String description;

    @SerializedName("DataClasses")
    private List<String> dataClasses;

    @SerializedName("IsVerified")
    private boolean isVerified;

    @SerializedName("IsFabricated")
    private boolean isFabricated;

    @SerializedName("IsSensitive")
    private boolean isSensitive;

    @SerializedName("IsRetired")
    private boolean isRetired;

    @SerializedName("IsSpamList")
    private boolean isSpamList;

    @SerializedName("LogoPath")
    private String logoPath;
}
