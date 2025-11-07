package com.github.xepozz.moonshine.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Route(
    val domain: String?,
    val method: String,
    val uri: String,
    val name: String?,
    val action: String,
    val middleware: List<String>,
)

fun toRouteCollection(json: String) = Json.decodeFromString<Collection<Route>>(json)

//{
//  "domain" : null,
//  "method" : "GET|HEAD",
//  "uri" : "/",
//  "name" : null,
//  "action" : "Closure",
//  "middleware" : [ "web" ]
//}

