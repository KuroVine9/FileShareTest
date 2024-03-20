package com.kuro9.fileshare.config

import com.kuro9.fileshare.annotation.resolver.GetSessionResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ResolverConfig(
    val getSessionResolver: GetSessionResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(getSessionResolver)
    }
}