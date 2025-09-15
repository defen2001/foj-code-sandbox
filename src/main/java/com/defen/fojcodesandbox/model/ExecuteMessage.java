package com.defen.fojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进程执行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteMessage {

    /**
     * 退出码
     */
    private Integer exitValue;

    /**
     * 正常信息
     */
    private String message;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行时间
     */
    private Long time;

    /**
     * 运行内存
     */
    private Long memory;
}