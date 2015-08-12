package net.schmizz.sshj.xfer.scp;

import com.hierynomus.sshj.test.SshFixture;
import com.hierynomus.sshj.test.util.FileUtil;
import net.schmizz.sshj.SSHClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SCPFileTransferTest {

    public static final String DEFAULT_FILE_NAME = "my_file.txt";
    File targetDir;
    File sourceFile;
    File targetFile;
    SSHClient sshClient;

    @Rule
    public SshFixture fixture = new SshFixture();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        sourceFile = tempFolder.newFile(DEFAULT_FILE_NAME);
        FileUtil.writeToFile(sourceFile, "This is my file");
        targetDir = tempFolder.newFolder();
        targetFile = new File(targetDir + File.separator + DEFAULT_FILE_NAME);
        sshClient = fixture.setupConnectedDefaultClient();
        sshClient.authPassword("test", "test");
    }
    
    @After
    public void cleanup() {
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }

    @Test
    public void should_SCP_Upload_File() throws IOException {
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer();
        assertFalse(targetFile.exists());
        scpFileTransfer.upload(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        assertTrue(targetFile.exists());
    }

    @Test
    public void should_SCP_Upload_File_With_Bandwidth_Limit() throws IOException {
        // Limit upload transfert at 2Mo/s
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer().bandwidthLimit(16000);
        assertFalse(targetFile.exists());
        scpFileTransfer.upload(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        assertTrue(targetFile.exists());
    }

    @Test
    public void should_SCP_Download_File() throws IOException {
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer();
        assertFalse(targetFile.exists());
        scpFileTransfer.download(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        assertTrue(targetFile.exists());
    }

    @Test
    public void should_SCP_Download_File_With_Bandwidth_Limit() throws IOException {
        // Limit download transfert at 128Ko/s
        SCPFileTransfer scpFileTransfer = sshClient.newSCPFileTransfer().bandwidthLimit(1024);
        assertFalse(targetFile.exists());
        scpFileTransfer.download(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());
        assertTrue(targetFile.exists());
    }
}
