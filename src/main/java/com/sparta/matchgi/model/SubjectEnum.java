package com.sparta.matchgi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum SubjectEnum {
    SOCCER("축구"),
    BASKETBALL("농구"),
    TENNIS("테니스"),
    BADMINTON("배드민턴"),
    BILLIARDS("당구"),
    BOWLING("볼링"),

    ETC("기타");


    private final String value;

    SubjectEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    @JsonCreator
    public static SubjectEnum from(String name)
    {
        name=name.toUpperCase();
        return SubjectEnum.valueOf(name);
    }
}
