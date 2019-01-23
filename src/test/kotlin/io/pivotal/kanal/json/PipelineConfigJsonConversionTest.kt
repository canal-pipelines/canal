package io.pivotal.kanal.json

import io.pivotal.kanal.fluent.Stages
import io.pivotal.kanal.model.*
import net.javacrumbs.jsonunit.assertj.JsonAssertions
import org.assertj.core.api.Assertions
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class PipelineConfigJsonConversionTest  {

    companion object {

        @Language("JSON")
        @JvmStatic
        val json = """
    {
        "schema": "v2",
        "application": "waze",
        "name": "My First SpEL Pipeline",
        "template": {
            "source": "spinnaker://newSpelTemplate"
        },
        "variables": {
            "waitTime": 6
        },
        "inherit": [],
        "triggers": [
            {
                "type": "pubsub",
                "enabled": true,
                "pubsubSystem": "google",
                "subscription": "super-derp",
                "subscriptionName": "super-derp",
                "source": "jack",
                "attributeConstraints": {},
                "payloadConstraints": {}
            }
        ],
        "parameters": [],
        "notifications": [],
        "description": "",
        "stages": [
            {
                "refId": "wait2",
                "name": "",
                "requisiteStageRefIds": ["wait1"],
                "type": "wait",
                "waitTime": "67",
                "comments": ""
            },
            {
                "refId": "wait0",
                "name": "",
                "inject": {
                    "type": "first",
                    "first": true
                },
                "type": "wait",
                "waitTime": "2",
                "comments": "",
                "requisiteStageRefIds": []
            }
        ]
    }
    """.trimMargin()

        @JvmStatic
        val model = io.pivotal.kanal.model.PipelineConfig(
                "waze",
                "My First SpEL Pipeline",
                TemplateSource("spinnaker://newSpelTemplate"),
                Pipeline(
                        triggers = listOf(
                                PubSubTrigger(
                                        "google",
                                        "super-derp",
                                        "jack"
                                )
                        ),
                        stageGraph = Stages.of(
                                WaitStage(67),
                                refId = "wait2",
                                requisiteStageRefIds = listOf("wait1")
                        ).and(
                                WaitStage(2),
                                refId = "wait0",
                                inject = Inject.First()
                        ).stageGraph
                ),
                mapOf("waitTime" to 6)
        )
    }

    @Test
    fun `JSON pipeline template to model`() {
        val pipeline = JsonAdapterFactory().pipelineConfigAdapter().fromJson(json)
        Assertions.assertThat(pipeline).isEqualTo(model)
    }

    @Test
    fun `generate pipeline template JSON`() {
        val json = JsonAdapterFactory().pipelineConfigAdapter().toJson(model)
        JsonAssertions.assertThatJson(json).isEqualTo(json)
    }

}
