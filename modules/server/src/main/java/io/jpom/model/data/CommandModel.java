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
package io.jpom.model.data;

import io.jpom.model.BaseJsonModel;
import io.jpom.model.BaseWorkspaceModel;
import io.jpom.service.h2db.TableName;
import io.jpom.util.StringUtil;

import java.util.List;

/**
 * 指令信息
 *
 * @author : Arno
 * @since : 2021/12/4 18:38
 */
@TableName(value = "COMMAND_INFO", name = "命令管理")
public class CommandModel extends BaseWorkspaceModel {
	/**
	 * 命令名称
	 */
	private String name;
	/**
	 * 命令描述
	 */
	private String desc;
	/**
	 * 指令内容
	 */
	private String command;
	/**
	 * 命令类型，0-shell，1-powershell
	 */
	private Integer type;
	/**
	 * 命令默认参数
	 */
	private String defParams;
	/**
	 * 默认关联大 ssh id
	 */
	private String sshIds;
	/**
	 * 自动执行的 cron
	 */
	private String autoExecCron;

	public String getAutoExecCron() {
		return autoExecCron;
	}

	public void setAutoExecCron(String autoExecCron) {
		this.autoExecCron = autoExecCron;
	}

	public String getSshIds() {
		return sshIds;
	}

	public void setSshIds(String sshIds) {
		this.sshIds = sshIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDefParams() {
		return defParams;
	}

	public void setDefParams(String defParams) {
		this.defParams = defParams;
	}

	public List<CommandParam> params() {
		return params(getDefParams());
	}

	public static List<CommandParam> params(String defParams) {
		return StringUtil.jsonConvertArray(defParams, CommandParam.class);
	}

	public static class CommandParam extends BaseJsonModel {
		/**
		 * 参数值
		 */
		private String value;
		/**
		 * 描述
		 */
		private String desc;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
