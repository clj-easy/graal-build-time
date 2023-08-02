package clj_easy.graal_build_time;

import java.util.List;
import java.nio.file.Path;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

public class InitClojureClasses implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        List<Path> classPath = access.getApplicationClassPath();
        String[] packages = clj_easy.graal_build_time.packages.list(classPath);
        String packagesStr = clj_easy.graal_build_time.packages.listStr(packages);
        System.out.println("[clj-easy/graal-build-time] Registering packages for build time initialization: " + packagesStr);
        RuntimeClassInitialization.initializeAtBuildTime(packages);
    }

}
