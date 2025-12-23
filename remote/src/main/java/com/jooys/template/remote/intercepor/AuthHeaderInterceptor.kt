package com.jooys.template.remote.intercepor

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val urlBuilder: HttpUrl.Builder = chain.request().url.newBuilder()

        builder
            .header("Accept", "application/json")
            .header("Authorization", "Client-ID Eg954NzHsrXGGFGbTKJBjS_iRdfUuTVgQijW4KGNxIU")
            .url(urlBuilder.build())

        return chain.proceed(builder.build())
    }

}
