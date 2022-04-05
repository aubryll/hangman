package com.freeman.hangman.config.mapper.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import org.springframework.stereotype.Component

@Component
class GenericMapperService @Autowired constructor(appContext: ApplicationContext) {

    private val mapperInfo: MutableMap<String, GenericMapper<*, *>> = HashMap()
    private val appContext: ApplicationContext

    private fun findGenericMappers(): Collection<GenericMapper<*, *>> {
        return appContext.getBeansOfType(GenericMapper::class.java).values

    }
    init {
        this.appContext = appContext
        val foundMappers: Collection<GenericMapper<*, *>> = findGenericMappers()
        for (foundMapper in foundMappers) {
            val sourceType = getMapperSourceType(foundMapper)
            val targetType = getMapperTargetType(foundMapper)
            mapperInfo["$sourceType-$targetType"] = foundMapper
            mapperInfo["$targetType-$sourceType"] = foundMapper
        }
    }
    private fun getMapperSourceType(mapper: GenericMapper<*, *>): String {
        val mapperTypeInfo = GenericTypeResolver.resolveTypeArguments(
            mapper.javaClass,
            GenericMapper::class.java
        )
        return mapperTypeInfo!![0].simpleName
    }

    private fun getMapperTargetType(mapper: GenericMapper<*, *>): String {
        val mapperTypeInfo = GenericTypeResolver.resolveTypeArguments(
            mapper.javaClass,
            GenericMapper::class.java
        )
        return mapperTypeInfo!![1].simpleName
    }

    fun getMapper(sourceType: Class<*>, targetType: Class<*>): GenericMapper<*, *>? {
        val mapperKey = sourceType.simpleName + "-" + targetType.simpleName
        return mapperInfo[mapperKey]
    }


}