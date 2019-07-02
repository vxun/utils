package fiftyone.utils.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * @author : Stephen
 * @date : 2019/7/2 19:53
 * @description :
 */
public class SSHTunnel {

    public static void main(String[] args) {
        String sshUser = "";
        String sshHost = "";
        String password = "";
        int sshPort = 22;

        String remoteHost = "mysqlhost";
        int remotePort = 3306;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(sshUser, sshHost, sshPort);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            ServerSocket serverSocket = new ServerSocket();
            int localPort = serverSocket.getLocalPort();
            int assignedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }

    }
}
