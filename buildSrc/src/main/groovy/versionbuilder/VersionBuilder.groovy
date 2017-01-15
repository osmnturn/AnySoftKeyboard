package versionbuilder

class VersionBuilder {
    static final int GIT_COMMIT_COUNT_NORMALIZE = 2728;
    static final int GIT_COMMIT_COUNT_MINOR_NORMALIZE = 1;

    static def buildGitVersionNumber() {
        int k=Integer.parseInt('git rev-list --count HEAD'.execute().text.trim());

        int  versionNumber= k- GIT_COMMIT_COUNT_NORMALIZE;
        println("versionNumber="+versionNumber);
        return versionNumber;
    }

    static def buildGitVersionName() {
        String versionName=String.format("%d.%d.%d", 1, 0, buildGitVersionNumber() - GIT_COMMIT_COUNT_MINOR_NORMALIZE);
        println("version Name="+versionName);
        return versionName;
    }

}