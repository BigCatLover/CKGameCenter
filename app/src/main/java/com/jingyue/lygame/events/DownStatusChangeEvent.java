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

package com.jingyue.lygame.events;

import android.util.Log;

import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.utils.download.TasksManager;

/**
 * Created by liu hong liang on 2016/9/14.
 */
public class DownStatusChangeEvent {
    public Integer id;
    public String gameId;

    public DownStatusChangeEvent(Integer id, String gamenId) {
        if (id != null && gamenId == null) {
            DownloadBean taskModelById = TasksManager.getImpl().getTaskModelById(id);
            if (taskModelById == null) {
                Log.d("DownStatusChangeEvent", "error=no get TasksManagerModel by id=" + id);
            } else {
                gamenId = taskModelById.getGameId();
            }
        } else if (id == null && gamenId != null) {
            DownloadBean taskModelByGameId = TasksManager.getImpl().getTaskModelByGameId(gamenId);
            if (taskModelByGameId == null) {
            } else {
                id = taskModelByGameId.getId();
            }
        } else if (id == null && gamenId == null) {
        }
        this.id = id;
        this.gameId = gamenId;
    }
}
