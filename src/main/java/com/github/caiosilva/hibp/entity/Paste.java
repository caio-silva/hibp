/* (C)2023 */
package com.github.caiosilva.hibp.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Paste {

    @SerializedName("Source")
    private String source;

    @SerializedName("Id")
    private String id;

    @SerializedName("Title")
    private String title;

    @SerializedName("Date")
    private Date date;

    @SerializedName("EmailCount")
    private int emailCount;
}
