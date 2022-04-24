/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Code Technology Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.jpom.util;

import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import io.jpom.system.JpomRuntimeException;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 文件跟随器工具
 *
 * @author bwcx_jzy
 * @date 2019/7/21
 */
public abstract class BaseFileTailWatcher<T> {

    protected FileTailWatcherRun tailWatcherRun;
    protected File logFile;

    /**
     * 所有会话
     */
    protected final Set<T> socketSessions = new HashSet<>();

    public BaseFileTailWatcher(File logFile) throws IOException {
        this.logFile = logFile;
        this.tailWatcherRun = new FileTailWatcherRun(logFile, this::sendAll);
    }

    protected void send(T session, String msg) {
        try {
            if (session instanceof Session) {
                SocketSessionUtil.send((Session) session, msg);
            } else if (session instanceof WebSocketSession) {
                SocketSessionUtil.send((WebSocketSession) session, msg);
            } else {
                throw new JpomRuntimeException("没有对应类型");
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * 有新的日志
     *
     * @param msg 日志
     */
    private void sendAll(String msg) {
        Iterator<T> iterator = socketSessions.iterator();
        while (iterator.hasNext()) {
            T socketSession = iterator.next();
            try {
                this.send(socketSession, msg);
            } catch (Exception e) {
                DefaultSystemLog.getLog().error("发送消息失败", e);
                iterator.remove();
            }
        }
        if (this.socketSessions.isEmpty()) {
            this.close();
        }
    }

    /**
     * 添加监听会话
     *
     * @param name    文件名
     * @param session 会话
     */
    protected void add(T session, String name) {
        if (this.socketSessions.contains(session) || this.socketSessions.add(session)) {
            LimitQueue<String> limitQueue = this.tailWatcherRun.getLimitQueue();
            if (limitQueue.size() <= 0) {
                this.send(session, "日志文件为空");
                return;
            }
            this.send(session, StrUtil.format("监听{}日志成功,目前共有{}人正在查看", name, this.socketSessions.size()));
            // 开发发送头信息
            for (String s : limitQueue) {
                this.send(session, s);
            }
        }
        //        else {
        //            this.send(session, "添加日志监听失败");
        //        }
    }

    /**
     * 关闭
     */
    protected void close() {
        this.tailWatcherRun.close();
    }
}
