package com.infinum.jsonapix.processor.specs

import com.infinum.jsonapix.core.common.JsonApiConstants
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec

/**
 * Created included parameter that is used in *JsonXExtensions* class
 */
internal object IncludedSpecBuilder {

    fun build(
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): CodeBlock {
        val statement = StringBuilder("listOf(\n  ")
        oneRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                statement.append("  ${property.name}!!.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}()")
            } else {
                statement.append("  ${property.name}.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}()")
            }

            if (index != oneRelationships.lastIndex ||
                (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
            ) {
                statement.append(",\n  ")
            }
        }

        manyRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                statement.append(
                    "  *${property.name}!!.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()"
                )
            } else {
                statement.append(
                    "  *${property.name}.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()"
                )
            }

            if (index != manyRelationships.lastIndex) {
                statement.append(",\n  ")
            }
        }
        statement.append("\n  )")

        return CodeBlock.of(statement.toString())
    }

    fun buildForList(
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): CodeBlock {
        val statement = StringBuilder("listOf(\n  ")
        oneRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                statement.append("  *map { it.${property.name}!!.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")
            } else {
                statement.append("  *map { it.${property.name}.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")
            }

            if (index != oneRelationships.lastIndex ||
                (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
            ) {
                statement.append(",\n  ")
            }
        }

        manyRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                statement.append(
                    "  *flatMap { it.${property.name}!!.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() } }.toTypedArray()"
                )
            } else {
                statement.append(
                    "  *flatMap { it.${property.name}.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() } }.toTypedArray()"
                )
            }
            if (index != manyRelationships.lastIndex) {
                statement.append(",\n  ")
            }
        }
        statement.append("\n  )")

        return CodeBlock.of(statement.toString())
    }
}
