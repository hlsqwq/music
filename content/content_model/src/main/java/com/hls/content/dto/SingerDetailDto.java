package com.hls.content.dto;

import com.hls.base.po.Singer;
import com.hls.base.po.Song;

import java.util.List;

public class SingerDetailDto extends Singer {
    List<Song> songs;
}
