package org.athenian

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.slf4j.event.Level

suspend fun ApplicationCall.respondWith(json: String, contentType: ContentType) {
  response.header("cache-control", "must-revalidate,no-cache,no-store")
  //response.header("Access-Control-Allow-Credentials", "true")
  //response.header("Access-Control-Allow-Origin", "*")
  response.status(HttpStatusCode.OK)
  respondText(json, contentType)
}

fun main() {
  val httpServer =
      embeddedServer(CIO, port = 8080) {

        install(CORS) {
          method(HttpMethod.Options)
          method(HttpMethod.Put)
          method(HttpMethod.Delete)
          method(HttpMethod.Patch)
          header(HttpHeaders.Authorization)
          header("MyCustomHeader")
          allowCredentials = true
          anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
        }

        install(ContentNegotiation) {
          serialization(
              contentType = ContentType.Application.Json,
              json = Json(DefaultJsonConfiguration.copy(prettyPrint = true))
          )
        }

        install(CallLogging) {
          level = Level.INFO
        }

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

          static("static") {
            resources("static")
          }

          get("/") {
            call.respondWith("index.html requested", ContentType.Text.Plain)
          }
          get("/json-ez") {
            val map = mapOf("Field3" to "val1", "Field4" to "val2")
            call.respond(map)
          }
          get("/json-manual") {
            val map = mapOf("Field1" to "val1", "Field2" to "val2")
            val json = Json.stringify(map)
            call.respondWith(json, ContentType.Application.Json)
          }
          get("/jsonp") {
            val map = mapOf("Field1" to "val1", "Field2" to "val2")
            val json = Json.stringify(map)
            call.respondWith("parseResponse($json)", ContentType.Application.JavaScript)
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
