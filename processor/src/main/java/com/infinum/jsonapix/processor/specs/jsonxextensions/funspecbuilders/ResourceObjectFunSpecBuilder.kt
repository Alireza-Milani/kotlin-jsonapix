package com.infinum.jsonapix.processor.specs.jsonxextensions.funspecbuilders

import com.infinum.jsonapix.core.common.JsonApiConstants
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

/**
 * Created function *toResourceObject* according to json model
 */
internal object ResourceObjectFunSpecBuilder {

    fun build(
        originalClass: ClassName,
        resourceObjectClass: ClassName,
        attributesClass: ClassName?,
        relationshipsClass: ClassName?
    ): FunSpec {
        val returnStatement = StringBuilder()
        val builderArgs = mutableListOf<Any>(resourceObjectClass)

        returnStatement.append("""
            |return %T(
            |  id = (this as? JsonApiModel)?.let { this.id() } ?: "0",  
            |""".trimMargin())

        if (attributesClass != null) {
            returnStatement.append(
                "  attributes = %T.${JsonApiConstants.Members.FROM_ORIGINAL_OBJECT}(this)"
            )
            builderArgs.add(attributesClass)
        }

        if (relationshipsClass != null) {
            if (attributesClass != null) {
                returnStatement.appendLine(", ")
            }
            returnStatement.append(
                "  relationships = %T.${JsonApiConstants.Members.FROM_ORIGINAL_OBJECT}(this)\n"
            )
            builderArgs.add(relationshipsClass)
        } else {
            returnStatement.append("\n")
        }

        returnStatement.append(")")

        return FunSpec.builder(JsonApiConstants.Members.TO_RESOURCE_OBJECT)
            .receiver(originalClass)
            .returns(resourceObjectClass)
            .addStatement(
                format = returnStatement.toString(),
                args = builderArgs.toTypedArray()
            )
            .build()
    }
}
