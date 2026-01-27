package com.hls.controller;

import com.hls.content.po.SongList;
import com.hls.content.service.ISongListService;
import com.hls.config.Access;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 歌单信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/song-list")
public class SongListController {

  @Resource
  private ISongListService songListService;

  /**
   * 添加歌单
   * 
   * @param songList 歌单信息
   */
  @Access(value = "member")
  @PostMapping("/add")
  public void addSongList(@RequestBody SongList songList) {
    songListService.addSongList(songList);
  }

  /**
   * 删除歌单
   * 
   * @param songListId 歌单ID
   */
  @Access(value = "member")
  @DeleteMapping("/delete/{songListId}")
  public void deleteSongList(@PathVariable Integer songListId) {
    songListService.deleteSongList(songListId);
  }

  /**
   * 更新歌单
   * 
   * @param songList 歌单信息
   */
  @Access(value = "member")
  @PutMapping("/update")
  public void updateSongList(@RequestBody SongList songList) {
    songListService.updateSongList(songList);
  }

  /**
   * 获取歌单详情
   * 
   * @param songListId 歌单ID
   * @return 歌单详情
   */
  @GetMapping("/detail/{songListId}")
  public SongList getSongListDetail(@PathVariable Integer songListId) {
    return songListService.getSongListDetail(songListId);
  }

  /**
   * 获取用户歌单列表
   * 
   * @param userId 用户ID
   * @return 歌单列表
   */
  @GetMapping("/list/{userId}")
  public List<SongList> getSongListByUserId(@PathVariable Integer userId) {
    return songListService.getSongListByUserId(userId);
  }

}
