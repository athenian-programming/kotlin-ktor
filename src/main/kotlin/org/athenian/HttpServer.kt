package org.athenian

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify

fun main() {
    val httpServer =
            embeddedServer(CIO, port = 8080) {

                install(DefaultHeaders)
                install(Compression) {
                    gzip {
                        priority = 1.0
                    }
                    deflate {
                        priority = 10.0
                        minimumSize(1024) // condition
                    }
                }

                routing {
                    get("/") {
                        call.apply {
                            response.header("cache-control", "must-revalidate,no-cache,no-store")
                            response.status(HttpStatusCode.OK)
                            respondText("index.html requested", ContentType.Text.Plain)
                        }
                    }
                    get("/json") {
                        call.apply {
                            response.header("cache-control", "must-revalidate,no-cache,no-store")
                            response.status(HttpStatusCode.OK)
                            val map = mapOf("Field1" to "val1", "Field2" to "val2")
                            val json = Json.stringify(map)
                            respondText(json, ContentType.Application.Json)
                        }
                    }
                    get("/html-dsl") {
                        call.apply {
                            response.header("cache-control", "must-revalidate,no-cache,no-store")
                            response.status(HttpStatusCode.OK)
                            respondHtml {
                            body {
                                h1 { +"HTML DSL" }
                                ul {
                                    for (n in 1..10) {
                                        li { +"$n" }
                                    }
                                }
                            }
                        }
                        }
                    }

                }
            }

    httpServer.start(true)

}
