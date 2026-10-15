package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "title") val title: String? = "",
    @Json(name = "description") val description: String? = "",
    @Json(name = "timestamp") val timestamp: String? = "",
    @Json(name = "read") val read: Boolean = false,
    @Json(name = "type") val type: String? = "general", // "new_message", "missed_audio_call", "missed_video_call", "meeting_invite", "general", "game_invite", "pos_sale", "new_follower"
    @Json(name = "link") val link: String? = null
)

@JsonClass(generateAdapter = true)
data class NotificationsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "notifications") val notifications: List<NotificationDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class MarkReadResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "notificationId") val notificationId: Long? = null,
    @Json(name = "read") val read: Boolean? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class MarkReadAllResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "readAll") val readAll: Boolean? = null,
    @Json(name = "error") val error: String? = null
)
