package io.jpom.controller.build;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.validator.ValidatorConfig;
import cn.jiangzeyin.common.validator.ValidatorItem;
import cn.jiangzeyin.common.validator.ValidatorRule;
import com.alibaba.fastjson.JSONObject;
import io.jpom.build.BuildUtil;
import io.jpom.common.BaseServerController;
import io.jpom.common.interceptor.OptLog;
import io.jpom.model.BaseEnum;
import io.jpom.model.data.BuildInfoModel;
import io.jpom.model.data.BuildModel;
import io.jpom.model.data.UserModel;
import io.jpom.model.log.BuildHistoryLog;
import io.jpom.model.log.UserOperateLogV1;
import io.jpom.model.vo.BuildHistoryLogVo;
import io.jpom.plugin.ClassFeature;
import io.jpom.plugin.Feature;
import io.jpom.plugin.MethodFeature;
import io.jpom.service.dblog.BuildInfoService;
import io.jpom.service.dblog.DbBuildHistoryLogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * new version for build info history controller
 * @author Hotstrip
 * @since 2021-08-26
 */
@RestController
@Feature(cls = ClassFeature.BUILD)
public class BuildInfoHistoryController extends BaseServerController {

	@Resource
	private BuildInfoService buildInfoService;
	@Resource
	private DbBuildHistoryLogService dbBuildHistoryLogService;

	/**
	 * 下载构建物
	 *
	 * @param logId 日志id
	 */
	@RequestMapping(value = "/build/history/download_file.html", method = RequestMethod.GET)
	@Feature(method = MethodFeature.DOWNLOAD)
	public void downloadFile(@ValidatorConfig(@ValidatorItem(value = ValidatorRule.NOT_BLANK, msg = "没有数据")) String logId) {
		BuildHistoryLog buildHistoryLog = dbBuildHistoryLogService.getByKey(logId);
		if (buildHistoryLog == null) {
			return;
		}
		BuildInfoModel item = buildInfoService.getByKey(buildHistoryLog.getBuildDataId());
		if (item == null) {
			return;
		}
		File logFile = BuildUtil.getHistoryPackageFile(item.getId(), buildHistoryLog.getBuildNumberId(), buildHistoryLog.getResultDirFile());
		if (!logFile.exists()) {
			return;
		}
		if (logFile.isFile()) {
			ServletUtil.write(getResponse(), logFile);
		} else {
			File zipFile = BuildUtil.isDirPackage(logFile);
			assert zipFile != null;
			ServletUtil.write(getResponse(), zipFile);
		}
	}


	@RequestMapping(value = "/build/history/download_log.html", method = RequestMethod.GET)
	@ResponseBody
	@Feature(method = MethodFeature.DOWNLOAD)
	public void downloadLog(@ValidatorItem(value = ValidatorRule.NOT_BLANK, msg = "没有数据") String logId) throws IOException {
		BuildHistoryLog buildHistoryLog = dbBuildHistoryLogService.getByKey(logId);
		Objects.requireNonNull(buildHistoryLog);
		BuildInfoModel item = buildInfoService.getByKey(buildHistoryLog.getBuildDataId());
		Objects.requireNonNull(item);
		File logFile = BuildUtil.getLogFile(item.getId(), buildHistoryLog.getBuildNumberId());
		if (!logFile.exists()) {
			return;
		}
		if (logFile.isFile()) {
			ServletUtil.write(getResponse(), logFile);
		}
	}

	@RequestMapping(value = "/build/history/history_list.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Feature(method = MethodFeature.LOG)
	public String historyList(String status,
							  @ValidatorConfig(value = {
									  @ValidatorItem(value = ValidatorRule.POSITIVE_INTEGER, msg = "limit error")
							  }, defaultVal = "10") int limit,
							  @ValidatorConfig(value = {
									  @ValidatorItem(value = ValidatorRule.POSITIVE_INTEGER, msg = "page error")
							  }, defaultVal = "1") int page,
							  String buildDataId) {
		Page pageObj = new Page(page, limit);
		Entity entity = Entity.create();
		//
		this.doPage(pageObj, entity, "startTime");
		BaseEnum anEnum = null;
		if (StrUtil.isNotEmpty(status)) {
			Integer integer = Convert.toInt(status);
			if (integer != null) {
				anEnum = BaseEnum.getEnum(BuildModel.Status.class, integer);
			}
		}

		if (anEnum != null) {
			entity.set("status", anEnum.getCode());
		}
		UserModel userModel = getUser();
		if (userModel.isSystemUser()) {
			if (StrUtil.isNotBlank(buildDataId)) {
				entity.set("buildDataId", buildDataId);
			}
		} else {
			Set<String> dataIds = this.getDataIds();
			if (StrUtil.isNotBlank(buildDataId)) {
				if (CollUtil.contains(dataIds, buildDataId)) {
					entity.set("buildDataId", buildDataId);
				} else {
					entity.set("buildDataId", StrUtil.DASHED);
				}
			} else {
				entity.set("buildDataId", dataIds);
			}
		}
		PageResult<BuildHistoryLog> pageResult = dbBuildHistoryLogService.listPage(entity, pageObj);
		List<BuildHistoryLogVo> buildHistoryLogVos = new ArrayList<>();
		pageResult.forEach(buildHistoryLog -> {
			BuildHistoryLogVo historyLogVo = new BuildHistoryLogVo();
			BeanUtil.copyProperties(buildHistoryLog, historyLogVo);
			String dataId = buildHistoryLog.getBuildDataId();
			BuildInfoModel item = buildInfoService.getByKey(dataId);
			if (item != null) {
				historyLogVo.setBuildName(item.getName());
			}
			buildHistoryLogVos.add(historyLogVo);
		});
		JSONObject jsonObject = JsonMessage.toJson(200, "获取成功", buildHistoryLogVos);
		jsonObject.put("total", pageResult.getTotal());
		return jsonObject.toString();
	}

	private Set<String> getDataIds() {
		Entity where = Entity.create();
		Page pageReq = new Page();
		List<BuildInfoModel> list = buildInfoService.listPage(where, pageReq);
		if (CollUtil.isEmpty(list)) {
			return new HashSet<>();
		} else {
			return list.stream().map(BuildInfoModel::getId).collect(Collectors.toSet());
		}
	}

	/**
	 * 构建
	 *
	 * @param logId id
	 * @return json
	 */
	@RequestMapping(value = "/build/history/delete_log.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@OptLog(UserOperateLogV1.OptType.DelBuildLog)
	@ResponseBody
	@Feature(method = MethodFeature.DEL_LOG)
	public String delete(@ValidatorConfig(@ValidatorItem(value = ValidatorRule.NOT_BLANK, msg = "没有数据")) String logId) {
		BuildHistoryLog buildHistoryLog = dbBuildHistoryLogService.getByKey(logId);
		Objects.requireNonNull(buildHistoryLog);

		if (!CollUtil.contains(this.getDataIds(), buildHistoryLog.getBuildDataId())) {
			return JsonMessage.getString(405, "没有权限");
		}
		JsonMessage<String> jsonMessage = dbBuildHistoryLogService.deleteLogAndFile(logId);
		return jsonMessage.toString();
	}
}