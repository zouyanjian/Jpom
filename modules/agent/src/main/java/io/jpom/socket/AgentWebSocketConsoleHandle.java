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
package io.jpom.socket;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import io.jpom.JpomApplication;
import io.jpom.common.commander.AbstractProjectCommander;
import io.jpom.model.data.NodeProjectInfoModel;
import io.jpom.service.manage.ConsoleService;
import io.jpom.service.manage.ProjectInfoService;
import io.jpom.util.SocketSessionUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;

/**
 * 插件端,控制台socket
 *
 * @author jiangzeyin
 * @date 2019/4/16
 */
@ServerEndpoint(value = "/console")
@Component
public class AgentWebSocketConsoleHandle extends BaseAgentWebSocketHandle {

	private static ProjectInfoService projectInfoService;

	@OnOpen
	public void onOpen(Session session) {
		try {
			if (super.checkAuthorize(session)) {
				return;
			}
			String projectId = super.getParameters(session, "projectId");
			String copyId = super.getParameters(session, "copyId");
			// 判断项目
			if (!JpomApplication.SYSTEM_ID.equals(projectId)) {
				if (projectInfoService == null) {
					projectInfoService = SpringUtil.getBean(ProjectInfoService.class);
				}
				NodeProjectInfoModel nodeProjectInfoModel = this.checkProject(projectId, copyId, session);
				if (nodeProjectInfoModel == null) {
					return;
				}
				//
				SocketSessionUtil.send(session, "连接成功：" + nodeProjectInfoModel.getName());
			}
		} catch (Exception e) {
			DefaultSystemLog.getLog().error("socket 错误", e);
			try {
				SocketSessionUtil.send(session, JsonMessage.getString(500, "系统错误!"));
				session.close();
			} catch (IOException e1) {
				DefaultSystemLog.getLog().error(e1.getMessage(), e1);
			}
		}
	}

	/**
	 * 静默消息不做过多处理
	 *
	 * @param consoleCommandOp 操作
	 * @param session          回话
	 * @return true
	 */
	private boolean silentMsg(ConsoleCommandOp consoleCommandOp, Session session) {
		if (consoleCommandOp == ConsoleCommandOp.heart) {
			return true;
		}
//        if (consoleCommandOp == ConsoleCommandOp.top) {
//            TopManager.addMonitor(session);
//            return true;
//        }
		return false;
	}

	private NodeProjectInfoModel checkProject(String projectId, String copyId, Session session) throws IOException {
		NodeProjectInfoModel nodeProjectInfoModel = projectInfoService.getItem(projectId);
		if (nodeProjectInfoModel == null) {
			SocketSessionUtil.send(session, "没有对应项目：" + projectId);
			session.close();
			return null;
		}
		// 判断副本集
		if (StrUtil.isNotEmpty(copyId)) {
			NodeProjectInfoModel.JavaCopyItem copyItem = nodeProjectInfoModel.findCopyItem(copyId);
			if (copyItem == null) {
				SocketSessionUtil.send(session, "获取项目信息错误,没有对应副本：" + copyId);
				session.close();
				return null;
			}
		}
		return nodeProjectInfoModel;
	}

	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
		JSONObject json = JSONObject.parseObject(message);
		String op = json.getString("op");
		ConsoleCommandOp consoleCommandOp = ConsoleCommandOp.valueOf(op);
		if (silentMsg(consoleCommandOp, session)) {
			return;
		}
		String projectId = json.getString("projectId");
		String copyId = json.getString("copyId");
		NodeProjectInfoModel nodeProjectInfoModel = this.checkProject(projectId, copyId, session);
		if (nodeProjectInfoModel == null) {
			return;
		}
		runMsg(consoleCommandOp, session, nodeProjectInfoModel, copyId, json);
	}

	private void runMsg(ConsoleCommandOp consoleCommandOp, Session session, NodeProjectInfoModel nodeProjectInfoModel, String copyId, JSONObject reqJson) throws Exception {
		ConsoleService consoleService = SpringUtil.getBean(ConsoleService.class);
		//
		NodeProjectInfoModel.JavaCopyItem copyItem = nodeProjectInfoModel.findCopyItem(copyId);
		JSONObject resultData = null;
		String strResult;
		boolean logUser = false;
		try {
			// 执行相应命令
			switch (consoleCommandOp) {
				case start:
				case restart:
					logUser = true;
					strResult = consoleService.execCommand(consoleCommandOp, nodeProjectInfoModel, copyItem);
					if (strResult.contains(AbstractProjectCommander.RUNNING_TAG)) {
						resultData = JsonMessage.toJson(200, "操作成功:" + strResult);
					} else {
						resultData = JsonMessage.toJson(400, strResult);
					}
					break;
				case stop:
					logUser = true;
					// 停止项目
					strResult = consoleService.execCommand(consoleCommandOp, nodeProjectInfoModel, copyItem);
					if (strResult.contains(AbstractProjectCommander.STOP_TAG)) {
						resultData = JsonMessage.toJson(200, "操作成功：" + strResult);
					} else {
						resultData = JsonMessage.toJson(500, strResult);
					}
					break;
				case status:
					// 获取项目状态
					strResult = consoleService.execCommand(consoleCommandOp, nodeProjectInfoModel, copyItem);
					if (strResult.contains(AbstractProjectCommander.RUNNING_TAG)) {
						resultData = JsonMessage.toJson(200, "运行中", strResult);
					} else {
						resultData = JsonMessage.toJson(404, "未运行", strResult);
					}
					break;
				case showlog: {
					// 进入管理页面后需要实时加载日志
					//        日志文件路径
					String fileName = reqJson.getString("fileName");
					File file;
					if (StrUtil.isEmpty(fileName)) {
						file = copyItem == null ? new File(nodeProjectInfoModel.getLog()) : nodeProjectInfoModel.getLog(copyItem);
					} else {
						file = FileUtil.file(nodeProjectInfoModel.allLib(), fileName);
					}
					try {
						boolean watcher = AgentFileTailWatcher.addWatcher(file, session);
						if (!watcher) {
							SocketSessionUtil.send(session, "监听文件失败,可能文件不存在");
						}
					} catch (IOException io) {
						DefaultSystemLog.getLog().error("监听日志变化", io);
						SocketSessionUtil.send(session, io.getMessage());
					}
					break;
				}
				default:
					resultData = JsonMessage.toJson(404, "不支持的方式：" + consoleCommandOp.name());
					break;
			}
		} catch (Exception e) {
			DefaultSystemLog.getLog().error("执行命令失败", e);
			SocketSessionUtil.send(session, "执行命令失败,详情如下：");
			SocketSessionUtil.send(session, ExceptionUtil.stacktraceToString(e));
			return;
		} finally {
			if (logUser) {
				// 记录操作人
				NodeProjectInfoModel newNodeProjectInfoModel = projectInfoService.getItem(nodeProjectInfoModel.getId());
				String name = getOptUserName(session);
				newNodeProjectInfoModel.setModifyUser(name);
				projectInfoService.updateItem(newNodeProjectInfoModel);
			}
		}
		// 返回数据
		if (resultData != null) {
			reqJson.putAll(resultData);
			reqJson.put("JPOM_MSG", "JPOM_MSG");
			DefaultSystemLog.getLog().info(reqJson.toString());
			SocketSessionUtil.send(session, reqJson.toString());
		}
	}

	@Override
	@OnClose
	public void onClose(Session session) {
		super.onClose(session);
		AgentFileTailWatcher.offline(session);
	}

	@OnError
	@Override
	public void onError(Session session, Throwable thr) {
		super.onError(session, thr);
	}
}
