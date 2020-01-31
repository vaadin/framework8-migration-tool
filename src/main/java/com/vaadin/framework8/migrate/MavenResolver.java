package com.vaadin.framework8.migrate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

public class MavenResolver {
    private static final RepositorySystem repoSystem = createServiceLocator()
            .getService(RepositorySystem.class);
    private static final List<RemoteRepository> repositories = Arrays.asList(
            new RemoteRepository.Builder("central", "default",
                    "https://repo.maven.apache.org/maven2").build(),
            new RemoteRepository.Builder("vaadin-prereleases", "default",
                    "https://maven.vaadin.com/vaadin-prereleases").build(),
            new RemoteRepository.Builder("snapshot", "default",
                    "https://oss.sonatype.org/content/repositories/vaadin-snapshots")
                            .build());

    private final DefaultRepositorySystemSession session;

    public MavenResolver(String targetFolder) {
        session = MavenRepositorySystemUtils.newSession();

        session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(
                session, new LocalRepository(targetFolder)));
        session.setReadOnly();
    }

    private static DefaultServiceLocator createServiceLocator() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils
                .newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class,
                BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class,
                FileTransporterFactory.class);
        locator.addService(TransporterFactory.class,
                HttpTransporterFactory.class);
        return locator;
    }

    public Stream<Artifact> resolve(String groupId, String artifactId,
            String version) {
        CollectRequest collectRequest = new CollectRequest(new Dependency(
                new DefaultArtifact(groupId, artifactId, "jar", version),
                "compile"), repositories);

        try {
            DependencyResult dependencyResult = repoSystem.resolveDependencies(
                    session, new DependencyRequest(collectRequest, null));
            return dependencyResult.getArtifactResults().stream()
                    .map(ArtifactResult::getArtifact);
        } catch (DependencyResolutionException e) {
            throw new RuntimeException(e);
        }

    }
}
