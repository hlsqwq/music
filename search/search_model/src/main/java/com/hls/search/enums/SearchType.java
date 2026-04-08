package com.hls.search.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    Song("song"),
    singer("singer"),
    mv("mv"),
    album("album"),
    songList("songList");


    private final String value;
}
