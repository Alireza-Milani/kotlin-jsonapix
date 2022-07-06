package com.infinum.jsonapix.processor.specs

import com.infinum.jsonapix.annotations.HasMany
import com.infinum.jsonapix.annotations.HasOne
import com.infinum.jsonapix.core.common.JsonApiConstants
import com.infinum.jsonapix.core.common.JsonApiConstants.Prefix.withName
import com.infinum.jsonapix.core.resources.Links
import com.infinum.jsonapix.core.resources.ManyRelationshipMember
import com.infinum.jsonapix.core.resources.OneRelationshipMember
import com.infinum.jsonapix.core.resources.Relationships
import com.infinum.jsonapix.core.resources.ResourceIdentifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Created data class according to data class's properties that have relation annotation
 */
internal object RelationshipsSpecBuilder {

    private val serializableClassName = Serializable::class.asClassName()

    fun build(
        className: ClassName,
        type: String,
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): TypeSpec {
        val generatedName = JsonApiConstants.Prefix.RELATIONSHIPS.withName(className.simpleName)

        val properties: MutableList<PropertySpec> = oneRelationships.map {
            val builder = if (it.type.isNullable) {
                PropertySpec.builder(it.name, OneRelationshipMember::class.asTypeName().copy(nullable = true))
            } else {
                PropertySpec.builder(it.name, OneRelationshipMember::class)
            }
            builder.initializer(it.name).build()
        }.toMutableList()

        properties.addAll(
            manyRelationships.map {
                val builder = if (it.type.isNullable) {
                    PropertySpec.builder(it.name, ManyRelationshipMember::class.asTypeName().copy(nullable = true))
                } else {
                    PropertySpec.builder(it.name, ManyRelationshipMember::class)
                }
                builder.initializer(it.name).build()
            }
        )

        val parameterSpecs = mapPropertiesToParams(properties)

        return TypeSpec.classBuilder(generatedName)
            .addModifiers(KModifier.DATA)
            .addSuperinterface(Relationships::class)
            .addAnnotation(serializableClassName)
            .addAnnotation(Specs.getSerialNameSpec(JsonApiConstants.Prefix.RELATIONSHIPS.withName(type)))
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(parameterSpecs)
                    .build()
            )
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(fromOriginalObjectSpec(className, generatedName, oneRelationships, manyRelationships))
                    .build()
            )
            .addProperties(properties)
            .addProperty(linksPropertySpec(oneRelationships, manyRelationships))
            .build()
    }

    @SuppressWarnings("SpreadOperator")
    private fun fromOriginalObjectSpec(
        originalClass: ClassName,
        generatedName: String,
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): FunSpec {
        val constructorStringBuilder = StringBuilder()
        val builderArgs = mutableListOf<Any>(generatedName)
        oneRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                constructorStringBuilder.append(
                    """
                    |${property.name} = originalObject.${property.name}?.let {
                    |    %T(%T(%L, it.id())) 
                    |  }""".trimMargin()
                )
            } else {
                constructorStringBuilder.append(
                    """
                    |${property.name} = originalObject.${property.name}.let {
                    |    %T(%T(%L, it.id())) 
                    |  }""".trimMargin()
                )
            }

            builderArgs.add(OneRelationshipMember::class.asClassName())
            builderArgs.add(ResourceIdentifier::class.asClassName())
            builderArgs.add(getTypeOfRelationship(property))
            if (
                index != oneRelationships.lastIndex ||
                (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
            ) {
                constructorStringBuilder.append(",\n  ")
            }
        }

        manyRelationships.forEachIndexed { index, property ->
            if (property.type.isNullable) {
                constructorStringBuilder.append(
                    """
                    |${property.name} = originalObject.${property.name}?.let {
                    |    %T(it.map { %T(%L, it.id()) }) 
                    |  }""".trimMargin()
                )
            } else {
                constructorStringBuilder.append(
                    """
                    |${property.name} = %T(originalObject.${property.name}.map {
                    |    %T(%L, it.id())
                    |  })""".trimMargin()
                )
            }
            builderArgs.add(ManyRelationshipMember::class.asClassName())
            builderArgs.add(ResourceIdentifier::class.asClassName())
            builderArgs.add(getTypeOfRelationship(property))
            if (index != manyRelationships.lastIndex) {
                constructorStringBuilder.append(",\n  ")
            }
        }

        return FunSpec.builder(JsonApiConstants.Members.FROM_ORIGINAL_OBJECT)
            .addParameter(
                ParameterSpec.builder("originalObject", originalClass).build()
            )
            .addCode(
                """
                |return %L(
                |  $constructorStringBuilder
                |)""".trimMargin(),
                *builderArgs.toTypedArray()
            )
            .build()
    }

    private fun linksPropertySpec(
        oneRelationships: List<PropertySpec>,
        manyRelationships: List<PropertySpec>
    ): PropertySpec {
        var returnStatement = "mapOf(\n  "
        oneRelationships.forEachIndexed { index, it ->
            returnStatement += if (it.type.isNullable) {
                "\"${it.name}\" to ${it.name}?.links"
            } else {
                "\"${it.name}\" to ${it.name}.links"
            }

            if (
                index != oneRelationships.lastIndex ||
                (index == oneRelationships.lastIndex && manyRelationships.isNotEmpty())
            ) {
                returnStatement += (",\n  ")
            }
        }
        manyRelationships.forEachIndexed { index, it ->
            returnStatement += if (it.type.isNullable) {
                "\"${it.name}\" to ${it.name}?.links"
            } else {
                "\"${it.name}\" to ${it.name}.links"
            }

            if (index != manyRelationships.lastIndex) {
                returnStatement += (",\n  ")
            }
        }
        returnStatement += "\n)"

        val builder = PropertySpec.builder(
            JsonApiConstants.Keys.LINKS,
            Map::class.asClassName()
                .parameterizedBy(
                    String::class.asTypeName(),
                    Links::class.asTypeName().copy(nullable = true)
                ),
            KModifier.OVERRIDE
        ).addAnnotation(AnnotationSpec.builder(Transient::class.asClassName()).build())

        return builder.initializer(returnStatement).build()
    }

    private fun getTypeOfRelationship(property: PropertySpec): String {
        return property.annotations.first { annotation ->
            annotation.typeName == HasOne::class.asTypeName() ||
                annotation.typeName == HasMany::class.asTypeName()
        }.members.first { member ->
            member.toString().trim().startsWith("type")
        }.toString().split("=")[1].trim()
    }

    private fun mapPropertiesToParams(properties: List<PropertySpec>): List<ParameterSpec> {
        return properties.map {
            ParameterSpec.builder(it.name, it.type)
                .addAnnotation(Specs.getSerialNameSpec(it.name))
                .apply {
                    if (it.type.isNullable) {
                        defaultValue("%L", null)
                    }
                }
                .build()
        }
    }
}
