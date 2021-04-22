package io.jpom.build;

import cn.jiangzeyin.common.DefaultSystemLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * 监听rsync返回类 可拓展
 * @author myzf
 * @date 2021/4/20
 */
public class ConsoleOutputProcessReader  implements Runnable  {

    /** the default prefix for stdout. */
    public static String PREFIX_STDOUT = "[OUT] ";

    /** the default prefix for stderr. */
    public static String PREFIX_STDERR = "[ERR] ";

    public ConsoleOutputProcessReader(Process process, boolean stdout, String prefix) {
        m_Process = process;
        m_Stdout = stdout;
        m_Prefix = (prefix == null) ? (stdout ? PREFIX_STDOUT : PREFIX_STDERR) : prefix;
    }

    public ConsoleOutputProcessReader(Process process, boolean stdout) {
        this(process, stdout, null);
    }


    /** the process to read from. */
    protected Process m_Process;

    protected boolean m_Stdout;

    /** the prefix to use. */
    protected String m_Prefix;


    public String getPrefix() {
        return m_Prefix;
    }

    @Override
    public void run() {
        String 		line;
        BufferedReader reader;

        try {
            if (m_Stdout)
                reader = new BufferedReader(new InputStreamReader(m_Process.getInputStream()), 1024);
            else
                reader = new BufferedReader(new InputStreamReader(m_Process.getErrorStream()), 1024);
            while (m_Process.isAlive()) {
                try {
                    line = reader.readLine();
                }
                catch (IOException ioe) {
                    // has process stopped?
                    if (ioe.getMessage().toLowerCase().contains("stream closed"))
                        return;
                    else
                        throw ioe;
                }
                if (line != null)
                    process(line);
            }

            // make sure all data has been read
            while (true) {
                try {
                    line = reader.readLine();
                    if (line == null)
                        break;
                    else
                        process(line);
                }
                catch (Exception e) {
                    break;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Failed to read from " + (m_Stdout ? "stdout" : "stderr") + " for process #" + m_Process.hashCode() + ":");
            e.printStackTrace();
        }
    }


    protected void process(String line) {
        if (m_Stdout)
            DefaultSystemLog.getLog().info(m_Prefix + line);
        else
            DefaultSystemLog.getLog().error(m_Prefix + line);
    }
}
