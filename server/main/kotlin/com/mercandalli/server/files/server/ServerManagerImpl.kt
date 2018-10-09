package com.mercandalli.server.files.server

import com.mercandalli.sdk.files.api.online.FileOnlineLoginManager
import com.mercandalli.server.files.file_handler.FileHandlerGet
import com.mercandalli.server.files.file_handler.FileHandlerPost
import com.mercandalli.server.files.log.LogManager
import com.mercandalli.server.files.shell.ShellManager
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.content.*
import io.ktor.features.CallLogging
import io.ktor.features.StatusPages
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.pipeline.PipelineContext
import io.ktor.request.path
import io.ktor.request.receiveText
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class ServerManagerImpl(
        private val rootServerPath: String,
        private val fileHandlerGet: FileHandlerGet,
        private val fileHandlerPost: FileHandlerPost,
        private val fileOnlineLoginManager: FileOnlineLoginManager,
        private val shellManager: ShellManager,
        private val logManager: LogManager,
        private val pullSubRepositoryShellFile: java.io.File
) : ServerManager {

    private val server: NettyApplicationEngine

    init {
        val module: Application.() -> Unit = createModule()
        // http://ktor.io/servers/configuration.html
        server = embeddedServer(
                Netty,
                port = 80,
                module = module,
                host = "0.0.0.0"
        )
    }

    override fun start() {
        server.start(wait = true)
    }

    override fun stop() {
        server.stop(1, 1, TimeUnit.SECONDS)
    }

    private fun createModule(): Application.() -> Unit {
        return {
            install(StatusPages) {
                exception<Throwable> {
                    call.respond(HttpStatusCode.InternalServerError)
                }
                status(HttpStatusCode.NotFound) {
                    respondNotFound(it)
                }
            }
            install(Authentication) {
                basic(name = "myauth1") {
                    realm = "Ktor Server"
                    validate { credentials ->
                        if (credentials.name == credentials.password) {
                            UserIdPrincipal(credentials.name)
                        } else {
                            null
                        }
                    }
                }
            }
            intercept(ApplicationCallPipeline.Call) {
                logManager.logRequest(TAG, call.request)
            }
            routing {
                authenticate("myauth1") {
                    get("/admin") {
                        // ...
                    }
                }
                get("/status") {
                    respondStatus()
                }
                get("/pull") { _ ->
                    val absolutePath = pullSubRepositoryShellFile.absolutePath
                    shellManager.execute("bash $absolutePath") {

                    }
                    call.respondText("Pull sub repository")
                }
                get("/file-api/file") {
                    val response = fileHandlerGet.get()
                    call.respondText(response)
                }
                post("/file-api/file") {
                    val body = call.receiveText()
                    val response = fileHandlerPost.post(body)
                    call.respondText(response)
                }
                get("/file-api/file/{id}") {
                    val id = call.parameters["id"]
                    val response = fileHandlerGet.get(id!!)
                    call.respondText(response)
                }
                get("/timothe") {
                    call.respondRedirect("/timothe/index.html")
                }
                static("/timothe") {
                    staticRootFolder = File("$rootServerPath/static")
                    files("timothe")
                    default("timothe/index.html")
                }
                get("/1418") {
                    call.respondRedirect("/1418/index.html")
                }
                static("/1418") {
                    staticRootFolder = File("$rootServerPath/static")
                    files("1418")
                    default("1418/index.html")
                }
                static {
                    staticRootFolder = File(rootServerPath)
                    files("static")
                    default("static/index.html")
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.respondStatus() {
        call.application.environment.log.debug("/status")
        val uri = call.request.uri
        val origin = call.request.origin
        val responseJson = JSONObject()
        responseJson.put("running", true)
        responseJson.put("uri", uri)
        responseJson.put("origin", origin)
        responseJson.put("root_server_path", rootServerPath)
        call.respondText(responseJson.toString(), ContentType.Text.Plain)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.respondNotFound(httpStatusCode: HttpStatusCode) {
        val statusCode = httpStatusCode.value
        val statusDescription = httpStatusCode.description
        val responseJson = JSONObject()
        val uri = call.request.uri
        responseJson.put("uri", uri)
        responseJson.put("status_code", statusCode)
        responseJson.put("status_description", statusDescription)
        val message = TextContent(
                responseJson.toString(),
                ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                httpStatusCode
        )
        call.respond(
                message
        )
    }

    companion object {
        val TAG = "ServerManager"
    }
}