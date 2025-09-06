package com.defen.fojcodesandbox;


import com.defen.fojcodesandbox.model.ExecuteCodeRequest;
import com.defen.fojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

    // 添加查看代码沙箱状态的接口
}