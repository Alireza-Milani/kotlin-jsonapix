package com.infinum.jsonapix.processor.specs.jsonxextensions.funspecbuilders

import com.infinum.jsonapix.core.common.JsonApiConstants
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

/**
 * Created function *toJsonApiX* according to json model
 */
internal object WrapperFunSpecBuilder {

    fun build(
        originalClass: ClassName,
        wrapperClass: ClassName,
        includedListStatement: String?
    ): FunSpec {
        val builderArgs =
            mutableListOf<Any>(wrapperClass)
        val returnStatement = StringBuilder(
            """
            |return %T(
            |  data = this.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}()""".trimMargin()
        )

        if (includedListStatement != null) {
            returnStatement.appendLine(", ")
            returnStatement.append("  included = $includedListStatement")
        }
        returnStatement.append(")")
        return FunSpec.builder(JsonApiConstants.Members.JSONX_WRAPPER_GETTER)
            .receiver(originalClass)
            .returns(wrapperClass)
            .addCode(
                format = returnStatement.toString(),
                args = builderArgs.toTypedArray()
            )
            .build()
    }
}
