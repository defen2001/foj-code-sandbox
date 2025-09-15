package com.defen.fojcodesandbox.service.impl;

import com.defen.fojcodesandbox.model.ExecuteCodeRequest;
import com.defen.fojcodesandbox.model.ExecuteCodeResponse;
import com.defen.fojcodesandbox.service.SandboxService;
import com.defen.fojcodesandbox.template.JavaDockerSandbox;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SandboxServiceImpl implements SandboxService {

    @Resource
    private JavaDockerSandbox javaDockerSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();
        ExecuteCodeResponse executeCodeResponse = javaDockerSandbox.executeCode(inputList, code, ".java");
        return executeCodeResponse;
    }
}
