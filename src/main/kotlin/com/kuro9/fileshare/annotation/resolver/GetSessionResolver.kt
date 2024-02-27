package com.kuro9.fileshare.annotation.resolver

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.exception.NotAuthorizedException
import com.kuro9.fileshare.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class GetSessionResolver(
    private val sessionService: SessionService
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter) =
        parameter.hasParameterAnnotation(GetSession::class.java)
                && parameter.parameterType == Session::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val headerVal = request.cookies?.find { it.name == "auth_code" }?.name
        return headerVal?.let {
            sessionService.getSession(it)
        } ?: throw NotAuthorizedException("Authorization Header is empty")
    }
}