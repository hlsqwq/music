package com.hls.content.dto;

import com.hls.base.po.Song;
import com.hls.base.po.SongList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongListDto extends SongList {

    private List<Song> songList;

    private List<Integer> songIds;

}
