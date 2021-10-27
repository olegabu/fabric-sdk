package com.example.fabric2.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CopyArchiveToContainerCmd;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;

@Component
@Profile("test")
public class FileUtilsOutsideDocker extends FileUtils {

    @Value("${fabric2_api.cli.container}")
    private String cliContainer;

    @Override
    public Path saveStreamToFile(InputStream inputStream, Path resultFilePath) {
        Path path = super.saveStreamToFile(inputStream, resultFilePath);

        return copyFileToDockerContainer(cliContainer, path);
    }

    private Path copyFileToDockerContainer(String dockerContainer, Path path) {
        DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost()).build();

        DockerClient dockerClient = DockerClientImpl.getInstance(dockerConfig, httpClient);
        Path containerPath = Path.of("/tmp");
        CopyArchiveToContainerCmd copyArchiveToContainerCmd = dockerClient.copyArchiveToContainerCmd(dockerContainer);
        copyArchiveToContainerCmd
                .withHostResource(path.toString())
                .withRemotePath(/*path*/containerPath./*getParent().*/toString())
                .exec();

        return containerPath.resolve(path.getFileName());
    }
}
