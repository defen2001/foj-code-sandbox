package com.defen.fojcodesandbox.controller;

import com.defen.fojcodesandbox.model.ExecuteCodeRequest;
import com.defen.fojcodesandbox.model.ExecuteCodeResponse;
import com.defen.fojcodesandbox.service.SandboxService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/")
public class MainController {

    private static final String AUTH_HEADER = "Authorization";

    private static final String AUTH_SECRET = "33a9f64d-a07a-44b4-96a3-04004b49430d";

    @Resource
    private SandboxService sandboxService;

    /**
     * 执行方法
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse) {
        // 基本认证
        String header = httpServletRequest.getHeader(AUTH_HEADER);
        if (header == null || !header.equals(AUTH_SECRET)) {
            httpServletResponse.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("Execute code request is null");
        }
        return sandboxService.executeCode(executeCodeRequest);
    }
}
