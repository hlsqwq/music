package com.hls.content.vo;

import lombok.Data;

/**
 * 审核VO
 * @author hls
 * @since 2026-03-31
 */
@Data
public class AuditVo extends Audit {

    /**
     * 提交用户头像
     */
    private String avatar;

    /**
     * 提交用户名
     */
    private String username;

}
