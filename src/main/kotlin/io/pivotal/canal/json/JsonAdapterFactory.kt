/*
 * Copyright 2019 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package io.pivotal.canal.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.pivotal.canal.extensions.builder.Artifacts
import io.pivotal.canal.extensions.builder.Triggers
import io.pivotal.canal.model.*
import io.pivotal.canal.model.cloudfoundry.*

class JsonAdapterFactory {
    @JvmOverloads fun jsonAdapterBuilder(builder: Moshi.Builder = Moshi.Builder(),
                                         useCloudSpecificAdapter: Boolean = true): Moshi.Builder {
        builder
                .add(StageGraphAdapter())
                .add(ExpressionConditionAdapter())
                .add(ExpressionPreconditionAdapter())
                .add(PipelineTemplateInstanceAdapter())

        if (useCloudSpecificAdapter) {
            builder.add(CloudSpecificToJsonAdapter())
        }

        builder
                .add(jsonNumberAdapter)
                .add(PolymorphicJsonAdapterFactory.of(Triggers.Trigger::class.java, "type")
                        .withSubtype(Triggers.Artifactory::class.java, "artifactory")
                        .withSubtype(Triggers.Jenkins::class.java, "jenkins")
                        .withSubtype(Triggers.GitHub::class.java, "git")
                )
                .add(PolymorphicJsonAdapterFactory.of(Artifacts.Artifact::class.java, "type")
                        .withSubtype(Artifacts.MavenFile::class.java, "maven/file")
                        .withSubtype(Artifacts.JenkinsFile::class.java, "jenkins/file")
                )
                .add(PolymorphicJsonAdapterFactory.of(Condition::class.java, "type")
                        .withSubtype(ExpressionCondition::class.java, "expression")
                )
                .add(PolymorphicJsonAdapterFactory.of(Precondition::class.java, "type")
                        .withSubtype(ExpressionPrecondition::class.java, "expression")
                )
                .add(PolymorphicJsonAdapterFactory.of(Notification::class.java, "type")
                        .withSubtype(EmailNotification::class.java, "email")
                )
                .add(PolymorphicJsonAdapterFactory.of(Cluster::class.java, "cloudProvider")
                        .withSubtype(CloudFoundryCluster::class.java, "cloudfoundry")
                )
                .add(PolymorphicJsonAdapterFactory.of(Manifest::class.java, "type")
                        .withSubtype(DirectManifest::class.java, "direct")
                        .withSubtype(ArtifactManifest::class.java, "artifact")
                )
                .add(PolymorphicJsonAdapterFactory.of(SpecificStageConfig::class.java, "type")
                        .withSubtype(DestroyServerGroup::class.java, "destroyServerGroup")
                        .withSubtype(DeployService::class.java, "deployService")
                        .withSubtype(DestroyService::class.java, "destroyService")
                        .withSubtype(DisableServerGroup::class.java, "disableServerGroup")
                        .withSubtype(EnableServerGroup::class.java, "enableServerGroup")
                        .withSubtype(ResizeServerGroup::class.java, "resizeServerGroup")
                        .withSubtype(Wait::class.java, "wait")
                        .withSubtype(ManualJudgment::class.java, "manualJudgment")
                        .withSubtype(Webhook::class.java, "webhook")
                        .withSubtype(Canary::class.java, "kayentaCanary")
                        .withSubtype(Deploy::class.java, "deploy")
                        .withSubtype(CheckPreconditions::class.java, "checkPreconditions")
                        .withSubtype(Jenkins::class.java, "jenkins")
                        .withSubtype(Rollback::class.java, "rollbackCluster")
                )
                .add(PolymorphicJsonAdapterFactory.of(Variable::class.java, "type")
                        .withSubtype(IntegerVariable::class.java, "int")
                        .withSubtype(StringVariable::class.java, "string")
                        .withSubtype(FloatVariable::class.java, "float")
                        .withSubtype(BooleanVariable::class.java, "boolean")
                        .withSubtype(ListVariable::class.java, "list")
                        .withSubtype(ObjectVariable::class.java, "object")
                )
                .add(PolymorphicJsonAdapterFactory.of(Inject::class.java, "type")
                        .withSubtype(Inject.Before::class.java, "before")
                        .withSubtype(Inject.After::class.java, "after")
                        .withSubtype(Inject.First::class.java, "first")
                        .withSubtype(Inject.Last::class.java, "last")
                )
                .add(PolymorphicJsonAdapterFactory.of(ManifestSource::class.java, "type")
                        .withSubtype(ManifestSourceArtifact::class.java, "artifact")
                        .withSubtype(ManifestSourceUserProvided::class.java, "userProvided")
                        .withSubtype(ManifestSourceDirect::class.java, "direct")
                )
                .add(PolymorphicJsonAdapterFactory.of(ResizeAction::class.java, "action")
                        .withSubtype(ScaleExactResizeAction::class.java, "scale_exact")
                )
                .add(KotlinJsonAdapterFactory())
        return builder
    }

    // without this Adapter that has no CloudSpecificToJsonAdapter, map to stage from Json will loop as we try
    // to pull the flattened cloudprovider out and convert the rest of the map to a stage
    inline fun <reified T>  createNonCloudSpecificAdapter(): JsonAdapter<T> =
            jsonAdapterBuilder(Moshi.Builder(), false)
                    .build().adapter(T::class.java)

    inline fun <reified T> createAdapter(): JsonAdapter<T> =
            jsonAdapterBuilder(Moshi.Builder())
                    .build().adapter(T::class.java)

}
