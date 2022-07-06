package com.infinum.jsonapix.processor.specs

import com.infinum.jsonapix.core.common.JsonApiConstants
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.withIndent

/**
 * Created included parameter that is used in *JsonXExtensions* class
 */
internal object IncludedSpecBuilder {

    fun build(
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        if (oneRelationships.isEmpty() && manyRelationships.isEmpty()) {
            codeBlockBuilder.addStatement("listOf()")
        } else {
            codeBlockBuilder.addStatement("listOf(").indent().withIndent {
                oneRelationships.forEachIndexed { index, property ->
                    if (property.type.isNullable) {
                        codeBlockBuilder.add("${property.name}!!.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}()")
                    } else {
                        codeBlockBuilder.add("${property.name}.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}()")
                    }

                    if (index != oneRelationships.lastIndex ||
                        (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
                    ) {
                        codeBlockBuilder.addStatement(",")
                    }
                }

                manyRelationships.forEachIndexed { index, property ->
                    if (property.type.isNullable) {
                        codeBlockBuilder.add("*${property.name}!!.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")

                    } else {
                        codeBlockBuilder.add("*${property.name}.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")
                    }

                    if (index != manyRelationships.lastIndex) {
                        codeBlockBuilder.addStatement(",")
                    } else {
                        codeBlockBuilder.addStatement("")
                    }
                }
            }
            codeBlockBuilder.addStatement(")")
        }

        return codeBlockBuilder.build()
    }

    fun buildForList(
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()

        if (oneRelationships.isEmpty() && manyRelationships.isEmpty()) {
            codeBlockBuilder.addStatement("listOf()")
        } else {
            codeBlockBuilder.addStatement("listOf(").indent().withIndent {
                oneRelationships.forEachIndexed { index, property ->
                    if (property.type.isNullable) {
                        codeBlockBuilder.add("*map { it.${property.name}!!.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")
                    } else {
                        codeBlockBuilder.add("*map { it.${property.name}.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() }.toTypedArray()")
                    }

                    if (index != oneRelationships.lastIndex ||
                        (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
                    ) {
                        codeBlockBuilder.addStatement(",")
                    }
                }

                manyRelationships.forEachIndexed { index, property ->
                    if (property.type.isNullable) {
                        codeBlockBuilder.add("*flatMap { it.${property.name}!!.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() } }.toTypedArray()")
                    } else {
                        codeBlockBuilder.add("*flatMap { it.${property.name}.map { it.${JsonApiConstants.Members.TO_RESOURCE_OBJECT}() } }.toTypedArray()")
                    }
                    if (index != manyRelationships.lastIndex) {
                        codeBlockBuilder.addStatement(",")
                    } else {
                        codeBlockBuilder.addStatement("")
                    }
                }
            }
            codeBlockBuilder.addStatement(")")
        }

        return codeBlockBuilder.build()
    }
}
