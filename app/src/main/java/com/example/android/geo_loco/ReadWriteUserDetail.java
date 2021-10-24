package com.example.android.geo_loco;

import java.io.Serializable;

public class ReadWriteUserDetail  implements Serializable {
    public String textFullName ,textDob , textGender ,textEnrollment;

    public ReadWriteUserDetail(String textFullName , String textEnrollment , String textDob ,String textGender){ ;
        this.textFullName = textFullName;
        this.textEnrollment = textEnrollment;
        this.textDob = textDob;
        this.textGender = textGender;
    }
}
