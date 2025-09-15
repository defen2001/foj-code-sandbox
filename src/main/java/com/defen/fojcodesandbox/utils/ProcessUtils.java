package com.defen.fojcodesandbox.utils;

import com.defen.fojcodesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 进程工具类
 */
@Slf4j
public class ProcessUtils {

    /**
     * 执行进程，并获取信息
     *
     * @param runProcess 进程
     * @return 执行信息
     */
    public static ExecuteMessage getProcessMessage(Process runProcess) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            // 计时
            long start = System.currentTimeMillis();

            // 等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            executeMessage.setMessage(getProcessOutput(runProcess.getInputStream()));
            executeMessage.setErrorMessage(getProcessOutput(runProcess.getErrorStream()));

            long end = System.currentTimeMillis();
            executeMessage.setTime(end - start);
        } catch (Exception e) {
            log.error("编译失败：{}", e.toString());
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取信息
     *
     * @param runProcess
     * @param input
     * @return
     * @throws InterruptedException
     */
    public static ExecuteMessage getProcessMessage(Process runProcess, String input) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StringReader inputReader = new StringReader(input);
            BufferedReader inputBufferedReader = new BufferedReader(inputReader);

            // 计时
            long start = System.currentTimeMillis();

            // 输入（模拟控制台输入）
            PrintWriter consoleInput = new PrintWriter(runProcess.getOutputStream());
            String line;
            while ((line = inputBufferedReader.readLine()) != null) {
                consoleInput.println(line);
            }
            consoleInput.flush();
            consoleInput.close();

            // 获取退出码
            int exitValue = runProcess.waitFor();

            executeMessage.setExitValue(exitValue);
            executeMessage.setMessage(getProcessOutput(runProcess.getInputStream()));
            executeMessage.setErrorMessage(getProcessOutput(runProcess.getErrorStream()));

            long end = System.currentTimeMillis();
            executeMessage.setTime(end - start);
        } catch (Exception e) {
            log.error("运行失败：{}", e.toString());
        }
        return executeMessage;
    }

    /**
     * 获取某个流的输出
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String getProcessOutput(InputStream inputStream) throws IOException {
        // 分批获取进程的正常输出
        // Linux写法
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        // Windows写法
        // BufferedReader bufferedReader = new BufferedReader(new
        // InputStreamReader(inputStream, "GBK"));
        StringBuilder outputSb = new StringBuilder();
        // 逐行读取
        String outputLine;
        while ((outputLine = bufferedReader.readLine()) != null) {
            outputSb.append(outputLine).append("\n");
        }
        bufferedReader.close();
        return outputSb.toString();
    }

}