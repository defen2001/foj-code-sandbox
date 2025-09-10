package com.defen.fojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

/**
 * 连接 docker 测试用例
 */
public class DockerDemo {

    private static DockerClient dockerClient;

    static {
        // 默认使用本地 Docker，确保 Docker Daemon 已启动并允许 tcp:// 或 unix:///var/run/docker.sock
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock") // Linux / Mac
                // .withDockerHost("tcp://127.0.0.1:2375") // Windows Docker Desktop (开启2375无TLS)
                .build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    /**
     * 在 Docker 容器中执行 Java 代码
     *
     * @param userCode 用户提交的 Java 代码（必须有 public class Main）
     * @return 程序运行输出
     */
    public static String runJavaCode(String userCode) throws Exception {
        // 1. 准备临时代码目录
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + "tempCode";
        File tempDir = new File(globalCodePathName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File codeFile = new File(tempDir, "Main.java");
        try (FileWriter fw = new FileWriter(codeFile)) {
            fw.write(userCode);
        }

        // 2. 拉取 openjdk 镜像
        dockerClient.pullImageCmd("openjdk:8u92-jdk-alpine")
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // 3. 挂载目录
        Volume volume = new Volume("/app");
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withBinds(new Bind(tempDir.getAbsolutePath(), volume));

        // 4. 创建容器
        CreateContainerResponse container = dockerClient.createContainerCmd("openjdk:8-alpine")
                .withHostConfig(hostConfig)
                .withCmd("sleep", "60") // 先让容器存活一段时间
                .withWorkingDir("/app")
                .exec();

        String containerId = container.getId();

        try {
            // 启动容器
            dockerClient.startContainerCmd(containerId).exec();

            // 5. 编译代码
            String compileId = dockerClient.execCreateCmd(containerId)
                    .withCmd("javac", "/app/Main.java")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withWorkingDir("/app")
                    .exec()
                    .getId();

            dockerClient.execStartCmd(compileId).exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame frame) {
                    System.out.print(new String(frame.getPayload())); // 编译输出
                }
            }).awaitCompletion(10, TimeUnit.SECONDS);

            // 6. 执行代码
            String execId = dockerClient.execCreateCmd(containerId)
                    .withCmd("java", "-cp", "/app", "Main")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withWorkingDir("/app")   // 指定目录
                    .exec()
                    .getId();

            StringBuilder output = new StringBuilder();
            dockerClient.execStartCmd(execId).exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame frame) {
                    output.append(new String(frame.getPayload()));
                }
            }).awaitCompletion(10, TimeUnit.SECONDS);


            return output.toString();
        } finally {
            // 停止 & 删除容器
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
        }
    }

    // 测试
    public static void main(String[] args) throws Exception {
        String code = "public class Main {\n" +
                "                    public static void main(String[] args) {\n" +
                "                        System.out.println(\"Hello from Docker Sandbox!\");\n" +
                "                    }\n" +
                "                }";
        String result = runJavaCode(code);
        System.out.println("执行结果：\n" + result);
    }
}
