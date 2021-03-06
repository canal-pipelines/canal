/**
 * Copyright ${year} Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.canal.extensions

import io.pivotal.canal.builders.PipelineBuilder
import io.pivotal.canal.model.PipelineModel
import io.pivotal.canal.model.Pipelines


fun pipelines(appendApps: AppPipelineAppender.() -> Unit): Pipelines {
    val appender = AppPipelineAppender(Pipelines())
    appender.appendApps()
    return appender.pipelines
}

class AppPipelineAppender(var pipelines: Pipelines) {

    fun app(name: String, appendPipelines: PipelineAppender.() -> Unit) {
        val appender = PipelineAppender()
        appender.appendPipelines()
        val newPipelines = appender.pipelines
        pipelines = pipelines.withPipelinesForApp(name, newPipelines)
    }

}

class PipelineAppender(var pipelines: List<PipelineModel> = emptyList()) {

    fun pipeline(name: String, withinPipeline: PipelineBuilder.() -> Unit) {
        val pb = PipelineBuilder(PipelineModel(name))
        pb.withinPipeline()
        pipelines += pb.pipeline
    }

}
