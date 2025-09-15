package com.defen.fojcodesandbox.template;

import cn.hutool.core.collection.CollectionUtil;
import com.defen.fojcodesandbox.model.ExecuteCodeResponse;
import com.defen.fojcodesandbox.model.ExecuteMessage;
import com.defen.fojcodesandbox.model.enums.ExecuteCodeStatusEnum;
import com.defen.fojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 原生代码沙箱实现
 */
@Component
@Slf4j
public class JavaNativeCodeSandbox extends SandboxTemplate {

    @Override
    protected ExecuteCodeResponse compileAndRun(File codeFile, List<String> inputList) throws IOException {
        // 1. 编译代码
        String[] compileCmd = {"javac", "-encoding", "utf-8", codeFile.getAbsolutePath()};
        log.info("执行命令：{}", Arrays.toString(compileCmd));

        // 创建并执行编译process
        Process compileProcess = Runtime.getRuntime().exec(compileCmd);
        // 拿到编译process执行信息
        ExecuteMessage compileResult = ProcessUtils.getProcessMessage(compileProcess);
        if (compileResult.getExitValue() != 0) {
            return ExecuteCodeResponse.builder()
                    .status(ExecuteCodeStatusEnum.COMPILE_ERROR.getValue())
                    .message(compileResult.getErrorMessage().replaceAll(codeFile.getParent(), ""))
                    .build();
        }

        // 2. 执行代码
        List<ExecuteMessage> executeMessages = new ArrayList<>();

        if (CollectionUtil.isEmpty(inputList)) {
            inputList = new ArrayList<>();
            inputList.add(null);
        }

        for (String input : inputList) {
            ExecuteMessage executeResult = runOneCase(codeFile, input);
            executeMessages.add(executeResult);

            // 已经有用例失败了
            if (executeResult.getExitValue() != 0) {
                Integer exitValue = executeResult.getExitValue();
                return ExecuteCodeResponse.builder()
                        .status(exitValue)
                        .message(executeResult.getErrorMessage())
                        .results(executeMessages)
                        .build();
            }
        }

        return ExecuteCodeResponse.builder()
                .status(ExecuteCodeStatusEnum.SUCCESS.getValue())
                .message(ExecuteCodeStatusEnum.SUCCESS.getText())
                .results(executeMessages)
                .build();
    }

    public ExecuteMessage runOneCase(File codeFile, String input) throws IOException {
        String[] runCmd = {"java", String.format("-Xmx%dm", this.javaXmx), "-Dfile.encoding=UTF-8",
                "-cp", codeFile.getParent(), "Main"};

        long start = System.currentTimeMillis();

        Process runProcess = Runtime.getRuntime().exec(runCmd);
        // 超时控制
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(this.timeout);
                // 超时了，直接杀死运行代码的进程
                runProcess.destroy();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

        ExecuteMessage executeMessage = null;
        if (input == null) {
            executeMessage = ProcessUtils.getProcessMessage(runProcess);
        } else {
            executeMessage = ProcessUtils.getProcessMessage(runProcess, input);
        }

        long end = System.currentTimeMillis();
        executeMessage.setTime(end - start);

        if (!thread.isAlive()) {
            executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(2);
            executeMessage.setErrorMessage("超出时间限制");
        }

        return executeMessage;
    }
}