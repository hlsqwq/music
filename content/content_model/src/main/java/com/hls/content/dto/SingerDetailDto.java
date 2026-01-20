package com.hls.content.dto;

import com.hls.content.po.Singer;
import com.hls.content.po.Song;

import java.util.List;

public class SingerDetailDto extends Singer {
    List<Song> songs;
}
