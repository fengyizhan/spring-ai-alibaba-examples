package com.hd.ai.rag;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

public class Test {
    public static void main(String[] args) throws GitAPIException {
        CloneCommand clone = Git.cloneRepository();
        clone.setURI("https://hdgit.huadingyun.cn/huadingyun/oms-adb.git");
        clone.setDirectory(new File("D://test/1"));
        clone.setBranch("test");
        clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider("fengyizhan", "qq19861026"));
        Git git = clone.call();
    }
}
