/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jingyue.lygame.utils;


import com.jingyue.lygame.bean.GameBean;

/**
 * Created by liu hong liang on 2016/9/24.
 */
//标题，图片，下载次数，类别，下载状态，下载进度，文件大小
public interface IGameLayout {
    int MODE_INSTALLED = 1;//显示启动模型：只要手机安装过这个包名就显示启动
    int MODE_DOWNLOADED_INSTALLED = 2;//是从这个平台下载，并且安装了才显示启动
    int showStartMode = MODE_DOWNLOADED_INSTALLED;//要使用的启动模型

    void setGameBean(GameBean gameBean);

    GameBean getGameBean();
}
