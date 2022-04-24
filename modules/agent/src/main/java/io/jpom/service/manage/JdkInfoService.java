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
package io.jpom.service.manage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.JavaInfo;
import cn.hutool.system.JavaRuntimeInfo;
import io.jpom.service.BaseOperService;
import io.jpom.model.data.JdkInfoModel;
import io.jpom.system.AgentConfigBean;
import org.springframework.stereotype.Service;

/**
 * jdk 管理
 *
 * @author Arno
 * @date 2019/11/20
 */
@Service
public class JdkInfoService extends BaseOperService<JdkInfoModel> {

    public JdkInfoService() {
        super(AgentConfigBean.JDK_CONF);
    }

    /**
     * 使用中的jdk
     *
     * @return JdkInfoModel
     */
    private JdkInfoModel getDefaultJdk() {
        JavaRuntimeInfo info = new JavaRuntimeInfo();
        String homeDir = info.getHomeDir();
        String version = new JavaInfo().getVersion();
        if (StrUtil.isEmpty(homeDir) || StrUtil.isEmpty(version)) {
            return null;
        }
        String path = FileUtil.normalize(homeDir.replace("jre", ""));
        JdkInfoModel jdkInfoModel = new JdkInfoModel();
        jdkInfoModel.setId(IdUtil.fastUUID());
        jdkInfoModel.setVersion(version);
        jdkInfoModel.setPath(path);
        return jdkInfoModel;
    }

}
